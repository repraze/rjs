package rjs.lexer;

public class Token {
    public enum Type{
        // Reserved
        END, //;
        BO, //{
        BC, //}
        PO, //(
        PC, //)
        LET, //let
        CONST, //const
        EQ, //=
        NOT, //!
        EQEQ, //==
        MT, //>
        LT, //<
        ME, //>=
        LE, //<=
        NEQ, //!=

        // Control
        EOF, //end of file

        // Value
        IDENTIFIER, // foo
        STRING, // "foo"
        NUMBER, // 123
    }

    public final Type type;

    public Token(Type type){
        this.type = type;
    }

    public String toString(){
        return "<" + this.type + ">";
    }
}
