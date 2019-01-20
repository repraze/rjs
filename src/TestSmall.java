import rjs.parser.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// value

interface SmallValue{
    int evaluate();
}

class SmallUndefinedValue implements SmallValue{
    public SmallUndefinedValue(){}

    public int evaluate(){
        return -1;
    }

    public String toString() {
        return "UNDEFINED";
    }
}

class SmallNumberValue implements SmallValue{
    private int number;

    public SmallNumberValue(int number){
        this.number = number;
    }

    public int evaluate(){
        return this.number;
    }

    public String toString() {
        return "NUMBER " + this.number;
    }
}

// tokens

enum SmallTokenType{
    NUMBER,
    ADD,
    MUL,
    BO,
    BC
}

class SmallTokenValue implements TokenValue<SmallTokenType, SmallValue>{
    private SmallTokenType type;
    private SmallValue value;

    public SmallTokenValue(SmallTokenType type){
        this(type, new SmallUndefinedValue());
    }

    public SmallTokenValue(SmallTokenType type, SmallValue value){
        this.type = type;
        this.value = value;
    }

    public SmallTokenType token(){
        return type;
    }

    public SmallValue value(){
        return value;
    }
}

// values

// rules using tokens

interface SmallRuleInterface extends RuleInterface<SmallTokenType, SmallValue>{};

class SmallTokenRule extends TokenRule<SmallTokenType, SmallValue> implements SmallRuleInterface{
    public SmallTokenRule(SmallTokenType smallTokenGen) {
        super(smallTokenGen);
    }

    public String toString(){
        switch(this.token){
            case ADD:
                return "+";
            case MUL:
                return "*";
            case BO:
                return "(";
            case BC:
                return ")";
            case NUMBER:
                return "NUMBER";
        }
        return "NONE";
    }
}

class SmallNumberRule extends SmallTokenRule{
    private int number;

    public SmallNumberRule(int number){
        super(SmallTokenType.NUMBER);
        this.number = number;
    }
}

class SmallRule extends Rule<SmallTokenType, SmallValue> implements SmallRuleInterface{
    public SmallRule(String name){
        super(name);
    }
}

public class TestSmall{
    public static void main(String[] args){
        //Runner.evaluate(code);

        // parser rules

        SmallTokenRule number = new SmallTokenRule(SmallTokenType.NUMBER);
        SmallTokenRule add = new SmallTokenRule(SmallTokenType.ADD);
        SmallTokenRule mul = new SmallTokenRule(SmallTokenType.MUL);
        SmallTokenRule bo = new SmallTokenRule(SmallTokenType.BO);
        SmallTokenRule bc = new SmallTokenRule(SmallTokenType.BC);

        SmallRule expression = new SmallRule("expression");
        SmallRule term = new SmallRule("term");
        SmallRule factor = new SmallRule("factor");

        factor.add(new SmallRuleInterface[]{bo, expression, bc}, (params)->{
            int a = params.get(1).evaluate();
            return new SmallNumberValue(a);
        });
        factor.add(new SmallRuleInterface[]{number});

        term.add(new SmallRuleInterface[]{factor, mul, term}, (params)->{
            int a = params.get(0).evaluate();
            int b = params.get(2).evaluate();
            return new SmallNumberValue(a * b);
        });
        term.add(new SmallRuleInterface[]{factor});

        expression.add(new SmallRuleInterface[]{term, add, expression}, (params)->{
            int a = params.get(0).evaluate();
            int b = params.get(2).evaluate();
            return new SmallNumberValue(a + b);
        });
        expression.add(new SmallRuleInterface[]{term});

        System.out.println(expression);
        System.out.println(term);
        System.out.println(factor);

        // sentence
        SmallTokenValue[] rawPhrase = new SmallTokenValue[]{
                new SmallTokenValue(SmallTokenType.BO),
                new SmallTokenValue(SmallTokenType.NUMBER, new SmallNumberValue(1)),
                new SmallTokenValue(SmallTokenType.ADD),
                new SmallTokenValue(SmallTokenType.NUMBER, new SmallNumberValue(2)),
                new SmallTokenValue(SmallTokenType.BC),
                new SmallTokenValue(SmallTokenType.MUL),
                new SmallTokenValue(SmallTokenType.BO),
                new SmallTokenValue(SmallTokenType.NUMBER, new SmallNumberValue(3)),
                new SmallTokenValue(SmallTokenType.ADD),
                new SmallTokenValue(SmallTokenType.NUMBER, new SmallNumberValue(4)),
                new SmallTokenValue(SmallTokenType.BC),
        };
        List<TokenValue<SmallTokenType, SmallValue>> phrase = new ArrayList<>(Arrays.asList(rawPhrase));

        // go

        Grammar<SmallTokenType, SmallValue> grammar = new Grammar<>(expression);

        SmallValue out = grammar.parse(phrase);
        System.out.println(out);
        if(out != null){
            System.out.println(out.evaluate());
        }
    }
}
