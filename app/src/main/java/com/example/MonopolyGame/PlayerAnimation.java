package com.example.MonopolyGame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.util.Stack;

/*
    负责玩家静止画面、移动动画
 */

public class PlayerAnimation {

    private Bitmap playerBitmap; // 居中正常显示
    private Bitmap playerSmallBitmap; // 作为角标显示
    private int frameTime = 220;
    private long lastTime = 0;
    private int endPos = 0; // 移动目的地
    private Stack<Integer> posStack; // 移动路线，栈顶为当前移动位置

    public PlayerAnimation(int resId, int w, int h){
        initBitmap(resId);
        playerBitmap = MonopolyGameView.adjustBitmap(playerBitmap, w, h);
        playerSmallBitmap = MonopolyGameView.adjustBitmap(playerBitmap, w / 2, h / 2);
        posStack = new Stack<>();
        posStack.push(0);
    }

    private void initBitmap(int resId){
        playerBitmap = BitmapFactory.decodeResource(MonopolyGameActivity.getMonopolyGameActivity().getResources(),
                resId);
    }

    public Bitmap getPlayerBitmap(){
        return playerBitmap;
    }

    public int getNextPos(){
        return posStack.peek();
    }

    public int getEndPos(){
        return endPos;
    }

    public void setPos(int newPos, int squareNum){
        endPos = newPos;
        int oldPos = posStack.pop();
        while(newPos != oldPos){ // 逆序压入移动路线
            posStack.push(newPos);
            newPos = (newPos - 1 + squareNum) % squareNum;
        }
        posStack.push(oldPos);
    }

    // x, y, w, h: 所在格子的参数
    // pri: 优先级，0: 大图，1~3: 角标
    public boolean draw(Canvas canvas, float x, float y, float w, float h, int pri){
        long curTime = android.os.SystemClock.uptimeMillis();
        float x0 = x + (w - playerBitmap.getWidth()) * 0.5f;
        float y0 = y + (h - playerBitmap.getHeight()) * 0.5f;
        float x1, y1;
        if(pri == 0){
            canvas.drawBitmap(playerBitmap, x0, y0, null);
        }
        else{
            x1 = x + (x0 - x - playerSmallBitmap.getWidth()) * 0.5f;
            y1 = y + h * 0.25f * pri - playerSmallBitmap.getHeight() * 0.5f;
            canvas.drawBitmap(playerSmallBitmap, x1, y1, null);
        }
        if(posStack.size() == 1){
            lastTime = 0;
            return false; // 移动结束，无需重绘
        }
        if(lastTime == 0 || curTime - lastTime >= frameTime){
            posStack.pop();
            lastTime = curTime;
        }
        return true;
    }
}
