package rjs.lexer;

public class StringToken extends Token {
    private String string;

    public StringToken(String string){
        super(Type.STRING);
        this.string = string;
    }

    public String toString(){
        return "<"+this.type+"> \""+this.string+"\"";
    }
}
