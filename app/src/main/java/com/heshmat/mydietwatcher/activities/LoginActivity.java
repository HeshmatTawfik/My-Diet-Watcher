package com.heshmat.mydietwatcher.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.heshmat.mydietwatcher.LoadingDialog;
import com.heshmat.mydietwatcher.MainActivity;
import com.heshmat.mydietwatcher.R;
import com.heshmat.mydietwatcher.models.User;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = "LOGIN_ACTIVITY";
    public static GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;
    LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getActionBar() != null)
            getActionBar().hide();
        setContentView(R.layout.activity_login);
        loadingDialog=new LoadingDialog(LoginActivity.this);
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton mFacebookSignInButton = (LoginButton) findViewById(R.id.fbLoginButton);
        mFacebookSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.startLoadingDialog();
            }
        });
        mFacebookSignInButton.setPermissions("email", "public_profile");
        mFacebookSignInButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        SignInButton mGoogleSignInButton = findViewById(R.id.googleLoginButton);
        mGoogleSignInButton.setOnClickListener(this);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    new Authentication().execute(user);


                } else {

                }
            }
        };


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.googleLoginButton:
                signIn();
                loadingDialog.startLoadingDialog();
                break;
            default:
                return;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                        } else {

                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                            Disconnect_google(LoginActivity.this, LoginActivity.this);
                        }
                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {


                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result != null && result.isSuccess()) {
                firebaseAuthWithGoogle(Objects.requireNonNull(result.getSignInAccount()));
            } else {
                Log.i("google sign in fail", "onActivityResult: " + Objects.requireNonNull(result.getStatus().getStatusMessage()) + " +" + result.getStatus().getStatusCode());
                // Google Sign In failed
            }
        }
    }


    private void signIn() {
        Intent signInIntent = mGoogleApiClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public class Authentication extends AsyncTask<FirebaseUser, Void, Void> {

        FirebaseUser user;

        @Override
        protected Void doInBackground(FirebaseUser... users) {
            user = users[0];


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (user != null) {
                User.currentUser.setId(user.getUid());
                User.currentUser.setName(user.getDisplayName());
                if (user.getPhotoUrl() != null) {
                    String uri = user.getPhotoUrl().toString() + "?height=200";
                    User.currentUser.setImgUrl(uri);
                }
                loadingDialog.dismissDialog();

                User.userRedirect(LoginActivity.this);
            }
            super.onPostExecute(aVoid);
        }
    }

    public static void Disconnect_google(Activity activity, Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleApiClient = GoogleSignIn.getClient(context, gso);

        try {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.signOut().addOnCompleteListener(activity,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseAuth.getInstance().signOut();
                                LoginManager.getInstance().logOut();
                                activity.startActivity(new Intent(activity, MainActivity.class));
                                activity.finish();

                            }
                        });
                mGoogleApiClient.revokeAccess().addOnCompleteListener(activity,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
            }
        } catch (Exception e) {
            Log.i(TAG, "Disconnect_google: " + e.toString());
        }
    }
}
