package de.melvil.stacksrs.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.melvil.stacksrs.model.Card;
import de.melvil.stacksrs.model.Deck;

public class DeckBrowserActivity extends AppCompatActivity {

    private Deck deck;

    private ListView cardList;
    private ArrayAdapter<Card> cardAdapter;
    private List<Card> cards = new ArrayList<>();

    private String searchTerm = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_browser);

        cardList = (ListView) findViewById(R.id.card_list);
        cardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cards);
        cardList.setAdapter(cardAdapter);

        String deckName = getIntent().getStringExtra("deck name");
        setTitle(deckName);
        try {
            deck = Deck.loadDeck(deckName);
        } catch(IOException e){
            Toast.makeText(getApplicationContext(), "Deck could not be loaded...",
                    Toast.LENGTH_SHORT).show();
            // TODO better error handling
        }

        displayCardList();
    }

    private void displayCardList(){
        cards.clear();
        cards.addAll(deck.searchCards(searchTerm));
        cardAdapter.notifyDataSetChanged();
    }
}
