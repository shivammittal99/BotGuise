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
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmailField, mPasswordField, mNameField;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        mNameField = (EditText) findViewById(R.id.field_name);

        findViewById(R.id.verify_email_button).setOnClickListener(this);
        findViewById(R.id.create_account_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.continue_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setEnabled(false);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (!user.isEmailVerified()) {
                        Toast.makeText(SignUpActivity.this, "Account created successfully! Verify email to continue.", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.verify_email_button).setEnabled(true);
                        findViewById(R.id.continue_button).setVisibility(View.VISIBLE);
                        findViewById(R.id.continue_button).setEnabled(false);
                        findViewById(R.id.create_account_button).setVisibility(View.GONE);

                    } else {
                        findViewById(R.id.continue_button).setEnabled(true);
                    }
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.continue_button).getVisibility() == View.VISIBLE) {
            mAuth.signOut();
        }
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (findViewById(R.id.continue_button).getVisibility() == View.VISIBLE) {
                    mAuth.signOut();
                }
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

    private void createAccount(final String name, String email, String password) {
        if (!validForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "1.Account creation unsuccessful. Try again...",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            final FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "2.Account creation unsuccessful. Try again...", Toast.LENGTH_SHORT).show();
                                                user.delete();
                                            }
                                        }
                                    });
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void sendEmailVerification() {
        findViewById(R.id.verify_email_button).setEnabled(false);

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                findViewById(R.id.verify_email_button).setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this,
                            "Verification email sent to " + user.getEmail(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("EmailAuthActivity", "sendEmailVerification", task.getException());
                    Toast.makeText(SignUpActivity.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validForm() {
        boolean valid = true;
        String name = mNameField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mNameField.setError("Required.");
            valid = false;
        } else {
            mNameField.setError(null);
        }

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
        if (i == R.id.create_account_button) {
            createAccount(mNameField.getText().toString(), mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.login_button) {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();
        } else if (i == R.id.continue_button) {
            FirebaseUser user = mAuth.getCurrentUser();
            String name = user.getDisplayName();
            String email = user.getEmail();
            String uid = user.getUid();
            Uri photoUrl = user.getPhotoUrl();
            Intent intent;
            intent = new Intent(SignUpActivity.this, Introduction.class);
            Toast.makeText(SignUpActivity.this, "You are signed in as " + email, Toast.LENGTH_SHORT).show();
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("uid", uid);
            intent.putExtra("photoUrl", photoUrl);
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
