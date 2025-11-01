package demo.gameshop.interfaces.models;

import java.util.function.Function;

import lombok.NonNull;

/**
 * Contract for mapping a document/DTO object of type {@code D} into a model of type {@code M}.
 *
 * <p>Implementations should copy relevant data from the provided document into the model
 * instance (often {@code this}) and return the mapped model. The {@link NonNull} annotation
 * enforces that {@code doc} must not be {@code null} and implementations may assume a
 * non-null value.</p>
 *
 * @param <D> The document/source type
 * @param <M> The model type that implements this interface
 */
public interface Mappable<D, M> {
	/**
	 * Map values from the provided document into this model (or another model instance)
	 * and return the mapped model.
	 *
	 * <p>Implementations should:
	 * <ul>
	 *   <li>Copy necessary fields from {@code doc} into the model</li>
	 *   <li>Return the populated model instance (commonly {@code this})</li>
	 * </ul>
	 * </p>
	 *
	 * @param doc The source document to map from; must not be {@code null}
	 * @return The mapped model instance
	 * @throws NullPointerException if {@code doc} is {@code null}
	 */
	M mapper(@NonNull D doc);
}
