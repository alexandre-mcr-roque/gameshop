package demo.gameshop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import demo.gameshop.annotations.FileValidation;
import demo.gameshop.annotations.PasswordsMatch;
import demo.gameshop.models.NewPasswordForm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles(profiles = {"test"})
public class ConstraintValidatorTests {
	private final Validator validator;

	public ConstraintValidatorTests() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		this.validator = factory.getValidator();
	}

	// Classses that implement the validators
	private final class TestPasswordForm implements NewPasswordForm {
		private String password;
		private String confirmPassword;

		public TestPasswordForm(String password, String confirmPassword) {
			this.password = password;
			this.confirmPassword = confirmPassword;
		}

		@Override public String getPassword() { return this.password; }
		@Override public String getConfirmPassword() { return this.confirmPassword; }
	}

	@PasswordsMatch(message = "Test Error Message")
	private final class TestPasswordFormTwo implements NewPasswordForm {
		private String password;
		private String confirmPassword;

		public TestPasswordFormTwo(String password, String confirmPassword) {
			this.password = password;
			this.confirmPassword = confirmPassword;
		}

		@Override public String getPassword() { return this.password; }
		@Override public String getConfirmPassword() { return this.confirmPassword; }
	}

	private final class TestMultipartFileForm {
		@FileValidation // not required, no limit
		private MultipartFile file1;

		@FileValidation(max = "32KB", message = "Limit is 32KiB") // not required, 32KiB limit
		private MultipartFile file2;
		
		@FileValidation(required = true, max = "128KB", message = "Required and limit is 128KiB") // required, 128KiB limit
		private MultipartFile file3;
		
		public TestMultipartFileForm(MultipartFile file1, MultipartFile file2, MultipartFile file3) {
			this.file1 = file1;
			this.file2 = file2;
			this.file3 = file3;
		}
	}
	
	private final class TestMultipartFileForm2 {
		@FileValidation(max = "invalid string")
		private MultipartFile file;
		
		public TestMultipartFileForm2() {
			this.file = new MockMultipartFile("empty.txt", new byte[0]);
		}
	}
    
    @Test
    void passwordsMatchValidatorTests() {
    	// Create used variables
    	NewPasswordForm form;
    	Set<ConstraintViolation<NewPasswordForm>> violations;
    	boolean messageMatch;
    	
    	// Test null form
    	// (validator should throw IllegalArgumentException as the object should never be null)
    	assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> validator.validate((TestPasswordForm) null));

    	// Test null values
    	form = new TestPasswordForm(null, null);
    	
    	// Expecting 2 violations with equal text, one for the password field
        // and the other for the confirmPassword field
    	violations = validator.validate(form);
        messageMatch = violations.stream()
                .anyMatch(v -> "Passwords do not match".equals(v.getMessage()));
        assertThat(messageMatch).isTrue();
        assertThat(violations.size()).isEqualTo(2);
	
    	// Test different passwords
    	form = new TestPasswordForm("secret123", "secret456");

    	// Expecting 2 violations with equal text, one for the password field
        // and the other for the confirmPassword field
        violations = validator.validate(form);
        messageMatch = violations.stream()
                .anyMatch(v -> "Passwords do not match".equals(v.getMessage()));
        assertThat(messageMatch).isTrue();
        assertThat(violations.size()).isEqualTo(2);
       
        // Test overwriting the default message
        // by declaring the annotation in the implementing class
        form = new TestPasswordFormTwo("secret123", "secret456");

        // Expecting 2 violations with equal text, one for the password field
        // and the other for the confirmPassword field
        violations = validator.validate(form);
        messageMatch = violations.stream()
				.anyMatch(v -> "Test Error Message".equals(v.getMessage()));
        assertThat(messageMatch).isTrue();
        assertThat(violations.size()).isEqualTo(2);
        
        // Test a successful validation
        form = new TestPasswordForm("secret123", "secret123");

        // Expecting 0 violations
        violations = validator.validate(form);
        assertThat(violations.isEmpty()).isTrue();
    }
    
    @Test
    void fileValidationValidatorTests() throws FileNotFoundException, IOException {
    	// Create used variables
    	TestMultipartFileForm form;
    	MockMultipartFile file;
    	Set<ConstraintViolation<TestMultipartFileForm>> violations;
    	boolean messageMatch;
    	
    	// Test null form
    	// (validator should throw IllegalArgumentException as the object should never be null)
    	assertThatExceptionOfType(IllegalArgumentException.class)
		.isThrownBy(() -> validator.validate((TestMultipartFileForm) null));
    	
    	// Test null files
    	form = new TestMultipartFileForm(null, null, null);
    	
    	// Expecting 3 violation
    	violations = validator.validate(form);
    	messageMatch = violations.stream()
    			.anyMatch(v -> "Failed validation".equals(v.getMessage()));
    	assertThat(messageMatch).isTrue();
    	assertThat(violations.size()).isEqualTo(3);
    	
    	// Test empty files
    	file = new MockMultipartFile("empty.txt", new byte[0]);
    	form = new TestMultipartFileForm(file, file, file);
    	
    	// Expecting 1 violation from file3
    	violations = validator.validate(form);
    	messageMatch = violations.stream()
    			.anyMatch(v -> "Required and limit is 128KiB".equals(v.getMessage())
    					&& "file3".equals(v.getPropertyPath().toString()));
    	assertThat(violations.size()).isEqualTo(1);
    	
    	// Test with files (~110KiB)
		file = new MockMultipartFile("image.png", new FileInputStream("src/test/resources/demo/gameshop/test-image.png"));
		form = new TestMultipartFileForm(file, file, file);
		
		// Expecting 1 violation from file2
    	violations = validator.validate(form);messageMatch = violations.stream()
    			.anyMatch(v -> "Limit is 32KiB".equals(v.getMessage())
    					&& "file2".equals(v.getPropertyPath().toString()));
    	assertThat(violations.size()).isEqualTo(1);
        
        // Test invalid max file size string in annotation
        assertThatExceptionOfType(ValidationException.class)
        	.isThrownBy(() -> validator.validate(new TestMultipartFileForm2()))
        	.withCauseInstanceOf(IllegalArgumentException.class);
    }
}