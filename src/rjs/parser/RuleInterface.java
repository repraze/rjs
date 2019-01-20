package rjs.parser;

import java.util.List;

public interface RuleInterface<TToken, TValueGen>{
    boolean match(List<TokenValue<TToken, TValueGen>> phrase);
    boolean match(List<TokenValue<TToken, TValueGen>> phrase, Cursor cursor);

    TValueGen parse(List<TokenValue<TToken, TValueGen>> phrase);
    TValueGen parse(List<TokenValue<TToken, TValueGen>> phrase, Cursor cursor);

    String toString();
}
