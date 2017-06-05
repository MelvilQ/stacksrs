package de.melvil.stacksrs.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import de.melvil.stacksrs.model.Card;
import de.melvil.stacksrs.model.Deck;
import de.melvil.stacksrs.model.DeckCollection;

public class ReviewActivity extends AppCompatActivity {

    private TextView questionText;
    private TextView answerText;
    private Button wrongButton;
    private Button answerButton;
    private Button rightButton;

    private String deckName;
    private Deck deck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        questionText = (TextView) findViewById(R.id.text_front);
        answerText = (TextView) findViewById(R.id.text_back);
        wrongButton = (Button) findViewById(R.id.button_wrong);
        answerButton = (Button) findViewById(R.id.button_answer);
        rightButton = (Button) findViewById(R.id.button_correct);

        wrongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deck.putReviewedCardBack(false);
                showNextQuestion();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deck.putReviewedCardBack(true);
                showNextQuestion();
            }
        });
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer();
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
        showNextQuestion();
    }

    @Override
    protected void onPause(){
        super.onPause();
        deck.saveDeck();
    }

    private void showNextQuestion(){
        questionText.setText(deck.getNextCardToReview().getFront());
        answerText.setText("");
        wrongButton.setVisibility(View.GONE);
        rightButton.setVisibility(View.GONE);
        answerButton.setVisibility(View.VISIBLE);
    }

    private void showAnswer(){
        answerText.setText(deck.getNextCardToReview().getBack());
        wrongButton.setVisibility(View.VISIBLE);
        rightButton.setVisibility(View.VISIBLE);
        answerButton.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reviewactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            final Dialog dialog = new Dialog(ReviewActivity.this);
            dialog.setContentView(R.layout.card_dialog);
            dialog.setTitle("Add New Card");
            final EditText frontEdit = (EditText) dialog.findViewById(R.id.edit_front);
            final EditText backEdit = (EditText) dialog.findViewById(R.id.edit_back);
            Button cancelButton = (Button) dialog.findViewById(R.id.button_cancel);
            Button okButton = (Button) dialog.findViewById(R.id.button_ok);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String front = frontEdit.getText().toString().trim();
                    String back = backEdit.getText().toString().trim();
                    if (front.length() == 0)
                        Toast.makeText(getApplicationContext(),
                                "Front is empty.", Toast.LENGTH_SHORT).show();
                    else if(back.length() == 0)
                        Toast.makeText(getApplicationContext(),
                                "Back is empty.", Toast.LENGTH_SHORT).show();
                    else {
                        deck.addNewCard(new Card(front, back));
                        showNextQuestion();
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } else if(item.getItemId() == R.id.action_edit){
            final Dialog dialog = new Dialog(ReviewActivity.this);
            dialog.setContentView(R.layout.card_dialog);
            dialog.setTitle("Edit Current Card");
            final EditText frontEdit = (EditText) dialog.findViewById(R.id.edit_front);
            frontEdit.setText(deck.getNextCardToReview().getFront());
            final EditText backEdit = (EditText) dialog.findViewById(R.id.edit_back);
            backEdit.setText(deck.getNextCardToReview().getBack());
            Button cancelButton = (Button) dialog.findViewById(R.id.button_cancel);
            Button okButton = (Button) dialog.findViewById(R.id.button_ok);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String front = frontEdit.getText().toString().trim();
                    String back = backEdit.getText().toString().trim();
                    if (front.length() == 0)
                        Toast.makeText(getApplicationContext(),
                                "Front is empty.", Toast.LENGTH_SHORT).show();
                    else if(back.length() == 0)
                        Toast.makeText(getApplicationContext(),
                                "Back is empty.", Toast.LENGTH_SHORT).show();
                    else {
                        deck.editCurrentCard(front, back);
                        showNextQuestion();
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } else if(item.getItemId() == R.id.action_delete){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Card");
            builder.setMessage("Do you really want to delete the current card?");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    boolean successful = deck.deleteCurrentCard();
                    if(!successful)
                        Toast.makeText(getApplicationContext(),
                                "The last card can't be deleted!", Toast.LENGTH_SHORT).show();
                    showNextQuestion();
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else if(item.getItemId() == R.id.action_browser){
            Intent intent = new Intent(getApplicationContext(), DeckBrowserActivity.class);
            intent.putExtra("deck name", deck.getName());
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_options) {
            final Dialog dialog = new Dialog(ReviewActivity.this);
            dialog.setContentView(R.layout.deck_dialog);
            dialog.setTitle("Deck Options");
            final EditText editDeckName = (EditText) dialog.findViewById(R.id.edit_deck_name);
            editDeckName.setText(deckName);
            Button cancelButton = (Button) dialog.findViewById(R.id.button_cancel);
            Button okButton = (Button) dialog.findViewById(R.id.button_ok);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeckCollection deckCollection = new DeckCollection();
                    String newDeckName = editDeckName.getText().toString().trim();
                    if(deckName.equals(newDeckName)) {
                        // deck name has not been edited, so no action necessary...
                    } else if (deckCollection.isIllegalDeckName(newDeckName)) {
                        Toast.makeText(getApplicationContext(),
                                "Illegal deck name.", Toast.LENGTH_SHORT).show();
                    } else if(deckCollection.deckWithNameExists(newDeckName)){
                        Toast.makeText(getApplicationContext(),
                                "A deck \"" + deckName + "\" already exists!", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        deck.changeName(newDeckName);
                        ReviewActivity.this.setTitle(newDeckName);
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
