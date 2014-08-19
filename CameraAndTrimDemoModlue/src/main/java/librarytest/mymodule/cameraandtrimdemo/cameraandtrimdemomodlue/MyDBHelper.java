package librarytest.mymodule.cameraandtrimdemo.cameraandtrimdemomodlue;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper {

	private static MyDBHelper helper;

	// 筋トレ項目を保存するテーブル
	String SQL = "CREATE TABLE " + "imagestable" + "(" + "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT," + // 連番
            "base64" + " TEXT" + // image Base64値
            ")";


	public MyDBHelper(Context context) {
		super(context, "base64DB", null, 1);
	}

	/**
	 * synchronizedを使って、データベースにロックをかけて、同時に接続することができないようにする
	 * 
	 * @return helper データベースのインスタンスsynchronized
	 */
	public static MyDBHelper getInstance(Context context) {
		if (helper == null) {
			helper = new MyDBHelper(context);
		}

		return helper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String settingTable = "setting・テーブル　OK！！";
		try {
			db.execSQL(SQL);
		} catch (Exception e) {
			settingTable = "setting・テーブル　NG！！";
		}
		Log.d("debug", settingTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
