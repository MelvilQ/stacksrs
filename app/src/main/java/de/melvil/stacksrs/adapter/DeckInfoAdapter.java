package de.melvil.stacksrs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.melvil.stacksrs.model.DeckInfo;
import de.melvil.stacksrs.view.R;

public class DeckInfoAdapter extends ArrayAdapter<DeckInfo> {

    private List<DeckInfo> deckInfoList;
    private Context context;

    public DeckInfoAdapter(Context context, List<DeckInfo> deckInfoList){
        super(context, R.layout.item_deck_info, deckInfoList);
        this.context = context;
        this.deckInfoList = deckInfoList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeckInfo deckInfo = deckInfoList.get(position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View deckInfoView = inflater.inflate(R.layout.item_deck_info, parent, false);

        TextView viewName = (TextView) deckInfoView.findViewById(R.id.view_name);
        viewName.setText(deckInfo.getName());
        TextView viewNumCards = (TextView) deckInfoView.findViewById(R.id.view_num_cards);
        viewNumCards.setText(deckInfo.getNumCards());
        TextView viewNumHotCards = (TextView) deckInfoView.findViewById(R.id.view_num_hot_cards);
        viewNumHotCards.setText(deckInfo.getNumHotCards());
        TextView viewNumKnownCards = (TextView) deckInfoView.findViewById(R.id.view_num_known_cards);
        viewNumKnownCards.setText(deckInfo.getNumKnownCards());

        return deckInfoView;
    }
}
