package de.melvil.stacksrs.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

import de.melvil.stacksrs.model.Deck;

public class DeckBrowserActivity extends AppCompatActivity {

    private Deck deck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_browser);

        String deckName = getIntent().getStringExtra("deck name");
        setTitle(deckName);
        try {
            deck = Deck.loadDeck(deckName);
        } catch(IOException e){
            Toast.makeText(getApplicationContext(), "Deck could not be loaded...",
                    Toast.LENGTH_SHORT).show();
            // TODO better error handling
        }
    }
}
