package demo.gameshop;

import demo.gameshop.annotations.FileValidation;
import demo.gameshop.annotations.PasswordsMatch;
import demo.gameshop.models.NewPasswordForm;
import jakarta.validation.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ActiveProfiles(profiles = {"test"})
public class ConstraintValidatorTests {
	private final Validator validator;

	public ConstraintValidatorTests() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    // Classes that implement the validators
    private record TestPasswordForm(String password, String confirmPassword) implements NewPasswordForm {
        @Override public String getPassword() { return this.password; }
        @Override public String getConfirmPassword() { return this.confirmPassword; }
    }

	@PasswordsMatch(message = "Test Error Message")
    private record TestPasswordFormTwo(String password, String confirmPassword) implements NewPasswordForm {
		@Override public String getPassword() { return this.password; }
		@Override public String getConfirmPassword() { return this.confirmPassword; }
	}


    private record TestMultipartFileForm(
            // not required, no limit
            @FileValidation MultipartFile file1,
            // not required, 32KiB limit
            @FileValidation(max = "32KB", message = "Limit is 32KiB") MultipartFile file2,
            // required, 128KiB limit
            @FileValidation(required = true, max = "128KB", message = "Required and limit is 128KiB") MultipartFile file3) {}
	
	private static final class TestMultipartFileForm2 {
        // Should throw an exception on validation
		@FileValidation(max = "invalid string")
		private final MultipartFile file = new MockMultipartFile("empty.txt", new byte[0]);
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
    void fileValidationValidatorTests() throws IOException {
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
        assertThat(messageMatch).isTrue();
    	assertThat(violations.size()).isEqualTo(1);
    	
    	// Test with files (~110KiB)
		file = new MockMultipartFile("image.png", new FileInputStream("src/test/resources/demo/gameshop/test-image.png"));
		form = new TestMultipartFileForm(file, file, file);
		
		// Expecting 1 violation from file2
    	violations = validator.validate(form);messageMatch = violations.stream()
    			.anyMatch(v -> "Limit is 32KiB".equals(v.getMessage())
    					&& "file2".equals(v.getPropertyPath().toString()));
        assertThat(messageMatch).isTrue();
    	assertThat(violations.size()).isEqualTo(1);
        
        // Test invalid max file size string in annotation
        assertThatExceptionOfType(ValidationException.class)
        	.isThrownBy(() -> validator.validate(new TestMultipartFileForm2()))
        	.withCauseInstanceOf(IllegalArgumentException.class);
    }
}