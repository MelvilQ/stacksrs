package de.melvil.stacksrs.model;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {

    private String name;
    private String languageFront;
    private String languageBack;
    private boolean useTTS;

    private List<Card> stack = new ArrayList<>();
    private static Random random = new Random(System.currentTimeMillis());

    @SuppressWarnings("unused")
    public Deck() {}

    public Deck(String name, String languageFront, String languageBack) {
        this.name = name;
        this.languageFront = languageFront;
        this.languageBack = languageBack;
        this.useTTS = false;
    }

    public static Deck loadDeck(String name) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(Environment.getExternalStorageDirectory()
                + "/StackSRS/" + name + ".json");
        return gson.fromJson(FileUtils.readFileToString(file, Charset.forName("UTF-8")), Deck.class);

    }

    public void saveDeck() {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/StackSRS/" + name + ".json");
            FileUtils.writeStringToFile(file, gson.toJson(this), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName(){
        return name;
    }

    public Card getNextCardToReview() {
        if(stack.isEmpty())
            return null;
        return stack.get(0);
    }

    public void putReviewedCardBack(boolean right) {
        if(stack.isEmpty())
            return;
        Card card = stack.remove(0);
        if (right) {
            card.increaseLevel();
            int newPos;
            if (card.getLevel() >= 10)
                newPos = Integer.MAX_VALUE;
            else
                newPos = 1 << (card.getLevel() * 2 + 2);
            newPos = newPos + random.nextInt((newPos / 3) + 1) - (newPos / 3);
            stack.add(Math.min(stack.size(), newPos), card);
        } else {
            card.decreaseLevel();
            stack.add(Math.min(stack.size(), 3), card);
        }
        saveDeck();
    }

    public void addNewCard(Card newCard) {
        stack.add(0, newCard);
        saveDeck();
    }

    public void editCurrentCard(String front, String back) {
        if(stack.isEmpty())
            return;
        stack.get(0).edit(front, back);
        saveDeck();
    }

    public boolean deleteCurrentCard() {
        if (stack.size() > 1) {
            stack.remove(0);
            saveDeck();
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteCard(Card card) {
        if (stack.size() > 1) {
            stack.remove(card);
            saveDeck();
            return true;
        } else {
            return false;
        }
    }

    public boolean isUsingTTS(){
        return useTTS;
    }

    public void setUsingTTS(boolean u){
        useTTS = u;
    }

    public void shuffleDeck() {
        Collections.shuffle(stack);
        saveDeck();
    }

    public void resetStrength(int level){
        for(Card c : stack){
            c.resetLevel(level);
        }
    }

    public void fillWithCards(List<Card> cards){
        stack = cards;
        saveDeck();
    }

    public List<Card> searchCards(String searchTerm){
        List<Card> result = new ArrayList<>();
        for(Card c : stack){
            if(c.contains(searchTerm))
                result.add(c);
            if(result.size() >= 100)    // limit result to 100
                return result;
        }
        return result;
    }

}
