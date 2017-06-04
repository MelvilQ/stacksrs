package de.melvil.stacksrs.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import de.melvil.stacksrs.adapter.DeckInfoAdapter;
import de.melvil.stacksrs.model.Card;
import de.melvil.stacksrs.model.Deck;
import de.melvil.stacksrs.model.DeckInfo;

public class DeckListActivity extends AppCompatActivity {

    private ListView deckListView;
    private DeckInfoAdapter deckListAdapter;
    private final List<DeckInfo> deckInfoList = new ArrayList<>();

    private Button newButton;
    private Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_list);

        deckListView = (ListView) findViewById(R.id.deck_list);
        deckListAdapter = new DeckInfoAdapter(this, deckInfoList);
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
                dialog.setTitle("Delete Deck");
                dialog.setMessage("Do you really want to delete \"" + deckName + "\"?");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // delete deck
                        File deckFile = new File(Environment.getExternalStorageDirectory()
                                + "/StackSRS/" + deckName + ".json");
                        if(deckFile.exists())
                            deckFile.delete();
                        reloadDeckList();
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
        deckInfoList.clear();
        File stackSRSDir = new File(Environment.getExternalStorageDirectory() + "/StackSRS");
        stackSRSDir.mkdir();    // create dir if not exists
        File[] deckFiles = stackSRSDir.listFiles();
        if(deckFiles == null){
            Toast.makeText(this, "Unable to load deck collection.", Toast.LENGTH_SHORT).show();
            deckListAdapter.notifyDataSetChanged();
            return;
        }
        // sort deck files by last edit
        Arrays.sort(deckFiles, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
            }
        });
        // load statistics
        Properties stats = new Properties();
        try {
            File statsFile = new File(Environment.getExternalStorageDirectory() + "/StackSRS/stats");
            if (!statsFile.exists()) // create stats file if it does not exist
                statsFile.createNewFile();
            stats.load(new FileReader(statsFile));
        } catch(IOException e){
            e.printStackTrace();
        }
        // add decks to list
        for(File f : deckFiles){
            if(f.getName().endsWith(".json")) {
                String deckName = f.getName().replace(".json", "");
                DeckInfo deckInfo = new DeckInfo(deckName, stats);
                deckInfoList.add(deckInfo);
            }
        }
        deckListAdapter.notifyDataSetChanged();
    }

    public void showNewDeckDialog(){
        final Dialog dialog = new Dialog(DeckListActivity.this);
        dialog.setContentView(R.layout.deck_dialog);
        dialog.setTitle("New Deck");
        final EditText editDeckName = (EditText) dialog.findViewById(R.id.edit_deck_name);
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
                if (!deckName.matches("^[a-zA-Z0-9 \\-_.,()]+$")) {
                    Toast.makeText(getApplicationContext(),
                            "Illegal deck name.", Toast.LENGTH_SHORT).show();
                } else if(deckInfoList.contains(deckName)){
                    Toast.makeText(getApplicationContext(),
                            "A deck \"" + deckName + "\" already exists!", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Deck newDeck = new Deck(deckName, "", "");
                    newDeck.addNewCard(new Card("default", "default", 10));
                    newDeck.saveDeck();
                    reloadDeckList();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
}
