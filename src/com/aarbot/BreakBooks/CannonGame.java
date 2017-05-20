// CannonGame.java
// Main Activity for the Cannon Game app.
package com.aarbot.BreakBooks;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import android.widget.Toast;


public class CannonGame extends Activity implements SensorListener 
{
   public TextView t ;
   private GestureDetector gestureDetector; // listens for double taps
   
   //private cannonViewEasy cannonViewEasy; // start screen
//   private CannonView cannonView; // custom view to display the game
   private CannonViewEasy cannonViewEasy;
//   private CannonViewMed cannonViewMed;
//   private CannonViewHard cannonViewHard;
   
//   private CannonView cannonView2; // custom view to display the 2nd level

   // For shake motion detection.
//   private SensorManager sensorMgr; // listens for shaking
   private long lastUpdate = -1;
   private float x, y, z;
   private float last_x, last_y, last_z;
   private static final int SHAKE_THRESHOLD = 800;

//	StatusData statusData;
//	
	// called when the app first launches
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
	   
	   
      super.onCreate(savedInstanceState); // call super's onCreate method
      try {
		//setContentView(R.layout.main); // inflate the layout
		  setContentView(R.layout.start); // inflate the layout

		  // get the cannonViewEasy
		  cannonViewEasy = (CannonViewEasy) findViewById(R.id.cannonViewEasy);
		  
		  // initialize the GestureDetector
		  gestureDetector = new GestureDetector(this, gestureListener);
		  
		  // allow volume keys to set game volume
		  setVolumeControlStream(AudioManager.STREAM_MUSIC);
		  
//		  OverflowMenu;
//		  CannonGame.class().getOverflowMenu;
		  
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      
//   // start motion detection
//  	sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
//  	boolean accelSupported = sensorMgr.registerListener(this,
//  		SensorManager.SENSOR_ACCELEROMETER,
//  		SensorManager.SENSOR_DELAY_GAME);
//   
//  	if (!accelSupported) {
//  	    // on accelerometer on this device
//  	    sensorMgr.unregisterListener(this,
//                  SensorManager.SENSOR_ACCELEROMETER);
//  	}
      
      
      try {
    	  ViewConfiguration config = ViewConfiguration.get(this);
    	  Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
    	  if(menuKeyField != null) {
    		  menuKeyField.setAccessible(true);
    		  menuKeyField.setBoolean(config, false);
    	  }
      } catch (Exception e) {
    	  e.printStackTrace();
      }
   } // end method onCreate
   public void getOverflowMenu() {

	}
   public void onClick(View v) {
//   	Intent myIntent; myIntent = new Intent();
//
//   	try {
//   		startActivityForResult(myIntent, 0); 
//   	}
//   	catch(ActivityNotFoundException ex) {
	   
        try {
			setContentView(R.layout.easy);
			
			cannonViewEasy = (CannonViewEasy) findViewById(R.id.cannonViewEasy);

			
			// initialize the GestureDetector
			gestureDetector = new GestureDetector(this, gestureListener);
			
			// allow volume keys to set game volume
       setVolumeControlStream(AudioManager.STREAM_MUSIC);
//	    cannonViewEasy = (cannonViewEasy) findViewById(R.id.cannonViewEasy);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//   	} 
   }
   // when the app is pushed to the background, pause it
   @Override
   public void onPause(){
//	   if (sensorMgr != null) {
//		    sensorMgr.unregisterListener(this,
//	                SensorManager.SENSOR_ACCELEROMETER);
//		    sensorMgr = null;
//	        }

      super.onPause(); // call the super method
      try {
		cannonViewEasy.stopGame(); // terminates the start game
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      //cannonView.stopGame(); // terminates the game
//      cannonView2.stopGame();
   } // end method onPause

   // release resources
   
   @Override
   public void onResume(){
	   super.onResume();
	   try {
		cannonViewEasy.resumeGame();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   
   }
   @Override
   protected void onDestroy()
   {
      super.onDestroy();
      	try {
			cannonViewEasy.releaseResources();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//      cannonView.releaseResources();
//      cannonView2.releaseResources();
   } // end method onDestroy
   
   //Menu Stuff
	//@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

@Override

public boolean onOptionsItemSelected(MenuItem item) {
//		Intent intentUpdater = new Intent(this, UpdaterService.class);
//		Intent intentRefresh = new Intent(this, RefreshService.class);
//		
//		Intent intent;
	switch(item.getItemId()) {
			case R.id.item_prefs:
		try {
			//		      setContentView(R.layout.prefs); // inflate the layout
						setContentView(R.layout.easy);
				        cannonViewEasy = (CannonViewEasy) findViewById(R.id.cannonViewEasy);
			
				        
				        // initialize the GestureDetector
				        gestureDetector = new GestureDetector(this, gestureListener);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
			return true;
			case R.id.item_stop_sound:
//		        allow volume keys to set game volume
				
				AudioManager audioMan = 
			    (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audioMan.adjustVolume(AudioManager.ADJUST_LOWER,
			    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				
//				cannonViewEasy = (cannonViewEasy) findViewById(R.id.cannonViewEasy);
			return true;
			case R.id.item_start_sound:
				setVolumeControlStream(AudioManager.STREAM_MUSIC);
				return true;
			case R.id.item_email:
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"breakbooks@gmail.com"});
				i.putExtra(Intent.EXTRA_SUBJECT, "Break Books on Android");
				i.putExtra(Intent.EXTRA_TEXT   , "");
				try {
				    startActivity(Intent.createChooser(i, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(CannonGame.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
				return true;
			case R.id.item_resume:
		try {
			cannonViewEasy.resumeGame();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				return true;
			case R.id.item_pause:
		try {
			cannonViewEasy.stopGame();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				return true;
				
			case R.id.item_review:
		try {
			Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bit.ly/QOxG1s"));
			startActivity(browserIntent1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				return true;
			case R.id.item_blog:
		try {
			Intent browserIntent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bit.ly/RLvGHH"));
			startActivity(browserIntent2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				return true;

			case R.id.item_depot:
		try {
			Intent browserIntent3 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bit.ly/Sdt4MP"));
			startActivity(browserIntent3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
				return true;
			case R.id.item_twitter:
		try {
			//https://twitter.com/BreakBooks
			Intent browserIntent4 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bit.ly/Sdta71"));
			startActivity(browserIntent4);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				return true;

			case R.id.item_facebook:
		try {
			//http://www.facebook.com/pages/Break-Books/207547466043591
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://on.fb.me/SdtaUG"));
			startActivity(browserIntent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				return true;

			default:
			return false;}
		
		}
   // called when the user touches the screen in this Activity
   @Override
   public boolean onTouchEvent(MotionEvent event)
   {
      // get int representing the type of action which caused this event
      int action = event.getAction();

      // the user user touched the screen or dragged along the screen
      if (action == MotionEvent.ACTION_DOWN ||
         action == MotionEvent.ACTION_MOVE)
      {
         try {
			cannonViewEasy.alignCannon(event); // align the cannon
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

      } // end if

      // call the GestureDetector's onTouchEvent method
      return gestureDetector.onTouchEvent(event);
   } // end method onTouchEvent

   // listens for touch events sent to the GestureDetector
   SimpleOnGestureListener gestureListener = new SimpleOnGestureListener()
   {
      // called when the user double taps the screen
      @Override
      public boolean onDoubleTap(MotionEvent e)
//      public boolean onSingleTap(MotionEvent e)

      {
    		 try {
				cannonViewEasy.fireCannonball(e); // fire the cannonball
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		 return true;
    	 }
    		 }
   ;

@Override
public void onAccuracyChanged(int sensor, int accuracy) {
	// TODO Auto-generated method stub
	
}

@Override
public void onSensorChanged(int sensor, float[] values) {
	// TODO Auto-generated method stub
}	;
//}}
       // end method onDoubleTap
   // end gestureListener
public void pullAndInsert() {
//		statusData.insert(booksBroken);
//	List<Status> timeline = getTwitter().getPublicTimeline();
//	
//	for (Status status : timeline) {
//		statusData.insert(status);
//		Log.d(TAG, String.format("%s: %s:", 
//				status.user.name,
//				status.text));
	}
}   
   
//@Override
//public void onAccuracyChanged(int sensor, int accuracy) {
//	// TODO Auto-generated method stub
//	
//}
//
//@Override
//public void onSensorChanged(int sensor, float[] values) {
//    // Do something with value
//	//Toast.makeText(aarBarcodeActivity.this, value, Toast.LENGTH_LONG)
//	//.show();
//	
//	
//	
//		if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
//	Toast.makeText(CannonGame.this, SensorManager.DATA_X & SensorManager.DATA_Y & SensorManager.DATA_Z, Toast.LENGTH_LONG)
//	.show();
//			Log.d("aarBarcodeActivity","SensorManager Values: " + SensorManager.DATA_X + SensorManager.DATA_Y + SensorManager.DATA_Z);
//		
			
			
//			Working Shake Detection
//			x = values[SensorManager.DATA_X];
//			y = values[SensorManager.DATA_Y];
//			z = values[SensorManager.DATA_Z];
//
			
//			Log.d("aarBarcodeActivity","x Value: " + x); 
//			Log.d("aarBarcodeActivity","                                y Value: " + y);
//			Log.d("aarBarcodeActivity","                                                                z Value: " + z);
//			
//		}
		
//	    long curTime = System.currentTimeMillis();
//	    // only allow one update every 100ms.
//	    if ((curTime - lastUpdate) &gt; 100) {
//		long diffTime = (curTime - lastUpdate);
//		lastUpdate = curTime;
// 
//		x = values[SensorManager.DATA_X];
//		y = values[SensorManager.DATA_Y];
//		z = values[SensorManager.DATA_Z];
// 
//		float speed = Math.abs(x+y+z - last_x - last_y - last_z)
//                              / diffTime * 10000;
//		if (speed & cannonView.totalElapsedTime; this.SHAKE_THRESHOLD) {
//		    // yes, this is a shake action! Do something about it!
//		}
//		last_x = x;
//		last_y = y;
//		last_z = z;
//	    }
//	}
//    }
//} // end class CannonGame

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
