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

import de.melvil.stacksrs.model.Card;
import de.melvil.stacksrs.model.Deck;

public class ReviewActivity extends AppCompatActivity {

    private TextView questionText;
    private TextView answerText;
    private Button wrongButton;
    private Button answerButton;
    private Button rightButton;

    private Deck stack;

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
                stack.putReviewedCardBack(false);
                showNextQuestion();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stack.putReviewedCardBack(true);
                showNextQuestion();
            }
        });
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer();
            }
        });

        stack = Deck.loadDeck("default");
        showNextQuestion();
    }

    private void showNextQuestion(){
        questionText.setText(stack.getNextCardToReview().getFront());
        answerText.setText("");
        wrongButton.setVisibility(View.GONE);
        rightButton.setVisibility(View.GONE);
        answerButton.setVisibility(View.VISIBLE);
    }

    private void showAnswer(){
        answerText.setText(stack.getNextCardToReview().getBack());
        wrongButton.setVisibility(View.VISIBLE);
        rightButton.setVisibility(View.VISIBLE);
        answerButton.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Add new card");
        menu.add("Edit current card");
        menu.add("Delete card");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals("Add new card")){
            final Dialog dialog = new Dialog(ReviewActivity.this);
            dialog.setContentView(R.layout.card_dialog);
            dialog.setTitle("Add new card");
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
                        stack.addNewCard(new Card(q, a));
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } else if(item.getTitle().equals("Edit current card")){
            final Dialog dialog = new Dialog(ReviewActivity.this);
            dialog.setContentView(R.layout.card_dialog);
            dialog.setTitle("Edit current card");
            final EditText questionEdit = (EditText) dialog.findViewById(R.id.questionEdit);
            questionEdit.setText(stack.getNextCardToReview().getFront());
            final EditText answerEdit = (EditText) dialog.findViewById(R.id.answerEdit);
            answerEdit.setText(stack.getNextCardToReview().getBack());
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
                        stack.editCurrentCard(q, a);
                        showNextQuestion();
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } else if(item.getTitle().equals("Delete card")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete");
            builder.setMessage("Do you really want to delete the current card?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    boolean successful = stack.deleteCurrentCard();
                    if(!successful)
                        Toast.makeText(getApplicationContext(),
                                "Last card can't be deleted!", Toast.LENGTH_SHORT);
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
