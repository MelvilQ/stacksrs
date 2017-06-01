package de.melvil.stacksrs.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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

    private ProgressBar circle;

    private AsyncHttpClient httpClient = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_download);

        setTitle("Download Deck");

        deckListView = (ListView) findViewById(R.id.deck_list);
        deckListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deckNames);
        deckListView.setAdapter(deckListAdapter);

        deckListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DeckInfo deckInfo = deckNames.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(DeckDownloadActivity.this);
                builder.setTitle("Download Deck");
                builder.setMessage("Do you want to download the deck \"" + deckInfo.name + "\"?");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton("Yes, as Beginner", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        downloadDeck(deckInfo.file, 0);
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes, as Expert", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        downloadDeck(deckInfo.file, 2);
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        circle = (ProgressBar) findViewById(R.id.circle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        circle.setVisibility(View.VISIBLE);
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
                circle.setVisibility(View.INVISIBLE);
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
