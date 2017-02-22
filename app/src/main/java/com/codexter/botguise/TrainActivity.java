package com.codexter.botguise;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
    private static String mUsername;
    private static TrainMessageAdaptor mAdaptor;
    private EditText trainMessageToSend;
    private ProgressBar trainLoadingProgressBar;
    private ArrayList<TrainMessage> mMessages = new ArrayList<>();
    private String mMessage;
    private String statement1;
    private String statement2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        mUsername = getIntent().getStringExtra("username");

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
                mMessage = trainMessageToSend.getText().toString();
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
                        statement1 = previous.getMessage();
                        statement2 = changedResponse;
                        if (getLoaderManager().getLoader(CHANGE_MESSAGE_LOADER_ID) == null) {
                            getLoaderManager().initLoader(CHANGE_MESSAGE_LOADER_ID, null, TrainActivity.this);
                        } else {
                            getLoaderManager().restartLoader(CHANGE_MESSAGE_LOADER_ID, null, TrainActivity.this);
                        }
                        satisfied_radio.setChecked(true);
                        TrainMessage change = mAdaptor.getItem(index);
                        change.setMessage(statement2);
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

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
        trainLoadingProgressBar.setVisibility(View.VISIBLE);
        switch (id) {
            case GET_MESSAGE_LOADER_ID:
                return new GetMessage(getApplicationContext(), mUsername, getString(R.string.pack01), true, mMessage);
            case CHANGE_MESSAGE_LOADER_ID:
                return new ChangeMessage(getApplicationContext(), mUsername, getString(R.string.pack01), statement1, statement2);
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
        private String from, category, message;
        private boolean train;

        public GetMessage(Context context, String from, String category, boolean train, String message) {
            super(context);
            this.from = from;
            this.category = category;
            this.train = train;
            this.message = message;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public ArrayList<String> loadInBackground() {
            return GetTrainResponse.getMessage(from, null, category, train, message);
        }
    }

    public static class ChangeMessage extends AsyncTaskLoader<ArrayList<String>> {

        private String from, statement1, statement2, category;

        public ChangeMessage(Context context, String from, String category, String statement1, String statement2) {
            super(context);
            this.from = from;
            this.statement1 = statement1;
            this.statement2 = statement2;
            this.category = category;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public ArrayList<String> loadInBackground() {
            GetTrainResponse.changeMessage(from, null, category, statement1, statement2);
            return new ArrayList<>();
        }

    }
}
