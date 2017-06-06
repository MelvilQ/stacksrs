package de.melvil.stacksrs.model;

import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class DeckCollection {

    private final List<String> deckNames = new ArrayList<>();
    private final List<DeckInfo> deckInfos = new ArrayList<>();

    public void reload() throws IOException {
        deckNames.clear();
        deckInfos.clear();
        File stackSRSDir = new File(Environment.getExternalStorageDirectory() + "/StackSRS");
        stackSRSDir.mkdir();    // create dir if not exists
        File[] deckFiles = stackSRSDir.listFiles();
        if(deckFiles == null){
            throw new IOException("Deck files are not accessible.");
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
                deckNames.add(deckName);
                DeckInfo deckInfo = new DeckInfo(deckName, stats);
                deckInfos.add(deckInfo);
            }
        }
    }

    public final List<DeckInfo> getDeckInfos(){
        return deckInfos;
    }

    public boolean deckWithNameExists(String deckName){
        return deckNames.contains(deckName);
    }

    public boolean isIllegalDeckName(String deckName){
        return !deckName.matches("^[\\p{L}0-9 \\-_.,()]+$");
    }

    public void deleteDeckFile(String deckName){
        File deckFile = new File(Environment.getExternalStorageDirectory()
                + "/StackSRS/" + deckName + ".json");
        if(deckFile.exists())
            deckFile.delete();
    }
}
