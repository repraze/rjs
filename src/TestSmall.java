import rjs.parser.*;

import java.util.*;

// context

class Context{
    private Map<String, Integer> values;
    private Context parent;

    public Context(){
        this.values = new HashMap<>();
    }

    public Context(Context parent){
        this.values = new HashMap<>();
        this.parent = parent;
    }

    public void set(String name, int value){
        this.values.put(name, value);
    }

    public int get(String name){
        if(this.values.containsKey(name)){
            return this.values.get(name);
        }else if(this.parent != null){
            return this.parent.get(name);
        }
        throw new Error("Variable undefined");
    }
}

// value

interface SmallValue{
    int evaluate(Context context);
    String raw();
}

class SmallUndefinedValue implements SmallValue{
    public SmallUndefinedValue(){}

    public int evaluate(Context context){
        return -1;
    }

    public String raw(){
        return null;
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

    public int evaluate(Context context){
        return this.number;
    }

    public String raw(){
        return String.valueOf(number);
    }

    public String toString() {
        return "NUMBER " + this.number;
    }
}

class SmallVarValue implements SmallValue{
    private String name;

    public SmallVarValue(String name){
        this.name = name;
    }

    public int evaluate(Context context){
        return context.get(this.name);
    }

    public String raw(){
        return this.name;
    }

    public String toString() {
        return "VAR " + this.name;
    }
}

// tokens

enum SmallTokenType{
    VAR,
    NUMBER,
    EQ,
    ADD,
    REM,
    MUL,
    DIV,
    BO,
    BC,
    EOL
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
}

class SmallRule extends Rule<SmallTokenType, SmallValue> implements SmallRuleInterface{
    public SmallRule(String name){
        super(name);
    }
}

public class TestSmall{
    private static Grammar<SmallTokenType, SmallValue> getGrammar(){
        SmallTokenRule number = new SmallTokenRule(SmallTokenType.NUMBER);
        SmallTokenRule var = new SmallTokenRule(SmallTokenType.VAR);
        SmallTokenRule add = new SmallTokenRule(SmallTokenType.ADD);
        SmallTokenRule rem = new SmallTokenRule(SmallTokenType.REM);
        SmallTokenRule mul = new SmallTokenRule(SmallTokenType.MUL);
        SmallTokenRule div = new SmallTokenRule(SmallTokenType.DIV);
        SmallTokenRule eq = new SmallTokenRule(SmallTokenType.EQ);
        SmallTokenRule bo = new SmallTokenRule(SmallTokenType.BO);
        SmallTokenRule bc = new SmallTokenRule(SmallTokenType.BC);
        SmallTokenRule eol = new SmallTokenRule(SmallTokenType.EOL);

        SmallRule line = new SmallRule("line");
        SmallRule lhs = new SmallRule("lhs");
        SmallRule rhs = new SmallRule("rhs");
        SmallRule statement = new SmallRule("statement");
        SmallRule expression = new SmallRule("expression");
        SmallRule term = new SmallRule("term");
        SmallRule factor = new SmallRule("factor");

        factor.add(new SmallRuleInterface[]{bo, expression, bc}, (params)->{
            return params.get(1);
        });
        factor.add(new SmallRuleInterface[]{var});
        factor.add(new SmallRuleInterface[]{number});

        term.add(new SmallRuleInterface[]{factor, mul, term}, (params)->{
            return new SmallValue(){
                public int evaluate(Context context){return params.get(0).evaluate(context) * params.get(2).evaluate(context);}
                public String raw(){return null;}
            };
        });
        term.add(new SmallRuleInterface[]{factor, div, term}, (params)->{
            return new SmallValue(){
                public int evaluate(Context context){return params.get(0).evaluate(context) / params.get(2).evaluate(context);}
                public String raw(){return null;}
            };
        });
        term.add(new SmallRuleInterface[]{factor});

        expression.add(new SmallRuleInterface[]{term, add, expression}, (params)->{
            return new SmallValue(){
                public int evaluate(Context context){return params.get(0).evaluate(context) + params.get(2).evaluate(context);}
                public String raw(){return null;}
            };
        });
        expression.add(new SmallRuleInterface[]{term, rem, expression}, (params)->{
            return new SmallValue(){
                public int evaluate(Context context){return params.get(0).evaluate(context) - params.get(2).evaluate(context);}
                public String raw(){return null;}
            };
        });
        expression.add(new SmallRuleInterface[]{term});

        lhs.add(new SmallRuleInterface[]{var});

        rhs.add(new SmallRuleInterface[]{expression});

        statement.add(new SmallRuleInterface[]{lhs, eq, rhs}, (params)->{
            return new SmallValue(){
                public int evaluate(Context context){
                    String name = params.get(0).raw();
                    int value = params.get(2).evaluate(context);
                    context.set(name, value);
                    return value;
                }
                public String raw(){return null;}
            };
        });
        statement.add(new SmallRuleInterface[]{rhs});

        line.add(new SmallRuleInterface[]{statement, eol, line}, (params)->{
            return new SmallValue(){
                public int evaluate(Context context){
                    params.get(0).evaluate(context);
                    return params.get(2).evaluate(context);
                }
                public String raw(){return null;}
            };
        });
        line.add(new SmallRuleInterface[]{statement, eol});

        System.out.println(line);
        System.out.println(expression);
        System.out.println(term);
        System.out.println(factor);

        return new Grammar<>(line);
    }

    private static boolean isWhitespace(char c){
        return Character.isWhitespace(c);
    }

    private static boolean isControl(char c){
        return c == '=' || c == '!' || c == '<' || c == '>'
                || c == '+' || c == '-' || c == '/' || c == '*'
                || c == ';'
                || c == '(' || c == ')'
                || c == '{' || c == '}';
    }

    private static boolean isShortControl(char c){
        return c == ';'
                || c == '(' || c == ')'
                || c == '{' || c == '}';
    }

    private static boolean isString(char c){
        return c == '\'' || c == '"';
    }

    private static List<TokenValue<SmallTokenType, SmallValue>> dirtyLex(String input){
        List<TokenValue<SmallTokenType, SmallValue>> phrase = new ArrayList<>();

        StringBuffer buffer = new StringBuffer(0);

        char[] chrs = input.toCharArray();
        int len = input.length();
        for(int i = 0; i < len; ++i){
            char c = chrs[i];
            char n = i+1 < len ? chrs[i+1] : '\n';

            if(isWhitespace(c)){ // parse the buffer
                if(buffer.length() > 0){
                    phrase.add(getToken(buffer.toString()));
                    buffer.setLength(0);
                }
            }else if(isControl(c) != isControl(n) || isShortControl(c)){
                buffer.append(c);
                phrase.add(getToken(buffer.toString()));
                buffer.setLength(0);
            }else{ // add to buffer
                buffer.append(c);
            }
        }

        if(buffer.length() > 0){
            phrase.add(getToken(buffer.toString()));
            buffer.setLength(0);
        }

        return phrase;
    }

    private static TokenValue<SmallTokenType, SmallValue> getToken(String str){
        if(str.equals("(")){
            return new SmallTokenValue(SmallTokenType.BO);
        }
        if(str.equals(")")){
            return new SmallTokenValue(SmallTokenType.BC);
        }
        if(str.equals("=")){
            return new SmallTokenValue(SmallTokenType.EQ);
        }
        if(str.equals("+")){
            return new SmallTokenValue(SmallTokenType.ADD);
        }
        if(str.equals("-")){
            return new SmallTokenValue(SmallTokenType.REM);
        }
        if(str.equals("*")){
            return new SmallTokenValue(SmallTokenType.MUL);
        }
        if(str.equals("/")){
            return new SmallTokenValue(SmallTokenType.DIV);
        }
        if(str.equals(";")){
            return new SmallTokenValue(SmallTokenType.EOL);
        }
        if(Character.isDigit(str.charAt(0))){
            return new SmallTokenValue(SmallTokenType.NUMBER, new SmallNumberValue(Integer.parseInt(str)));
        }
        if(Character.isLetter(str.charAt(0))){
            return new SmallTokenValue(SmallTokenType.VAR, new SmallVarValue(str));
        }
        return null;
    }

    public static void main(String[] args){
        Grammar<SmallTokenType, SmallValue> grammar = getGrammar();

        SmallValue out = grammar.parse(dirtyLex("a = 1; b = 2; a + 3*b;"));
        System.out.println(out);
        if(out != null){
            Context context = new Context();
            System.out.println(out.evaluate(context));
        }
    }
}
