package com.codexter.botguise;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BattleActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ChatMessage> {

    private static final int TYPE_QUESTION = 1;
    private static final int TYPE_ANSWER = 2;
    private static final int TYPE_USER = 3;
    private static final int TYPE_BOT = 4;
    private static final int QUESTION_BOT_INIT_LOADER_ID = 1;
    private static final int ANSWER_BOT_INIT_LOADER_ID = 2;
    private static final int GET_ANSWER_MESSAGE_LOADER_ID = 3;
    private static final int GET_QUESTION_MESSAGE_LOADER_ID = 4;
    private static String GAME_ID = "demo123";
    private static String mUsername;
    private static int mMode;
    private static int mRoundNumber = 1;
    private static String CONVERSATION_ID = "null";
    private static String MESSAGE;
    private static ChatMessageAdaptor mMessageAdaptor;
    private ProgressBar battleLoadingProgressSpinner;
    private EditText battleMessageToSend;
    private String mMessageType = "question:";
    //TODO change the value of actual
    private int actual = 2;
    private boolean USER_RESPONDS = true;
    private int user_score, opponent_score;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mGameDatabaseReference;
    private ChildEventListener mChildEventListener;
    private ArrayList<ChatMessage> mMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        Intent intent = getIntent();
        mUsername = intent.getStringExtra("username");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView chatList = (ListView) findViewById(R.id.battleChatList);
        battleLoadingProgressSpinner = (ProgressBar) findViewById(R.id.battleLoadingProgressBar);
        battleMessageToSend = (EditText) findViewById(R.id.battleMessageToSend);

        mMessageAdaptor = new ChatMessageAdaptor(getApplication(), mMessages, mUsername);
        chatList.setAdapter(mMessageAdaptor);

        mMode = intent.getIntExtra("mode", 1);
        if (mMode == 1) {
            int temp = 0;
            getLoaderManager().initLoader(QUESTION_BOT_INIT_LOADER_ID, null, this);
            Log.v("Conc id final", CONVERSATION_ID);
        } else {
            int temp = 0;
            getLoaderManager().initLoader(ANSWER_BOT_INIT_LOADER_ID, null, this);
            Log.v("Conc id final", CONVERSATION_ID);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showBackDialogBox();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        showBackDialogBox();
    }

    private void showBackDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final boolean exit = false;
        builder.setMessage("Do you want to exit the game?");
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                NavUtils.navigateUpFromSameTask(BattleActivity.this);
            }
        });
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void initFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mGameDatabaseReference = mFirebaseDatabase.getReference().child("Battles").child(GAME_ID);

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                mMessageAdaptor.add(chatMessage);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mGameDatabaseReference.addChildEventListener(mChildEventListener);
    }

    private void showCreateGameDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Give this ID to your friend\n\n" + GAME_ID +
                "\nBot : What question should we start with?");
        final EditText editText = new EditText(this);
        editText.setGravity(Gravity.CENTER);
        builder.setView(editText);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    editText.setError("Enter something to get started");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    MESSAGE = editText.getText().toString();
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showJoinGameDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText editText = new EditText(this);
        editText.setGravity(Gravity.CENTER);
        builder.setMessage("Enter the ID given by your friend");
        builder.setView(editText);
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    editText.setError("Enter a Game Id to start the game");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    GAME_ID = editText.getText().toString();
                    initFirebase();
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void round() {
        if (mMode == 1) {
            if (mRoundNumber <= 5) {
                mMessageType = "Question";
                getAnsweringPersonforQuestion();
            } else if (mRoundNumber <= 10) {
                if (mRoundNumber == 6) {
                    getLoaderManager().destroyLoader(QUESTION_BOT_INIT_LOADER_ID);
                    getLoaderManager().initLoader(ANSWER_BOT_INIT_LOADER_ID, null, this);
                }
                mMessageType = "Answer";
                getAnsweringPersonforAnswer();
            } else {
                declareResults();
            }

        } else {
            if (mRoundNumber <= 5) {
                mMessageType = "Answer";
                getAnsweringPersonforAnswer();
            } else if (mRoundNumber <= 10) {
                if (mRoundNumber == 6) {
                    getLoaderManager().destroyLoader(ANSWER_BOT_INIT_LOADER_ID);
                    getLoaderManager().initLoader(QUESTION_BOT_INIT_LOADER_ID, null, this);
                }
                mMessageType = "Question";
                getAnsweringPersonforQuestion();
            } else {
                declareResults();
            }
        }
    }

    private void declareResults() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText editText = new EditText(this);
        editText.setGravity(Gravity.CENTER);
        if (user_score > opponent_score) {
            builder.setMessage("You Win!");
        } else if (opponent_score > user_score) {
            builder.setMessage("You Lose.");
        } else {
            builder.setMessage("The game is a Draw!!");
        }
        builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void question(boolean userResponds) {
        if (userResponds) {
            LinearLayout takeResponseView = (LinearLayout) findViewById(R.id.takeResponseView);
            takeResponseView.setVisibility(GONE);
            LinearLayout battleAddMessageBar = (LinearLayout) findViewById(R.id.battleAddMessageBar);
            battleAddMessageBar.setVisibility(VISIBLE);
            final EditText messageToSend = (EditText) findViewById(R.id.battleMessageToSend);
            Button sendMessage = (Button) findViewById(R.id.battleSendMessage);
            final MyCount timer = new MyCount(60000, 1000);
            timer.start();
            sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChatMessage chatMessage = new ChatMessage(messageToSend.getText().toString(), mUsername, mMessageType);
                    mGameDatabaseReference.push().setValue(chatMessage);
                    messageToSend.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(messageToSend.getWindowToken(), 0);
                    timer.cancel();
                    respondToAnswer();
                }
            });
        } else {
            getLoaderManager().initLoader(GET_QUESTION_MESSAGE_LOADER_ID, null, this);

        }
    }

    private void answer(boolean userResponds) {
        if (userResponds) {
            LinearLayout takeResponseView = (LinearLayout) findViewById(R.id.takeResponseView);
            takeResponseView.setVisibility(GONE);
            LinearLayout battleAddMessageBar = (LinearLayout) findViewById(R.id.battleAddMessageBar);
            battleAddMessageBar.setVisibility(VISIBLE);
            final EditText messageToSend = (EditText) findViewById(R.id.battleMessageToSend);
            Button sendMessage = (Button) findViewById(R.id.battleSendMessage);
            final MyCount timer = new MyCount(60000, 1000);
            timer.start();
            sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChatMessage chatMessage = new ChatMessage(messageToSend.getText().toString(), mUsername, mMessageType);
                    mGameDatabaseReference.push().setValue(chatMessage);
                    messageToSend.setText("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(messageToSend.getWindowToken(), 0);
                    timer.cancel();
                    respondToAnswer();
                }
            });
        } else {
            getLoaderManager().initLoader(GET_ANSWER_MESSAGE_LOADER_ID, null, this);
        }
    }

    private void respondToQuestion() {
        LinearLayout takeResponseView = (LinearLayout) findViewById(R.id.takeResponseView);
        takeResponseView.setVisibility(VISIBLE);
        LinearLayout battleAddMessageBar = (LinearLayout) findViewById(R.id.battleAddMessageBar);
        battleAddMessageBar.setVisibility(GONE);
        Button responseUserButton = (Button) findViewById(R.id.responseUserButton);
        Button responseBotButton = (Button) findViewById(R.id.responseBotButton);

        Toast.makeText(this, "Guess who sent this response", Toast.LENGTH_SHORT).show();
        final MyCount timer = new MyCount(60000, 1000);
        responseBotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeScore(TYPE_QUESTION, actual, TYPE_BOT);
                timer.cancel();
                mRoundNumber++;
                round();
            }
        });
        responseUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeScore(TYPE_QUESTION, actual, TYPE_USER);
                timer.cancel();
                mRoundNumber++;
                round();
            }
        });
        timer.start();
    }

    private void respondToAnswer() {
        LinearLayout takeResponseView = (LinearLayout) findViewById(R.id.takeResponseView);
        takeResponseView.setVisibility(VISIBLE);
        LinearLayout battleAddMessageBar = (LinearLayout) findViewById(R.id.battleAddMessageBar);
        battleAddMessageBar.setVisibility(GONE);
        Button responseUserButton = (Button) findViewById(R.id.responseUserButton);
        Button responseBotButton = (Button) findViewById(R.id.responseBotButton);

        Toast.makeText(this, "Guess who sent this response", Toast.LENGTH_SHORT).show();
        final MyCount timer = new MyCount(60000, 1000);
        responseBotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeScore(TYPE_ANSWER, actual, TYPE_BOT);
                timer.cancel();
                mRoundNumber++;
                round();
            }
        });
        responseUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeScore(TYPE_ANSWER, actual, TYPE_USER);
                timer.cancel();
                mRoundNumber++;
                round();
            }
        });
        timer.start();
    }

    private void getAnsweringPersonforQuestion() {
        LinearLayout takeResponseView = (LinearLayout) findViewById(R.id.takeResponseView);
        takeResponseView.setVisibility(VISIBLE);
        LinearLayout battleAddMessageBar = (LinearLayout) findViewById(R.id.battleAddMessageBar);
        battleAddMessageBar.setVisibility(GONE);
        Button responseUserButton = (Button) findViewById(R.id.responseUserButton);
        Button responseBotButton = (Button) findViewById(R.id.responseBotButton);
        Toast.makeText(this, "Choose who will play the round?", Toast.LENGTH_SHORT).show();
        responseBotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question(false);
            }
        });
        responseUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question(true);
            }
        });
    }

    private void getAnsweringPersonforAnswer() {
        LinearLayout takeResponseView = (LinearLayout) findViewById(R.id.takeResponseView);
        takeResponseView.setVisibility(VISIBLE);
        LinearLayout battleAddMessageBar = (LinearLayout) findViewById(R.id.battleAddMessageBar);
        battleAddMessageBar.setVisibility(GONE);
        Button responseUserButton = (Button) findViewById(R.id.responseUserButton);
        Button responseBotButton = (Button) findViewById(R.id.responseBotButton);
        Toast.makeText(this, "Choose who will play the round?", Toast.LENGTH_SHORT).show();
        responseBotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer(false);
            }
        });
        responseUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer(true);
            }
        });
    }

    private void changeScore(int type, int actual, int answered) {
        TextView userScore = (TextView) findViewById(R.id.user_score);
        TextView opponentScore = (TextView) findViewById(R.id.opponent_score);
        int UserCurrentScore = Integer.valueOf(userScore.getText().toString());
        int opponentCurrentScore = Integer.valueOf(opponentScore.getText().toString());

        if (type == TYPE_QUESTION) {
            if (actual == answered) {
                userScore.setText(String.valueOf(1));
                opponentScore.setText(String.valueOf(1));
            } else {
                userScore.setText(String.valueOf(1));
                opponentScore.setText(String.valueOf(1));
            }
        } else {
            if (actual == answered) {
                userScore.setText(String.valueOf(1));
                opponentScore.setText(String.valueOf(1));
            } else {
                userScore.setText(String.valueOf(1));
                opponentScore.setText(String.valueOf(1));
            }
        }
    }

    @Override
    public Loader<ChatMessage> onCreateLoader(int id, Bundle args) {
        battleLoadingProgressSpinner.setVisibility(ProgressBar.VISIBLE);
        Log.v("onCreateLoader", "Started Loading" + String.valueOf(id));
        switch (id) {
            case QUESTION_BOT_INIT_LOADER_ID:
                return new GetConversationId(getApplicationContext(), Keys.MOVIE_QUESTION_BOT_KEY_CODE);
            case ANSWER_BOT_INIT_LOADER_ID:
                return new GetConversationId(getApplicationContext(), Keys.MOVIE_ANSWER_BOT_KEY_CODE);
            case GET_QUESTION_MESSAGE_LOADER_ID:
                return new PostMessage(getApplicationContext(), mUsername, MESSAGE, Keys.MOVIE_QUESTION_BOT_KEY_CODE, "Question");
            default:
                return new PostMessage(getApplicationContext(), mUsername, MESSAGE, Keys.MOVIE_ANSWER_BOT_KEY_CODE, "Answer");
        }
    }

    @Override
    public void onLoadFinished(Loader<ChatMessage> loader, ChatMessage message) {
        int loader_id = loader.getId();
        if (loader_id == QUESTION_BOT_INIT_LOADER_ID) {
            GAME_ID = message.getMessage();
            initFirebase();
            battleLoadingProgressSpinner.setVisibility(GONE);
            getLoaderManager().destroyLoader(loader_id);
            showCreateGameDialog();
            round();
        } else if (loader_id == ANSWER_BOT_INIT_LOADER_ID) {
            showJoinGameDialog();
            battleLoadingProgressSpinner.setVisibility(GONE);
            getLoaderManager().destroyLoader(loader_id);
            round();
        } else if (loader_id == GET_ANSWER_MESSAGE_LOADER_ID) {
            MESSAGE = message.getMessage();
            ChatMessage chatMessage = new ChatMessage(MESSAGE, mUsername, mMessageType);
            mGameDatabaseReference.push().setValue(chatMessage);
            respondToQuestion();
        } else {
            MESSAGE = message.getMessage();
            ChatMessage chatMessage = new ChatMessage(MESSAGE, mUsername, mMessageType);
            mGameDatabaseReference.push().setValue(chatMessage);
            respondToAnswer();
        }
    }

    @Override
    public void onLoaderReset(Loader<ChatMessage> loader) {
    }

    public static class GetConversationId extends AsyncTaskLoader<ChatMessage> {

        private static int Key_CODE;

        public GetConversationId(Context context, int key_code) {
            super(context);
            Key_CODE = key_code;
        }

        @Override
        public ChatMessage loadInBackground() {
            Log.v("loadInBackground", "Loading in background");
            CONVERSATION_ID = GetBattleResponse.getConversationId(Key_CODE);
            return new ChatMessage(CONVERSATION_ID.substring(0, 6), mUsername, "ID");
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    public static class PostMessage extends AsyncTaskLoader<ChatMessage> {
        private static int KEY_CODE;
        private String mMessage;
        private String mUsername;
        private String mMessageType;

        public PostMessage(Context context, String username, String message, int Key_Code, String messageType) {
            super(context);
            mMessage = message;
            mUsername = username;
            KEY_CODE = Key_Code;
            mMessageType = messageType;
        }

        @Override
        public ChatMessage loadInBackground() {
            GetBattleResponse.postMessage(mUsername, mMessage, KEY_CODE);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ChatMessage message = new ChatMessage(GetBattleResponse.getMessage(KEY_CODE), mUsername, mMessageType);
            return message;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

    }

    public class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            TextView time_left = (TextView) findViewById(R.id.time_left);
            time_left.setText(String.valueOf(millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            TextView time_left = (TextView) findViewById(R.id.time_left);
            time_left.setText("0");
            Toast.makeText(BattleActivity.this, "Your time is up.", Toast.LENGTH_SHORT).show();
            USER_RESPONDS = false;
        }

    }
}