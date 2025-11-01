package demo.gameshop;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

import demo.gameshop.annotations.PasswordsMatch;
import demo.gameshop.models.NewPasswordForm;
import demo.gameshop.models.RegisterForm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class PasswordsMatchValidatorTests {

    private final Validator validator;

    public PasswordsMatchValidatorTests() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }
    
    

    @Test
    void whenPasswordsDoNotMatch_thenConstraintViolationOnConfirmPassword() {
        RegisterForm form = new RegisterForm();
        form.setUsername("testuser");
        form.setEmail("test@example.com");
        form.setPassword("secret123");
        form.setConfirmPassword("secret456");
        Set<ConstraintViolation<NewPasswordForm>> violations = validator.validate(form);

        // Expect at least one violation on the confirmPassword node with our message
        boolean found = violations.stream()
                .anyMatch(v -> "Passwords do not match".equals(v.getMessage()));

        assertThat(found).isTrue();
       
        // Test overwriting the default message
        // by declaring the annotation in the implementing class
        @PasswordsMatch(message = "Test Error Message")
        class TestPasswordForm implements NewPasswordForm {
		    private String password;
		    private String confirmPassword;

		    public TestPasswordForm(String password, String confirmPassword) {
		        this.password = password;
		        this.confirmPassword = confirmPassword;
		    }

		    @Override
		    public String getPassword() {
		        return this.password;
		    }

		    @Override
		    public String getConfirmPassword() {
		        return this.confirmPassword;
		    }

        }
        
        TestPasswordForm testForm = new TestPasswordForm("abc", "def");
        violations = validator.validate(testForm);
        found = violations.stream()
				.anyMatch(v -> "Test Error Message".equals(v.getMessage()));
        assertThat(found).isTrue();
        // Expecting 2 violations, one for the password field
        // and the other for the confirmPassword field
        assertThat(violations.size()).isEqualTo(2);
    }

    @Test
    void whenPasswordsMatch_thenNoEqualsViolation() {
        RegisterForm form = new RegisterForm();
        form.setUsername("testuser");
        form.setEmail("test@example.com");
        form.setPassword("secret123");
        form.setConfirmPassword("secret123");

        Set<ConstraintViolation<RegisterForm>> violations = validator.validate(form);

        boolean found = violations.stream()
                .anyMatch(v -> "Passwords do not match".equals(v.getMessage()));

        assertThat(found).isFalse();
    }
}