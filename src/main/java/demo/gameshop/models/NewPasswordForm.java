package demo.gameshop.models;

import demo.gameshop.annotations.PasswordsMatch;

/**
 * Interface representing a form with password and confirmPassword fields.
 * 
 * <p>Already annotated with {@link PasswordsMatch} to ensure matching passwords,
 * so implementing classes inherit this validation.<br>
 * However, implementing classes can override the annotation
 * to customize the validation message if needed.</p>
 * 
 * @see PasswordsMatch
 */
@PasswordsMatch(message = "Passwords do not match")
public interface NewPasswordForm {
	String getPassword();
	String getConfirmPassword();
}
