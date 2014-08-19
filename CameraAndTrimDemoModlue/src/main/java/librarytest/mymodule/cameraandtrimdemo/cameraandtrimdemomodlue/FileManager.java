package librarytest.mymodule.cameraandtrimdemo.cameraandtrimdemomodlue;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by myourinsyo on 2014/08/08.
 */
public class FileManager {
    private static final String TAG = FileManager.class.getSimpleName();

    //名前を生成する（カメラで撮った写真）
    public static String createPhotoFileName(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File app_dir = new File(Environment.getExternalStorageDirectory() + Constant.TEMP_PICTURE_DIR_NAME);
            if (!app_dir.exists()) {
                app_dir.mkdir();
            }
        }


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        //2014-08-15 11-51-06のような名前は大丈夫そうです。
        //2014-08-15 11:51:06だと古い機種が落ちる可能性がある「:」禁止
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return simpleDateFormat.format(calendar.getTime()) + ".jpg";

    }

    //名前を生成する（トリミングした写真）
    public static String createCropPhotoFileName(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File app_dir = new File(Environment.getExternalStorageDirectory() + Constant.TEMP_PICTURE_DIR_NAME);
            if (!app_dir.exists()) {
                app_dir.mkdir();
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        //2014-08-15 11-51-06のような名前は大丈夫そうです。
        //2014-08-15 11:51:06だと古い機種が落ちる可能性がある「:」禁止
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return "trim-" + simpleDateFormat.format(calendar.getTime()) + ".jpg";

    }


    //写真をフォルダに保存
    public static void saveToFolder(Uri _photoUri, Bitmap _bitmap) {
        deleteTempFile(_photoUri);

        // ファイル保存
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(_photoUri.getPath(), true);
            _bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();

        } catch (Exception e) {
            Log.e("Debug", e.getMessage());
        }

        _bitmap.recycle();
    }

    //フォルダから写真を削除
    public static void deleteTempFile(Uri _photoUri){
        File deleteFile = new File(_photoUri.getPath());

        if (deleteFile.exists()) {
            deleteFile.delete();
        }
    }

    //写真をギャラリーへ保存
    public static void saveToGallery(Uri _photoUri, Context context) {
        registAndroidDB(_photoUri.getPath(), context);
    }
    /**
     * アンドロイドのデータベースへ画像のパスを登録
     *
     * @param _path 登録するパス
     */
    public static void registAndroidDB(String _path,Context _context) {
        // アンドロイドのデータベースへ登録
        // (登録しないとギャラリーなどにすぐに反映されないため)
        ContentValues values = new ContentValues();
        ContentResolver contentResolver = _context.getContentResolver();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put("_data", _path);
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private static SQLiteDatabase db;
    private static ContentValues values;
    private static Cursor cursor;

    //BitmapをBase64でデータベースに保存
    public static void saveImageToDB(String _bitmapToString,Context context) {
        //TO-DO
        //保存する際、データの大きさを確認して保存すべき。しないと、読み出しするとき、落ちる
        //それとも、保存できるデータのサイズを設定する？
        db = MyDBHelper.getInstance(context).getWritableDatabase();
        values = new ContentValues();
        values.put("base64", _bitmapToString);
        long result = db.insert(Constant.DB_TABLE_NAME, null, values);
        if (result < 0) {
            return;
        }

        Toast.makeText(context, "DBに保存しました", Toast.LENGTH_SHORT).show();
    }
    static List<String> base64Data;

    //データベースからBitmapを取得
    public static List<String> getImageFromDB(Context context) {
        db = MyDBHelper.getInstance(context).getReadableDatabase();
        cursor = db.query(Constant.DB_TABLE_NAME, null, null, null, null, null, null);
        base64Data = new ArrayList<String>();

        while(cursor.moveToNext()){
            base64Data.add(cursor.getString(1));
        }

        cursor.close();
        return base64Data;
    }


}
