package com.example.englishflashcards;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity {
    private TabHost tab;
    private Button btnexam, btnlistening;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Tab setup ---
        tab = findViewById(R.id.tabhost);
        tab.setup();

        TabHost.TabSpec tab1 = tab.newTabSpec("tab1");
        tab1.setIndicator("", getResources().getDrawable(R.drawable._3_evernight));
        tab1.setContent(R.id.tab1);
        tab.addTab(tab1);

        TabHost.TabSpec tab2 = tab.newTabSpec("tab2");
        tab2.setIndicator("", getResources().getDrawable(R.drawable._3_evernight));
        tab2.setContent(R.id.tab2);
        tab.addTab(tab2);

        TabHost.TabSpec tab3 = tab.newTabSpec("tab3");
        tab3.setIndicator("", getResources().getDrawable(R.drawable._4_evernight));
        tab3.setContent(R.id.tab3);
        tab.addTab(tab3);

        tab.setCurrentTab(0);
        Button btnListening = findViewById(R.id.btnlistening);
        btnListening.setOnClickListener(v -> {
            Intent intent1 = new Intent(MainActivity.this, TopicsActivity.class);
            startActivity(intent1);
        });



    }
}