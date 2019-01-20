package rjs.parser;

public class Cursor{
    private int position;

    public Cursor(){
        this(0);
    }

    public Cursor(int position){
        this.position = position;
    }

    public void set(int position){
        this.position = position;
    }

    public int get(){
        return this.position;
    }

    public void next(){
        this.position += 1;
    }
}
