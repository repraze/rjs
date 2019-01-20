package rjs.parser;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Cursor{
    private int position;

    public Cursor(){
        this(0);
    }

    public Cursor(int position){
        this.position = position;
    }

    public void set(int position){
        this.position = position;
    }

    public int get(){
        return this.position;
    }

    public void next(){
        this.position += 1;
    }
}

public class Rule<TToken, TValueGen> extends BaseRule<TToken, TValueGen>{
    private String name;
    private List<Pair<List<RuleInterface<TToken, TValueGen>>, RuleParser<TValueGen>>> definitions;

    public static class Params<TValueGen>{
        private List<TValueGen> args;
        private boolean locked;
        public Params(){
            this.args = new ArrayList<>();
            this.locked = false;
        }

        public int size(){
            return this.args.size();
        }

        public TValueGen get(int index){
            if(index >= this.args.size()){
                return null;
            }
            return this.args.get(index);
        }

        public void add(TValueGen value){
            this.args.add(value);
        }

        public String toString(){
            return args.toString();
        }
    }

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

    public Rule<TToken, TValueGen> add(RuleInterface<TToken, TValueGen>[] expression){
        return this.add(Arrays.asList(expression));
    }

    public Rule<TToken, TValueGen> add(List<RuleInterface<TToken, TValueGen>> expression) {
        return this.add(expression, (Params<TValueGen> params)->{
            System.out.println(params);
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

    public boolean match(List<TokenValue<TToken, TValueGen>> phrase, Cursor cursor){
        // TODO transform to tree lookup ?
        int reset = cursor.get();
        int end = phrase.size();
        int left = end - reset; // definition length need to be at least left token

        // find a matching expression
        for(Pair<List<RuleInterface<TToken, TValueGen>>, RuleParser<TValueGen>> definition : this.definitions){
            List<RuleInterface<TToken, TValueGen>> expression = definition.getKey();

            if(expression.size() <= left){
                // check all rule match
                boolean match = true;

                for(RuleInterface<TToken, TValueGen> rule : expression){
                    System.out.println(rule);
                    if(!rule.match(phrase, cursor)){
                        match = false;
                        break;
                    }
                }

                if(match){
                    System.out.println(this.name + expression);
                    // found! already on next
                    return true;
                }else{
                    // not found, reset cursor
                    cursor.set(reset);
                }
            }
        }

        // no expression found
        return false;
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
                    int pReset = cursor.get();
                    if (!rule.match(phrase, cursor)) {
                        match = false;
                        break;
                    }else{
                        cursor.set(pReset);
                        params.add(rule.parse(phrase, cursor));
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
