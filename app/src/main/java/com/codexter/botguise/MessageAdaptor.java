package com.codexter.botguise;

import android.app.Application;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class MessageAdaptor extends ArrayAdapter<Message> {

    public MessageAdaptor(Application context, ArrayList<Message> messages) {
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
        }

        ImageView bot_icon = (ImageView) listView.findViewById(R.id.bot_icon);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Regular.ttf");

        Message message = getItem(position);
        String opponentInitials = message.getOpponentInitials();

        if (opponentInitials.equals("EEMMPPTTYY")) {
            bot_icon.setVisibility(View.INVISIBLE);
            TextView userMessageTextView = (TextView) listView.findViewById(R.id.userMessageTextview);
            listView.findViewById(R.id.opponentMessageTextview).setVisibility(View.GONE);
            userMessageTextView.setTypeface(typeface);
            userMessageTextView.setVisibility(View.VISIBLE);
            userMessageTextView.setText(message.getMessage());
        } else {
            bot_icon.setVisibility(View.VISIBLE);
            TextView messageTextView = (TextView) listView.findViewById(R.id.opponentMessageTextview);
            listView.findViewById(R.id.userMessageTextview).setVisibility(View.GONE);
            messageTextView.setTypeface(typeface);
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(message.getMessage());
        }


        return listView;
    }
}
