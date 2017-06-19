package de.melvil.stacksrs.model;

import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class DeckCollection {

    public static File stackSRSDir; // location of the deck files, also directly used by activities

    private final List<String> deckNames = new ArrayList<>();
    private final List<DeckInfo> deckInfos = new ArrayList<>();

    public void reload(File dir) throws IOException {
        deckNames.clear();
        deckInfos.clear();
        // scanning the folder for deck files
        stackSRSDir = dir;
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
            File statsFile = new File(stackSRSDir + "/stats");
            if (!statsFile.exists()) { // create stats file if it does not exist
                boolean createSuccess = statsFile.createNewFile();
                if(!createSuccess){
                    Log.w("DeckCollection", "Could not create stats file.");
                }
            }
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
        // we allow all unicode letters, numbers, spaces and some other characters
        return !deckName.matches("^[\\p{L}0-9 \\-_.,()]+$");
    }

    public void deleteDeckFile(String deckName){
        File deckFile = new File(stackSRSDir + "/" + deckName + ".json");
        if(deckFile.exists())
            deckFile.delete();
    }
}
