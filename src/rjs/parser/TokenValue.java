package rjs.parser;

public interface TokenValue<TToken, TValueGen>{
    public abstract TToken token();
    public abstract TValueGen value();
}
