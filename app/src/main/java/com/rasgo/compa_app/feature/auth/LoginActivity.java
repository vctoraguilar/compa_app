package com.rasgo.compa_app.feature.auth;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.rasgo.compa_app.R;
import com.rasgo.compa_app.utils.ViewModelFactory;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private static final String TAG = "LoginActivity";
    private static int REQ_ONE_TAP = 2;
    private boolean showOneTapUi = true;
    private SignInButton signInButton;
    private LoginViewModel viewModel;
    private ActivityResultLauncher<IntentSenderRequest> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel =new ViewModelProvider(this,new ViewModelFactory()).get(LoginViewModel.class);

        mAuth = FirebaseAuth.getInstance();
        oneTapClient = Identity.getSignInClient(this);
        signInButton = findViewById(R.id.btn_signin);
        registerLauncher();

        signInButton.setOnClickListener(v -> {
            Log.d(TAG, "Sign in button clicked");
            initiateSignIn();
        });
    }

    private void registerLauncher() {
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            Log.d(TAG, "ActivityResult: " + result.getResultCode());
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                handleSignInResult(result.getData());
            }
        });
    }

    private void initiateSignIn() {
        Log.d(TAG, "Initiating sign in");
        BeginSignInRequest signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();

        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        IntentSenderRequest request = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                        resultLauncher.launch(request);
                    } catch (Exception e) {
                        Log.e(TAG, "No se puede iniciar One Tap UI", e);
                    }

                })
                .addOnFailureListener(this, e -> Log.e(TAG, "One Tap signIn failed", e));
    }

    private void handleSignInResult(Intent data) {
        try {
            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
            String idToken = credential.getGoogleIdToken();
            if (idToken != null) {
                signInWithGoogle(idToken);
            } else {
                Log.w(TAG, "No se pudo obtener el token");
            }
        } catch (Exception e) {
            Log.w(TAG, "Falló el registro con Google", e);
        }
    }

    private void signInWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            // User is signed in
            Log.d(TAG, "User is signed in with UID: " + currentUser.getUid());
            // Navigate to another activity or update the UI as needed
        } else {
            // User is not signed in
            Log.d(TAG, "User is not signed in");
            // Show login form or error message
        }
    }

    public static class UserInfo{
        String uid, name, email, profileUrl, coverUrl, userToken;

        public UserInfo(String uid, String name, String email, String profileUrl, String coverUrl, String userToken) {
            this.uid = uid;
            this.name = name;
            this.email = email;
            this.profileUrl = profileUrl;
            this.coverUrl = coverUrl;
            this.userToken = userToken;
        }
    }
}