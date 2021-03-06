package rjs.parser;

import java.util.List;

public abstract class TokenRule<TToken, TValueGen> extends BaseRule<TToken, TValueGen>{
    protected TToken token;

    public TokenRule(TToken token){
        this.token = token;
    }

    public TValueGen parse(List<TokenValue<TToken, TValueGen>> phrase, Cursor cursor){
        if(cursor.get() < phrase.size()){
            TokenValue<TToken, TValueGen> word = phrase.get(cursor.get());
            if(word.token() == this.token){
                cursor.next();
                return word.value();
            }
        }
        return null;
    }

    public String toString(){
        return this.token.toString();
    }
}
