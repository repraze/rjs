package rjs;

import rjs.parser.Block;
import rjs.lexer.Lexer;
import rjs.lexer.Token;
import rjs.parser.Parser;

import java.util.Arrays;
import java.util.Deque;

public class Runner {
    public static void evaluate(String code){
        System.out.println(code);

        Deque<Token> tokens = Lexer.lex(code);
        System.out.println(tokens);

        Block block = Parser.parse(tokens);
        System.out.println(block);

        System.out.println(block.evaluate());
    }
}
