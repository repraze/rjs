package rjs.parser;

import rjs.lexer.Token;
import rjs.lexer.Token.Type;

import java.util.Deque;

public class Parser {
    public static Block parse(Deque<Token> tokens){
        return parse(tokens, Type.EOF);
    }

    private static Block parse(Deque<Token> tokens, Type end){
        Block block = new Block();
        while(true){
            Token t = tokens.poll();
            if(t == null || t.type == end){
                break;
            }
            Type tt = t.type;

            // Blocks {}
            if(tt == Token.Type.BO){
                block.add(Parser.parse(tokens, Type.BC));
            }

            // Assignment
            if(tt == Type.IDENTIFIER){
                Token tn = tokens.peek();
                if(tn != null){

                }
            }
        }

        return block;
    }
}
