package util.math;

@FunctionalInterface
public interface Provider<T> {
    T provide();
}
