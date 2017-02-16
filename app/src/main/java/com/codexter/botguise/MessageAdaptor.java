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

        TextView opponentInitialsTextView = (TextView) listView.findViewById(R.id.opponentInitialsTextView);
        TextView userInitialsTextView = (TextView) listView.findViewById(R.id.userInitialsTextView);
        TextView messageTextView = (TextView) listView.findViewById(R.id.messageTextview);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Regular.ttf");
//        opponentInitialsTextView.setTypeface(typeface);
//        userInitialsTextView.setTypeface(typeface);
        messageTextView.setTypeface(typeface);

        Message message = getItem(position);
        String opponentInitials = message.getOpponentInitials();
//        String userInitials = message.getUserInitials();

        if (opponentInitials.equals("EEMMPPTTYY")) {
            opponentInitialsTextView.setVisibility(View.INVISIBLE);
            userInitialsTextView.setVisibility(View.GONE);
            messageTextView.setBackgroundResource(R.drawable.message_box_user);
            messageTextView.setTextColor(Color.parseColor("#FFFFFF"));
            messageTextView.setGravity(View.TEXT_ALIGNMENT_VIEW_END);
//            String usernameShort = "";
//            for(String part : userInitials.split("\\s")){
//                usernameShort += part.charAt(0);
//            }
//            usernameShort = usernameShort.toUpperCase();
//            userInitialsTextView.setText(usernameShort);
            messageTextView.setText(message.getMessage());
        } else {
            userInitialsTextView.setVisibility(View.INVISIBLE);
            opponentInitialsTextView.setVisibility(View.GONE);
            messageTextView.setBackgroundResource(R.drawable.message_box_opponent);
            messageTextView.setTextColor(Color.parseColor("#000000"));
//            opponentInitialsTextView.setText(opponentInitials);
            messageTextView.setText(message.getMessage());
        }


        return listView;
    }
}
