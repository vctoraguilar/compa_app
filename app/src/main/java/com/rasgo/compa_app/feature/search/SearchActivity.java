package com.rasgo.compa_app.feature.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.rasgo.compa_app.R;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private TextView defaultText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);
        toolbar=findViewById(R.id.toolbar);
        searchView=findViewById(R.id.search);
        recyclerView=findViewById(R.id.search_recyv);
        defaultText=findViewById(R.id.default_text);
    }
}