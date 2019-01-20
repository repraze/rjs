package rjs.lexer;

import java.util.ArrayDeque;
import java.util.Deque;

public class Lexer {
    private static boolean isWhitespace(char c){
        return Character.isWhitespace(c);
    }

    private static boolean isControl(char c){
        return c == '=' || c == '!' || c == '<' || c == '>'
                || c == '+' || c == '-'
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

    public static Deque<Token> lex(String code){
        Deque<Token> tokens = new ArrayDeque<>();

        StringBuffer buffer = new StringBuffer(0);

        char[] chrs = code.toCharArray();
        int len = code.length();
        for(int i = 0; i < len; ++i){
            char c = chrs[i];
            char n = i+1 < len ? chrs[i+1] : '\n';

            if(isWhitespace(c)){ // parse the buffer
                if(buffer.length() > 0){
                    tokens.add(getToken(buffer.toString()));
                    buffer.setLength(0);
                }
            }else if(isControl(c) != isControl(n) || isShortControl(c)){ // parse the buffer
                buffer.append(c);
                tokens.add(getToken(buffer.toString()));
                buffer.setLength(0);
            }else if(isString(c)){ // parse all next chars as strings
                char quote = c;
                boolean escaped = false;
                buffer.append('"');
                for(int j = i+1; j < len; ++j){
                    char s = chrs[j];
                    if(s == '\\' && !escaped){
                        escaped = true;
                    }else{
                        if(escaped){
                            escaped = false;
                            buffer.append(s);
                        }else{
                            if(s == quote){
                                break;
                            }
                            buffer.append(s);
                        }
                    }
                }
                i += buffer.length(); // skip string
                buffer.append('"');
                tokens.add(getToken(buffer.toString()));
                buffer.setLength(0);
            }else{ // add to buffer
                buffer.append(c);
            }
        }
        tokens.add(new SymbolToken(Token.Type.EOF));
        return tokens;
    }

    private static Token getToken(String str){
        // reserved
        if(str.equals(";")){
            return new SymbolToken(Token.Type.END);
        }
        if(str.equals("{")){
            return new SymbolToken(Token.Type.BO);
        }
        if(str.equals("}")){
            return new SymbolToken(Token.Type.BC);
        }
        if(str.equals("(")){
            return new SymbolToken(Token.Type.PO);
        }
        if(str.equals(")")){
            return new SymbolToken(Token.Type.PC);
        }
        if(str.equals("let")){
            return new SymbolToken(Token.Type.LET);
        }
        if(str.equals("const")){
            return new SymbolToken(Token.Type.CONST);
        }
        if(str.equals("=")){
            return new SymbolToken(Token.Type.EQ);
        }
        if(str.equals("!")){
            return new SymbolToken(Token.Type.NOT);
        }
        if(str.equals("==")){
            return new SymbolToken(Token.Type.EQEQ);
        }
        if(str.equals(">")){
            return new SymbolToken(Token.Type.MT);
        }
        if(str.equals("<")){
            return new SymbolToken(Token.Type.LT);
        }
        if(str.equals(">=")){
            return new SymbolToken(Token.Type.ME);
        }
        if(str.equals("<=")){
            return new SymbolToken(Token.Type.LE);
        }
        if(str.equals("!=")){
            return new SymbolToken(Token.Type.NEQ);
        }

        // others
        return getTypedToken(str);
    }

    private static Token getTypedToken(String str){
        if(str.equals("null")){
            return new Token(null);
        }
        if(str.charAt(0) == '"'){
            return new StringToken(str.substring(1, str.length()-1));
        }
        if(Character.isLetter(str.charAt(0))){
            return new IdentifierToken(str);
        }
        if(Character.isDigit(str.charAt(0))){
            return new NumberToken(Float.parseFloat(str));
        }
        return null;
    }
}
