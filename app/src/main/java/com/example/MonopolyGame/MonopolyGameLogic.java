package com.example.MonopolyGame;

import android.os.Handler;
import android.util.Log;

/*
    负责游戏逻辑的管理
 */

public class MonopolyGameLogic extends Thread{

    private final String TAG = "MonopolyGameLogic"; // for Log.d

    // 游戏状态：其他，掷骰子阶段，移动阶段
    public static enum GameState{
        DEFAULT, ROLL, MOVE
    }

    private MonopolyGameView monopolyGameView;
    private Handler handler;
    private boolean isRunning = true;
    private boolean isPaused = false;
    private GameState gameState = GameState.DEFAULT;

    private final int playerNum = 4; // TODO: 可以自定义玩家数量
    private Dice dice;
    private Player[] players;
    private Board board;

    private int curPlayer = 0; // 当前行动玩家

    public MonopolyGameLogic(MonopolyGameView monopolyGameView, Handler handler){
        this.monopolyGameView = monopolyGameView;
        this.handler = handler;
        dice = new Dice();
        players = new Player[playerNum];
        for(int i = 0; i < playerNum; ++i){
            players[i] = new Player();
        }
        board = new Board(monopolyGameView.getSquareLNum());
    }

    public void pause(){
        isPaused = true;
    }

    public void unPause(){
        isPaused = false;
    }

    // 请求重绘
    private void draw(){
        monopolyGameView.postInvalidate();
    }

    @Override
    public void run(){
        Log.d(TAG, "run");
        while(isRunning){
            if(isPaused) continue;
            if(curPlayer > 0 && getGameState() == GameState.DEFAULT){ // 人机自动掷骰子
                setGameState(GameState.ROLL);
            }
            if(getGameState() == GameState.ROLL){
                dice.rollDice();
                draw();
                try {
                    sleep(2000); // 动画时间待斟酌
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 玩家获取移动后在板上的位置，并设定新位置
                players[curPlayer].setPosition(board.getPos(players[curPlayer].getPosition() + dice.getFaceValue()));
                setGameState(GameState.MOVE);
                draw();
                try {
                    sleep(300 + 240 * dice.getFaceValue()); // 动画时间待斟酌
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // TODO: 触发地段事件
                setGameState(GameState.DEFAULT);
                curPlayer = getNxtPlayer(); // 下一玩家行动
                draw();
            }
        }
    }

    public void setGameState(GameState gameState){
        this.gameState = gameState;
    }

    public GameState getGameState(){
        return gameState;
    }

    public int getCurPlayer(){
        return curPlayer;
    }

    public int getNxtPlayer(){
        return (curPlayer + 1) % playerNum;
    }

    public Dice getDice(){
        return dice;
    }

    public Player getPlayer(int id){
        return players[id];
    }
}
