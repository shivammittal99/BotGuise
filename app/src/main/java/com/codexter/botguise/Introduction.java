package com.codexter.botguise;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import static android.view.View.GONE;

public class Introduction extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Message>> {

    private EditText messageToSend;
    private static String mUsername;
    private ProgressBar loadingProgressSpinner;

    private ArrayList<Message> mMessages = new ArrayList<>();

    private static MessageAdaptor mAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        Intent intent = getIntent();
        mUsername = intent.getStringExtra("Username");

        ListView chatList = (ListView) findViewById(R.id.chatList);

        mAdaptor = new MessageAdaptor(getApplication(), mMessages);
        chatList.setAdapter(mAdaptor);

        loadingProgressSpinner = (ProgressBar) findViewById(R.id.loadingProgressSpinner);
        messageToSend = (EditText) findViewById(R.id.messageToSend);
        Button sendMessage = (Button) findViewById(R.id.sendMessage);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");
        messageToSend.setTypeface(typeface);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageToSend.getText().toString();
                mAdaptor.add(new Message(" ", message, mUsername));
                sendMessageToBot();
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    private void sendMessageToBot() {
        getLoaderManager().initLoader(1, null, this);
    }

    private void leaveIntroduction() {
        Intent leaveIntroduction = new Intent(Introduction.this, ChooseMode.class);
        leaveIntroduction.putExtra("username", mUsername);
        startActivity(leaveIntroduction);
    }

    @Override
    public Loader<ArrayList<Message>> onCreateLoader(int id, Bundle args) {
        loadingProgressSpinner.setVisibility(ProgressBar.VISIBLE);
        Log.v("onCreateLoader", "Started Loading");
        if (id == 0) {
            return new GetConversationId(getApplicationContext(), Keys.INTRO_BOT_KEY_CODE);
        } else {
            String message = messageToSend.getText().toString();
            messageToSend.setText("");
            return new GetMessage(getApplicationContext(), mUsername, message, Keys.INTRO_BOT_KEY_CODE);
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Message>> loader, ArrayList<Message> messages) {
        loadingProgressSpinner.setVisibility(GONE);
        Message lastMessage = messages.get(messages.size() - 1);

        Log.v("Checking", lastMessage.getMessage());
        if (lastMessage.getMessage().equals("http://www.theoldrobots.com/images62/Bender-18.JPG")) {
            messages.remove(lastMessage);
            mAdaptor.addAll(messages);
            LinearLayout addMessageBar = (LinearLayout) findViewById(R.id.addMessageBar);
            final Button leaveIntroduction = (Button) findViewById(R.id.leaveIntroduction);
            addMessageBar.setVisibility(GONE);
            leaveIntroduction.setVisibility(View.VISIBLE);
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");
            leaveIntroduction.setTypeface(typeface);
            leaveIntroduction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    leaveIntroduction();
                }
            });
        } else {
            mAdaptor.addAll(messages);
            getLoaderManager().destroyLoader(1);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Message>> loader) {
    }

    public static class GetConversationId extends AsyncTaskLoader<ArrayList<Message>> {

        private static int Key_CODE;

        public GetConversationId(Context context, int key_code) {
            super(context);
            Key_CODE = key_code;
        }

        @Override
        public ArrayList<Message> loadInBackground() {
            GetJSONResponse.getConversationId(Key_CODE);
            ArrayList<Message> messages = new ArrayList<>();
            messages.add(new Message("Bot", "Hello", ""));
            return messages;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    public static class GetMessage extends AsyncTaskLoader<ArrayList<Message>> {

        private String mMessage;
        private String mUsername;
        private static int KEY_CODE;

        public GetMessage(Context context, String username, String message, int Key_Code) {
            super(context);
            mMessage = message;
            mUsername = username;
            KEY_CODE = Key_Code;
        }

        @Override
        public ArrayList<Message> loadInBackground() {
            GetJSONResponse.postMessage(mUsername, mMessage, KEY_CODE);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<Message> messages = new ArrayList<>();
            String[] responses = GetJSONResponse.getMessage(KEY_CODE);
            for (int i = 0; i < responses.length; i++) {
                messages.add(new Message("Bot", responses[i], ""));
            }
            return messages;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }
}
