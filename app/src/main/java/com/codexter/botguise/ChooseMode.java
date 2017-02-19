package com.codexter.botguise;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ChooseMode extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);

        username = getIntent().getStringExtra("name");
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

    private void showUnderConstructionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("This page is under construction");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "You have been logged out successully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChooseMode.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
