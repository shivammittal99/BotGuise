package com.codexter.botguise;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

    private String PACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);

        PACK = getIntent().getStringExtra("PACK");

        try {
            if (PACK.equals(getString(R.string.pack01))) {
                Glide.with(this).load(R.drawable.marvel_dc_back).into((ImageView) findViewById(R.id.backdrop));
            } else if (PACK.equals(getString(R.string.pack02))) {
                Glide.with(this).load(R.drawable.harry_potter_back).into((ImageView) findViewById(R.id.backdrop));
            } else if (PACK.equals(getString(R.string.pack03))) {
                Glide.with(this).load(R.drawable.pack03).into((ImageView) findViewById(R.id.backdrop));
            } else if (PACK.equals(getString(R.string.pack04))) {
                Glide.with(this).load(R.drawable.friends_back).into((ImageView) findViewById(R.id.backdrop));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        final Button fight = (Button) findViewById(R.id.fightButton);
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
                train.putExtra("userId", mFirebaseUser.getUid());
                train.putExtra("PACK", PACK);
                finish();
                startActivity(train);
            }
        });

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                if (userDetails.getUsername() == mAuth.getCurrentUser().getDisplayName()) {
                    if (userDetails.getIntroductionStatus()) {
                        mUserDetails = dataSnapshot.getValue(UserDetails.class);
                    } else if (getIntent().getBooleanExtra("completedIntroduction", false)) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("introductionStatus", true);
                        mUserDatabaseReference.updateChildren(updates);
                    } else {
                        Intent intent = new Intent(ChooseMode.this, Introduction.class);
                        intent.putExtra("name", mFirebaseUser.getDisplayName());
                        intent.putExtra("PACK", PACK);
                        finish();
                        Toast.makeText(ChooseMode.this, "Please complete Introduction to continue.", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                if (userDetails.getUsername() == mAuth.getCurrentUser().getDisplayName()) {
                    if (userDetails.getIntroductionStatus()) {
                        mUserDetails = dataSnapshot.getValue(UserDetails.class);
                    } else if (getIntent().getBooleanExtra("completedIntroduction", false)) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("introductionStatus", true);
                        mUserDatabaseReference.updateChildren(updates);
                    } else {
                        Intent intent = new Intent(ChooseMode.this, Introduction.class);
                        intent.putExtra("name", mFirebaseUser.getDisplayName());
                        intent.putExtra("PACK", PACK);
                        finish();
                        Toast.makeText(ChooseMode.this, "Please complete Introduction to continue.", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
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

        findViewById(R.id.back_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_intent = new Intent(ChooseMode.this, PackSelectActivity.class);
                back_intent.putExtra("name", mFirebaseUser.getDisplayName());
                back_intent.putExtra("email", mFirebaseUser.getEmail());
                back_intent.putExtra("uid", mFirebaseUser.getUid());
                back_intent.putExtra("photoUrl", mFirebaseUser.getPhotoUrl());
                finish();
                startActivity(back_intent);
            }
        });
    }

    private void showCreateJoinGameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Create a new game or Join a game");
        builder.setNegativeButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(ChooseMode.this, BattleActivity.class);
                intent.putExtra("MODE_CREATE", true);
                intent.putExtra("PACK", PACK);
                finish();
                startActivity(intent);
            }
        });

        builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(ChooseMode.this, BattleActivity.class);
                intent.putExtra("MODE_CREATE", false);
                intent.putExtra("PACK", PACK);
                finish();
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent back_intent = new Intent(ChooseMode.this, PackSelectActivity.class);
        back_intent.putExtra("name", mFirebaseUser.getDisplayName());
        back_intent.putExtra("email", mFirebaseUser.getEmail());
        back_intent.putExtra("uid", mFirebaseUser.getUid());
        back_intent.putExtra("photoUrl", mFirebaseUser.getPhotoUrl());
        finish();
        startActivity(back_intent);
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