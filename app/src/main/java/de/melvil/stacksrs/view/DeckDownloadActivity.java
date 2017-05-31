package de.melvil.stacksrs.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.melvil.stacksrs.model.Deck;

public class DeckDownloadActivity extends AppCompatActivity {

    private class DeckInfo {
        public String name;
        public String file;
        public String front;
        public String back;
        public String updated;

        @Override
        public String toString(){
            return name + "\n" + "Last update: " + updated;
        }
    }

    private ListView deckListView;
    private ArrayAdapter<DeckInfo> deckListAdapter;
    private List<DeckInfo> deckNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_download);

        setTitle("Download Deck");

        deckListView = (ListView) findViewById(R.id.deck_list);
        deckListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deckNames);
        deckListView.setAdapter(deckListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadDeckList();
    }

    public void reloadDeckList() {
        try { // TODO do this in an async task, handle the circle
            deckNames.clear();
            String url = "http://stacksrs.droppages.com/decks.txt";
            JSONObject deckListJson = new JSONObject(IOUtils.toString(new URL(url).openStream()));
            JSONArray deckListArray = deckListJson.getJSONArray("decks");
            for (int i = 0; i < deckListArray.length(); ++i) {
                JSONObject deckInfoObject = deckListArray.getJSONObject(i);
                DeckInfo deckInfo = new DeckInfo();
                deckInfo.name = deckInfoObject.getString("name");
                deckInfo.file = deckInfoObject.getString("file");
                deckInfo.front = deckInfoObject.getString("front");
                deckInfo.back = deckInfoObject.getString("back");
                deckInfo.updated = deckInfoObject.getString("updated");
                deckNames.add(deckInfo);
            }
            deckListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Could not connect to server. Are you online?",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadDeck(DeckInfo deckInfo) {
        try { // TODO do this in async task
            String url = "http://stacksrs.droppages.com/" + deckInfo.file + ".txt";
            JSONObject deckJson = new JSONObject(IOUtils.toString(new URL(url).openStream()));
            Deck newDeck = new Deck(deckInfo.name, deckInfo.front, deckInfo.back);
            // TODO add cards
            newDeck.saveDeck();
            // TODO toast when finished
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Downloading the deck failed. Please try again.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
