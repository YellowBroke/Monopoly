package com.example.MonopolyGame;

import android.util.Log;

public class Player {

    private String name = "P"; // TODO: 玩家名
    private int position = 0;

    public void setPosition(int position){
        this.position = position;
        Log.d("QAQ", "new Pos: " + position);
    }

    public int getPosition(){
        return position;
    }
}
