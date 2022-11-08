package main.fetcher.data;

/**
 * Save two items as pair, and is modifiable, unlike kotlin is not allow to modify
 */
public class PPair<A, B> {
    public A first;
    public B second;

    public PPair(A a, B b) {
        first = a;
        second = b;
    }
}
