package com.example.MonopolyGame;

import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MonopolyGameActivity extends AppCompatActivity {

    private static MonopolyGameActivity monopolyGameActivity = null;
    private MonopolyGameView monopolyGameView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏显示
        monopolyGameActivity = this;
        monopolyGameView = new MonopolyGameView(this);
        setContentView(monopolyGameView);
    }

    public static MonopolyGameActivity getMonopolyGameActivity(){
        return monopolyGameActivity;
    }
}