package com.codexter.botguise;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailAuthActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean isFirstTime = false;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmailField, mPasswordField;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_auth);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);

        findViewById(R.id.go_back_button).setOnClickListener(this);
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);
        findViewById(R.id.continue_after_verification_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (!user.isEmailVerified()) {
                        findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
                        findViewById(R.id.email_password_fields).setVisibility(View.GONE);
                        findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
                        findViewById(R.id.verify_email_button).setEnabled(true);
                    } else {
                        String name = user.getDisplayName();
                        String email = user.getEmail();
                        String uid = user.getUid();
                        Uri photoUrl = user.getPhotoUrl();
                        Intent intent;
                        if (isFirstTime) {
                            intent = new Intent(EmailAuthActivity.this, Introduction.class);
                        } else {
                            intent = new Intent(EmailAuthActivity.this, ChooseMode.class);
                        }
                        Toast.makeText(EmailAuthActivity.this, "You are signed in as " + email, Toast.LENGTH_SHORT).show();
                        intent.putExtra("name", email);
                        intent.putExtra("email", email);
                        intent.putExtra("uid", uid);
                        intent.putExtra("photoUrl", photoUrl);
                        startActivity(intent);
                    }
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.email_password_fields).getVisibility() == View.VISIBLE) {
            mAuth.signOut();
            finish();
            super.onBackPressed();
        } else {
            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
            findViewById(R.id.verify_email_button).setEnabled(false);
            mAuth.signOut();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mAuth.signOut();
                finish();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        hideProgressDialog();
    }

    private void signIn(String email, String password) {
        if (!validForm())
            return;
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(EmailAuthActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        } else {
                            isFirstTime = false;
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void continue_after_verification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (!user.isEmailVerified()) {
                findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
                findViewById(R.id.email_password_fields).setVisibility(View.GONE);
                findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
                findViewById(R.id.verify_email_button).setEnabled(true);
            } else {
                String name = user.getDisplayName();
                String email = user.getEmail();
                String uid = user.getUid();
                Uri photoUrl = user.getPhotoUrl();
                Intent intent = new Intent(EmailAuthActivity.this, Introduction.class);
                Toast.makeText(EmailAuthActivity.this, "You are signed in as " + email, Toast.LENGTH_SHORT).show();
                intent.putExtra("name", email);
                intent.putExtra("email", email);
                intent.putExtra("uid", uid);
                intent.putExtra("photoUrl", photoUrl);
                startActivity(intent);
            }
        }
    }

    private void sendEmailVerification() {
        findViewById(R.id.verify_email_button).setEnabled(false);

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                findViewById(R.id.verify_email_button).setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(EmailAuthActivity.this,
                            "Verification email sent to " + user.getEmail(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("EmailAuthActivity", "sendEmailVerification", task.getException());
                    Toast.makeText(EmailAuthActivity.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validForm() {
        boolean valid = true;
        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Invalid Email.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Invalid Password.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.continue_after_verification_button) {
            continue_after_verification();
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();
        } else if (i == R.id.go_back_button) {
            Intent intent = new Intent(EmailAuthActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
