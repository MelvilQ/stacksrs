package de.melvil.stacksrs.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

import de.melvil.stacksrs.model.Card;
import de.melvil.stacksrs.model.Deck;
import de.melvil.stacksrs.model.DeckCollection;

public class ReviewActivity extends AppCompatActivity {

    private TextView frontText;
    private TextView backText;
    private Button wrongButton;
    private Button answerButton;
    private Button correctButton;

    private String deckName;
    private Deck deck;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        frontText = (TextView) findViewById(R.id.text_front);
        backText = (TextView) findViewById(R.id.text_back);
        wrongButton = (Button) findViewById(R.id.button_wrong);
        answerButton = (Button) findViewById(R.id.button_answer);
        correctButton = (Button) findViewById(R.id.button_correct);

        wrongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deck.putReviewedCardBack(false);
                showNextCard();
            }
        });
        correctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deck.putReviewedCardBack(true);
                showNextCard();
            }
        });
        answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBack();
            }
        });

        deckName = getIntent().getStringExtra("deck name");
    }

    @Override
    protected void onResume(){
        super.onResume();
        reloadDeck();
    }

    private void reloadDeck(){
        setTitle(deckName);
        try {
            deck = Deck.loadDeck(deckName);
            if(deck.isUsingTTS())
                initTTS();
            showNextCard();
        } catch(IOException e){
            Toast.makeText(getApplicationContext(), getString(R.string.deck_could_not_be_loaded),
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initTTS(){
        final Locale locale = getLocaleForTTS();
        if(locale != null){
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener(){
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        tts.setLanguage(locale);
                    }
                }
            });
        }
    }

    private Locale getLocaleForTTS(){
        String lang = deck.getLanguage();
        if(lang == null || lang.equals(""))
            return null;
        String country = deck.getAccent();
        if(country == null || country.equals(""))
            return new Locale(lang);
        return new Locale(lang, country);
    }

    @Override
    protected void onPause(){
        super.onPause();
        deck.saveDeck();
        if(tts != null){
            tts.shutdown();
            tts = null;
        }
    }

    private void showNextCard(){
        frontText.setText(deck.getNextCardToReview().getFront());
        backText.setText("");
        wrongButton.setVisibility(View.GONE);
        correctButton.setVisibility(View.GONE);
        answerButton.setVisibility(View.VISIBLE);
    }

    private void showBack(){
        String back = deck.getNextCardToReview().getBack();
        backText.setText(deck.getNextCardToReview().getBack());
        wrongButton.setVisibility(View.VISIBLE);
        correctButton.setVisibility(View.VISIBLE);
        answerButton.setVisibility(View.GONE);

        if(deck.isUsingTTS())
            speakWord(back);
    }

    private void speakWord(String text){
        if(tts == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
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
            dialog.setTitle(getString(R.string.add_new_card));
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
                                getString(R.string.front_is_empty), Toast.LENGTH_SHORT).show();
                    else if(back.length() == 0)
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.back_is_empty), Toast.LENGTH_SHORT).show();
                    else {
                        deck.addNewCard(new Card(front, back));
                        showNextCard();
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } else if(item.getItemId() == R.id.action_edit){
            final Dialog dialog = new Dialog(ReviewActivity.this);
            dialog.setContentView(R.layout.card_dialog);
            dialog.setTitle(getString(R.string.edit_current_card));
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
                                getString(R.string.front_is_empty), Toast.LENGTH_SHORT).show();
                    else if(back.length() == 0)
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.back_is_empty), Toast.LENGTH_SHORT).show();
                    else {
                        deck.editCurrentCard(front, back);
                        showNextCard();
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } else if(item.getItemId() == R.id.action_delete){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.delete_current_card));
            builder.setMessage(getString(R.string.really_delete_card));
            builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    boolean successful = deck.deleteCurrentCard();
                    if(!successful)
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.cannot_delete_last_card), Toast.LENGTH_SHORT)
                                .show();
                    showNextCard();
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
            dialog.setTitle(getString(R.string.deck_options));
            final EditText editDeckName = (EditText) dialog.findViewById(R.id.edit_deck_name);
            editDeckName.setText(deckName);
            final EditText editLanguage = (EditText) dialog.findViewById(R.id.edit_language);
            editLanguage.setText(deck.getLanguage());
            final EditText editAccent = (EditText) dialog.findViewById(R.id.edit_accent);
            editAccent.setText(deck.getAccent());
            final CheckBox checkBoxTTS = (CheckBox) dialog.findViewById(R.id.checkbox_tts);
            checkBoxTTS.setChecked(deck.isUsingTTS());
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
                    try {
                        deckCollection.reload(DeckCollection.stackSRSDir);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    String newDeckName = editDeckName.getText().toString().trim();
                    boolean deckNameChanged = !newDeckName.equals(deckName);
                    if (deckCollection.isIllegalDeckName(newDeckName)) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.illegal_deck_name), Toast.LENGTH_SHORT).show();
                    } else if(deckNameChanged && deckCollection.deckWithNameExists(newDeckName)){
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.deck_already_exists, newDeckName), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        if(deckNameChanged) {
                            deck.changeName(newDeckName);
                            deckName = newDeckName;
                        }
                        deck.setLanguage(editLanguage.getText().toString().trim().toLowerCase());
                        deck.setAccent(editAccent.getText().toString().trim().toUpperCase());
                        if(checkBoxTTS.isChecked())
                            deck.activateTTS();
                        else
                            deck.deactivateTTS();
                        deck.saveDeck();

                        reloadDeck();
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
