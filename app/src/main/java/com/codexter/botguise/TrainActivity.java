package com.codexter.botguise;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class TrainActivity extends AppCompatActivity {

    private static String mUsername;
    private static MessageAdaptor mAdaptor;
    private EditText trainMessageToSend;
    private ProgressBar trainLoadingProgressBar;
    private ArrayList<Message> mMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        mUsername = getIntent().getStringExtra("username");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView chatList = (ListView) findViewById(R.id.trainChatList);

        mAdaptor = new MessageAdaptor(getApplication(), mMessages);
        chatList.setAdapter(mAdaptor);

        trainLoadingProgressBar = (ProgressBar) findViewById(R.id.trainLoadingProgressBar);
        trainMessageToSend = (EditText) findViewById(R.id.trainMessageToSend);
        Button sendMessage = (Button) findViewById(R.id.trainSendMessage);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");
        trainMessageToSend.setTypeface(typeface);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = trainMessageToSend.getText().toString();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(trainMessageToSend.getWindowToken(), 0);
                mAdaptor.add(new Message("EEMMPPTTYY", message, mUsername));
            }
        });
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
        builder.setMessage("Trained Enough?\nWanna take a break");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                NavUtils.navigateUpFromSameTask(TrainActivity.this);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
