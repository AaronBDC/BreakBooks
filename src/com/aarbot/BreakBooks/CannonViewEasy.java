//stage 1
////CannonViewEasy.java
////Displays the Cannon Game
package com.aarbot.BreakBooks;

import java.util.HashMap;
import java.util.Map;

import com.aarbot.BreakBooks.R.color;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.RadialGradient; // version 2.0

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CannonViewEasy extends SurfaceView implements SurfaceHolder.Callback
{
  private CannonThread cannonThread; // controls the game loop
  private Activity activity; // to display Game Over dialog in GUI thread
  private boolean dialogIsDisplayed = false;   
              
  // constants for game play
  private static final int TARGET_PIECES = 10; // sections in the target
  private static final int TARGET_PIECES_EASY = 7; // sections in the target
  private static final int TARGET_PIECES_MEDIUM = 17; // sections in the target
  private static final int TARGET_PIECES_HARD = 75; // sections in the target
  private static final int MISS_PENALTY = 2; // seconds deducted on a miss
  private static final int HIT_REWARD = 3; // seconds added on a hit


  // variables for the game loop and tracking statistics
  private boolean gameOver; // is the game over?
  public double timeLeft; // the amount of time left in seconds
  public int shotsFired; // the number of shots the user has fired
  public int GameStatus;
  public int BooksBroken; // number of levels won = 1 broken book! for each broken book allow to retry last stage
  private double totalElapsedTime; // the number of seconds elapsed

  // variables for the blocker and target
  private Line blocker; // start and end points of the blocker
  private int blockerDistance; // blocker distance from left
  private int blockerBeginning; // blocker distance from top
  private int blockerEnd; // blocker bottom edge distance from top
  private int initialBlockerVelocity; // initial blocker speed multiplier
  private float blockerVelocity; // blocker speed multiplier during game

  private Line blockerTwo;  // start and end points of the blockerTwo
  private int blockerTwoDistance;  // blockerTwo distance from the left
  private int blockerTwoBeginning; // blockerTwo distance from top
  private int blockerTwoEnd; // blockerTwo bottom edge distance from top
  private int initialBlockerTwoVelocity; //initial blockerTwo speed multiplier 
  private float blockerTwoVelocity; // blockerTwo speed multiplier durin game
  
  private Line blockerThree;  // start and end points of the blockerThree
  private int blockerThreeDistance;  // blockerThree distance from the left
  private int blockerThreeBeginning; // blockerThree distance from top
  private int blockerThreeEnd; // blockerThree bottom edge distance from top
  private int initialBlockerThreeVelocity; //initial blockerThree speed multiplier
  private float blockerThreeVelocity; // blockerThree speed multiplier durin game
 
  private Line blockerFour;  // start and end points of the blockerFour
  private int blockerFourDistance;  // blockerFour distance from the left
  private int blockerFourBeginning; // blockerFour distance from top
  private int blockerFourEnd; // blockerFour bottom edge distance from top
  private int initialBlockerFourVelocity; //initial blockerFour speed multiplier
  private float blockerFourVelocity; // blockerFour speed multiplier durin game
  
  private Line blockerFive;  // start and end points of the blockerFive
  private int blockerFiveDistance;  // blockerFour distance from the left
  private int blockerFiveBeginning; // blockerFour distance from top
  private int blockerFiveEnd; // blockerFour bottom edge distance from top
  private int initialBlockerFiveVelocity; //initial blockerFour speed multiplier
  private float blockerFiveVelocity; // blockerFour speed multiplier durin game
  
  private Line booster; // start and end points of the SPEED booster
  private int boosterDistance; // blocker distance from left
  private int boosterBeginning; // blocker distance from top
  private int boosterEnd; // blocker bottom edge distance from top
  private int initialBoosterVelocity; // initial blocker speed multiplier
  private float boosterVelocity; // blocker speed multiplier during game

  private Line boosterTwo; // start and end points of the TIME booster
  private int boosterTwoDistance; // blocker distance from left
  private int boosterTwoBeginning; // blocker distance from top
  private int boosterTwoEnd; // blocker bottom edge distance from top
  private int initialBoosterTwoVelocity; // initial blocker speed multiplier
  private float boosterTwoVelocity; // blocker speed multiplier during game
  
  private Line target; // start and end points of the target
  private int targetDistance; // target distance from left
  private int targetBeginning; // target distance from top
  private double pieceLength; // length of a target piece
  private int targetEnd; // target bottom's distance from top
  private int initialTargetVelocity; // initial target speed multiplier
  private float targetVelocity; // target speed multiplier during game

  private int lineWidth; // width of the target and blocker
  private int lineWidthTwo;
  private boolean[] hitStates; // is each target piece hit?
  private int targetPiecesHit; // number of target pieces hit (out of 7)

  // variables for the cannon and cannonball
  private Point cannonball; // cannonball image's upper-left corner
  private int cannonballVelocityX; // cannonball's x velocity
  private int cannonballVelocityY; // cannonball's y velocity
  private boolean cannonballOnScreen; // is the cannonball on the screen
  private int cannonballRadius; // cannonball radius
  private int cannonballSpeed; // cannonball speed
  private int cannonBaseRadius; // cannon base radius
  private int cannonLength; // cannon barrel length
  private Point barrelEnd; // the endpoint of the cannon's barrel
  private int screenWidth; // width of the screen
  private int screenHeight; // height of the screen

  // constants and variables for managing sounds
  private static final int TARGET_SOUND_ID = 0;
  private static final int CANNON_SOUND_ID = 1;
  private static final int BLOCKER_SOUND_ID = 2;
  private static final int WIN_SOUND_ID = 3;
  private static final int LOSE_SOUND_ID = 4;
  private static final int DOINK_SOUND_ID = 5;
  private static final int BOOSTER_SOUND_ID = 6;
  private static final int BOOSTERTWO_SOUND_ID = 7;
  private static final int FREEZE_SOUND_ID = 8;
  private static final int HISCORE_SOUND_ID = 9;
  
  /** The drawable to use as the background of the animation canvas */
  private Bitmap mBackgroundImage;
  Resources mRes;
  
  
  private SoundPool soundPool; // plays sound effects
  private Map<Integer, Integer> soundMap; // maps IDs to SoundPool

  // Paint variables used when drawing each item on the screen
  private Paint textPaint; // Paint used to draw text
  private Paint scorePaint; //Paint used to draw score text
  private Paint cannonballPaint; // Paint used to draw the cannonball
  private Paint cannonPaint; // Paint used to draw the cannon
  private Paint blockerPaint; // Paint used to draw the blocker
  private Paint blockerTwoPaint; // Paint used to draw blockerTwo
  private Paint blockerThreePaint; // Paint used to draw blockerThree
  private Paint blockerFourPaint; // Paint used to draw blockerThree
  private Paint blockerFivePaint;
  // boosters
  
  private Paint boosterPaint;
  private Paint boosterTwoPaint;
  
  private Paint targetPaint; // Paint used to draw the target
  private Paint backgroundPaint; // Paint used to clear the drawing area

  public RadialGradient radGrad;
  public RadialGradient radGrad2;
  
  // public constructor
  public CannonViewEasy(Context context, AttributeSet attrs)
  {
     super(context, attrs); // call super's constructor
     activity = (Activity) context; 
     
     // register SurfaceHolder.Callback listener
     getHolder().addCallback(this); 

     Resources res = context.getResources();
     // cache handles to our key sprites & other drawables
//     mLanderImage = context.getResources().getDrawable(
//             R.drawable.lander_plain);
//     mFiringImage = context.getResources().getDrawable(
//             R.drawable.lander_firing);
//     mCrashedImage = context.getResources().getDrawable(
//             R.drawable.lander_crashed);

//      load background image as a Bitmap instead of a Drawable b/c
     // we don't need to transform it and it's faster to draw this way
     mBackgroundImage = BitmapFactory.decodeResource(res, R.drawable.texture01);	
     
     // initialize Lines and points representing game items
     blocker = new Line(); // create the blocker as a Line
     blockerTwo = new Line(); // Create Blocker two as line
     blockerThree = new Line(); // create blocker three as line
     blockerFour = new Line(); // create blocker four as line
     blockerFive = new Line();
     
     booster = new Line(); //booster SPEED
     boosterTwo = new Line(); //booster TIME
     
     target = new Line(); // create the target as a Line
     cannonball = new Point(); // create the cannonball as a point

     // initialize hitStates as a boolean array
     hitStates = new boolean[TARGET_PIECES];

     // initialize SoundPool to play the app's three sound effects
     soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//     soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM , 0);

     // create Map of sounds and pre-load sounds
     soundMap = new HashMap<Integer, Integer>(); // create new HashMap
     soundMap.put(TARGET_SOUND_ID,
        soundPool.load(context, R.raw.target_hit, 1));
     soundMap.put(CANNON_SOUND_ID,
        soundPool.load(context, R.raw.cannon_fire, 1));
     soundMap.put(BLOCKER_SOUND_ID,
        soundPool.load(context, R.raw.blocker_hit, 1));
     
     soundMap.put(WIN_SOUND_ID,
         soundPool.load(context, R.raw.you_win, 1));
     soundMap.put(LOSE_SOUND_ID,
             soundPool.load(context, R.raw.you_lose, 1));
     soundMap.put(DOINK_SOUND_ID,
             soundPool.load(context, R.raw.doink, 1));       
     soundMap.put(BOOSTER_SOUND_ID,
             soundPool.load(context, R.raw.booster, 1));

     soundMap.put(BOOSTERTWO_SOUND_ID,
             soundPool.load(context, R.raw.booster_two, 1));
     
     soundMap.put(FREEZE_SOUND_ID,
             soundPool.load(context, R.raw.freeze, 1));

     soundMap.put(HISCORE_SOUND_ID,
             soundPool.load(context, R.raw.hiscore, 1));
     
     // construct Paints for drawing text, cannonball, cannon,
     // blocker and target; these are configured in method onSizeChanged
     textPaint = new Paint(); // Paint ;.u'for drawing text
     scorePaint = new Paint(); //paint for drawing score text
     cannonPaint = new Paint(); // Paint for drawing the cannon
     
//     cannonballPaint = new Paint(); // Paint for drawing a cannonball
     cannonballPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Paint for drawing a cannonball
     RadialGradient radGrad = new RadialGradient(250, 175, 50, Color.WHITE, Color.YELLOW, Shader.TileMode.MIRROR);
     RadialGradient radGrad2 = new RadialGradient(250, 175, 50, Color.WHITE, Color.RED, Shader.TileMode.MIRROR);

     LinearGradient linGrad = new LinearGradient(250, 175, 50, 50, Color.WHITE, Color.BLACK, Shader.TileMode.MIRROR);
     
//     cannonballPaint.setShader(radGrad);
// 	     cannonballPaint.setShader(linGrad);
//     cannonballPaint.se/tShader(Color.WHITE);
     
     
     
     
     blockerPaint = new Paint(); // Paint for drawing the blocker
     blockerTwoPaint = new Paint(); // Paint for blockerTwo
     blockerThreePaint = new Paint(); // paint for blockerThree
     blockerFourPaint = new Paint(); // paint for blockerThree
     blockerFivePaint = new Paint();
    
     boosterPaint = new Paint();
     boosterTwoPaint = new Paint();
     
     targetPaint = new Paint(); // Paint for drawing the target
     backgroundPaint = new Paint(); // Paint for drawing the target
  } // end CannonView constructor

  // called by surfaceChanged when the size of the SurfaceView changes,
  // such as when it's first added to the View hierarchy
  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh)
  {
	  switch(GameStatus){
	  case 0:
		  newStart(w, h, oldw, oldh); //set up first game
		  newGame(); //  and start a new game
		  return;
	  case 1:
		  newStartTwo(w, h, oldw, oldh);
		  newGameTwo();
		  return;
	  case 2:
		  newStartThree(w, h, oldw, oldh);
		  newGameThree();
		  return;
				  
	  }
  } // end method onSizeChanged

  public void newStart(int w, int h, int oldw, int oldh)
  {
	  super.onSizeChanged(w, h, oldw, oldh);
	  screenWidth = w; // store the width
	  screenHeight = h; // store the height
	  cannonBaseRadius = h / 18; // cannon base radius 1/18 screen height
	  cannonLength = w / 8; // cannon length 1/8 screen width
	  cannonballRadius = w * 1 / 36;
	  cannonballSpeed = 1320; // cannonball speed multiplier
	  lineWidth = w / 8; // target and blocker 1/14 screen width
	  lineWidthTwo = w / 18;

	  // configure instance variables related to the blocker
	  blockerPaint.setColor(Color.GREEN);
	  blockerPaint.setStrokeWidth(lineWidthTwo); // set line thickness
	  blockerPaint.setShadowLayer(10, 0, 0, Color.GREEN);
	  blockerPaint.setAntiAlias(true);
	  blockerTwoPaint.setColor(Color.GREEN);
	  blockerThreePaint.setColor(Color.YELLOW);
	  blockerFourPaint.setColor(Color.BLUE);
	  blockerFivePaint.setColor(Color.GREEN);
	  blockerTwoPaint.setStrokeWidth(lineWidth);
	  blockerDistance = w * 4 / 8; // blocker 5/8 screen width from left
	  blockerBeginning = h / 8; // distance from top 1/8 screen height
	  blockerEnd = h * 3 / 8; // distance from top 3/8 screen height
	  blockerTwoDistance = w * 6 / 8; // blocker two huge
	  blockerTwoBeginning = h / 7;
	  blockerTwoEnd = h * 3 / 7;
	  initialBlockerVelocity = h / 3; // initial blocker speed multiplier
	  initialBlockerTwoVelocity = -h / 2;

	  blocker.start = new Point(blockerDistance, blockerBeginning);
	  blocker.end = new Point(blockerDistance, blockerEnd);

	  blockerTwo.start = new Point(blockerTwoDistance, blockerTwoBeginning);
	  blockerTwo.end = new Point(blockerTwoDistance, blockerTwoEnd);

	  blockerFiveDistance = w * 2 / 7; // blocker 5/8 screen width from left
	  blockerFiveBeginning = h * 1 / 9; // distance from top 1/8 screen height
	  blockerFiveEnd = h * 2 / 9; // distance from top 3/8 screen height
	  initialBlockerFiveVelocity = h * 1 / 2; // initial blocker speed
	  // multiplier
	  blockerFive.start = new Point(blockerFiveDistance, blockerFiveBeginning);
	  blockerFive.end = new Point(blockerFiveDistance, blockerFiveEnd);

	  // configure instance variables related to the target
	  targetDistance = w * 7 / 8; // target 7/8 screen width from left
	  targetBeginning = h * 1 / 8; // distance from top 1/8 screen height
	  targetEnd = h * 7 / 8; // distance from top 7/8 screen height
	  pieceLength = (targetEnd - targetBeginning) / TARGET_PIECES;
	  initialTargetVelocity = -h / 4; // initial target speed multiplier
	  target.start = new Point(targetDistance, targetBeginning);
	  target.end = new Point(targetDistance, targetEnd);

	  // endpoint of the cannon's barrel initially points horizontally
	  barrelEnd = new Point(cannonLength, h / 2);

	  // configure Paint objects for drawing game elements
	  textPaint.setTextSize(w / 15); // text size 1/10 of screen width

	  textPaint.setAntiAlias(true); // smoothes the text
	  textPaint.setColor(Color.WHITE); // Text color
	  textPaint.setShadowLayer(10, 2, 3, Color.BLUE);

	  scorePaint.setColor(Color.YELLOW);
	  scorePaint.setTextSize(w / 14);
	  scorePaint.setAntiAlias(true);

	  cannonPaint.setStrokeWidth(lineWidthTwo * 1.5f); // set line thickness
	  cannonPaint.setColor(Color.BLUE);
	  cannonPaint.setAntiAlias(true);

	  targetPaint.setStrokeWidth(lineWidthTwo); // set line thickness
	  targetPaint.setShadowLayer(20, 0, 0, Color.BLUE);

	  targetPaint.setAntiAlias(true);

	  backgroundPaint.setColor(Color.BLACK); // set background color

	  cannonballPaint.setColor(Color.WHITE); // set cannonballPaint white
	  cannonballPaint.setShadowLayer(20, 0, 0, Color.RED);
	  cannonballPaint.setAntiAlias(true);

	  // boosters + blocker from level 3
	  blockerThreePaint.setColor(Color.YELLOW);
	  blockerFourPaint.setColor(Color.BLUE);
	  blockerFivePaint.setColor(Color.GREEN);
	  boosterPaint.setColor(Color.RED);
	  boosterTwoPaint.setColor(Color.YELLOW);

	  boosterDistance = w * 1 / 9; // blocker 5/8 screen width from left
	  boosterBeginning = h * 1 / 18; // distance from top 1/8 screen height
	  boosterEnd = h * 2 / 18; // distance from top 3/8 screen height
	  initialBoosterVelocity = 0; // initial blocker speed multiplier

	  booster.start = new Point(boosterDistance, boosterBeginning);
	  booster.end = new Point(boosterDistance, boosterEnd);

	  boosterTwoDistance = w * 1 / 9; // blocker 5/8 screen width from left
	  boosterTwoBeginning = h * 16 / 18; // distance from top 1/8 screen
	  // height
	  boosterTwoEnd = h * 17 / 18; // distance from top 3/8 screen height
	  initialBoosterTwoVelocity = h / 3; // initial blocker speed multiplier
	  boosterTwo.start = new Point(boosterTwoDistance, boosterTwoBeginning);
	  boosterTwo.end = new Point(boosterTwoDistance, boosterTwoEnd);
	  boosterPaint.setStrokeWidth(lineWidthTwo);
	  boosterTwoPaint.setStrokeWidth(lineWidthTwo);
	     	  
  }
  public void newStartTwo(int w, int h, int oldw, int oldh)
  
  {
      super.onSizeChanged(w, h, oldw, oldh);

      screenWidth = w;  // store the width
      screenHeight = h; // store the height
      cannonBaseRadius = h / 15; // cannon base radius 1/15 screen height
     
      cannonLength = w / 7; // cannon length 1/8 screen width

      cannonballRadius = w / 28; // cannonball radius 1/18 screen width
     
      cannonballSpeed = w * 3 / 2; // cannonball speed multiplier

      lineWidth = w / 18; // target and blocker 1/14 screen width

      // configure instance variables related to the blocker
      blockerPaint.setColor(Color.GREEN);
      blockerTwoPaint.setColor(Color.GREEN);
      blockerThreePaint.setColor(Color.YELLOW);
     
      blockerDistance = w * 4 / 8; // blocker 5/8 screen width from left
      blockerBeginning = h / 8; // distance from top 1/8 screen height
      blockerEnd = h * 3 / 8; // distance from top 3/8 screen height
      initialBlockerVelocity = h / 3; // initial blocker speed multiplier
      blocker.start = new Point(blockerDistance, blockerBeginning);
      blocker.end = new Point(blockerDistance, blockerEnd);
     
      blockerTwoDistance = w * 3 / 8; //blocker two huge
      blockerTwoBeginning = h / 12 ;
      blockerTwoEnd = h * 2 / 7 ;
      initialBlockerTwoVelocity = -h/ 2;

      blockerTwo.start = new Point(blockerTwoDistance, blockerTwoBeginning);
      blockerTwo.end = new Point(blockerTwoDistance, blockerTwoEnd);

      blockerThreeDistance = 0; //blocker three huge
      blockerThreeBeginning = 0;
      blockerThreeEnd = h;
      initialBlockerThreeVelocity = -w / 2 ;

      blockerThree.start = new Point(blockerThreeDistance, blockerThreeBeginning);
      blockerThree.end = new Point(blockerThreeDistance, blockerThreeEnd);
     
     
      // configure instance variables related to the target
      targetDistance = w * 6 / 8; // target 7/8 screen width from left
      targetBeginning = h * 1 / 8; // distance from top 1/8 screen height
      targetEnd = h * 7 / 8; // distance from top 7/8 screen height
      pieceLength = (targetEnd - targetBeginning) / TARGET_PIECES;
      initialTargetVelocity = -h / 3; // initial target speed multiplier
      target.start = new Point(targetDistance, targetBeginning);
      target.end = new Point(targetDistance, targetEnd);

      // endpoint of the cannon's barrel initially points horizontally
      barrelEnd = new Point(cannonLength, h / 2);

      // configure Paint objects for drawing game elements
      textPaint.setTextSize(w / 10); // text size 1/10 of screen width
     
      textPaint.setAntiAlias(true); // smoothes the text
      textPaint.setColor(Color.WHITE); // Text color
 
     
      cannonPaint.setStrokeWidth(lineWidth * 2.5f); // set line thickness
      cannonPaint.setColor(Color.BLUE);
     
      blockerPaint.setStrokeWidth(lineWidth); // set line thickness  
      blockerTwoPaint.setStrokeWidth(lineWidth);
      targetPaint.setStrokeWidth(lineWidth); // set line thickness      
      backgroundPaint.setColor(Color.BLACK); // set background color

      cannonballPaint.setColor(Color.WHITE); // set cannonballPaint white
     
     
  }
  public void newStartThree(int w, int h, int oldw, int oldh)
  
  {
      super.onSizeChanged(w, h, oldw, oldh);
//      super.onSizeChanged(w, h, oldw, oldh);
      screenWidth = w; // store the width
      screenHeight = h; // store the height
      cannonBaseRadius = h / 15; // cannon base radius 1/15 screen height
      cannonLength = w / 7; // cannon length 1/8 screen width
     
// level 3
     
      cannonballRadius = w / 28; // cannonball radius 1/18 screen width
      cannonballSpeed = w * 3 / 2; // cannonball speed multiplier
      lineWidth = w / 18; // target and blocker 1/14 screen width
      lineWidthTwo = w / 36;
      // configure instance variables related to the blocker
      blockerPaint.setColor(Color.GREEN);
      blockerTwoPaint.setColor(Color.GREEN);
      blockerThreePaint.setColor(Color.YELLOW);
      blockerFourPaint.setColor(Color.BLUE);
      blockerFivePaint.setColor(Color.YELLOW);
      boosterPaint.setColor(Color.RED);
      boosterTwoPaint.setColor(Color.YELLOW);
     
      boosterDistance = w * 1 / 9; // blocker 5/8 screen width from left
      boosterBeginning = h * 1 / 18; // distance from top 1/8 screen height
      boosterEnd = h * 2 / 18; // distance from top 3/8 screen height
      initialBoosterVelocity = 0; // initial blocker speed multiplier
      
      booster.start = new Point(boosterDistance, boosterBeginning);
      booster.end = new Point(boosterDistance, boosterEnd);

      boosterTwoDistance = w * 1 / 9; // blocker 5/8 screen width from left
      boosterTwoBeginning = h * 16/ 18; // distance from top 1/8 screen height
      boosterTwoEnd = h * 17 / 18; // distance from top 3/8 screen height
      initialBoosterTwoVelocity = h / 3; // initial blocker speed multiplier
      boosterTwo.start = new Point(boosterTwoDistance, boosterTwoBeginning);
      boosterTwo.end = new Point(boosterTwoDistance, boosterTwoEnd);

     
     
     
      blockerDistance = w * 4 / 8; // blocker 5/8 screen width from left
      blockerBeginning = h / 8; // distance from top 1/8 screen height
      blockerEnd = h * 3 / 8; // distance from top 3/8 screen height
      initialBlockerVelocity = h / 3; // initial blocker speed multiplier
      blocker.start = new Point(blockerDistance, blockerBeginning);
      blocker.end = new Point(blockerDistance, blockerEnd);

      blockerTwoDistance = w * 5 / 8; //blocker two huge
      blockerTwoBeginning = h / 12 ;
      blockerTwoEnd = h * 2 / 7 ;
      initialBlockerTwoVelocity = -h/ 2;
      blockerTwo.start = new Point(blockerTwoDistance, blockerTwoBeginning);
      blockerTwo.end = new Point(blockerTwoDistance, blockerTwoEnd);

      blockerThreeDistance = w * 6/8 ; //blocker three huge
      blockerThreeBeginning = h * 2/5;
      blockerThreeEnd = h * 3/5;
      initialBlockerThreeVelocity = 0;
      blockerThree.start = new Point(blockerThreeDistance, blockerThreeBeginning);
      blockerThree.end = new Point(blockerThreeDistance, blockerThreeEnd);

      blockerFourDistance = 1; //blocker four huge
      blockerFourBeginning = 0;
      blockerFourEnd = h;
      initialBlockerFourVelocity = -w / 2 ;
      blockerFour.start = new Point(blockerFourDistance, blockerFourBeginning);
      blockerFour.end = new Point(blockerFourDistance, blockerFourEnd);


      blockerFiveDistance = w * 2 / 7; // blocker 5/8 screen width from left
      blockerFiveBeginning = h * 1 / 9; // distance from top 1/8 screen height
      blockerFiveEnd = h * 2 / 9; // distance from top 3/8 screen height
      initialBlockerFiveVelocity = h *  1/2 ; // initial blocker speed multiplier
      blockerFive.start = new Point(blockerFiveDistance, blockerFiveBeginning);
      blockerFive.end = new Point(blockerFiveDistance, blockerFiveEnd);
     
     
      // configure instance variables related to the target
      targetDistance = w * 8/9; // target 7/8 screen width from left
      targetBeginning = h * 1 / 8; // distance from top 1/8 screen height
      targetEnd = h * 7 / 8; // distance from top 7/8 screen height
      pieceLength = (targetEnd - targetBeginning) / TARGET_PIECES;
      initialTargetVelocity = -h / 3; // initial target speed multiplier
      target.start = new Point(targetDistance, targetBeginning);
      target.end = new Point(targetDistance, targetEnd);
      // endpoint of the cannon's barrel initially points horizontally
      barrelEnd = new Point(cannonLength, h / 2);
      // configure Paint objects for drawing game elements
      textPaint.setTextSize(w / 15); // text size 1/10 of screen width
      textPaint.setAntiAlias(true); // smoothes the text
      textPaint.setColor(Color.WHITE); // Text color
      cannonPaint.setStrokeWidth(lineWidth * 2.5f); // set line thickness
      cannonPaint.setColor(Color.BLUE);
      blockerPaint.setStrokeWidth(lineWidth); // set line thickness  
      blockerTwoPaint.setStrokeWidth(lineWidth);
      blockerFivePaint.setStrokeWidth(lineWidth);
      blockerThreePaint.setStrokeWidth(lineWidth);
      blockerFourPaint.setStrokeWidth(lineWidth);
      targetPaint.setStrokeWidth(lineWidth); // set line thickness  
      
      backgroundPaint.setColor(Color.BLACK); // set background color
      cannonballPaint.setColor(Color.WHITE); // set cannonballPaint white     
     
      boosterPaint.setStrokeWidth(lineWidthTwo);
      boosterTwoPaint.setStrokeWidth(lineWidthTwo);
     
     
  }  
  // reset all the screen elements and start a new game
  public void newGame()
  {
     // set every element of hitStates to false--restores target pieces
     for (int i = 0; i < TARGET_PIECES; ++i)
        hitStates[i] = false;
     GameStatus = 0;
     targetPiecesHit = 0; // no target pieces have been hit
     blockerVelocity = initialBlockerVelocity; // set initial velocity
     blockerTwoVelocity = initialBlockerTwoVelocity;
     targetVelocity = initialTargetVelocity; // set initial velocity
     //timeLeft = 10; // start the countdown at 10 seconds
     timeLeft = 30; // start the countdown at 20 seconds
     
     cannonballOnScreen = false; // the cannonball is not on the screen
     shotsFired = 0; // set the initial number of shots fired
     totalElapsedTime = 0.0; // set the time elapsed to zero
     blocker.start.set(blockerDistance, blockerBeginning);
     blocker.end.set(blockerDistance, blockerEnd);
     blockerTwo.start.set(blockerTwoDistance,  blockerTwoBeginning);
     blockerTwo.end.set(blockerTwoDistance, blockerTwoEnd);
     target.start.set(targetDistance, targetBeginning);
     target.end.set(targetDistance, targetEnd);
     
     if (gameOver)
     {
        gameOver = false; // the game is not over
        cannonThread = new CannonThread(getHolder());
        cannonThread.start();
//        newGame2();
     } // end if
  } // end method newGame
public void newGameTwo()
{
    // set every element of hitStates to false--restores target pieces
    for (int i = 0; i < TARGET_PIECES; ++i)
       hitStates[i] = false;

    targetPiecesHit = 0; // no target pieces have been hit
    blockerVelocity = initialBlockerVelocity; // set initial velocity
    blockerTwoVelocity = initialBlockerTwoVelocity;
    targetVelocity = initialTargetVelocity; // set initial velocity
    //timeLeft = 10; // start the countdown at 10 seconds
    //timeLeft = 15; // start the countdown at 20 seconds
    timeLeft += 15;
    
    cannonballOnScreen = false; // the cannonball is not on the screen
    shotsFired = 0; // set the initial number of shots fired
    totalElapsedTime = 0.0; // set the time elapsed to zero
    blocker.start.set(blockerDistance, blockerBeginning);
    blocker.end.set(blockerDistance, blockerEnd);
    blockerTwo.start.set(blockerTwoDistance,  blockerTwoBeginning);
    blockerTwo.end.set(blockerTwoDistance, blockerTwoEnd);
    blockerThree.start.set(blockerThreeDistance,  blockerThreeBeginning);
    blockerThree.end.set(blockerThreeDistance, blockerThreeEnd);
   
    target.start.set(targetDistance, targetBeginning);
    target.end.set(targetDistance, targetEnd);
   
    if (gameOver)
    {
       gameOver = false; // the game is not over
       cannonThread = new CannonThread(getHolder());
       cannonThread.start();
    } // end if
}

public void newGameThree()
{
    // set every element of hitStates to false--restores target pieces
    for (int i = 0; i < TARGET_PIECES; ++i)
       hitStates[i] = false;

    targetPiecesHit = 0; // no target pieces have been hit
    blockerVelocity = initialBlockerVelocity; // set initial velocity
    blockerTwoVelocity = initialBlockerTwoVelocity;
    blockerFiveVelocity = initialBlockerFiveVelocity;
    targetVelocity = initialTargetVelocity; // set initial velocity
    //timeLeft = 10; // start the countdown at 10 seconds
    //timeLeft = 15; // start the countdown at 20 seconds
    timeLeft += 30;
   
    cannonballOnScreen = false; // the cannonball is not on the screen
    shotsFired = 0; // set the initial number of shots fired
    totalElapsedTime = 0.0; // set the time elapsed to zero
    blocker.start.set(blockerDistance, blockerBeginning);
    blocker.end.set(blockerDistance, blockerEnd);
    blockerTwo.start.set(blockerTwoDistance,  blockerTwoBeginning);
    blockerTwo.end.set(blockerTwoDistance, blockerTwoEnd);
    blockerFive.start.set(blockerFiveDistance,  blockerFiveBeginning);
    blockerFive.end.set(blockerFiveDistance, blockerFiveEnd);
   
   
    target.start.set(targetDistance, targetBeginning);
    target.end.set(targetDistance, targetEnd);
   
    if (gameOver)
    {
       gameOver = false; // the game is not over
       cannonThread = new CannonThread(getHolder());
       cannonThread.start();
    } // end if
 } // end method newGame

  // called repeatedly by the CannonThread to update game elements
  private void updatePositions(double elapsedTimeMS)
  {
     double interval = elapsedTimeMS / 1000.0; // convert to seconds

     switch(GameStatus){
     case 0:
     //game0															STAGE 1 Update Positions
     if (cannonballOnScreen) // if there is currently a shot fired
     {
    	 // update cannonball position
    	 cannonball.x += interval * cannonballVelocityX;
    	 cannonball.y += interval * cannonballVelocityY;
    	 
    	 // check for collision with blocker
    	 if (cannonball.x + cannonballRadius > blockerDistance && 
    			 cannonball.x - cannonballRadius < blockerDistance &&
    			 cannonball.y + cannonballRadius > blocker.start.y &&
    			 cannonball.y - cannonballRadius < blocker.end.y)
    	 {
    		 cannonballVelocityX *= -1; // reverse cannonball's direction
    		 timeLeft -= MISS_PENALTY; // penalize the user
    		 
    		 // play blocker sound
    		 soundPool.play(soundMap.get(BLOCKER_SOUND_ID), 1, 1, 1, 0, 1f);
    	 } // end if
    	 
         // check for collisions with left and right walls
         else if (cannonball.x > screenWidth || cannonball.x < 0)
         {
              cannonballVelocityX *= -1;
              //cannonballOnScreen = false; // remove cannonball from screen
                // play blocker sound
              soundPool.play(soundMap.get(DOINK_SOUND_ID), 1, 1, 1, 0, 1f);
        
         }
           // check for collisions with top and bottom walls  4 BOUNCE BACK
           else if (cannonball.y > screenHeight || cannonball.y < 0)
           {
//                cannonballVelocityY *= -1; // reverse cannonball's direction
                cannonballOnScreen = false; // make the cannonball disappear
                // play blocker sound
//                soundPool.play(soundMap.get(DOINK_SOUND_ID), 1, 1, 1, 0, 1f);
               
           }
    	 
    	 
//    	 // check for collisions with left and right walls
//    	 else if (cannonball.x + cannonballRadius > screenWidth || 
//    			 cannonball.x - cannonballRadius < 0)
//    		 cannonballOnScreen = false; // remove cannonball from screen
//    	 
//    	 // check for collisions with top and bottom walls
//    	 else if (cannonball.y + cannonballRadius > screenHeight || 
//    			 cannonball.y - cannonballRadius < 0)
//    		 cannonballOnScreen = false; // make the cannonball disappear
    	 
    	 // check for cannonball collision with target
    	 else if (cannonball.x + cannonballRadius > targetDistance && 
    			 cannonball.x - cannonballRadius < targetDistance && 
    			 cannonball.y + cannonballRadius > target.start.y &&
    			 cannonball.y - cannonballRadius < target.end.y)
    	 {
    		 // determine target section number (0 is the top)
    		 int section = 
    				 (int) ((cannonball.y - target.start.y) / pieceLength);
    		 
    		 // check if the piece hasn't been hit yet
    		 if ((section >= 0 && section < TARGET_PIECES) && 
    				 !hitStates[section])
    		 {
    			 hitStates[section] = true; // section was hit
    			 cannonballOnScreen = false; // remove cannonball
    			 timeLeft += HIT_REWARD; // add reward to remaining time
    			 
    			 // play target hit sound
    			 soundPool.play(soundMap.get(TARGET_SOUND_ID), 1,
    					 1, 1, 0, 1f);
    			 
    			 // if all pieces have been hit
    			 if (++targetPiecesHit == TARGET_PIECES)
    			 {
    				 cannonThread.setRunning(false);
    				 // play blocker sound
    				 soundPool.play(soundMap.get(WIN_SOUND_ID), 1, 1, 1, 0, 1f);
    				 GameStatus = 1;
    				 showGameOverDialog(R.string.win); // show winning dialog   FIRST STAGE WIN
    				 //releaseResources();
    				 //drawGame2Elements(canvas canvas);
    				 cannonThread.setRunning(true);
    				 newGameTwo();
    				 
//                 newStartTwo();
//    				 gameOver = true; // the game is over
    				 gameOver = false;
    			 } // end if
    		 } // end if
    	 } // end else if
     } // end if

    	 
     // update the blocker's position
     double blockerUpdate = interval * blockerVelocity;
     blocker.start.y += blockerUpdate;
     blocker.end.y += blockerUpdate;
     
     double blockerTwoUpdate = interval * blockerTwoVelocity;
     blockerTwo.start.y += blockerTwoUpdate;
     blockerTwo.end.y += blockerTwoUpdate;
     
     
     // update the target's position
     double targetUpdate = interval * targetVelocity;
     target.start.y += targetUpdate;
     target.end.y += targetUpdate;
     
     // if the blocker hit the top or bottom, reverse direction
     if (blocker.start.y < 0 || blocker.end.y > screenHeight)
    	 blockerVelocity *= -1;
     
     // if the blockerTwo hits the top or bottom, reverse direciton
     if (blockerTwo.start.y < 0 || blockerTwo.end.y > screenHeight)
    	 blockerTwoVelocity *= -1;
     
     // if the target hit the top or bottom, reverse direction
     if (target.start.y < 0 || target.end.y > screenHeight)
    	 targetVelocity *= -1;
     
     timeLeft -= interval; // subtract from time left
     
     // if the timer reached zero
     if (timeLeft <= 0.0)
     {
    	 
    	 timeLeft = 0.0;
    	 gameOver = true; // the game is over
    	 cannonThread.setRunning(false);
    	 // play blocker sound
    	 soundPool.play(soundMap.get(LOSE_SOUND_ID), 1, 1, 1, 0, 1f);
    	 GameStatus = 0;
    	 showGameOverDialog(R.string.lose); // show the losing dialog    FIRST STAGE LOSE
     } // end if
     return;
     
     case 1:																// STAGE TWO Update Positions
         if (cannonballOnScreen) // if there is currently a shot fired
         {
            // update cannonball position
            cannonball.x += interval * cannonballVelocityX;
            cannonball.y += interval * cannonballVelocityY;

            // check for collision with blocker
            if (cannonball.x + cannonballRadius > blockerDistance &&
               cannonball.x - cannonballRadius < blockerDistance &&
               cannonball.y + cannonballRadius > blocker.start.y &&
               cannonball.y - cannonballRadius < blocker.end.y)
            {
               cannonballVelocityX *= -1; // reverse cannonball's direction
               timeLeft -= MISS_PENALTY; // penalize the user

               // play blocker sound
               soundPool.play(soundMap.get(BLOCKER_SOUND_ID), 1, 1, 1, 0, 1f);
            } // end if

            // check for collision with blockerTwo
            if (cannonball.x + cannonballRadius > blockerTwoDistance &&
               cannonball.x - cannonballRadius < blockerTwoDistance &&
               cannonball.y + cannonballRadius > blockerTwo.start.y &&
               cannonball.y - cannonballRadius < blockerTwo.end.y)
            {
               cannonballVelocityX *= -1; // reverse cannonball's direction
               timeLeft -= MISS_PENALTY; // penalize the user

               // play blocker sound
               soundPool.play(soundMap.get(BLOCKER_SOUND_ID), 1, 1, 1, 0, 1f);
            } // end if 
           
            // check for collision with blockerThree
            if (cannonball.x + cannonballRadius > blockerThreeDistance &&
               cannonball.x - cannonballRadius < blockerThreeDistance &&
               cannonball.y + cannonballRadius > blockerThree.start.y &&
               cannonball.y - cannonballRadius < blockerThree.end.y)
            {
               cannonballVelocityX *= -1; // reverse cannonball's direction
               //timeLeft -= MISS_PENALTY; // penalize the user

               // play blocker sound
               soundPool.play(soundMap.get(DOINK_SOUND_ID), 1, 1, 1, 0, 1f);
            } // end if          
            // check for collisions with left and right walls
            else if (cannonball.x > screenWidth || cannonball.x < 0)
            {
                 cannonballVelocityX *= -1;
                 //cannonballOnScreen = false; // remove cannonball from screen
                   // play blocker sound
                 soundPool.play(soundMap.get(DOINK_SOUND_ID), 1, 1, 1, 0, 1f);
           
            }
              // check for collisions with top and bottom walls  4 BOUNCE BACK
              else if (cannonball.y > screenHeight || cannonball.y < 0)
              {
//                   cannonballVelocityY *= -1; // reverse cannonball's direction
                   cannonballOnScreen = false; // make the cannonball disappear
                   // play blocker sound
//                   soundPool.play(soundMap.get(DOINK_SOUND_ID), 1, 1, 1, 0, 1f);
                  
              }
       	            
           
           
//            // check for collisions with left and right walls
//            else if (cannonball.x + cannonballRadius > screenWidth ||
//               cannonball.x - cannonballRadius < 0)
//               cannonballOnScreen = false; // remove cannonball from screen
//
//            // check for collisions with top and bottom walls
//            else if (cannonball.y + cannonballRadius > screenHeight ||
//               cannonball.y - cannonballRadius < 0)
//               cannonballOnScreen = false; // make the cannonball disappear

            // check for cannonball collision with target
            else if (cannonball.x + cannonballRadius > targetDistance &&
               cannonball.x - cannonballRadius < targetDistance &&
               cannonball.y + cannonballRadius > target.start.y &&
               cannonball.y - cannonballRadius < target.end.y)
            {
               // determine target section number (0 is the top)
               int section =
                  (int) ((cannonball.y - target.start.y) / pieceLength);
              
               // check if the piece hasn't been hit yet
               if ((section >= 0 && section < TARGET_PIECES) &&
                  !hitStates[section])
               {
                  hitStates[section] = true; // section was hit
                  cannonballOnScreen = false; // remove cannonball
                  timeLeft += HIT_REWARD; // add reward to remaining time

                  // play target hit sound
                  soundPool.play(soundMap.get(TARGET_SOUND_ID), 1,
                     1, 1, 0, 1f);

                  // if all pieces have been hit
                  if (++targetPiecesHit == TARGET_PIECES)
                  {
                     cannonThread.setRunning(false);
                     // play blocker sound
                     soundPool.play(soundMap.get(WIN_SOUND_ID), 1, 1, 1, 0, 1f); //SECOND STAGE WIN
                     GameStatus = 2;
                     showGameOverDialog(R.string.win); // show winning dialog
    				 //releaseResources();
    				 //drawGame2Elements(canvas canvas);
    				 cannonThread.setRunning(true);
    				 newGameThree();
    				 
//                 newStartTwo();
//    				 gameOver = true; // the game is over
    				 gameOver = false;
                  } // end if
               } // end if
            } // end else if
         } // end if

         // update the blocker's position
         double blockerUpdate1 = interval * blockerVelocity;
         blocker.start.y += blockerUpdate1;
         blocker.end.y += blockerUpdate1;
        
         double blockerTwoUpdate1 = interval * blockerTwoVelocity;
         blockerTwo.start.y += blockerTwoUpdate1;
         blockerTwo.end.y += blockerTwoUpdate1;
        

         // update the target's position
         double targetUpdate1 = interval * targetVelocity;
         target.start.y += targetUpdate1;
         target.end.y += targetUpdate1;

         // if the blocker hit the top or bottom, reverse direction
         if (blocker.start.y < 0 || blocker.end.y > screenHeight)
            blockerVelocity *= -1;
        
         // if the blockerTwo hits the top or bottom, reverse direciton
         if (blockerTwo.start.y < 0 || blockerTwo.end.y > screenHeight)
              blockerTwoVelocity *= -1;
        
         // if the target hit the top or bottom, reverse direction
         if (target.start.y < 0 || target.end.y > screenHeight)
            targetVelocity *= -1;

         timeLeft -= interval; // subtract from time left

         // if the timer reached zero
         if (timeLeft <= 0.0)
         {
            timeLeft = 0.0;
            gameOver = true; // the game is over
            cannonThread.setRunning(false);
            // play blocker sound
            soundPool.play(soundMap.get(LOSE_SOUND_ID), 1, 1, 1, 0, 1f);
            GameStatus = 0;
            showGameOverDialog(R.string.lose); // show the losing dialog SECOND STAGE LOSE
         } // end if
         return;
     case 2:
         if (cannonballOnScreen) // if there is currently a shot fired
         {
            // update cannonball position
            cannonball.x += interval * cannonballVelocityX;
            cannonball.y += interval * cannonballVelocityY;

            // check for collision with blocker
            if (cannonball.x + cannonballRadius > blockerDistance &&
               cannonball.x - cannonballRadius < blockerDistance &&
               cannonball.y + cannonballRadius > blocker.start.y &&
               cannonball.y - cannonballRadius < blocker.end.y)
            {
               cannonballVelocityX *= -1; // reverse cannonball's direction
               timeLeft -= MISS_PENALTY; // penalize the user
//               backgroundPaint.setColor(Color.DKGRAY); // set background color
//               cannonballPaint.setColor(Color.BLACK);
               // play blocker sound
               soundPool.play(soundMap.get(BLOCKER_SOUND_ID), 1, 1, 1, 0, 1f);
            } // end if

            // check for collision with blockerTwo
            if (cannonball.x + cannonballRadius > blockerTwoDistance &&
               cannonball.x - cannonballRadius < blockerTwoDistance &&
               cannonball.y + cannonballRadius > blockerTwo.start.y &&
               cannonball.y - cannonballRadius < blockerTwo.end.y)
            {
               cannonballVelocityX *= -1; // reverse cannonball's direction
               timeLeft -= MISS_PENALTY; // penalize the user
   
               // play blocker sound
               soundPool.play(soundMap.get(BLOCKER_SOUND_ID), 1, 1, 1, 0, 1f);
            } // end if 
           
            // check for collision with blockerThree ( Yellow
            if (cannonball.x + cannonballRadius > blockerThreeDistance &&
               cannonball.x - cannonballRadius < blockerThreeDistance &&
               cannonball.y + cannonballRadius > blockerThree.start.y &&
               cannonball.y - cannonballRadius < blockerThree.end.y)
            {
               cannonballVelocityX *= -1; // reverse cannonball's direction
               timeLeft += HIT_REWARD;
               //               timeLeft += MISS_PENALTY; // boost the users time
//               if (timeLeft < 5.0)
//               {
//               cannonballPaint.setColor(Color.BLUE); // set cannonballPaint white
//               cannonballSpeed = screenWidth * 1 / 4;
//               }
//               else
//               {
//                    cannonballPaint.setColor(Color.GREEN);
//                    cannonballSpeed = screenWidth * 1/6;
//               }
               // play blocker sound
               soundPool.play(soundMap.get(DOINK_SOUND_ID), 1, 1, 1, 0, 1f);
            } // end if

            // check for collision with blockerFour ( BLUE
            if (cannonball.x > blockerFourDistance &&
               cannonball.x < blockerFourDistance &&
               cannonball.y > blockerFour.start.y &&
               cannonball.y < blockerFour.end.y)
            {
               cannonballVelocityX *= -1; // reverse cannonball's direction
               //cannonballVelocityX =- 1;
               //cannonballVelocityY =+ 5;
               //timeLeft += MISS_PENALTY; // penalize the user

               // play blocker sound
               soundPool.play(soundMap.get(DOINK_SOUND_ID), 1, 1, 1, 0, 1f);
            } // end if


            // check for collision with blockerFive
            if (cannonball.x + cannonballRadius > blockerFiveDistance &&
               cannonball.x - cannonballRadius < blockerFiveDistance &&
               cannonball.y + cannonballRadius > blockerFive.start.y &&
               cannonball.y - cannonballRadius < blockerFive.end.y)
            {
               cannonballVelocityX *= -1; // reverse cannonball's direction
               timeLeft -= MISS_PENALTY; // penalize the user
//               backgroundPaint.setColor(Color.BLACK); // set background color
//               cannonballPaint.setColor(Color.DKGRAY);
               // play blocker sound
               soundPool.play(soundMap.get(BLOCKER_SOUND_ID), 1, 1, 1, 0, 1f);
            } // end if

           
            // check for collision with booster ( SPEED
            if (cannonball.x + cannonballRadius > boosterDistance &&
               cannonball.x - cannonballRadius < boosterDistance &&
               cannonball.y + cannonballRadius > booster.start.y &&
               cannonball.y - cannonballRadius < booster.end.y)
            {
               cannonballVelocityX *= -1; // reverse cannonball's direction
               cannonballPaint.setColor(Color.RED); // set cannonballPaint white
               cannonballPaint.setShader(radGrad2);
//               cannonballSpeed = screenWidth * 4 / 2;
//               cannonballSpeed = 1000 * 4 / 2;
               cannonballSpeed = 1320 * 4 / 2;
               cannonballRadius -= 1;
               // play blocker sound
               soundPool.play(soundMap.get(BOOSTER_SOUND_ID), 1, 1, 1, 0, 1f);
            } // end if
           
            // check for collision with booster ( TIME/size
            if (cannonball.x + cannonballRadius > boosterTwoDistance &&
               cannonball.x - cannonballRadius < boosterTwoDistance &&
               cannonball.y + cannonballRadius > boosterTwo.start.y &&
               cannonball.y - cannonballRadius < boosterTwo.end.y)
            {
               cannonballVelocityX *= -1; // reverse cannonball's direction
               cannonballPaint.setColor(Color.YELLOW); // set cannonballPaint white
               cannonballRadius += 1;
               //timeLeft += HIT_REWARD;
               //cannonballSpeed = screenWidth * 4 / 2;
               cannonballSpeed -= 1;
               // play blocker sound
               soundPool.play(soundMap.get(BOOSTERTWO_SOUND_ID), 1, 1, 1, 0, 1f);
            } // end if        
           
           
//            // check for collisions with left and right walls
//            else if (cannonball.x + cannonballRadius > screenWidth ||
//               cannonball.x - cannonballRadius < 0)
//               cannonballOnScreen = false; // remove cannonball from screen
   //
//            // check for collisions with top and bottom walls
//            else if (cannonball.y + cannonballRadius > screenHeight ||
//               cannonball.y - cannonballRadius < 0)
//               cannonballOnScreen = false; // make the cannonball disappear
           
          // check for collisions with left and right walls
          else if (cannonball.x > screenWidth || cannonball.x < 0)
          {
               cannonballVelocityX *= -1;
               //cannonballOnScreen = false; // remove cannonball from screen
                 // play blocker sound
               soundPool.play(soundMap.get(DOINK_SOUND_ID), 1, 1, 1, 0, 1f);
         
          }
            // check for collisions with top and bottom walls  4 BOUNCE BACK
            else if (cannonball.y > screenHeight || cannonball.y < 0)
            {
//                 cannonballVelocityY *= -1; // reverse cannonball's direction
                 cannonballOnScreen = false; // make the cannonball disappear
                 // play blocker sound
//                 soundPool.play(soundMap.get(DOINK_SOUND_ID), 1, 1, 1, 0, 1f);
                
            }
           
            // check for cannonball collision with target
            else if (cannonball.x + cannonballRadius > targetDistance &&
               cannonball.x - cannonballRadius < targetDistance &&
               cannonball.y + cannonballRadius > target.start.y &&
               cannonball.y - cannonballRadius < target.end.y)
            {
               // determine target section number (0 is the top)
               int section =
                  (int) ((cannonball.y - target.start.y) / pieceLength);
              
               // check if the piece hasn't been hit yet
               if ((section >= 0 && section < TARGET_PIECES) &&
                  !hitStates[section])
               {
                  hitStates[section] = true; // section was hit
                  cannonballOnScreen = false; // remove cannonball
                  timeLeft += HIT_REWARD; // add reward to remaining time

                  // play target hit sound
                  soundPool.play(soundMap.get(TARGET_SOUND_ID), 1,
                     1, 1, 0, 1f);

                  // if all pieces have been hit
                  if (++targetPiecesHit == TARGET_PIECES)
                  {
                     cannonThread.setRunning(false);
                     // play win sound
//                     soundPool.play(soundMap.get(WIN_SOUND_ID), 1, 1, 1, 0, 1f);
                     soundPool.play(soundMap.get(HISCORE_SOUND_ID), 1, 1, 1, 0, 1f);

                     GameStatus = 0;
                     showGameOverDialog(R.string.win); // show winning dialog   STAGE THREE WIN
                     gameOver = true; // the game is over
                  } // end if
                  return;
       // end method upatePositions
           
               }}}} // end method updatePositions
     // update the blocker's position
     double blockerUpdate = interval * blockerVelocity;
     blocker.start.y += blockerUpdate;
     blocker.end.y += blockerUpdate;
    
     double blockerTwoUpdate = interval * blockerTwoVelocity;
     blockerTwo.start.y += blockerTwoUpdate;
     blockerTwo.end.y += blockerTwoUpdate;

     double blockerFiveUpdate = interval * blockerFiveVelocity;
     blockerFive.start.y += blockerFiveUpdate;
     blockerFive.end.y += blockerFiveUpdate;

     // update the target's position
     double targetUpdate = interval * targetVelocity;
     target.start.y += targetUpdate;
     target.end.y += targetUpdate;

     // if the blocker hit the top or bottom, reverse direction
     if (blocker.start.y < 0 || blocker.end.y > screenHeight)
        blockerVelocity *= -1;
    
     // if the blockerTwo hits the top or bottom, reverse direciton
     if (blockerTwo.start.y < 0 || blockerTwo.end.y > screenHeight)
          blockerTwoVelocity *= -1;

     // if the blockerFive hits the top or bottom, reverse direciton
     if (blockerFive.start.y < 0 || blockerFive.end.y > screenHeight)
          blockerFiveVelocity *= -1;
    
     // if the target hit the top or bottom, reverse direction
     if (target.start.y < 0 || target.end.y > screenHeight)
        targetVelocity *= -1;

     timeLeft -= interval; // subtract from time left

     // if the timer reached zero
     if (timeLeft <= 0.0)
     {
        timeLeft = 0.0;
        gameOver = true; // the game is over
        cannonThread.setRunning(false);
        // play blocker sound
        soundPool.play(soundMap.get(LOSE_SOUND_ID), 1, 1, 1, 0, 1f);
        cannonballPaint.setColor(Color.WHITE); // reset the color to white
//        cannonballRadius = screenWidth / 28;
//        cannonballSpeed = screenWidth * 3/2;
	     cannonballRadius = screenWidth / 36; // cannonball radius 1/18 screen width
	     
//	     cannonballSpeed = screenWidth * 3 / 2; // cannonball speed multiplier
//	     cannonballSpeed = 1000;
	     cannonballSpeed = 1320;
	     showGameOverDialog(R.string.lose); // show the losing dialog				THIRD STAGE LOSE
        //GameStatus = 0;// starts all the way over @ beginning @ lose
        if (GameStatus > 0)
        
        	GameStatus -=1;
        else
        	GameStatus = 0;
        }
        return;
        
     } // end if     

  // fires a cannonball
  public void fireCannonball(MotionEvent event)
  {
	  cannonPaint.setShadowLayer(15, 5, 0, Color.YELLOW);
	  
//     if (cannonballOnScreen) // if a cannonball is already on the screen
//        return; // do nothing

     double angle = alignCannon(event); // get the cannon barrel's angle

     // move the cannonball to be inside the cannon
     cannonball.x = cannonballRadius; // align x-coordinate with cannon
     cannonball.y = screenHeight / 2; // centers ball vertically

     // get the x component of the total velocity
     cannonballVelocityX = (int) (cannonballSpeed * Math.sin(angle));

     // get the y component of the total velocity
     cannonballVelocityY = (int) (-cannonballSpeed * Math.cos(angle));
     cannonballOnScreen = true; // the cannonball is on the screen

     cannonPaint.setShadowLayer(0, 5, 0, Color.RED);
     
     
     ++shotsFired; // increment shotsFired

     // play cannon fired sound
     switch (cannonballPaint.getColor())
     {
     case Color.WHITE:
    	 soundPool.play(soundMap.get(CANNON_SOUND_ID), 1, 1, 1, 0, 1f);
    	 return;
     case Color.RED:
    	 soundPool.play(soundMap.get(BOOSTER_SOUND_ID), 1, 1, 1, 0, 1f);
    	 return;
     case Color.YELLOW:
    	 soundPool.play(soundMap.get(BOOSTERTWO_SOUND_ID), 1, 1, 1, 0, 1f);
    	 return;
    
     }
//	  cannonPaint.setShadowLayer(0, 5, 0, Color.YELLOW);

  } // end method fireCannonball

  // aligns the cannon in response to a user touch
  public double alignCannon(MotionEvent event)
  {
     // get the location of the touch in this view
     Point touchPoint = new Point((int) event.getX(), (int) event.getY());

     // compute the touch's distance from center of the screen
     // on the y-axis
     double centerMinusY = (screenHeight / 2 - touchPoint.y);

     double angle = 0; // initialize angle to 0

     // calculate the angle the barrel makes with the horizontal
     if (centerMinusY != 0) // prevent division by 0
        angle = Math.atan((double) touchPoint.x / centerMinusY);

     // if the touch is on the lower half of the screen
     if (touchPoint.y > screenHeight / 2)
        angle += Math.PI; // adjust the angle

     // calculate the endpoint of the cannon barrel
     barrelEnd.x = (int) (cannonLength * Math.sin(angle));
     barrelEnd.y = 
        (int) (-cannonLength * Math.cos(angle) + screenHeight / 2);

     return angle; // return the computed angle
  } // end method alignCannon

  // draws the game to the given Canvas

  public void drawGameElements(Canvas canvas)
  {
	  
	  switch(GameStatus){
	  case 0:
		  // clear the background
		  canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);

		  canvas.drawBitmap(mBackgroundImage,canvas.getWidth(), canvas.getHeight(), backgroundPaint);
//	
//		  canvas.drawBitmap(mBackgroundImage,0, 0, backgroundPaint);
			
		// display stage_name
	      canvas.drawText(getResources().getString(R.string.stage_one), screenWidth * 1/4, screenHeight * 1/16, textPaint);

//		   // display ScreenWidth & ScreenHeight
//		      canvas.drawText("w="+screenWidth+" h="+screenHeight, screenWidth * 1/15, screenHeight * 10/16, targetPaint);
//		   // display cannonballSpeed
//		      canvas.drawText("v="+ cannonballSpeed, screenWidth * 1/15, screenHeight * 11/16, targetPaint);
//	      // display cannonballRadius
//	      canvas.drawText("r=" + cannonballRadius, screenWidth * 1/15, screenHeight * 12/16, targetPaint);
//		   // display booksbroken & shotsfired
//	      canvas.drawText(" Hit=" + targetPiecesHit, screenWidth * 1/15, screenHeight * 13/16, targetPaint);
//	      canvas.drawText("Shot=" + shotsFired, screenWidth * 1/15, screenHeight * 14/16, targetPaint);
	        
	    	  
//	      canvas.drawText("Ratio="+ targetPiecesHit/shotsFired, screenWidth * 1/15, screenHeight * 15/16, scorePaint);
	      
	    		  
	    
	      // display time remaining
	      canvas.drawText(getResources().getString(R.string.time_remaining_format, timeLeft), screenWidth * 1/15, screenHeight * 16/16, textPaint);
		  
		  
		  // if a cannonball is currently on the screen, draw it
		  if (cannonballOnScreen)
			  canvas.drawCircle(cannonball.x, cannonball.y, cannonballRadius,
					  cannonballPaint);
		  
		  // draw the cannon barrel
		  canvas.drawLine(0, screenHeight / 2, barrelEnd.x, barrelEnd.y,
				  cannonPaint);
		  
		  // draw the cannon base
		  canvas.drawCircle(0, (int) screenHeight / 2,
				  (int) cannonBaseRadius, cannonPaint);
		  
		  // draw the blocker
		  canvas.drawLine(blocker.start.x, blocker.start.y, blocker.end.x,
				  blocker.end.y, blockerPaint);
		  
		  // draw the blockerTwo
		  /*canvas.drawLine(blockerTwo.start.x, blockerTwo.start.y, blockerTwo.end.x,
   blockerTwo.end.y, blockerTwoPaint);
		   */
		  
		  Point currentPoint = new Point(); // start of current target section
		  
		  // initialize curPoint to the starting point of the target
		  currentPoint.x = target.start.x;
		  currentPoint.y = target.start.y;
  
		  
		  
		  // draw the target
		  for (int i = 1; i <= TARGET_PIECES; ++i)
		  {
			  // if this target piece is not hit, draw it
			  if (!hitStates[i - 1])
			  {
				  // alternate coloring the pieces yellow and blue
				  if (i % 2 == 0)
					  targetPaint.setColor(Color.YELLOW);
				  else
					  targetPaint.setColor(Color.RED);
				  canvas.drawLine(currentPoint.x, currentPoint.y, 
						  target.end.x, (int) (currentPoint.y + pieceLength),
						  targetPaint);
			  } // end if
			  
			  // move curPoint to the start of the next piece
			  currentPoint.y += pieceLength;
		  } // end for
		  
		  return;
	  case 1:
	      // clear the background
	      canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
	         backgroundPaint);
		     // disply stage_name
	      canvas.drawText(getResources().getString(R.string.stage_two), screenWidth * 1/4, screenHeight * 1/16, textPaint);

//		   // display ScreenWidth & ScreenHeight
//	      canvas.drawText("w="+screenWidth+" h="+screenHeight, screenWidth * 1/15, screenHeight * 10/16, targetPaint);
//	   // display cannonballSpeed
//	      canvas.drawText("v="+ cannonballSpeed, screenWidth * 1/15, screenHeight * 11/16, targetPaint);
//      // display cannonballRadius
//      canvas.drawText("r=" + cannonballRadius, screenWidth * 1/15, screenHeight * 12/16, targetPaint);
//	   // display booksbroken & shotsfired
//      canvas.drawText(" Hit=" + targetPiecesHit, screenWidth * 1/15, screenHeight * 13/16, targetPaint);
//      canvas.drawText("Shot=" + shotsFired, screenWidth * 1/15, screenHeight * 14/16, targetPaint);
//        
    	  
//      canvas.drawText("Ratio="+ targetPiecesHit/shotsFired, screenWidth * 1/15, screenHeight * 15/16, scorePaint);
	      
	      // display time remaining
	      canvas.drawText(getResources().getString(
	         R.string.time_remaining_format, timeLeft), screenWidth * 1/15, screenHeight * 8/8, textPaint);
	      // display time remaining
//	      canvas.drawText(getResources().getString(
//	         R.string.time_remaining_format, timeLeft), 40, 600, textPaint);
//	      //R.string.time_remaining_format, timeLeft), 30, 50, textPaint);     
	     
	            
	      // if a cannonball is currently on the screen, draw it
	      if (cannonballOnScreen)
	         canvas.drawCircle(cannonball.x, cannonball.y, cannonballRadius,
	            cannonballPaint);

	      // draw the cannon barrel
	      canvas.drawLine(0, screenHeight / 2, barrelEnd.x, barrelEnd.y,
	         cannonPaint);

	      // draw the cannon base
	      canvas.drawCircle(0, (int) screenHeight / 2,
	         (int) cannonBaseRadius, cannonPaint);

	      // draw the blocker
	      canvas.drawLine(blocker.start.x, blocker.start.y, blocker.end.x,
	         blocker.end.y, blockerPaint);

	      // draw the blockerTwo
	      canvas.drawLine(blockerTwo.start.x, blockerTwo.start.y, blockerTwo.end.x,
	          blockerTwo.end.y, blockerPaint);
	    
	      // draw the blockerThree
	      canvas.drawLine(blockerThree.start.x, blockerThree.start.y, blockerThree.end.x,
	                blockerThree.end.y, blockerThreePaint);
	     
	     
	      Point currentPoint1 = new Point(); // start of current target section

	      // initialize curPoint to the starting point of the target
	      currentPoint1.x = target.start.x;
	      currentPoint1.y = target.start.y;



	      // draw the target
	      for (int i = 1; i <= TARGET_PIECES; ++i)
	      {
	         // if this target piece is not hit, draw it
	         if (!hitStates[i - 1])
	         {
	            // alternate coloring the pieces yellow and blue
	            if (i % 2 == 0)
	               targetPaint.setColor(Color.YELLOW);
	            else
	               targetPaint.setColor(Color.RED);
//	                 targetPaint.setColor(Color.RED);
	            canvas.drawLine(currentPoint1.x, currentPoint1.y,
	                      target.end.x, (int) (currentPoint1.y + pieceLength),
	                      targetPaint);
	         } // end if
	        
	         // move curPoint to the start of the next piece
	         currentPoint1.y += pieceLength;
	      } // end for
	  
		  return;
	  case 2:
	      // clear the background
	      canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(),
	         backgroundPaint);
	     // disply stage_name
	      canvas.drawText(getResources().getString(R.string.stage_three), screenWidth * 1/4, screenHeight * 1/16, textPaint);
	      
//		   // display ScreenWidth & ScreenHeight
//	      canvas.drawText("w="+screenWidth+" h="+screenHeight, screenWidth * 1/15, screenHeight * 10/16, targetPaint);
//	   // display cannonballSpeed
//	      canvas.drawText("v="+ cannonballSpeed, screenWidth * 1/15, screenHeight * 11/16, targetPaint);
//      // display cannonballRadius
//      canvas.drawText("r=" + cannonballRadius, screenWidth * 1/15, screenHeight * 12/16, targetPaint);
//	   // display booksbroken & shotsfired
//      canvas.drawText(" Hit=" + targetPiecesHit, screenWidth * 1/15, screenHeight * 13/16, targetPaint);
//      canvas.drawText("Shot=" + shotsFired, screenWidth * 1/15, screenHeight * 14/16, targetPaint);
//        
    	  
//      canvas.drawText("Ratio="+ targetPiecesHit/shotsFired, screenWidth * 1/15, screenHeight * 15/16, scorePaint);
     
      
      
	      // display time remaining
	      canvas.drawText(getResources().getString(
	         R.string.time_remaining_format, timeLeft), screenWidth * 1/15, screenHeight * 8/8, textPaint);
	      //R.string.time_remaining_format, timeLeft), 30, 50, textPaint);     
	     
//	      // display books broken
//	      canvas.drawText(getResources().getString(
//	                R.string.results_format, targetPiecesHit), 40, 60, textPaint);
	            
	      // if a cannonball is currently on the screen, draw it
	      if (cannonballOnScreen)
	         canvas.drawCircle(cannonball.x, cannonball.y, cannonballRadius,
	            cannonballPaint);

	      // draw the cannon barrel
	      canvas.drawLine(0, screenHeight / 2, barrelEnd.x, barrelEnd.y,
	         cannonPaint);

	      // draw the cannon base
	      canvas.drawCircle(0, (int) screenHeight / 2,
	         (int) cannonBaseRadius, cannonPaint);

	      // draw the blocker
	      canvas.drawLine(blocker.start.x, blocker.start.y, blocker.end.x,
	         blocker.end.y, blockerPaint);

//	      // draw the blockerTwo
//	      canvas.drawLine(blockerTwo.start.x, blockerTwo.start.y, blockerTwo.end.x,
//	          blockerTwo.end.y, blockerTwoPaint);

	      	      // draw the blockerTwo
	      canvas.drawLine(blockerTwo.start.x, blockerTwo.start.y, blockerTwo.end.x,
	          blockerTwo.end.y, blockerPaint);

	      
	      //	    
	      // draw the blockerThree
	      canvas.drawLine(blockerThree.start.x, blockerThree.start.y, blockerThree.end.x,
	                blockerThree.end.y, blockerPaint);

	     
	   // draw the blockerFour
	      canvas.drawLine(blockerFour.start.x, blockerFour.start.y, blockerFour.end.x,
	                blockerFour.end.y, blockerPaint);

	   // draw the blockerFive
	    canvas.drawLine(blockerFive.start.x, blockerFive.start.y, blockerFive.end.x,
	              blockerFive.end.y, blockerPaint);

	     
	      // draw the booster
	      canvas.drawLine(booster.start.x, booster.start.y, booster.end.x,
	                booster.end.y, boosterPaint);

	      // draw the boosterTwo
	      canvas.drawLine(boosterTwo.start.x, boosterTwo.start.y, boosterTwo.end.x,
	                boosterTwo.end.y, boosterTwoPaint);
	    
	     
	      Point currentPoint11 = new Point(); // start of current target section

	      // initialize curPoint to the starting point of the target
	      currentPoint11.x = target.start.x;
	      currentPoint11.y = target.start.y;

	     

	      // draw the target
	      for (int i = 1; i <= TARGET_PIECES; ++i)
	      {
	         // if this target piece is not hit, draw it
	         if (!hitStates[i - 1])
	         {
	            // alternate coloring the pieces yellow and blue
	            if (i % 2 == 0)
	               targetPaint.setColor(Color.YELLOW);
	            else
	               targetPaint.setColor(Color.RED);
	            canvas.drawLine(currentPoint11.x, currentPoint11.y,
	                      target.end.x, (int) (currentPoint11.y + pieceLength),
	                      targetPaint);
	         } // end if
	        
	         // move curPoint to the start of the next piece
	         currentPoint11.y += pieceLength;
	      } // end for
		  
	  }
	  
	  
  } // end method drawGameElements


  // display an AlertDialog when the game ends
  private void showGameOverDialog(int messageId)
  {
     // create a dialog displaying the given String
     final AlertDialog.Builder dialogBuilder = 
        new AlertDialog.Builder(getContext());
     dialogBuilder.setTitle(getResources().getString(messageId));
     dialogBuilder.setCancelable(false);
//     cannonballPaint.setColor(Color.WHITE); // reset the color to white
//     cannonballRadius = screenWidth / 28;
//     cannonballSpeed = screenWidth * 3/2;
     // display number of shots fired and total time elapsed
    /* dialogBuilder.setMessage(getResources().getString(
        R.string.results_format, shotsFired, totalElapsedTime));
     */
     
     // add targetPiecesHit to BooksBroken.db
//     ScoreData.insert;
     dialogBuilder.setMessage(getResources().getString(R.string.results_format, targetPiecesHit, shotsFired, totalElapsedTime));

     
     //     dialogBuilder.setPositiveButton(R.string.next_level,
//    	new DialogInterface.OnClickListener()
//     {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				 dialogIsDisplayed = false;
//				newGameTwo(); // set up and start a new gameTwo
//          	  return;
//			}}           
//				
//			
//     );	
    	
     dialogBuilder.setPositiveButton(R.string.reset_game,
        new DialogInterface.OnClickListener()
        {
           // called when "Reset Game" Button is pressed
           @Override
           public void onClick(DialogInterface dialog, int which)
           {
              dialogIsDisplayed = false;
              switch(GameStatus){

              case 0:
            	  newGame();// set up and start a new game
            	  return;
              case 1:
            	  totalElapsedTime = 0.0;
            	  newGameTwo(); // set up and start a new gameTwo
            	  return;
              case 2:
            	  totalElapsedTime = 0.0;
            	  newGameThree();
            	  return;
              }
           } // end method onClick
        } // end anonymous inner class
     ); // end call to setPositiveButton

     activity.runOnUiThread(
        new Runnable() {
           public void run()
           {
              dialogIsDisplayed = true;
              dialogBuilder.show(); // display the dialog
           } // end method run
        } // end Runnable
     ); // end call to runOnUiThread
  } // end method showGameOverDialog

  // stops the game
  public void stopGame()
  {
     if (cannonThread != null)
        cannonThread.setRunning(false);
  } // end method stopGame
  public void resumeGame()
  {
     if (cannonThread != null)
        cannonThread.setRunning(true);
  } // end method resumeGame

  // releases resources; called by CannonGame's onDestroy method 
  public void releaseResources()
  {
     soundPool.release(); // release all resources used by the SoundPool
     soundPool = null; 
  } // end method releaseResources

  // called when surface changes size
  @Override
  public void surfaceChanged(SurfaceHolder holder, int format,
     int width, int height)
  {
  } // end method surfaceChanged

  // called when surface is first created
  @Override
  public void surfaceCreated(SurfaceHolder holder)
  {
     if (!dialogIsDisplayed)
     {
        cannonThread = new CannonThread(holder);
        cannonThread.setRunning(true);
        cannonThread.start(); // start the game loop thread
     } // end if
  } // end method surfaceCreated

  // called when the surface is destroyed
  @Override
  public void surfaceDestroyed(SurfaceHolder holder)
  {
     // ensure that thread terminates properly
     boolean retry = true;
     cannonThread.setRunning(false);
     
     while (retry)
     {
        try
        {
           cannonThread.join();
           retry = false;
        } // end try
        catch (InterruptedException e)
        {
        } // end catch
     } // end while
  } // end method surfaceDestroyed
  
  // Thread subclass to control the game loop
  private class CannonThread extends Thread
  {
     private SurfaceHolder surfaceHolder; // for manipulating canvas
     private boolean threadIsRunning = true; // running by default
     
     // initializes the surface holder
     public CannonThread(SurfaceHolder holder)
     {
        surfaceHolder = holder;
        setName("CannonThread");
     } // end constructor
     
     // changes running state
     public void setRunning(boolean running)
     {
        threadIsRunning = running;
     } // end method setRunning
     
     // controls the game loop
     @Override
     public void run()
     {
        Canvas canvas = null; // used for drawing
        long previousFrameTime = System.currentTimeMillis(); 
       
        while (threadIsRunning)
        {
           try
           {
              canvas = surfaceHolder.lockCanvas(null);               
              
              // lock the surfaceHolder for drawing
              synchronized(surfaceHolder)
              {
                 long currentTime = System.currentTimeMillis();
                 double elapsedTimeMS = currentTime - previousFrameTime;
                 totalElapsedTime += elapsedTimeMS / 1000.00; 
                 updatePositions(elapsedTimeMS); // update game state
                 drawGameElements(canvas); // draw 
                 previousFrameTime = currentTime; // update previous time

         	
         		}// end synchronized block
         		}// end try
           finally
           {
              if (canvas != null) 
                 surfaceHolder.unlockCanvasAndPost(canvas);
           } // end finally
        } // end while
     } // end method run
  } // end nested class CannonThread
} // end class CannonView

/*********************************************************************************
* (C) Copyright 2012 by aarBot 
* Inc. All Rights Reserved. * * DISCLAIMER: The authors and publisher of this   *
* book have used their * best efforts in preparing the book. These efforts      *
* include the * development, research, and testing of the theories and programs *
* * to determine their effectiveness. The authors and publisher make * no       *
* warranty of any kind, expressed or implied, with regard to these * programs   *
* or to the documentation contained in these books. The authors * and publisher *
* shall not be liable in any event for incidental or * consequential damages in *
* connection with, or arising out of, the * furnishing, performance, or use of  *
* these programs.                                                               *
*********************************************************************************/

