package de.melvil.stacksrs.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import de.melvil.stacksrs.adapter.DeckInfoAdapter;
import de.melvil.stacksrs.model.Card;
import de.melvil.stacksrs.model.Deck;
import de.melvil.stacksrs.model.DeckCollection;

public class DeckListActivity extends AppCompatActivity {

    private ListView deckListView;
    private DeckInfoAdapter deckListAdapter;
    private DeckCollection deckCollection = new DeckCollection();

    private Button newButton;
    private Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_list);

        deckListView = (ListView) findViewById(R.id.deck_list);
        deckListAdapter = new DeckInfoAdapter(this, deckCollection.getDeckInfos());
        deckListView.setAdapter(deckListAdapter);

        // normal click: open deck
        deckListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // switch to download activity
                final String deckName = deckListAdapter.getItem(position).getName();
                Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
                intent.putExtra("deck name", deckName);
                startActivity(intent);
            }
        });

        // long click: delete deck
        deckListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String deckName = deckListAdapter.getItem(position).getName();
                AlertDialog.Builder dialog = new AlertDialog.Builder(DeckListActivity.this);
                dialog.setTitle(getString(R.string.delete_deck));
                dialog.setMessage(getString(R.string.really_delete_deck, deckName));
                dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deckCollection.deleteDeckFile(deckName);
                        reloadDeckList();
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.create().show();
                return true;
            }
        });

        newButton = (Button) findViewById(R.id.button_new);
        downloadButton = (Button) findViewById(R.id.button_download);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewDeckDialog();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // switch to download activity
                Intent intent = new Intent(getApplicationContext(), DeckDownloadActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadDeckList();
    }

    public void reloadDeckList() {
        try {
            File stackSRSDir = provideStackSRSDir();
            deckCollection.reload(stackSRSDir);
        } catch(IOException e){
            Toast.makeText(this, getString(R.string.collection_could_not_be_loaded),
                    Toast.LENGTH_SHORT).show();
        }
        deckListAdapter.notifyDataSetChanged();
    }

    private File provideStackSRSDir(){
        // if there is (possibly emulated) external storage available, we use it
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return getApplicationContext().getExternalFilesDir(null);
        } else { // otherwise we use an internal directory without access from the outside
            return getApplicationContext().getDir("StackSRS", MODE_PRIVATE);
        }
    }

    public void showNewDeckDialog(){
        final Dialog dialog = new Dialog(DeckListActivity.this);
        dialog.setContentView(R.layout.deck_dialog);
        dialog.setTitle(getString(R.string.new_deck));
        final EditText editDeckName = (EditText) dialog.findViewById(R.id.edit_deck_name);
        final EditText editLanguage = (EditText) dialog.findViewById(R.id.edit_language);
        final EditText editAccent = (EditText) dialog.findViewById(R.id.edit_accent);
        final CheckBox checkBoxTTS = (CheckBox) dialog.findViewById(R.id.checkbox_tts);
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
                String deckName = editDeckName.getText().toString().trim();
                if (deckCollection.isIllegalDeckName(deckName)) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.illegal_deck_name), Toast.LENGTH_SHORT).show();
                } else if(deckCollection.deckWithNameExists(deckName)){
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.deck_already_exists, deckName), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Deck newDeck = new Deck(deckName, "");
                    newDeck.addNewCard(new Card("default", "default", 10));
                    newDeck.setLanguage(editLanguage.getText().toString().trim().toLowerCase());
                    newDeck.setAccent(editAccent.getText().toString().trim().toUpperCase());
                    if(checkBoxTTS.isChecked())
                        newDeck.activateTTS();
                    newDeck.saveDeck();
                    reloadDeckList();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
}
