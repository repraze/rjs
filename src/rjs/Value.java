package rjs;

public abstract class Value{
    enum Type{
        String,
        Number,
        Boolean
    }

    public abstract String toString();
    public abstract Type type();
}
