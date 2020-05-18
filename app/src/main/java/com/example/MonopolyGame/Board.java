package com.example.MonopolyGame;

public class Board {

    private final int squareNum;
    private Square[] squares;

    public Board(int squareNum){
        this.squareNum = squareNum;
        squares = new Square[squareNum];
    }

    public int getSquareNum(){
        return squareNum;
    }

    // 通过板大小转化具体位置
    public int getPos(int pos){
        return pos % squareNum;
    }
}
