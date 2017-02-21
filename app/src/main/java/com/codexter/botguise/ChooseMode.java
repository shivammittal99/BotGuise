package com.codexter.botguise;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ChooseMode extends AppCompatActivity {

    private String username;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private UserDetails mUserDetails = new UserDetails();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        mUserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("introductionStatus") != null) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("username", mFirebaseUser.getDisplayName());
                    updates.put("email", mFirebaseUser.getEmail());
                    mUserDatabaseReference.updateChildren(updates);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        username = mUserDetails.getUsername();

        Button fight = (Button) findViewById(R.id.fightButton);
        Button train = (Button) findViewById(R.id.trainButton);

        fight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateJoinGameDialog();
            }
        });

        train.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent train = new Intent(ChooseMode.this, TrainActivity.class);
                train.putExtra("username", username);
                startActivity(train);
            }
        });

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.v("created", dataSnapshot.toString());
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                if (userDetails.getIntroductionStatus()) {
                    mUserDetails = dataSnapshot.getValue(UserDetails.class);
                } else if (getIntent().getBooleanExtra("completedIntroduction", false)) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("introductionStatus", true);
                    mUserDatabaseReference.updateChildren(updates);
                } else {
                    Intent intent = new Intent(ChooseMode.this, Introduction.class);
                    intent.putExtra("name", mFirebaseUser.getDisplayName());
                    Toast.makeText(ChooseMode.this, "Please complete Introduction to continue.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.v("updated", dataSnapshot.toString());
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                if (userDetails.getIntroductionStatus()) {
                    mUserDetails = dataSnapshot.getValue(UserDetails.class);
                } else if (getIntent().getBooleanExtra("completedIntroduction", false)) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("introductionStatus", true);
                    mUserDatabaseReference.updateChildren(updates);
                } else {
                    Intent intent = new Intent(ChooseMode.this, Introduction.class);
                    intent.putExtra("name", mFirebaseUser.getDisplayName());
                    Toast.makeText(ChooseMode.this, "Please complete Introduction to continue.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
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
    }

    private void showCreateJoinGameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Create a new game or Join a game");
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent battle = new Intent(ChooseMode.this, BattleActivity.class);
                battle.putExtra("username", username);
                battle.putExtra("mode", 1);
                startActivity(battle);
            }
        });

        builder.setNegativeButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent battle = new Intent(ChooseMode.this, BattleActivity.class);
                battle.putExtra("username", username);
                battle.putExtra("mode", 2);
                startActivity(battle);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.choose_mode_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                mAuth.signOut();
                Toast.makeText(this, "You have been logged out successully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChooseMode.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserDatabaseReference.getParent().addChildEventListener(mChildEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUserDatabaseReference.getParent().removeEventListener(mChildEventListener);
    }
}