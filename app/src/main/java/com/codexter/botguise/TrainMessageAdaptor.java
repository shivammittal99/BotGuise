package com.codexter.botguise;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class TrainMessageAdaptor extends ArrayAdapter<TrainMessage> {

    private Context mContext;

    public TrainMessageAdaptor(Application context, ArrayList<TrainMessage> messages, Context mContext) {
        super(context, 0, messages);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.train_message_item, parent, false);
        }

        ImageView bot_icon = (ImageView) listView.findViewById(R.id.bot_icon);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Regular.ttf");

        TrainMessage message = getItem(position);

        if (!message.getFromBot()) {
            bot_icon.setVisibility(View.INVISIBLE);
            TextView userMessageTextView = (TextView) listView.findViewById(R.id.userMessageTextview);
            listView.findViewById(R.id.opponentView).setVisibility(View.GONE);
            userMessageTextView.setTypeface(typeface);
            userMessageTextView.setVisibility(View.VISIBLE);
            userMessageTextView.setText(message.getMessage());
        } else {
            bot_icon.setVisibility(View.VISIBLE);
            final TextView messageTextView = (TextView) listView.findViewById(R.id.opponentMessageTextview);
            listView.findViewById(R.id.userMessageTextview).setVisibility(View.GONE);
            messageTextView.setTypeface(typeface);
            listView.findViewById(R.id.opponentView).setVisibility(View.VISIBLE);
            final RadioGroup satisfication = (RadioGroup) listView.findViewById(R.id.satisfication_radio_btns);
            satisfication.check(R.id.satisfied_radio_btn);
            final RadioButton satisfied = (RadioButton) listView.findViewById(R.id.satisfied_radio_btn);
            satisfied.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        if (mContext instanceof TrainActivity) {
                            ((TrainActivity) mContext).satisfaction(false, position, messageTextView, satisfied);
                        }
                    }
                }
            });
            messageTextView.setText(message.getMessage());
        }
        return listView;
    }
}
