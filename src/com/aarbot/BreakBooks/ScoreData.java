package com.aarbot.BreakBooks;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;



public class ScoreData {
	static final String TAG = "StatusData";
	public static final String DB_NAME = "scoreboard.db";
////	my old code
	public static final int DB_VERSION = 2;
	public static final String TABLE = "booksbroken";
	public static final String C_ID = BaseColumns._ID;
	public static final String C_CREATED_AT = "created_at";
//	public static final String C_USER = "user_name"; //String email = accounts[0].name;


	public static final String C_BOOKS = "books_broken";
	public static final String C_STAGE = "stage_name";
	public static final String C_ANDROID = "android_name";
	public static final String C_TIMEPLAYED = "time_played";
	public static final String C_GAMETIME = "game_time";
	
//	public static final String C_BOOKS = "books_broken";
	
//////	my new code
//   	public static final String TABLE_COMMENTS = "comments";
//	public static final String COLUMN_ID = "_id";
//	public static final String COLUMN_COMMENT = "comment";
//
//	private static final String DATABASE_NAME = "commments.db";
//	private static final int DATABASE_VERSION = 1;

	
	static Context context;
	DbHelper dbHelper;
	SQLiteDatabase db;
	//static AccountManager accountManager = AccountManager.get(context);

	
    //static Account[] accounts =
    //accountManager.getAccountsByType("com.google");
	
    // Android Operating System
    public static final int C_SDK = android.os.Build.VERSION.SDK_INT;

    // Device Name
    public static final String C_DEVICE = android.os.Build.MODEL;

    // Google Account Name
    
    //public static final String C_USER = accounts[0].name;
	
	public ScoreData(Context context) {
		this.context = context;
		dbHelper = new DbHelper();
	}
	public void insert(String gameStatus, int booksBroken, double playTime, 
						int shotsFired, String stageName, String androidName,
						String userName, String TimePlayed, String androidOS) {
		
		ContentValues values = new ContentValues();
//		values.put(C_ID, status.id);
		values.put(C_CREATED_AT, TimePlayed);
//		values.put(C_USER, userName);
		values.put(C_BOOKS, gameStatus);
		
		db = dbHelper.getWritableDatabase();
//		db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
	}
	
	public Cursor query() {
		//List list = new ArrayList();
		
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE, null, null, null, null, null,
				C_CREATED_AT + " DESC");	//SELECT * FROM status
		return cursor;
	}


	
	class DbHelper extends SQLiteOpenHelper {

		public DbHelper() {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// only called once ever for the user! only if uninstall the app and remove the data
			//String sql = String.format("create table %s " + 
		//	"(%s int primary key, %s int, %s text, %s text)", 
			//TABLE, C_ID, C_CREATED_AT, C_USER, C_BOOKS);
			//Log.d(TAG, "onCreate with SQL:" +sql);
	//		db.execSQL(sql);
			
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "onUpgrade from "+oldVersion+" to "+newVersion);
    	db.execSQL("drop table if exists "+TABLE);
			onCreate(db);
		
		}
		
	}
}

