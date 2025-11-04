package demo.gameshop.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Annotation for validating uploaded files in a Spring application.
 * 
 * <p>This constraint allows you to specify whether a file is required and
 * optionally limit the maximum allowed file size.
 * It is intended to be used on fields of type {@link MultipartFile}.</p>
 */
@Documented
@Constraint(validatedBy = FileValidation.FileValidationValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface FileValidation {
	boolean required() default false;
	
	String max() default "-1";
	
	String message() default "Failed validation";

	Class<?>[] groups() default { };

	Class<?>[] payload() default { };
	
	public class FileValidationValidator implements ConstraintValidator<FileValidation, MultipartFile> {
		private boolean required;
		private DataSize max;
		
		@Override
		public void initialize(FileValidation constraintAnnotation) {
			this.required = constraintAnnotation.required();
			this.max = DataSize.parse(constraintAnnotation.max());
		}
		
		@Override
		public boolean isValid(MultipartFile value, ConstraintValidatorContext context)
				throws IllegalArgumentException {
			// If the value is null immediately return as failed validation
			if (value == null) return false;
			// If the file is required but actual is empty, return as failed validation
			if (this.required && value.isEmpty()) return false;
			// If the max file size is negative, pass validation (consider as no limit)
			return this.max.isNegative() || this.max.toBytes() >= value.getSize();
		}

	}
}
