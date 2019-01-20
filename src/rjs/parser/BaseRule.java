package rjs.parser;

import java.util.List;

public abstract class BaseRule<TToken, TValueGen> implements RuleInterface<TToken, TValueGen>{
    public abstract TValueGen parse(List<TokenValue<TToken, TValueGen>> phrase, Cursor cursor);

    public abstract String toString();
}
