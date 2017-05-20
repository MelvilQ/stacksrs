package de.melvil.stacksrs.view;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DeckListActivity extends AppCompatActivity {

    private ListView deckListView;
    private ArrayAdapter<String> deckListAdapter;
    private List<String> deckNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_list);

        deckListView = (ListView) findViewById(R.id.deck_list);
        deckListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deckNames);
        deckListView.setAdapter(deckListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadDeckList();
    }

    public void reloadDeckList() {
        deckNames.clear();
        File stackSRSDir = new File(Environment.getExternalStorageDirectory() + "/StackSRS");
        stackSRSDir.mkdir();    // create dir if not exists
        File[] deckFiles = stackSRSDir.listFiles();
        // sort by last edit
        Arrays.sort(deckFiles, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });
        // add to list
        for(File f : deckFiles){
            if(f.getName().endsWith(".json"))
                deckNames.add(f.getName().replace(".json", ""));
        }
        deckListAdapter.notifyDataSetChanged();
    }
}
