package rjs.parser;

import rjs.Evaluable;
import rjs.Value;

public abstract class Statement implements Evaluable {
    public abstract String toString();
    public abstract Value evaluate();
}
