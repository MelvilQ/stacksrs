package de.melvil.stacksrs;

public class Card {

    private String front;
    private String back;
    private int level;

    public Card(String front, String back){
        this.front = front;
        this.back = back;
        level = 0;
    }

    public String getFront(){
        return front;
    }

    public String getBack(){
        return back;
    }

    public void edit(String front, String back){
        this.front = front;
        this.back = back;
    }

    public int getLevel(){
        return level;
    }

    public void setLevel(int level){
        this.level = level;
    }

    public double getScore(){
        return level * 0.1;
    }
}
