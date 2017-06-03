package de.melvil.stacksrs.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

        displayCardList("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_deckbrowseractivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_search){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Search Card");
            final EditText searchTermInput = new EditText(this);
            searchTermInput.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(searchTermInput);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    displayCardList(searchTermInput.getText().toString().trim());
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (item.getItemId() == R.id.action_add) {
            final Dialog dialog = new Dialog(DeckBrowserActivity.this);
            dialog.setContentView(R.layout.card_dialog);
            dialog.setTitle("Add New Card");
            final EditText questionEdit = (EditText) dialog.findViewById(R.id.questionEdit);
            final EditText answerEdit = (EditText) dialog.findViewById(R.id.answerEdit);
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
                    else if (a.length() == 0)
                        Toast.makeText(getApplicationContext(),
                                "Answer is empty.", Toast.LENGTH_SHORT).show();
                    else {
                        Card card = new Card(q, a);
                        deck.addNewCard(card);
                        displayCardList("");
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } else if(item.getItemId() == R.id.action_shuffle){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Shuffle Deck");
            builder.setMessage("Do you really want to shuffle the deck?");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    deck.shuffleDeck();
                    displayCardList("");
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else if(item.getItemId() == R.id.action_reset){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Reset Card Strength");
            builder.setMessage("Reset the strength of all cards?");
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setNeutralButton("Yes, Beginner", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    deck.resetStrength(0);
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Yes, Expert", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    deck.resetStrength(2);
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (item.getItemId() == R.id.action_options) {
            // TODO deck options
        }
        return true;
    }

    private void displayCardList(String searchTerm){
        cards.clear();
        cards.addAll(deck.searchCards(searchTerm));
        cardAdapter.notifyDataSetChanged();
    }
}
