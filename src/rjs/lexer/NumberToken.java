package rjs.lexer;

public class NumberToken extends Token{
    private float number;

    public NumberToken(float number){
        super(Type.NUMBER);
        this.number = number;
    }

    private String strValue(){
        if(this.number == (int) this.number){
            return String.format("%s", (int) this.number);
        }else{
            return String.format("%f", this.number);
        }
    }

    public String toString(){
        return "<"+this.type+"> "+this.strValue();
    }
}