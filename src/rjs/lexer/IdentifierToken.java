package rjs.lexer;

public class IdentifierToken extends Token{
    private String name;

    public IdentifierToken(String name){
        super(Type.IDENTIFIER);
        this.name = name;
    }

    public String toString(){
        return "<"+this.type+"> \""+this.name+"\"";
    }
}