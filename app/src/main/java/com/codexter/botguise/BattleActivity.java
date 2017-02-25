package com.codexter.botguise;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.view.View.GONE;

public class BattleActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String>> {

    private static int score_user;
    private static int score_opponent;
    private static String PACK;
    private static boolean MODE_CREATE;
    private final int GET_MESSAGE_LOADER_ID = 1;
    private final int CHANGE_MESSAGE_LOADER_ID = 2;
    private final int BOT = 1;
    private final int USER = 2;
    public String mCurrentReference;
    private String[] three_letter_words = {"aah", "aal", "aas", "aba", "abo", "abs", "aby", "ace", "act", "add", "ado", "ads", "adz", "aff", "aft", "aga", "age", "ago", "ags", "aha", "ahi", "ahs", "aid", "ail", "aim", "ain", "air", "ais", "ait", "ala", "alb", "ale", "all", "alp", "als", "alt", "ama", "ami", "amp", "amu", "ana", "and", "ane", "ani", "ant", "any", "ape", "apo", "app", "apt", "arb", "arc", "are", "arf", "ark", "arm", "ars", "art", "ash", "ask", "asp", "ass", "ate", "att", "auk", "ava", "ave", "avo", "awa", "awe", "awl", "awn", "axe", "aye", "ays", "azo", "baa", "bad", "bag", "bah", "bal", "bam", "ban", "bap", "bar", "bas", "bat", "bay", "bed", "bee", "beg", "bel", "ben", "bes", "bet", "bey", "bib", "bid", "big", "bin", "bio", "bis", "bit", "biz", "boa", "bob", "bod", "bog", "boo", "bop", "bos", "bot", "bow", "box", "boy", "bra", "bro", "brr", "bub", "bud", "bug", "bum", "bun", "bur", "bus", "but", "buy", "bye", "bys", "cab", "cad", "cam", "can", "cap", "car", "cat", "caw", "cay", "cee", "cel", "cep", "chi", "cig", "cis", "cob", "cod", "cog", "col", "con", "coo", "cop", "cor", "cos", "cot", "cow", "cox", "coy", "coz", "cru", "cry", "cub", "cud", "cue", "cum", "cup", "cur", "cut", "cwm", "dab", "dad", "dag", "dah", "dak", "dal", "dam", "dan", "dap", "daw", "day", "deb", "dee", "def", "del", "den", "dev", "dew", "dex", "dey", "dib", "did", "die", "dif", "dig", "dim", "din", "dip", "dis", "dit", "doc", "doe", "dog", "dol", "dom", "don", "dor", "dos", "dot", "dow", "dry", "dub", "dud", "due", "dug", "duh", "dui", "dun", "duo", "dup", "dye", "ear", "eat", "eau", "ebb", "ecu", "edh", "eds", "eek", "eel", "eff", "efs", "eft", "egg", "ego", "eke", "eld", "elf", "elk", "ell", "elm", "els", "eme", "ems", "emu", "end", "eng", "ens", "eon", "era", "ere", "erg", "ern", "err", "ers", "ess", "eta", "eth", "eve", "ewe", "eye", "fab", "fad", "fag", "fan", "far", "fas", "fat", "fax", "fay", "fed", "fee", "feh", "fem", "fen", "fer", "fes", "fet", "feu", "few", "fey", "fez", "fib", "fid", "fie", "fig", "fil", "fin", "fir", "fit", "fix", "fiz", "flu", "fly", "fob", "foe", "fog", "foh", "fon", "fop", "for", "fou", "fox", "foy", "fro", "fry", "fub", "fud", "fug", "fun", "fur", "gab", "gad", "gae", "gag", "gal", "gam", "gan", "gap", "gar", "gas", "gat", "gay", "ged", "gee", "gel", "gem", "gen", "get", "gey", "ghi", "gib", "gid", "gie", "gig", "gin", "gip", "git", "gnu", "goa", "gob", "god", "goo", "gor", "gos", "got", "gox", "goy", "gul", "gum", "gun", "gut", "guv", "guy", "gym", "gyp", "had", "hae", "hag", "hah", "haj", "ham", "hao", "hap", "has", "hat", "haw", "hay", "heh", "hem", "hen", "hep", "her", "hes", "het", "hew", "hex", "hey", "hic", "hid", "hie", "him", "hin", "hip", "his", "hit", "hmm", "hob", "hod", "hoe", "hog", "hon", "hop", "hos", "hot", "how", "hoy", "hub", "hue", "hug", "huh", "hum", "hun", "hup", "hut", "hyp", "ice", "ich", "ick", "icy", "ids", "iff", "ifs", "igg", "ilk", "ill", "imp", "ink", "inn", "ins", "ion", "ire", "irk", "ism", "its", "ivy", "jab", "jag", "jam", "jar", "jaw", "jay", "jee", "jet", "jeu", "jew", "jib", "jig", "jin", "job", "joe", "jog", "jot", "jow", "joy", "jug", "jun", "jus", "jut", "kab", "kae", "kaf", "kas", "kat", "kay", "kea", "kef", "keg", "ken", "kep", "kex", "key", "khi", "kid", "kif", "kin", "kip", "kir", "kis", "kit", "koa", "kob", "koi", "kop", "kor", "kos", "kue", "kye", "lab", "lac", "lad", "lag", "lam", "lap", "lar", "las", "lat", "lav", "law", "lax", "lay", "lea", "led", "lee", "leg", "lei", "lek", "les", "let", "leu", "lev", "lex", "ley", "lez", "lib", "lid", "lie", "lin", "lip", "lis", "lit", "lob", "log", "loo", "lop", "lot", "low", "lox", "lug", "lum", "luv", "lux", "lye", "mac", "mad", "mae", "mag", "man", "map", "mar", "mas", "mat", "maw", "max", "may", "med", "meg", "mel", "mem", "men", "met", "mew", "mho", "mib", "mic", "mid", "mig", "mil", "mim", "mir", "mis", "mix", "moa", "mob", "moc", "mod", "mog", "mol", "mom", "mon", "moo", "mop", "mor", "mos", "mot", "mow", "mud", "mug", "mum", "mun", "mus", "mut", "myc", "nab", "nae", "nag", "nah", "nam", "nan", "nap", "naw", "nay", "neb", "nee", "neg", "net", "new", "nib", "nil", "nim", "nip", "nit", "nix", "nob", "nod", "nog", "noh", "nom", "noo", "nor", "nos", "not", "now", "nth", "nub", "nun", "nus", "nut", "oaf", "oak", "oar", "oat", "oba", "obe", "obi", "oca", "oda", "odd", "ode", "ods", "oes", "off", "oft", "ohm", "oho", "ohs", "oil", "oka", "oke", "old", "ole", "oms", "one", "ono", "ons", "ooh", "oot", "ope", "ops", "opt", "ora", "orb", "orc", "ore", "ors", "ort", "ose", "oud", "our", "out", "ova", "owe", "owl", "own", "oxo", "oxy", "pac", "pad", "pah", "pal", "pam", "pan", "pap", "par", "pas", "pat", "paw", "pax", "pay", "pea", "pec", "ped", "pee", "peg", "peh", "pen", "pep", "per", "pes", "pet", "pew", "phi", "pht", "pia", "pic", "pie", "pig", "pin", "pip", "pis", "pit", "piu", "pix", "ply", "pod", "poh", "poi", "pol", "pom", "poo", "pop", "pot", "pow", "pox", "pro", "pry", "psi", "pst", "pub", "pud", "pug", "pul", "pun", "pup", "pur", "pus", "put", "pya", "pye", "pyx", "qat", "qis", "qua", "rad", "rag", "rah", "rai", "raj", "ram", "ran", "rap", "ras", "rat", "raw", "rax", "ray", "reb", "rec", "red", "ree", "ref", "reg", "rei", "rem", "rep", "res", "ret", "rev", "rex", "rho", "ria", "rib", "rid", "rif", "rig", "rim", "rin", "rip", "rob", "roc", "rod", "roe", "rom", "rot", "row", "rub", "rue", "rug", "rum", "run", "rut", "rya", "rye", "sab", "sac", "sad", "sae", "sag", "sal", "sap", "sat", "sau", "saw", "sax", "say", "sea", "sec", "see", "seg", "sei", "sel", "sen", "ser", "set", "sew", "sex", "sha", "she", "shh", "shy", "sib", "sic", "sim", "sin", "sip", "sir", "sis", "sit", "six", "ska", "ski", "sky", "sly", "sob", "sod", "sol", "som", "son", "sop", "sos", "sot", "sou", "sow", "sox", "soy", "spa", "spy", "sri", "sty", "sub", "sue", "suk", "sum", "sun", "sup", "suq", "syn", "tab", "tad", "tae", "tag", "taj", "tam", "tan", "tao", "tap", "tar", "tas", "tat", "tau", "tav", "taw", "tax", "tea", "ted", "tee", "teg", "tel", "ten", "tet", "tew", "the", "tho", "thy", "tic", "tie", "til", "tin", "tip", "tis", "tit", "tod", "toe", "tog", "tom", "ton", "too", "top", "tor", "tot", "tow", "toy", "try", "tsk", "tub", "tug", "tui", "tun", "tup", "tut", "tux", "twa", "two", "tye", "udo", "ugh", "uke", "ulu", "umm", "ump", "uns", "upo", "ups", "urb", "urd", "urn", "urp", "use", "uta", "ute", "uts", "vac", "van", "var", "vas", "vat", "vau", "vav", "vaw", "vee", "veg", "vet", "vex", "via", "vid", "vie", "vig", "vim", "vis", "voe", "vow", "vox", "vug", "vum", "wab", "wad", "wae", "wag", "wan", "wap", "war", "was", "wat", "waw", "wax", "way", "web", "wed", "wee", "wen", "wet", "wha", "who", "why", "wig", "win", "wis", "wit", "wiz", "woe", "wog", "wok", "won", "woo", "wop", "wos", "wot", "wow", "wry", "wud", "wye", "wyn", "xis", "yag", "yah", "yak", "yam", "yap", "yar", "yaw", "yay", "yea", "yeh", "yen", "yep", "yes", "yet", "yew", "yid", "yin", "yip", "yob", "yod", "yok", "yom", "yon", "you", "yow", "yuk", "yum", "yup", "zag"

    };
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mBattleReference = null;
    private DatabaseReference mScoreReference = null;
    private ChildEventListener mBattleChildListener = null;
    private ChildEventListener mScoreChildListener = null;
    private String GAME_ID = "PlaceHolder";
    private ArrayList<ChatMessage> messageArrayList = new ArrayList<>();
    private ChatMessageAdaptor mAdaptor;
    private String mUsername;
    private String mUserId;

    private ProgressBar battleLoadingProgressBar;
    private EditText messageToSend;
    private Button sendMessage;

    private String mMessage;
    private LinearLayout battleAddMessageBar;
    private RadioGroup respond_radio_btns;
    private TextView user_score_textview, opponent_score_textview;

    //TODO assign prevblob and trainblob
    private String prevblob;
    private String trainblob;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {

        }

        PACK = getIntent().getStringExtra("PACK");
        MODE_CREATE = getIntent().getBooleanExtra("MODE_CREATE", true);
        if (MODE_CREATE) {
            GAME_ID = three_letter_words[new Random().nextInt(999)] + " " + three_letter_words[new Random().nextInt(999)];
            showCreateGameDialog();
        } else {
            showJoinGameDialog();
        }

        mAuth = FirebaseAuth.getInstance();
        mUserId = mAuth.getCurrentUser().getUid();
        mUsername = mAuth.getCurrentUser().getDisplayName();

        ListView listView = (ListView) findViewById(R.id.battleChatList);
        listView.setEmptyView(findViewById(R.id.battle_empty_view));
        mAdaptor = new ChatMessageAdaptor(getApplication(), messageArrayList, mUsername, BattleActivity.this);
        listView.setAdapter(mAdaptor);

        battleLoadingProgressBar = (ProgressBar) findViewById(R.id.battleLoadingProgressBar);
        messageToSend = (EditText) findViewById(R.id.battleMessageToSend);
        sendMessage = (Button) findViewById(R.id.battleSendMessage);
        battleAddMessageBar = (LinearLayout) findViewById(R.id.battleAddMessageBar);
        respond_radio_btns = (RadioGroup) findViewById(R.id.respond_radio_btns);
        user_score_textview = (TextView) findViewById(R.id.user_score);
        opponent_score_textview = (TextView) findViewById(R.id.opponent_score);

        messageToSend.setFocusable(false);
        sendMessage.setEnabled(false);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessage = messageToSend.getText().toString().trim();
                if (!TextUtils.isEmpty(mMessage)) {
                    mCurrentReference = mBattleReference.push().getKey();
                    Map<String, Object> current = new HashMap<>();
                    current.put("current", mCurrentReference);
                    mBattleReference.updateChildren(current);
                    mBattleReference.child(mCurrentReference).setValue(new ChatMessage(mMessage, mUsername, USER));
                }
                messageToSend.setText("");
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(messageToSend.getWindowToken(), 0);
                messageToSend.setFocusable(false);
                sendMessage.setEnabled(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBattleReference != null && mBattleChildListener != null) {
            mBattleReference.addChildEventListener(mBattleChildListener);
        }
        if (mScoreReference != null && mScoreChildListener != null) {
            mScoreReference.addChildEventListener(mScoreChildListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBattleReference != null && mBattleChildListener != null) {
            mBattleReference.removeEventListener(mBattleChildListener);
        }
        if (mScoreReference != null && mScoreChildListener != null) {
            mScoreReference.removeEventListener(mScoreChildListener);
        }
    }

    private void showCreateGameDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Give this ID to your friend:");
        TextView message = new TextView(this);
        message.setTextSize(60);
        message.setText(GAME_ID);
        message.setGravity(Gravity.CENTER);
        Typeface typeface = Typeface.createFromAsset(BattleActivity.this.getAssets(), "fonts/Raleway-Black.ttf");
        message.setTypeface(typeface);
        builder.setView(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                initFirebase();
                score_user = 0;
                score_opponent = 0;
                updateScore();
                userResponds();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showJoinGameDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText editText = new EditText(this);
        Typeface typeface = Typeface.createFromAsset(BattleActivity.this.getAssets(), "fonts/Raleway-Black.ttf");
        editText.setTypeface(typeface);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setGravity(Gravity.CENTER);
        editText.setTextColor(Color.parseColor("#656565"));
        editText.setTextSize(60);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        builder.setMessage("Enter the ID given by your friend");
        builder.setView(editText);
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    editText.setError("Enter a Game Id to start the game");
                } else {
                    GAME_ID = editText.getText().toString().trim().toLowerCase();
                    dialog.dismiss();
                    initFirebase();
                    score_opponent = 0;
                    score_user = 0;
                    updateScore();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(BattleActivity.this, ChooseMode.class);
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

    private void initFirebase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mBattleReference = mFirebaseDatabase.getReference().child("Battles").child(GAME_ID);
        mScoreReference = mBattleReference.child("Score");

        Map<String, Object> current = new HashMap<>();
        current.put("current", "PlaceHolder");
        mBattleReference.updateChildren(current);

        mBattleChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("current")) {
                    mCurrentReference = dataSnapshot.getValue().toString();
                    Log.v("current", mCurrentReference);
                } else if (!dataSnapshot.getKey().equals("Score")) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    mAdaptor.add(chatMessage);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("current")) {
                    mCurrentReference = dataSnapshot.getValue().toString();
                    Log.v("current", mCurrentReference);
                } else if (!dataSnapshot.getKey().equals("Score")) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    for (DataSnapshot data : dataSnapshot.child(mCurrentReference).getChildren()) {
                        if (data.getKey().equals("guess")) {
                            mAdaptor.getItem(mAdaptor.getCount() - 1).setGuess(Integer.valueOf(data.getValue().toString()));
                            mAdaptor.notifyDataSetChanged();
                        }
                    }
                }
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
        mBattleReference.addChildEventListener(mBattleChildListener);
        mScoreChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getKey().equals("p1")) {
                    if (MODE_CREATE) {
                        score_user = Integer.valueOf(dataSnapshot.getValue().toString());
                    } else {
                        score_opponent = Integer.valueOf(dataSnapshot.getValue().toString());
                    }
                } else if (dataSnapshot.getKey().equals("p2")) {
                    if (!MODE_CREATE) {
                        score_user = Integer.valueOf(dataSnapshot.getValue().toString());
                    } else {
                        score_opponent = Integer.valueOf(dataSnapshot.getValue().toString());
                    }
                }
                user_score_textview.setText(String.valueOf(score_user));
                opponent_score_textview.setText(String.valueOf(score_opponent));
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
        mScoreReference.addChildEventListener(mScoreChildListener);
    }

    public void userResponds() {
        messageToSend.setFocusable(false);
        sendMessage.setEnabled(false);
        battleAddMessageBar.setVisibility(View.GONE);
        respond_radio_btns.setVisibility(View.VISIBLE);
        respond_radio_btns.check(-1);
        for (int i = 0; i < respond_radio_btns.getChildCount(); i++) {
            respond_radio_btns.getChildAt(i).setEnabled(true);
        }

        respond_radio_btns.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < respond_radio_btns.getChildCount(); i++) {
                    respond_radio_btns.getChildAt(i).setEnabled(false);
                }
                if (checkedId == R.id.respond_user_radio_btn) {
                    respond(true);
                } else {
                    respond(false);
                }
            }
        });
    }

    private void respond(boolean userResponds) {
        respond_radio_btns.setOnCheckedChangeListener(null);
        if (userResponds) {
            battleAddMessageBar.setVisibility(View.VISIBLE);
            respond_radio_btns.setVisibility(View.GONE);
            messageToSend.setFocusable(true);
            sendMessage.setEnabled(true);
            Toast.makeText(this, "User responds", Toast.LENGTH_SHORT).show();
            messageToSend.setFocusable(true);
            messageToSend.setFocusableInTouchMode(true);
            sendMessage.setEnabled(true);
        } else {
            Toast.makeText(this, "Bot responds", Toast.LENGTH_SHORT).show();
            try {
                mMessage = mAdaptor.getItem(mAdaptor.getCount() - 1).getMessage();
            } catch (IndexOutOfBoundsException e) {
                mMessage = getString(R.string.empty_prev_response_for_bot);
            }
            if (getLoaderManager().getLoader(GET_MESSAGE_LOADER_ID) == null) {
                getLoaderManager().initLoader(GET_MESSAGE_LOADER_ID, null, this);
            } else {
                getLoaderManager().restartLoader(GET_MESSAGE_LOADER_ID, null, this);
            }
        }
    }

    public void changescore(int actual, int guess) {
        Map<String, Object> updated_guess = new HashMap<>();
        updated_guess.put("guess", guess);
        mBattleReference.child(mCurrentReference).updateChildren(updated_guess);
        if (actual == USER) {
            if (guess == USER) {
                score_user += 2;
            } else if (guess == BOT) {
                score_opponent += 3;
            }
        } else if (actual == BOT) {
            if (guess == USER) {
                score_opponent += 5;
            } else if (guess == BOT) {
                score_user += 2;
            }
        }
        updateScore();
        userResponds();
    }

    private void updateScore() {
        Map<String, Object> updated_score = new HashMap<>();
        if (MODE_CREATE) {
            updated_score.put("p1", score_user);
            updated_score.put("p2", score_opponent);
        } else {
            updated_score.put("p2", score_user);
            updated_score.put("p1", score_opponent);
        }
        mScoreReference.setValue(updated_score);
    }

    @Override
    public Loader<ArrayList<String>> onCreateLoader(int id, Bundle args) {
        battleLoadingProgressBar.setVisibility(View.VISIBLE);
        switch (id) {
            case GET_MESSAGE_LOADER_ID:
                return new GetMessage(getApplicationContext(), mUserId, PACK, false, mMessage);
            case CHANGE_MESSAGE_LOADER_ID:
                return new ChangeMessage(getApplicationContext(), mUserId, PACK, prevblob, trainblob);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        if (loader.getId() == GET_MESSAGE_LOADER_ID) {
            for (int i = 0; i < data.size(); i++) {
                mCurrentReference = mBattleReference.push().getKey();
                Map<String, Object> current = new HashMap<>();
                current.put("current", mCurrentReference);
                mBattleReference.updateChildren(current);
                mBattleReference.child(mCurrentReference).setValue(new ChatMessage(data.get(i), mUsername, BOT));
            }
        } else if (loader.getId() == CHANGE_MESSAGE_LOADER_ID) {

        }
        battleLoadingProgressBar.setVisibility(GONE);
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
            return GetBattleResponseNew.getMessage(user, part, train, message);
        }
    }

    public static class ChangeMessage extends AsyncTaskLoader<ArrayList<String>> {

        private String user, prevblob, trainblob, part;

        public ChangeMessage(Context context, String user, String part, String prevblob, String trainblob) {
            super(context);
            this.user = user;
            this.prevblob = prevblob;
            this.trainblob = trainblob;
            this.part = part;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public ArrayList<String> loadInBackground() {
            GetBattleResponseNew.changeMessage(user, part, prevblob, trainblob);
            return new ArrayList<>();
        }

    }
}
