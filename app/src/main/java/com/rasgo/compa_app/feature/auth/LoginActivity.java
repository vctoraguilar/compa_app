package com.rasgo.compa_app.feature.auth;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rasgo.compa_app.R;
import com.rasgo.compa_app.feature.homepage.MainActivity;
import com.rasgo.compa_app.model.auth.AuthResponse;
import com.rasgo.compa_app.utils.ViewModelFactory;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN=50;
    private static final String TAG="LoginActivity";
    private SignInButton signInButton;
    private LoginViewModel viewModel;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Cargando ...");
        progressDialog.setMessage("Iniciando Sesíón ...  ");
        viewModel= new ViewModelProvider(this, new ViewModelFactory()).get(LoginViewModel.class);
        signInButton=findViewById(R.id.btn_GoogleInicio);

        //Configurando Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient= GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signIn();
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle: " + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed: " + e.getStatusCode(), e);
            }
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN){
//            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
//                firebaseAuthWithGoogle(account.getIdToken());
//            } catch (ApiException e) {
//                Log.w(TAG, "Falló Inicio de Sesión con Google", e);
//            }
//        }
//    }

    private void firebaseAuthWithGoogle(String idToken){
        progressDialog.show();
        signInButton.setVisibility(View.GONE);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    viewModel.login(new UserInfo(
                                            user.getUid(),
                                            user.getDisplayName(),
                                            user.getEmail(),
                                            user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null,
                                            null, // Cover URL can be set to null or provide the correct value
                                            token
                                    )).observe(LoginActivity.this, new Observer<AuthResponse>() {
                                        @Override
                                        public void onChanged(AuthResponse authResponse) {
                                            progressDialog.hide();
                                            Toast.makeText(LoginActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                            if (authResponse.getAuth() != null) {
                                                updateUI(user);
                                            } else {
                                                FirebaseAuth.getInstance().signOut();
                                                updateUI(null);
                                            }
                                        }
                                    });
                                    //{
//                                        progressDialog.hide();
//                                        Toast.makeText(LoginActivity.this, authResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                                        if (authResponse.getAuth() != null) {
//                                            updateUI(user);
//                                        } else {
//                                            FirebaseAuth.getInstance().signOut();
//                                            updateUI(null);
//                                        }
                                    //});
                                }
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to get FCM token", e);
                                progressDialog.hide();
                                signInButton.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, "Failed to get FCM token", Toast.LENGTH_SHORT).show();
                            });

                        }else {
                            Log.w(TAG,"signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    public static class UserInfo {
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
