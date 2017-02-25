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

import static com.codexter.botguise.R.id.guess_user_radio_btn;

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
        RadioGroup guessed_radio_btns = null;

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
            guessed_radio_btns = (RadioGroup) listView.findViewById(R.id.guessed_radio_btns);
            for (int i = 0; i < guessed_radio_btns.getChildCount(); i++) {
                guessed_radio_btns.getChildAt(i).setEnabled(false);
            }
        } else {
            messageTextView = (TextView) listView.findViewById(R.id.opponentMessageTextView);
            messageTextView.setTypeface(typeface);
            person_icon.setVisibility(View.VISIBLE);
            listView.findViewById(R.id.userView).setVisibility(View.GONE);
            listView.findViewById(R.id.opponentView).setVisibility(View.VISIBLE);
            guess_radio_btns = (RadioGroup) listView.findViewById(R.id.guess_radio_btns);
        }
        messageTextView.setText(message.getMessage());

        if (userInitials.equals(mUsername)) {
            if (guessed_radio_btns.getCheckedRadioButtonId() != -1) {
                if (message.getGuess() == USER) {
                    ((RadioButton) listView.findViewById(R.id.guessed_user_radio_btn)).setChecked(true);
                } else if (message.getGuess() == BOT) {
                    ((RadioButton) listView.findViewById(R.id.guessed_bot_radio_btn)).setChecked(true);
                }
            }
        } else {
            if (guess_radio_btns.getCheckedRadioButtonId() == -1) {
                if (mContext instanceof BattleActivity) {
                    guess(message, listView);
                }
            } else {
                if (message.getGuess() == USER) {
                    ((RadioButton) listView.findViewById(guess_user_radio_btn)).setChecked(true);
                } else if (message.getGuess() == BOT) {
                    ((RadioButton) listView.findViewById(R.id.guess_bot_radio_btn)).setChecked(true);
                }
            }
        }
        return listView;
    }

    private void guess(final ChatMessage message, View listView) {
        final int actual = message.getActual();
        final RadioButton guess_user_radio_btn = ((RadioButton) listView.findViewById(R.id.guess_user_radio_btn));
        final RadioButton guess_bot_radio_btn = ((RadioButton) listView.findViewById(R.id.guess_bot_radio_btn));
        guess_user_radio_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((BattleActivity) mContext).changescore(actual, USER);
                    message.setGuess(USER);
                }
                guess_user_radio_btn.setOnCheckedChangeListener(null);
                guess_bot_radio_btn.setEnabled(false);
                guess_user_radio_btn.setEnabled(false);
                notifyDataSetChanged();
            }
        });
        guess_bot_radio_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((BattleActivity) mContext).changescore(actual, BOT);
                    message.setGuess(BOT);
                }
                guess_user_radio_btn.setOnCheckedChangeListener(null);
                guess_bot_radio_btn.setEnabled(false);
                guess_user_radio_btn.setEnabled(false);
                notifyDataSetChanged();
            }
        });
    }
}
