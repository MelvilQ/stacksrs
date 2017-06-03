package de.melvil.stacksrs.model;

public class Card {

    private String front;
    private String back;
    private int level;

    @SuppressWarnings("unused")
    public Card(){}

    public Card(String front, String back){
        this(front, back, 0);
    }

    public Card(String front, String back, int level){
        this.front = front;
        this.back = back;
        this.level = level;
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

    public void resetLevel(int level){
        this.level = level;
    }

    public void increaseLevel(){
        this.level += 1;
    }

    public void decreaseLevel(){
        this.level = Math.max(0, level - 2);
    }

    public String toString(){
        String f = front.replace("\n", " ");
        if(f.length() >= 40)
            f = f.substring(0, Math.min(37, f.length())) + "...";
        String b = back.replace("\n", " ");
        if(b.length() >= 40)
            b = b.substring(0, Math.min(37, b.length())) + "...";
        return f + "\n" + b;
    }

    public boolean contains(String searchTerm){
        return (front.contains(searchTerm) || back.contains(searchTerm));
    }
}
