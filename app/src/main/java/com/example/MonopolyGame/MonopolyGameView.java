package com.example.MonopolyGame;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;

/*
    负责游戏界面的绘制
 */

public class MonopolyGameView extends View {

    private static final String TAG = "MonopolyGameView"; // for Log.d

    private final int squareLNum = 5; // 左右放置的方格数
    private final int squareUNum = 7; // 上下放置的方格数
    private final int squareNum = squareLNum * 2 + squareUNum * 2 + 4;
    private final int playerNum = 4;

    // 图形类型：其他，方格，骰子按钮
    public static enum ViewType{
        DEFAULT, SQUARE, BUTTON_DICE
    }

    // 各图形 w × h 信息
    private int viewWidth;
    private int viewHeight;
    private float baseGapWidth;
    private float baseGapHeight;
    private float headerWidth;
    private float headerHeight;
    private float dataWidth;
    private float dataHeight;
    private float boardWidth;
    private float boardHeight;
    private float bodyWidth;
    private float bodyHeight;
    private float eventWndWidth;
    private float eventWndHeight;
    private float squareLWidth;
    private float squareLHeight;
    private float squareUWidth;
    private float squareUHeight;
    private int diceWidth;
    private int diceHeight;
    private int playerWidth;
    private int playerHeight;
    private int playerIconWidth;
    private int playerIconHeight;
    private float buttonDiceWidth;
    private float buttonDiceHeight;

    // 保存频繁使用的图形坐标
    private float eventWndX;
    private float eventWndY;
    private int diceX;
    private int diceY;
    private float buttonDiceX;
    private float buttonDiceY;
    private float dataX[] = new float[playerNum];
    private float dataY[] = new float[playerNum];

    // 方格右下角编号0，编号按逆时针递增
    private float squareX[] = new float[squareNum];
    private float squareY[] = new float[squareNum];
    private float squareW[] = new float[squareNum];
    private float squareH[] = new float[squareNum];

    private int[] colorOfPlayers; // TODO: 可以自定义颜色

    private Bitmap backgroundBitmap;
    private Paint paint = new Paint();
    private DiceAnimation diceAnimation;
    private PlayerAnimation[] playerAnimations;
    private int[] playerNumOfSquare;

    private MonopolyGameLogic monopolyGameLogic;

    public MonopolyGameView(Context context){
        super(context);
        monopolyGameLogic = new MonopolyGameLogic(this, new Handler());
        setOnTouchListener(new MonopolyGameListener(this, monopolyGameLogic));
    }

    public MonopolyGameLogic getThread(){
        return monopolyGameLogic;
    }

    @Override
    protected void onDraw(Canvas canvas){
        Log.d(TAG, "onDraw");
        drawView(canvas);
    }

    private void drawView(Canvas canvas){
        Log.d(TAG, "drawView");
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        drawCurPlayer(canvas);
        boolean reDraw = false; // 是否有动画未完成，需要重绘
        reDraw |= drawDice(canvas);
        for(int i = 0; i < squareNum; ++i){
            playerNumOfSquare[i] = 0;
        }
        int curPlayer = monopolyGameLogic.getCurPlayer();
        reDraw |= drawPlayer(canvas, curPlayer);
        for(int i = (curPlayer + 1) % playerNum; i != curPlayer; i = (i + 1) % playerNum) {
            reDraw |= drawPlayer(canvas, i); // 越先行动显示优先级越高
        }
        if(reDraw) invalidate();
    }

    // 绘制当前玩家边框
    private void drawCurPlayer(Canvas canvas){
        Log.d(TAG, "drawCurPlayer");
        int curPlayer = monopolyGameLogic.getCurPlayer();
        paint.setColor(colorOfPlayers[curPlayer] & 0X00FFFFFF | 0X7F000000);
        paint.setStrokeWidth(Math.min(baseGapWidth * 0.66f, baseGapHeight));
        canvas.drawRoundRect(new RectF(dataX[curPlayer], dataY[curPlayer],
                dataX[curPlayer] + dataWidth, dataY[curPlayer] + dataHeight),
                20, 20, paint);
    }

    // 绘制骰子动画
    private boolean drawDice(Canvas canvas){
        Log.d(TAG, "drawDice");
        if(monopolyGameLogic.getGameState() == MonopolyGameLogic.GameState.ROLL
                && diceAnimation.drawRotate(canvas, diceX, diceY)){
            return true;
        }
        diceAnimation.drawPip(canvas, diceX, diceY, monopolyGameLogic.getDice().getFaceValue());
        return false;
    }

    // 绘制玩家移动动画
    private boolean drawPlayer(Canvas canvas, int id){
        Log.d(TAG, "drawPlayer");
        if(monopolyGameLogic.getGameState() == MonopolyGameLogic.GameState.MOVE){
            if(playerAnimations[id].getEndPos() != monopolyGameLogic.getPlayer(id).getPosition()){
                playerAnimations[id].setPos(monopolyGameLogic.getPlayer(id).getPosition(), squareNum);
                Log.d(TAG, "setPos");
            }
        }
        int nxtPos = playerAnimations[id].getNextPos();
        if(playerAnimations[id].draw(canvas, squareX[nxtPos], squareY[nxtPos],
                squareW[nxtPos], squareH[nxtPos], playerNumOfSquare[nxtPos]++)){
            return true;
        }
        return false;
    }

    // view初始化工作，适配屏幕
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("TAG", "onSizeChanged");
        viewWidth = w;
        viewHeight = h;
        Log.d(TAG, "viewSize: " + w + " x " + h);
        initParams();
        createBackground();
        monopolyGameLogic.start();
    }

    // 将位图bitmap调整为w × h大小
    public static Bitmap adjustBitmap(Bitmap bitmap, int w, int h){
        Matrix matrix = new Matrix();
        matrix.postScale((float)w / bitmap.getWidth(), (float)h / bitmap.getHeight());
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    // 游戏图形、动画参数初始化
    private void initParams(){
        Log.d(TAG, "initParams");
        baseGapWidth = viewWidth * 0.01f;
        baseGapHeight = viewHeight * 0.01f;
        headerWidth = (viewWidth - baseGapWidth * 3) * 0.2f;
        headerHeight = viewHeight - baseGapHeight * 2;
        dataWidth = headerWidth - baseGapWidth * 2;
        dataHeight = (headerHeight - baseGapHeight * 10) * 0.25f;
        boardWidth = viewWidth - baseGapWidth * 3 - headerWidth;
        boardHeight = headerHeight;
        squareLWidth = squareUHeight = Math.min(boardWidth * 0.2f, boardHeight * 0.2f);
        bodyWidth = boardWidth - squareLWidth * 2;
        bodyHeight = boardHeight - squareUHeight * 2;
        eventWndWidth = bodyWidth * 0.66f * 0.88f;
        eventWndHeight = bodyHeight * 0.88f;
        squareUWidth = bodyWidth / squareUNum;
        squareLHeight = bodyHeight / squareLNum;
        diceWidth = (int)Math.min((bodyWidth - eventWndWidth - baseGapWidth * 4) * 0.66f, bodyHeight * 0.33f);
        diceHeight = diceWidth;
        playerWidth = (int)(Math.min(Math.min(squareLWidth, squareLHeight), Math.min(squareUWidth, squareUHeight)) * 0.66f);
        playerHeight = playerWidth;
        playerIconWidth = (int)(dataWidth * 0.4f * 0.8f);
        playerIconHeight = playerIconWidth;
        buttonDiceWidth = (bodyWidth - eventWndWidth) * 0.55f;
        buttonDiceHeight = bodyHeight * 0.2f;

        Log.d(TAG, "baseGap size: " + baseGapWidth + " x " + baseGapHeight);
        Log.d(TAG, "header size: " + headerWidth + " x " + headerHeight);
        Log.d(TAG, "data size: " + dataWidth + " x " + dataHeight);
        Log.d(TAG, "board size: " + boardWidth + " x " + boardHeight);
        Log.d(TAG, "body size: " + bodyWidth + " x " + bodyHeight);
        Log.d(TAG, "squareL size: " + squareLWidth + " x " + squareLHeight);
        Log.d(TAG, "squareU size: " + squareUWidth + " x " + squareUHeight);
        Log.d(TAG, "eventWnd size: " + eventWndWidth + " x " + eventWndHeight);
        Log.d(TAG, "dice size: " + diceWidth + " x " + diceHeight);
        Log.d(TAG, "player size: " + playerWidth + " x " + playerHeight);
        Log.d(TAG, "playerIcon size: " + playerIconWidth + " x " + playerIconHeight);
        Log.d(TAG, "buttonDice size: " + buttonDiceWidth + " x " + buttonDiceHeight);

        eventWndX = viewWidth - baseGapWidth - squareLWidth
                - eventWndWidth - (bodyWidth * 0.66f - eventWndWidth) * 0.5f;
        eventWndY = (bodyHeight - eventWndHeight) * 0.5f + baseGapHeight + squareUHeight;
        diceX = (int)(baseGapWidth * 2 + headerWidth + squareLWidth
                + (bodyWidth - eventWndWidth - baseGapWidth * 2 - diceWidth) * 0.5f);
        diceY = (int)(baseGapHeight * 2 + squareUHeight + bodyHeight * 0.1f);
        buttonDiceX = (diceX + diceWidth / 2 - buttonDiceWidth * 0.5f);
        buttonDiceY = baseGapHeight + squareUHeight + bodyHeight * 0.5f;
        dataX[0] = baseGapWidth * 2;
        dataY[0] = baseGapHeight * 3;
        for(int i = 1; i < playerNum; ++i){
            dataX[i] = dataX[i - 1];
            dataY[i] = dataY[i - 1] + dataHeight + baseGapHeight * 2;
        }
        squareX[0] = headerWidth + baseGapWidth * 2 + boardWidth - squareLWidth;
        squareY[0] = baseGapHeight + boardHeight - squareUHeight;
        squareW[0] = squareLWidth;
        squareH[0] = squareUHeight;
        float curX = squareX[0], curY = squareY[0];
        for(int i = 1; i < squareNum; ++i){
            if(i == squareLNum + squareUNum + 2){
                curX -= squareLWidth;
            }
            else if(i == squareLNum * 2 + squareUNum + 4){
                curX += squareLWidth;
            }
            else if(i > squareLNum + 1 && i < squareLNum + squareUNum + 2){
                curX -= squareUWidth;
            }
            else if(i > squareLNum * 2 + squareUNum + 4){
                curX += squareUWidth;
            }
            if(i == squareLNum + 1){
                curY -= squareUHeight;
            }
            else if(i == squareLNum + squareUNum + 3){
                curY += squareUHeight;
            }
            else if(i < squareLNum + 1){
                curY -= squareLHeight;
            }
            else if(i > squareLNum + squareUNum + 3 && i < squareLNum * 2 + squareUNum + 4){
                curY += squareLHeight;
            }
            squareX[i] = curX;
            squareY[i] = curY;
            if(i == squareLNum + 1 || i == squareLNum + squareUNum + 2 || i == squareLNum * 2 + squareUNum + 3){
                squareW[i] = squareLWidth;
                squareH[i] = squareUHeight;
            }
            else if(i < squareLNum + 1 || i > squareLNum + squareUNum + 2 && i < squareLNum * 2 + squareUNum + 3){
                squareW[i] = squareLWidth;
                squareH[i] = squareLHeight;
            }
            else{
                squareW[i] = squareUWidth;
                squareH[i] = squareUHeight;
            }
        }

        diceAnimation = new DiceAnimation(diceWidth, diceHeight);
        playerAnimations = new PlayerAnimation[playerNum];
        playerAnimations[0] = new PlayerAnimation(R.drawable.player_image1, playerWidth, playerHeight);
        playerAnimations[1] = new PlayerAnimation(R.drawable.player_image2, playerWidth, playerHeight);
        playerAnimations[2] = new PlayerAnimation(R.drawable.player_image3, playerWidth, playerHeight);
        playerAnimations[3] = new PlayerAnimation(R.drawable.player_image4, playerWidth, playerHeight);
        playerNumOfSquare = new int[squareNum];

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        paint.setTextSize(buttonDiceHeight * 0.8f);

        colorOfPlayers = new int[playerNum];
        colorOfPlayers[0] = 0X3FFFFF00;
        colorOfPlayers[1] = 0X3F663333;
        colorOfPlayers[2] = 0X3F00FF00;
        colorOfPlayers[3] = 0X3FFF0000;
    }

    // 绘制静态背景
    private void createBackground(){
        Log.d(TAG, "createBackground");
        backgroundBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
        Canvas backgroundCanvas = new Canvas(backgroundBitmap);
        drawHeader(backgroundCanvas);
        drawBoard(backgroundCanvas);
        drawBody(backgroundCanvas);
        drawData(backgroundCanvas);
        drawEventWnd(backgroundCanvas);
        drawButton(backgroundCanvas);
        drawSquare(backgroundCanvas);
    }

    private void drawHeader(Canvas canvas){
        Log.d(TAG, "drawHeader");
        paint.setColor(0X1F0000FF);
        RectF header = new RectF(baseGapWidth, baseGapHeight,
                baseGapWidth + headerWidth, baseGapHeight + headerHeight);
        canvas.drawRoundRect(header, 20, 20, paint);
    }

    private void drawBoard(Canvas canvas){
        Log.d(TAG, "drawBoard");
        paint.setColor(0X330000FF);
        RectF board = new RectF(headerWidth + baseGapWidth * 2, baseGapHeight,
                headerWidth + baseGapWidth * 2 + boardWidth, baseGapHeight + boardHeight);
        canvas.drawRoundRect(board, 10, 10, paint);
    }

    private void drawBody(Canvas canvas){
        Log.d(TAG, "drawBody");
        paint.setColor(0X5500FF00);
        float x1 = headerWidth + baseGapWidth * 2 + squareLWidth;
        float y1 = baseGapHeight + squareUHeight;
        float x2 = x1 + bodyWidth;
        float y2 = y1 + bodyHeight;
        RectF body = new RectF(x1, y1, x2, y2);
        canvas.drawRoundRect(body, 0, 0, paint);
    }

    private void drawData(Canvas canvas){
        Log.d(TAG, "drawData");
        RectF data;
        for(int i = 0; i < playerNum; ++i){
            paint.setColor(colorOfPlayers[i]);
            data = new RectF(dataX[i], dataY[i],
                    dataX[i] + dataWidth, dataY[i] + dataHeight);
            canvas.drawRoundRect(data, 20, 20, paint);
        }
        for(int i = 0; i < playerNum; ++i){
            canvas.drawBitmap(adjustBitmap(playerAnimations[i].getPlayerBitmap(), playerIconWidth, playerIconHeight),
                    dataX[i] + (dataWidth * 0.4f - playerIconWidth) * 0.5f,
                    dataY[i] + (dataHeight * 0.6f - playerIconHeight) * 0.5f, null);
        }
    }

    private void drawEventWnd(Canvas canvas){
        Log.d(TAG, "drawEventWnd");
        paint.setColor(0X3300FF00);
        RectF board = new RectF(eventWndX, eventWndY,
                eventWndX + eventWndWidth, eventWndY + eventWndHeight);
        canvas.drawRoundRect(board, 20, 20, paint);
    }

    // 绘制按钮 TODO: 参数有待细化
    private void drawButton(Canvas canvas){
        Log.d(TAG, "drawButton");
        paint.setColor(0X7FFF6600);
        RectF buttonDice = new RectF(buttonDiceX, buttonDiceY,
                buttonDiceX + buttonDiceWidth, buttonDiceY + buttonDiceHeight);
        canvas.drawRoundRect(buttonDice, buttonDiceHeight * 0.5f, buttonDiceHeight * 0.5f, paint);
        paint.setColor(0X7F000000);
        paint.setTextSize(buttonDiceWidth);
        paint.setTextSize(Math.min(buttonDiceWidth * buttonDiceWidth * 0.66f / paint.measureText("大写的按钮"),
                buttonDiceHeight * 0.66f));
        Rect buttonText = new Rect();
        paint.getTextBounds("嘉冰廉灏", 0, 1, buttonText);
        canvas.drawText("大写的按钮", buttonDice.centerX(),
                buttonDiceY + buttonDiceHeight * 0.5f + buttonText.height() * 0.5f, paint);
    }

    private void drawSquare(Canvas canvas){
        Log.d(TAG, "drawSquare");
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLUE);
        paint.setAlpha(128);
        RectF square;
        for(int i = 0; i < squareNum; ++i){
            square = new RectF(squareX[i], squareY[i],
                    squareX[i] + squareW[i], squareY[i] + squareH[i]);
            canvas.drawRoundRect(square, 10, 10, paint);
        }
    }

    // (px, py)是否在矩形(x, y, x + w, y + h)中
    public boolean isInRect(float px, float py, float x, float y, float w, float h){
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }

    // 获取<图形类型, 附加编号>
    public Pair<ViewType, Integer> getViewType(float x, float y){
        if(isInRect(x, y, buttonDiceX, buttonDiceY, buttonDiceWidth, buttonDiceHeight)){
            return Pair.create(ViewType.BUTTON_DICE, -1);
        }
        // TODO: <方格，方格编号>，<玩家信息，玩家编号>
        return Pair.create(ViewType.DEFAULT, -1);
    }

    public int getSquareLNum(){
        return squareNum;
    }

    public int getPlayerNum(){
        return playerNum;
    }
}
