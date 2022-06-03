package com.example.a220523;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{
    Animation startTextAnime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_start);

        LinearLayout mainLayout = findViewById(R.id.all);
        mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SubActivity.class);
                startActivity(intent);
            }
        });

        TextView startText = findViewById(R.id.start_text);
        startTextAnime = new AlphaAnimation(1.0f, 0.2f);
        startTextAnime.setDuration(600);
        startTextAnime.setStartOffset(0);
        startTextAnime.setRepeatMode(Animation.REVERSE);
        startTextAnime.setRepeatCount(Animation.INFINITE);
        startText.startAnimation(startTextAnime);
    }
}
