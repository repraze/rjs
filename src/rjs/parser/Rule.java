package rjs.parser;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Rule<TToken, TValueGen> extends BaseRule<TToken, TValueGen>{
    private String name;
    private List<Pair<List<RuleInterface<TToken, TValueGen>>, RuleParser<TValueGen>>> definitions;

    public interface RuleParser<TValueGen>{
        public TValueGen parse(Params<TValueGen> params);
    }

    public Rule(String name){
        this.name = name;
        this.definitions = new ArrayList<>();
    }

    public String getName(){
        return this.name;
    }

    public List<Pair<List<RuleInterface<TToken, TValueGen>>, RuleParser<TValueGen>>> getDefinitions(){
        return this.definitions;
    }

    public Rule<TToken, TValueGen> add(RuleInterface<TToken, TValueGen>[] expression){
        return this.add(Arrays.asList(expression));
    }

    public Rule<TToken, TValueGen> add(List<RuleInterface<TToken, TValueGen>> expression) {
        return this.add(expression, (Params<TValueGen> params)->{
            return params.get(0);
        });
    }

    public Rule<TToken, TValueGen> add(RuleInterface<TToken, TValueGen>[] expression, RuleParser<TValueGen> parser){
        return this.add(Arrays.asList(expression), parser);
    }

    public Rule<TToken, TValueGen> add(List<RuleInterface<TToken, TValueGen>> expression, RuleParser<TValueGen> parser){
        // TODO cannot be a rule that loops on itself, cycle check
        this.definitions.add(new Pair<>(expression, parser));
        return this;
    }

    public TValueGen parse(List<TokenValue<TToken, TValueGen>> phrase, Cursor cursor){
        // we know one expression should match
        int reset = cursor.get();
        int end = phrase.size();
        int left = end - reset; // definition length need to be at least left token

        // find a matching expression
        for(Pair<List<RuleInterface<TToken, TValueGen>>, RuleParser<TValueGen>> definition : this.definitions){
            List<RuleInterface<TToken, TValueGen>> expression = definition.getKey();
            RuleParser<TValueGen> parser = definition.getValue();

            if(expression.size() <= left) {
                // check all rule match
                boolean match = true;

                Params<TValueGen> params = new Params<>();
                for (RuleInterface<TToken, TValueGen> rule : expression) {
                    TValueGen value = rule.parse(phrase, cursor);
                    if(value != null){
                        params.add(value);
                    }else{
                        match = false;
                        break;
                    }
                }

                if(match){
                    return parser.parse(params);
                } else {
                    // not found, reset cursor
                    cursor.set(reset);
                }
            }
        }

        // no expression found
        return null;
    }

    public String toString(){
        String out = this.name + " ::= ";
        int pad = out.length();

        for(int index = 0; index < this.definitions.size(); index++){
            List<RuleInterface<TToken, TValueGen>> expression = this.definitions.get(index).getKey();

            String line = "";
            for(RuleInterface<TToken, TValueGen> rule : expression){
                if(!(rule instanceof Rule)){
                    line += rule.toString();
                }else{
                    line += ((Rule) rule).getName();
                }
                line += " ";
            }

            out += line;
            if(index < this.definitions.size() - 1){
                out += System.lineSeparator() + padLeft(" | ", pad);
            }
        }

        return out;
    }

    public static String padLeft(String str, int n){
        return String.format("%1$" + n + "s", str);
    }
}
