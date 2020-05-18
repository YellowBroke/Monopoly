package com.example.MonopolyGame;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/*
    负责骰子静态点数画面、转动动画
 */
public class DiceAnimation {

    private Bitmap[] diceRotate = new Bitmap[4];
    private Bitmap[] dicePip = new Bitmap[6];
    private int frameTime = 140;
    private int durationTime = 1680;
    private long startTime = 0;

    public DiceAnimation(int w, int h){
        initBitmap();
        for(int i = 0; i < 4; ++i){
            diceRotate[i] = MonopolyGameView.adjustBitmap(diceRotate[i], w, h);
        }
        for(int i = 0; i < 6; ++i){
            dicePip[i] = MonopolyGameView.adjustBitmap(dicePip[i], w, h);
        }
    }

    private void initBitmap(){
        diceRotate[0] = BitmapFactory.decodeResource(MonopolyGameActivity.getMonopolyGameActivity().getResources(),
                R.drawable.dice_rotate0);
        diceRotate[1] = BitmapFactory.decodeResource(MonopolyGameActivity.getMonopolyGameActivity().getResources(),
                R.drawable.dice_rotate1);
        diceRotate[2] = BitmapFactory.decodeResource(MonopolyGameActivity.getMonopolyGameActivity().getResources(),
                R.drawable.dice_rotate2);
        diceRotate[3] = BitmapFactory.decodeResource(MonopolyGameActivity.getMonopolyGameActivity().getResources(),
                R.drawable.dice_rotate3);
        dicePip[0] = BitmapFactory.decodeResource(MonopolyGameActivity.getMonopolyGameActivity().getResources(),
                R.drawable.dice_pip1);
        dicePip[1] = BitmapFactory.decodeResource(MonopolyGameActivity.getMonopolyGameActivity().getResources(),
                R.drawable.dice_pip2);
        dicePip[2] = BitmapFactory.decodeResource(MonopolyGameActivity.getMonopolyGameActivity().getResources(),
                R.drawable.dice_pip3);
        dicePip[3] = BitmapFactory.decodeResource(MonopolyGameActivity.getMonopolyGameActivity().getResources(),
                R.drawable.dice_pip4);
        dicePip[4] = BitmapFactory.decodeResource(MonopolyGameActivity.getMonopolyGameActivity().getResources(),
                R.drawable.dice_pip5);
        dicePip[5] = BitmapFactory.decodeResource(MonopolyGameActivity.getMonopolyGameActivity().getResources(),
                R.drawable.dice_pip6);
    }

    // 返回值为是否需要重绘
    public boolean drawRotate(Canvas canvas, float diceX, float diceY){
        long curTime = android.os.SystemClock.uptimeMillis();
        if(startTime == 0){
            startTime = curTime;
        }
        if(curTime - startTime > durationTime){ // 旋转动画结束，无需重绘
            return false;
        }
        int curFrame = (int)((curTime - startTime) / frameTime % 4); // 获取当前帧
        canvas.drawBitmap(diceRotate[curFrame], diceX, diceY, null);
        return true;
    }

    public void drawPip(Canvas canvas, float diceX, float diceY, int pip){
        canvas.drawBitmap(dicePip[pip - 1], diceX, diceY, null);
        startTime = 0; // 绘制点数静态图，重置旋转动画计时器
    }
}