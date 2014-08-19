package librarytest.mymodule.cameraandtrimdemo.cameraandtrimdemomodlue;

import android.app.Application;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by myourinsyo on 2014/08/08.
 */
public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName();
    //カメラで撮った写真のUri
    private static Uri mPhotoUri;
    //トリミングした写真のUri
    private static Uri mCropPhotoUri;

    private static boolean isCropPhotoUri;

    private static MyApplication instance;

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    public static void init() {
        mPhotoUri = null;
        mCropPhotoUri = null;
        isCropPhotoUri = false;
    }

    public static void setPhotoUri(Uri _photoUri){
        mPhotoUri = _photoUri;
    }

    public static void setPhotoUri(String _photoName){
        mPhotoUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory() + Constant.TEMP_PICTURE_DIR_NAME, _photoName));
    }

    //カメラで撮った写真の保存先のURIを取得
    public static Uri getPhotoUri() {
        return mPhotoUri;
    }

    public static void setCropPhotoUri(Uri _cropPhotoUri){
        mCropPhotoUri = _cropPhotoUri;
    }

    public static void setCropPhotoUri(String _cropPhotoName){
        mCropPhotoUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory() + Constant.TEMP_PICTURE_DIR_NAME, _cropPhotoName));
    }

    //トリミングした写真の保存先のURIを取得
    public static Uri getCropPhotoUri() {
        Log.d("debug","mCropPhotoUri = " + mCropPhotoUri);
        Log.d("debug","mCropPhotoUri.Path = " + mCropPhotoUri.getPath());
        return mCropPhotoUri;
    }



    public static void setIsCropPhotoUri(boolean _isCropPhotoUri){
        isCropPhotoUri = _isCropPhotoUri;
    }
    public boolean getIsCropPhotoUri(){
        return isCropPhotoUri;
    }






//    private static final String TAG = MyApplication.class.getSimpleName();
//    //カメラで撮った写真の名前
//    private static String mPhotoName;
//    //トリミングした写真の名前
//    private static String mTrimPhotoName;
//    //ギャラリーで選んだ写真のパス
//    private static String mGalleryPhotoPath;
//
//    private static MyApplication instance;
//
//    public static MyApplication getInstance() {
//        if (instance == null) {
//            instance = new MyApplication();
//        }
//        return instance;
//    }
//
//    public static void init() {
//        mPhotoName = "";
//        mTrimPhotoName = "";
//        mGalleryPhotoPath = "";
//    }
//
//    public static void setPhotoName() {
//        mPhotoName = FileManager.createPhotoFileName();
//    }
//
//    //カメラで撮った写真の保存先のURI
//    public static Uri getPhotoUri() {
//        return Uri.fromFile(new File(Environment
//                .getExternalStorageDirectory() + Constant.TEMP_PICTURE_DIR_NAME, mPhotoName));
//    }
//
//    public static void setTrimFileName() {
//        mTrimPhotoName = FileManager.createTrimPhotoFileName();
//    }
//
//    //トリミングした写真の保存先のURI
//    public static Uri getPhotoUriIsTriming() {
//        return Uri.fromFile(new File(Environment
//                .getExternalStorageDirectory() + Constant.TEMP_PICTURE_DIR_NAME, mTrimPhotoName));
//    }
//
//    //ギャラリーから選んだ写真の保存先のURI
//    public static Uri getGalleryPhotoUri() {
//        return Uri.fromFile(new File(mGalleryPhotoPath));
//    }
//
//    public static void setGalleryPhotoPath(String _galleryPhotoPath) {
//        mGalleryPhotoPath = _galleryPhotoPath;
//    }
}
