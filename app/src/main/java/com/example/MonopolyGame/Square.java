package com.example.MonopolyGame;

public class Square {

    // 方格类型，待细化
    public enum SquareType{
        STARTPOINT, NORMAL, SEPECIAL
    }

    SquareType squareType = SquareType.NORMAL;

    public SquareType getSquareType(){
        return squareType;
    }
}
