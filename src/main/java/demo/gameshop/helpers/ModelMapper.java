package demo.gameshop.helpers;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import demo.gameshop.interfaces.models.Mappable;

public final class ModelMapper {
    private ModelMapper() {}

    public static <D, M extends Mappable<D, M>> M fromDocument(D doc, Supplier<M> supplier) {
        return supplier.get().mapper(doc);
    }

    public static <D, M extends Mappable<D, M>> List<M> fromDocuments(D[] docs, Supplier<M> supplier) {
        return fromDocuments(Stream.of(docs), supplier);
    }

    public static <D, M extends Mappable<D, M>> List<M> fromDocuments(List<D> docs, Supplier<M> supplier) {
        return fromDocuments(docs.stream(), supplier);
    }

    public static <D, M extends Mappable<D, M>> List<M> fromDocuments(Stream<D> docs, Supplier<M> supplier) {
        M model = supplier.get();
    	return docs.map(model::mapper).toList();
    }
}
