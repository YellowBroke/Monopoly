package com.example.MonopolyGame;

import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

/*
    负责监听屏幕动作
 */

public class MonopolyGameListener implements View.OnTouchListener{

    private static final String TAG = "MonopolyGameListener"; // for Log.d

    private final MonopolyGameView monopolyGameView;
    private final MonopolyGameLogic monopolyGameLogic;
    private float touchX, touchY;
    Pair<MonopolyGameView.ViewType, Integer> viewType; // <图形类型，附加编号>

    public MonopolyGameListener(MonopolyGameView monopolyGameView, MonopolyGameLogic monopolyGameLogic){
        this.monopolyGameView = monopolyGameView;
        this.monopolyGameLogic = monopolyGameLogic;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "action down");
                touchX = event.getX();
                touchY = event.getY();
                viewType = monopolyGameView.getViewType(touchX, touchY);
                // 点击骰子按钮 && 当前轮到玩家移动 && 处于准备掷骰子阶段
                if(viewType.first == MonopolyGameView.ViewType.BUTTON_DICE
                        && monopolyGameLogic.getCurPlayer() == 0
                        && monopolyGameLogic.getGameState() == MonopolyGameLogic.GameState.DEFAULT){
                    monopolyGameLogic.setGameState(MonopolyGameLogic.GameState.ROLL); // 进入掷骰子阶段
                }
                // TODO: 更多点击事件
                break;
            default: break;
        }
        return true;
    }
}
