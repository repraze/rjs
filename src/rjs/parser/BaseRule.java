package rjs.parser;

import java.util.List;

public abstract class BaseRule<TToken, TValueGen> implements RuleInterface<TToken, TValueGen>{
    public boolean match(List<TokenValue<TToken, TValueGen>> phrase){
        return this.match(phrase, new Cursor());
    }

    public abstract boolean match(List<TokenValue<TToken, TValueGen>> phrase, Cursor cursor);

    public TValueGen parse(List<TokenValue<TToken, TValueGen>> phrase){
        TValueGen value = this.parse(phrase, new Cursor());
        if(value == null){
            throw new Error("Expression match not found");
        }
        return value;
    }

    public abstract TValueGen parse(List<TokenValue<TToken, TValueGen>> phrase, Cursor cursor);

    public abstract String toString();
}
