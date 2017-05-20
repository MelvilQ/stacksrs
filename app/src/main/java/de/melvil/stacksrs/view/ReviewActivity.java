package de.melvil.stacksrs.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

public class ReviewActivity extends AppCompatActivity {

    private TextView questionText;
    private TextView answerText;
    private Button wrongButton;
    private Button answerButton;
    private Button rightButton;

    private Deck deck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        questionText = (TextView) findViewById(R.id.questionText);
        answerText = (TextView) findViewById(R.id.answerText);
        wrongButton = (Button) findViewById(R.id.wrongButton);
        answerButton = (Button) findViewById(R.id.answerButton);
        rightButton = (Button) findViewById(R.id.rightButton);

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

        String deckName = getIntent().getStringExtra("deck name");
        try {
            deck = Deck.loadDeck(deckName);
        } catch(IOException e){
            Toast.makeText(getApplicationContext(), "Deck could not be loaded...",
                    Toast.LENGTH_SHORT).show();
            // TODO better error handling
        }
        showNextQuestion();
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
        menu.add("Add New Card");
        menu.add("Edit Current Card");
        menu.add("Delete Card");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals("Add New Card")){
            final Dialog dialog = new Dialog(ReviewActivity.this);
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
                    else if(a.length() == 0)
                        Toast.makeText(getApplicationContext(),
                                "Answer is empty.", Toast.LENGTH_SHORT).show();
                    else {
                        deck.addNewCard(new Card(q, a));
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } else if(item.getTitle().equals("Edit Current Card")){
            final Dialog dialog = new Dialog(ReviewActivity.this);
            dialog.setContentView(R.layout.card_dialog);
            dialog.setTitle("Edit Current Card");
            final EditText questionEdit = (EditText) dialog.findViewById(R.id.questionEdit);
            questionEdit.setText(deck.getNextCardToReview().getFront());
            final EditText answerEdit = (EditText) dialog.findViewById(R.id.answerEdit);
            answerEdit.setText(deck.getNextCardToReview().getBack());
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
                        deck.editCurrentCard(q, a);
                        showNextQuestion();
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } else if(item.getTitle().equals("Delete Card")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Card");
            builder.setMessage("Do you really want to delete the current card?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    boolean successful = deck.deleteCurrentCard();
                    if(!successful)
                        Toast.makeText(getApplicationContext(),
                                "The last card can't be deleted!", Toast.LENGTH_SHORT);
                    showNextQuestion();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
