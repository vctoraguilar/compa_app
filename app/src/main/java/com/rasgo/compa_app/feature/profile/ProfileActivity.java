package com.rasgo.compa_app.feature.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.rasgo.compa_app.R;

public class ProfileActivity extends AppCompatActivity {

    private String uid="",profileUrl="", coverUrl="";
    private int current_state=0;

    /*
        0=perfil cargando
        1=compas
        2=hemos enviado solicitud de compa
        3=hemos recibido solicitud de compa
        4=no somos compas
        5=nuestro perfil
     */
    private Button profileOptionButton;
    private ImageView profileImage, coverImage;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileOptionButton=findViewById(R.id.profile_action_button);
        profileImage = findViewById(R.id.profile_image);
        recyclerView=findViewById(R.id.recyv_profile);
        progressBar=findViewById(R.id.progressbar);
        coverImage=findViewById(R.id.profile_cover);
        toolbar=findViewById(R.id.toolbar);
        collapsingToolbarLayout=findViewById(R.id.collapsing_toolbar);

        uid = getIntent().getStringExtra(uid);

        if (uid.equals(FirebaseAuth.getInstance().getUid())){
            current_state=5;
            profileOptionButton.setText("Editar...");
        }else{
            profileOptionButton.setText("Cargando...");
            profileOptionButton.setEnabled(false);
        }

        fetchProfileInfo();
    }

    private void fetchProfileInfo(){
        //Llamar API

    }

}