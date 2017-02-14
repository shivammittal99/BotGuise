package com.codexter.botguise;

import android.app.Application;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

        TextView opponentInitialsTextView = (TextView) listView.findViewById(R.id.opponentInitialsTextView);
        TextView userInitialsTextView = (TextView) listView.findViewById(R.id.battleUserInitialsTextView);
        TextView messageType = (TextView) listView.findViewById(R.id.messageType);
        TextView messageTextView = (TextView) listView.findViewById(R.id.messageTextview);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Regular.ttf");
        opponentInitialsTextView.setTypeface(typeface);
        userInitialsTextView.setTypeface(typeface);
        messageTextView.setTypeface(typeface);
        messageType.setTypeface(typeface);

        ChatMessage message = getItem(position);
        String userInitials = message.getUserInitials();

        String usernameShort = "";
        for(String part : userInitials.split("\\s")){
            usernameShort += part.charAt(0);
        }
        usernameShort = usernameShort.toUpperCase();

        if (userInitials.equals(mUsername)) {
            opponentInitialsTextView.setVisibility(View.INVISIBLE);
            messageTextView.setBackgroundResource(R.drawable.message_box_user);
            messageTextView.setTextColor(Color.parseColor("#FFFFFF"));
            userInitialsTextView.setText(usernameShort);
        } else {
            userInitialsTextView.setVisibility(View.INVISIBLE);
            messageTextView.setBackgroundResource(R.drawable.message_box_opponent);
            messageTextView.setTextColor(Color.parseColor("#000000"));
            opponentInitialsTextView.setText(usernameShort);
        }

        messageType.setText(message.getMessageType());
        messageTextView.setText(message.getMessage());

        return listView;
    }
}
