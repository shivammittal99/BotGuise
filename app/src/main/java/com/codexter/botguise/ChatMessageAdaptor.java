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


public class ChatMessageAdaptor extends ArrayAdapter<ChatMessage> {

    private String mUsername;
    public ChatMessageAdaptor(Application context, ArrayList<ChatMessage> messages, String username) {
        super(context, 0, messages);
        mUsername = username;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.battle_message_item, parent, false);
        }

        ImageView person_icon = (ImageView) listView.findViewById(R.id.person_icon);
        TextView messageType, messageTextView;

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Regular.ttf");

        ChatMessage message = getItem(position);
        String userInitials = message.getUserInitials();

        String nameShort = "";
        for(String part : userInitials.split("\\s")){
            nameShort += part.charAt(0);
        }
        nameShort = nameShort.toUpperCase();

        if (userInitials.equals(mUsername)) {
            messageType = (TextView) listView.findViewById(R.id.userMessageType);
            messageTextView = (TextView) listView.findViewById(R.id.userMessageTextView);
            messageTextView.setTypeface(typeface);
            messageType.setTypeface(typeface);
            person_icon.setVisibility(View.INVISIBLE);
            messageTextView.setVisibility(View.VISIBLE);
            messageType.setVisibility(View.VISIBLE);
            listView.findViewById(R.id.opponentMessageTextView).setVisibility(View.GONE);
            listView.findViewById(R.id.opponentMessageType).setVisibility(View.GONE);
        } else {
            messageType = (TextView) listView.findViewById(R.id.opponentMessageType);
            messageTextView = (TextView) listView.findViewById(R.id.opponentMessageTextView);
            messageTextView.setTypeface(typeface);
            messageType.setTypeface(typeface);
            person_icon.setVisibility(View.VISIBLE);
            messageTextView.setVisibility(View.VISIBLE);
            messageType.setVisibility(View.VISIBLE);
            listView.findViewById(R.id.userMessageTextView).setVisibility(View.GONE);
            listView.findViewById(R.id.userMessageType).setVisibility(View.GONE);
        }

        messageType.setText(message.getMessageType());
        messageTextView.setText(message.getMessage());

        return listView;
    }
}
