package demo.gameshop.annotations;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import demo.gameshop.models.NewPasswordForm;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Documented
@Constraint(validatedBy = PasswordsMatch.PasswordsMatchValidator.class)
@Target({ TYPE_USE })
@Retention(RUNTIME)
/**
 * Custom annotation to validate that {@code password} and {@code confirmPassword} fields match
 * in classes implementing {@link NewPasswordForm}.
 * 
 * @see NewPasswordForm
 */
public @interface PasswordsMatch {
	String message() default "Passwords do not match";

	Class<?>[] groups() default { };

	Class<?>[] payload() default { };
	

	public static class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, NewPasswordForm> {

		private PasswordsMatch declaredAnnotation;

		@Override
		public void initialize(PasswordsMatch constraintAnnotation) {
			this.declaredAnnotation = constraintAnnotation;
		}

		@Override
		public boolean isValid(NewPasswordForm value, ConstraintValidatorContext context) {
			// If the value is null, it's invalid for our use-case
			if (value == null) {
				throw new IllegalArgumentException("NewPasswordForm object cannot be null");
			}

			// If the runtime class has its own @PasswordsMatch declaration and
			// the annotation instance on that class is different from the one
			// this validator was initialized with, skip this invocation so the
			// class-level constraint takes precedence.
			PasswordsMatch classLevel = value.getClass().getAnnotation(PasswordsMatch.class);
			if (classLevel != null && classLevel != this.declaredAnnotation) {
				return true; // defer to class-level annotation
			}

			// Get the message from the declared annotation instance (if any)
			String msg = this.declaredAnnotation != null ? this.declaredAnnotation.message() : "Passwords do not match";
			// Validate that password and confirmPassword match
			if (value.getPassword() != null && value.getConfirmPassword() != null) {
				boolean equal = value.getPassword().equals(value.getConfirmPassword());
				if (!equal) {
					return setViolations(context, msg);
				}
				return true;
			}
			return setViolations(context, msg);
		}
		
		private boolean setViolations(ConstraintValidatorContext context, String message) {
			// Attach the annotation message to both password and confirmPassword fields
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(message).addPropertyNode("password").addConstraintViolation();
			context.buildConstraintViolationWithTemplate(message).addPropertyNode("confirmPassword").addConstraintViolation();
			return false;
		}
	}
}