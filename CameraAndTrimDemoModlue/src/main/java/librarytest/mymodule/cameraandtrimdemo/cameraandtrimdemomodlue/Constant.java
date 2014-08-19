package librarytest.mymodule.cameraandtrimdemo.cameraandtrimdemomodlue;

/**
 * Created by myourinsyo on 2014/08/07.
 */
public class Constant {
    private static final String TAG = Constant.class.getSimpleName();

    //システムカメラアプリで撮った画像を保存せず、利用する場合（画質が悪い）
    public static final int SYSTEM_CAMERA_CAPTURE_REQUEST_CODE = 1;
    //システムカメラアプリで撮った画像を保存して、URIで利用する場合（画質を調整できる）
    public static final int SYSTEM_CAMERA_REQUEST_CODE = 2;
    //カメラを実装して、撮った画像を保存して、URIで利用する場合（画質を調整できる、特別にカスタマイズできる）
    public static final int CREATE_CAMERA_REQUEST_CODE = 3;
    //システムのトリミング機能でトリミングする
    public static final int CROP_REQUEST_CODE = 4;
    //ギャラリーから写真を取得
    public static final int GALLERY_REQUEST_CODE = 5;


    //写真を格納するフォルダ
    public static final String TEMP_PICTURE_DIR_NAME = "/CameraDemo/";
    //Androidデフォルトトリミング機能を呼び出すAction
    public static final String ACTION_CROP = "com.android.camera.action.CROP";
    //Base64データを格納するテーブル名
    public static final String DB_TABLE_NAME = "imagestable";
    //ギャラリーから取得する写真のフォーマットを設定 「*全て」
    public static final String INTENT_TYPE_IAMGE = "image/*";
    //imageViewで表示する写真の最大サイズを設定
    public static final float IMAGE_SIZE = 600;

    //アラートのタイトルを設定
    public static final String ALERT_DIALOG_TITLE = "保存先を設定してください";

    public static final String CROP_PHOTO_URI = "crop_photo_uri";
    public static final String PHOTO_URI = "photo_uri";
}
