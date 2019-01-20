package rjs.parser;

import rjs.Evaluable;
import rjs.Value;

import java.util.ArrayList;
import java.util.List;

public class Block extends Statement {
    private List<Statement> statements;

    public Block(){
        this.statements = new ArrayList<>();
    }

    public void add(Statement statement){
        this.statements.add(statement);
    }

    public Value evaluate(){
        this.statements.forEach(s->{s.evaluate();});
        return null;
    }

    public String toString(){
        return "<Block>"+this.statements;
    }
}
