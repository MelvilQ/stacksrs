package de.melvil.stacksrs.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.melvil.stacksrs.model.Card;
import de.melvil.stacksrs.model.Deck;

public class DeckDownloadActivity extends AppCompatActivity {

    private final String SERVER_URL = "http://stacksrs.droppages.com/";

    private class DeckInfo {
        public String name;
        public String file;
        public String front;
        public String back;
        public String updated;

        @Override
        public String toString() {
            return name + "\n" + "Last update: " + updated;
        }
    }

    private ListView deckListView;
    private ArrayAdapter<DeckInfo> deckListAdapter;
    private List<DeckInfo> deckNames = new ArrayList<>();

    private AsyncHttpClient httpClient = new AsyncHttpClient();

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
        httpClient.get(SERVER_URL + "decks.txt", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                deckNames.clear();
                try {
                    JSONArray deckListArray = response.getJSONArray("decks");
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
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Could not load deck list from server.",
                            Toast.LENGTH_SHORT).show();
                }
                deckListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Could not connect to server. Are you online?",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void downloadDeck(final String file, final int level) {
        httpClient.get(SERVER_URL + file + ".txt", null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String deckName = response.getString("name");
                    Deck newDeck = new Deck(deckName, response.getString("front"),
                            response.getString("back"));
                    JSONArray cardArray = response.getJSONArray("cards");
                    List<Card> cards = new ArrayList<>();
                    for(int i = 0; i < cardArray.length(); ++i) {
                        JSONObject cardObject = cardArray.getJSONObject(i);
                        Card c = new Card(cardObject.getString("front"),
                                cardObject.getString("back"), level);
                        cards.add(c);
                    }
                    newDeck.fillWithCards(cards);
                    Toast.makeText(getApplicationContext(), "Downloaded " + deckName + ".",
                            Toast.LENGTH_SHORT).show();
                } catch(JSONException e) {
                    Toast.makeText(getApplicationContext(), "Could not load deck.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                                  Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Downloading the deck failed. Are you online?",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    
}
