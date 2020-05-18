package com.example.MonopolyGame;

import java.util.Random;

public class Dice {

    private final int totValue = 6;
    private int faceValue = 1;
    private Random random = new Random();

    public void setFaceValue(int faceValue){
        this.faceValue = faceValue;
    }

    public int getFaceValue(){
        return faceValue;
    }

    public void rollDice(){
        setFaceValue(random.nextInt(totValue) + 1);
    }
}
