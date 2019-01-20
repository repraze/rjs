package rjs.parser;

import javafx.util.Pair;

import java.util.List;

public class Grammar<TToken, TValueGen>{
    private Rule<TToken, TValueGen> baseRule;

    public Grammar(Rule baseRule){
        this.baseRule = baseRule;
    }

    public TValueGen parse(List<TokenValue<TToken, TValueGen>> phrase){
        int size = phrase.size();
        Cursor cursor = new Cursor();

        List<Pair<List<RuleInterface<TToken, TValueGen>>, Rule.RuleParser<TValueGen>>> definitions = this.baseRule.getDefinitions();

        TValueGen output = null;

        // find a matching expression
        for(Pair<List<RuleInterface<TToken, TValueGen>>, Rule.RuleParser<TValueGen>> definition : definitions){
            List<RuleInterface<TToken, TValueGen>> expression = definition.getKey();
            Rule.RuleParser<TValueGen> parser = definition.getValue();

            if(expression.size() <= size){
                // check one rule match
                boolean match = true;

                Params<TValueGen> params = new Params<>();
                for(RuleInterface<TToken, TValueGen> rule : expression){
                    TValueGen value = rule.parse(phrase, cursor);
                    if(value != null){
                        params.add(value);
                    }else{
                        match = false;
                        break;
                    }
                }

                if(match && cursor.get() == size){
                    output = parser.parse(params);
                    break;
                } else {
                    // not found, reset cursor
                    cursor.set(0);
                }
            }
        }

        if(output == null){
            throw new Error("Could not parse tokens");
        }
        return output;
    }
}
