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
import android.widget.Toast;

import java.util.ArrayList;

public class ChatMessageAdaptor extends ArrayAdapter<ChatMessage> {
    private final int BOT = 1;
    private final int USER = 2;
    private String mUsername;
    private Context mContext;

    public ChatMessageAdaptor(Application context, ArrayList<ChatMessage> messages, String username, Context mContext) {
        super(context, 0, messages);
        mUsername = username;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listView = convertView;
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.battle_message_item, parent, false);
        }

        ImageView person_icon = (ImageView) listView.findViewById(R.id.person_icon);
        TextView messageTextView;
        RadioGroup guess_radio_btns = null;

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Regular.ttf");

        ChatMessage message = getItem(position);
        String userInitials = message.getUserInitials();
//
//        String nameShort = "";
//        for(String part : userInitials.split("\\s")){
//            nameShort += part.charAt(0);
//        }
//        nameShort = nameShort.toUpperCase();

        if (userInitials.equals(mUsername)) {
            messageTextView = (TextView) listView.findViewById(R.id.userMessageTextView);
            messageTextView.setTypeface(typeface);
            person_icon.setVisibility(View.INVISIBLE);
            listView.findViewById(R.id.userView).setVisibility(View.VISIBLE);
            listView.findViewById(R.id.opponentView).setVisibility(View.GONE);
            RadioGroup Satisfaction = (RadioGroup) listView.findViewById(R.id.satisfication_radio_btns);
            Satisfaction.check(R.id.satisfied_radio_btn);
            RadioButton satisfied_radio = (RadioButton) listView.findViewById(R.id.satisfied_radio_btn);

            satisfied_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Toast.makeText(mContext, "Satisfied", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Not Satisfied", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            messageTextView = (TextView) listView.findViewById(R.id.opponentMessageTextView);
            messageTextView.setTypeface(typeface);
            person_icon.setVisibility(View.VISIBLE);
            listView.findViewById(R.id.userView).setVisibility(View.GONE);
            listView.findViewById(R.id.opponentView).setVisibility(View.VISIBLE);
            guess_radio_btns = (RadioGroup) listView.findViewById(R.id.guess_radio_btns);
        }
        messageTextView.setText(message.getMessage());

        if (mContext instanceof BattleActivity) {
            if (!message.getUserInitials().equals(mUsername)) {
                guess(message, guess_radio_btns);
//                ((BattleActivity) mContext).userResponds();
            } else {
//                ((BattleActivity) mContext).userResponds();
            }
        }
        return listView;
    }

    private void guess(final ChatMessage message, final RadioGroup guess_radio_btns) {
        final int actual = message.getActual();
        guess_radio_btns.check(-1);
        guess_radio_btns.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.guess_user_radio_btn) {
                    ((BattleActivity) mContext).changescore(actual, USER);
                    message.setGuess(USER);
                } else {
                    ((BattleActivity) mContext).changescore(actual, BOT);
                    message.setGuess(BOT);
                }
                guess_radio_btns.setOnCheckedChangeListener(null);
                for (int i = 0; i < guess_radio_btns.getChildCount(); i++) {
                    guess_radio_btns.getChildAt(i).setEnabled(false);
                }
            }
        });
    }
}
