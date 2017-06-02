package de.melvil.stacksrs.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.melvil.stacksrs.model.Card;
import de.melvil.stacksrs.model.Deck;

public class DeckBrowserActivity extends AppCompatActivity {

    private String deckName;
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

        // normal click: edit
        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Card card = cards.get(position);
                final Dialog dialog = new Dialog(DeckBrowserActivity.this);
                dialog.setContentView(R.layout.card_dialog);
                dialog.setTitle("Edit Card");
                final EditText questionEdit = (EditText) dialog.findViewById(R.id.questionEdit);
                questionEdit.setText(card.getFront());
                final EditText answerEdit = (EditText) dialog.findViewById(R.id.answerEdit);
                answerEdit.setText(card.getBack());
                Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
                Button okButton = (Button) dialog.findViewById(R.id.okButton);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String q = questionEdit.getText().toString().trim();
                        String a = answerEdit.getText().toString().trim();
                        if (q.length() == 0)
                            Toast.makeText(getApplicationContext(),
                                    "Question is empty.", Toast.LENGTH_SHORT).show();
                        else if(a.length() == 0)
                            Toast.makeText(getApplicationContext(),
                                    "Answer is empty.", Toast.LENGTH_SHORT).show();
                        else {
                            card.edit(q, a);
                            deck.saveDeck();
                            cardAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        // long click: delete
        cardList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Card card = cardAdapter.getItem(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(DeckBrowserActivity.this);
                dialog.setTitle("Delete Card");
                dialog.setMessage("Do you really want to delete this card?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(deck.deleteCard(card)) {
                            cards.remove(position);
                            cardAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "The last card can't be deleted!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.create().show();
                return true;
            }
        });

        deckName = getIntent().getStringExtra("deck name");
        setTitle(deckName);
    }

    @Override
    protected void onResume(){
        super.onResume();
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
