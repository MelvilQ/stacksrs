package de.melvil.stacksrs.model;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class Deck {

    private String name;
    private String language;
    private String accent; // to determine which accent to use for TTS
    private boolean useTTS;

    private List<Card> stack = new ArrayList<>();

    private static Random random = new Random(System.currentTimeMillis());
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @SuppressWarnings("unused") // this constructor is necessary because of GSON
    public Deck() {
    }

    public Deck(String name, String language) {
        this.name = name;
        this.language = language;
        this.accent = "";
        this.useTTS = false;
    }

    public static Deck loadDeck(String name) throws IOException {
        // load deck from file using GSON
        File file = new File(DeckCollection.stackSRSDir + "/" + name + ".json");
        return gson.fromJson(FileUtils.readFileToString(file, Charset.forName("UTF-8")), Deck.class);
    }

    public File getDeckFile(){
        return new File(DeckCollection.stackSRSDir + "/" + name + ".json");
    }

    public void saveDeck() {
        try {   // save deck to file using GSON
            FileUtils.writeStringToFile(getDeckFile(), gson.toJson(this), Charset.forName("UTF-8"));
            saveStatistics();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDeckContentAsCsvString(){
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n"); // first line: deck title
        sb.append("\t").append(language).append("\n"); // second line: column headers
        for(Card c : stack){ // one csv line for each card
            sb.append(c.getFront()).append("\t").append(c.getBack()).append("\n");
        }
        return sb.toString();
    }

    private void saveStatistics() {
        try {   // adding the statistics of this deck to the global stats file
            Properties stats = new Properties();
            File statsFile = new File(DeckCollection.stackSRSDir + "/stats");
            if (!statsFile.exists()) // create stats file if it does not exist
                statsFile.createNewFile();
            stats.load(new FileReader(statsFile));

            int numCards = stack.size();
            stats.setProperty(name + ".num_cards", "" + numCards);
            int numKnownCards = getNumberOfCardsWithMinLevel(3);
            stats.setProperty(name + ".num_known_cards", "" + numKnownCards);
            int numHotCards = numCards - numKnownCards;
            stats.setProperty(name + ".num_hot_cards", "" + numHotCards);

            stats.store(new FileWriter(statsFile), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getNumberOfCardsWithMinLevel(int level) {
        int numCards = 0;
        for (Card c : stack) {
            if (c.getLevel() >= level)
                numCards++;
        }
        return numCards;
    }

    public String getName() {
        return name;
    }

    public void changeName(String newDeckName){
        // delete old file
        getDeckFile().delete();
        // create new file
        name = newDeckName;
        saveDeck();
    }

    public Card getNextCardToReview() {
        if (stack.isEmpty())
            return null;
        return stack.get(0);
    }

    public void putReviewedCardBack(boolean correct) {
        if (stack.isEmpty())
            return;
        Card card = stack.remove(0);
        if (correct) {
            card.increaseLevel();
            int newPos;
            if (card.getLevel() >= 10) {
                newPos = Integer.MAX_VALUE;
            } else {
                // this is the magic SRS formula:
                // level 1 => 8 cards later
                // level 2 => 32 cards later
                // level 3 => 128 cards later
                // and so on...
                newPos = 1 << (card.getLevel() * 2 + 2);
            }
            // adding some randomness so that it doesn't get too predictable
            newPos = newPos + random.nextInt((newPos / 3) + 1) - (newPos / 3);
            stack.add(Math.min(stack.size(), newPos), card);
        } else {
            // if the answer was wrong, the card always comes up again after three other cards
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
        if (stack.isEmpty())
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

    public boolean isUsingTTS() {
        return useTTS;
    }

    public void activateTTS() {
        useTTS = true;
    }

    public void deactivateTTS() {
        useTTS = false;
    }

    public String getLanguage(){
        if(language != null)
            return language.toLowerCase();  // language codes are always lower case
        else
            return "";
    }

    public void setLanguage(String lang){
        this.language = lang;
    }

    public String getAccent(){
        if(accent != null)
            return accent.toUpperCase();    // country codes are always upper case
        else
            return "";
    }

    public void setAccent(String country){
        accent = country;
    }

    public void shuffleDeck() {
        Collections.shuffle(stack);
        saveDeck();
    }

    public void reverseDeck() {
        Collections.reverse(stack);
        saveDeck();
    }

    public void resetStrength(int level) {
        for (Card c : stack) {  // setting all cards to the given level
            c.resetLevel(level);
        }
        saveDeck();
    }

    public void fillWithCards(List<Card> cards) {
        stack = cards;
        saveDeck();
    }

    public List<Card> searchCards(String searchTerm, int maxResults) {
        List<Card> result = new ArrayList<>();
        for (Card c : stack) {
            if (c.contains(searchTerm))
                result.add(c);
            if (result.size() >= maxResults)    // limit result set
                return result;
        }
        return result;
    }

    public boolean isNew(){
        // we consider a deck as new if all cards are level 2
        int level = stack.get(0).getLevel();
        if(level != 0 && level != 2)
            return false;
        for(Card c: stack){
            if(c.getLevel() != level)
                return false;
        }
        return true;
    }

}
