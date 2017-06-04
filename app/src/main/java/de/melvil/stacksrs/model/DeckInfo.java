package de.melvil.stacksrs.model;

import java.util.Properties;

public class DeckInfo {
    private String name;
    private String numCards;
    private String numHotCards;
    private String numKnownCards;

    public DeckInfo(String name, Properties stats){
        this.name = name;
        this.numCards = stats.getProperty(name + ".num_cards", "?");
        this.numHotCards = stats.getProperty(name + ".num_hot_cards", "?");
        this.numKnownCards = stats.getProperty(name + ".num_known_cards", "?");
    }

    public String getName() {
        return name;
    }

    public String getNumCards() {
        return numCards;
    }

    public String getNumHotCards() {
        return numHotCards;
    }

    public String getNumKnownCards() {
        return numKnownCards;
    }

}
