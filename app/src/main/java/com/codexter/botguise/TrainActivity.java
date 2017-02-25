package com.codexter.botguise;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.View.GONE;

public class TrainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>> {

    private static final int GET_MESSAGE_LOADER_ID = 1;
    private static final int CHANGE_MESSAGE_LOADER_ID = 2;
    private static String mUserId;
    private static String PACK;
    private static TrainMessageAdaptor mAdaptor;
    private EditText trainMessageToSend;
    private ProgressBar trainLoadingProgressBar;
    private ArrayList<TrainMessage> mMessages = new ArrayList<>();
    private String mMessage;
    private String prevblob;
    private String currblob;
    private String trainblob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        mUserId = getIntent().getStringExtra("userId");
        PACK = getIntent().getStringExtra("PACK");

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {

        }

        ListView chatList = (ListView) findViewById(R.id.trainChatList);

        chatList.setEmptyView(findViewById(R.id.train_empty_view));

        mAdaptor = new TrainMessageAdaptor(getApplication(), mMessages, TrainActivity.this);
        chatList.setAdapter(mAdaptor);

        trainLoadingProgressBar = (ProgressBar) findViewById(R.id.trainLoadingProgressBar);
        trainMessageToSend = (EditText) findViewById(R.id.trainMessageToSend);
        Button trainSendMessage = (Button) findViewById(R.id.trainSendMessage);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");
        trainMessageToSend.setTypeface(typeface);

        trainSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessage = trainMessageToSend.getText().toString().trim();
                if (!TextUtils.isEmpty(mMessage)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(trainMessageToSend.getWindowToken(), 0);
                    mAdaptor.add(new TrainMessage(false, mMessage));
                    if (getLoaderManager().getLoader(GET_MESSAGE_LOADER_ID) == null) {
                        getLoaderManager().initLoader(GET_MESSAGE_LOADER_ID, null, TrainActivity.this);
                    } else {
                        getLoaderManager().restartLoader(GET_MESSAGE_LOADER_ID, null, TrainActivity.this);
                    }
                    trainMessageToSend.setText("");
                }
            }
        });

        chatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

    public void satisfaction(final boolean satisfied, final int index, final TextView messageTextVIew, final RadioButton satisfied_radio) {
        if (!satisfied) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.change_popup);
            final EditText field_changed_response = (EditText) dialog.findViewById(R.id.field_changed_response);
            Button negative_button = (Button) dialog.findViewById(R.id.negativeButton);
            Button positive_button = (Button) dialog.findViewById(R.id.positiveButton);
            negative_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    satisfied_radio.setChecked(true);
                    dialog.dismiss();
                }
            });
            positive_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String changedResponse = field_changed_response.getText().toString().trim();
                    if (TextUtils.isEmpty(changedResponse)) {
                        field_changed_response.setError("Required");
                    } else {
                        TrainMessage previous = mAdaptor.getItem(index - 1);
                        TrainMessage change = mAdaptor.getItem(index);
                        prevblob = previous.getMessage();
                        trainblob = changedResponse;
                        currblob = change.getMessage();
                        if (getLoaderManager().getLoader(CHANGE_MESSAGE_LOADER_ID) == null) {
                            getLoaderManager().initLoader(CHANGE_MESSAGE_LOADER_ID, null, TrainActivity.this);
                        } else {
                            getLoaderManager().restartLoader(CHANGE_MESSAGE_LOADER_ID, null, TrainActivity.this);
                        }
                        satisfied_radio.setChecked(true);
                        change.setMessage(trainblob);
                        mAdaptor.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }
            });
            dialog.setCanceledOnTouchOutside(true);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    satisfied_radio.setChecked(true);
                }
            });
            dialog.show();
        }
    }


    private void showBackDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final boolean exit = false;
        builder.setMessage("Trained Enough?\nWanna take a break");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(TrainActivity.this, ChooseMode.class);
                intent.putExtra("PACK", PACK);
                finish();
                startActivity(intent);
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

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
        trainLoadingProgressBar.setVisibility(View.VISIBLE);
        switch (id) {
            case GET_MESSAGE_LOADER_ID:
                return new GetMessage(getApplicationContext(), mUserId, PACK, true, mMessage);
            case CHANGE_MESSAGE_LOADER_ID:
                return new ChangeMessage(getApplicationContext(), mUserId, PACK, prevblob, currblob, trainblob);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        if (loader.getId() == GET_MESSAGE_LOADER_ID) {
            for (int i = 0; i < data.size(); i++) {
                mAdaptor.add(new TrainMessage(true, data.get(i)));
            }
        } else if (loader.getId() == CHANGE_MESSAGE_LOADER_ID) {

        }
        trainLoadingProgressBar.setVisibility(GONE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {
    }

    public static class GetMessage extends AsyncTaskLoader<ArrayList<String>> {
        private String user, part, message;
        private boolean train;

        public GetMessage(Context context, String user, String part, boolean train, String message) {
            super(context);
            this.user = user;
            this.part = part;
            this.train = train;
            this.message = message;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public ArrayList<String> loadInBackground() {
            return GetTrainResponse.getMessage(user, part, train, message);
        }
    }

    public static class ChangeMessage extends AsyncTaskLoader<ArrayList<String>> {

        private String user, prevblob, currblob, trainblob, part;

        public ChangeMessage(Context context, String user, String part, String prevblob, String currblob, String trainblob) {
            super(context);
            this.user = user;
            this.prevblob = prevblob;
            this.currblob = currblob;
            this.trainblob = trainblob;
            this.part = part;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public ArrayList<String> loadInBackground() {
            GetTrainResponse.changeMessage(user, part, prevblob, currblob, trainblob);
            return new ArrayList<>();
        }

    }
}
