package rjs.parser;

import java.util.ArrayList;
import java.util.List;

public class Params<TValueGen>{
    private List<TValueGen> args;
    private boolean locked;
    public Params(){
        this.args = new ArrayList<>();
        this.locked = false;
    }

    public int size(){
        return this.args.size();
    }

    public TValueGen get(int index){
        if(index >= this.args.size()){
            return null;
        }
        return this.args.get(index);
    }

    public void add(TValueGen value){
        this.args.add(value);
    }

    public String toString(){
        return args.toString();
    }
}
