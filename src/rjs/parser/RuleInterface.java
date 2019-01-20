package rjs.parser;

import java.util.List;

public interface RuleInterface<TToken, TValueGen>{
    TValueGen parse(List<TokenValue<TToken, TValueGen>> phrase, Cursor cursor);

    String toString();
}
