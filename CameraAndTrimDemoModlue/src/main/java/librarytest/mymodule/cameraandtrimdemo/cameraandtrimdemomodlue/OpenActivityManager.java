package librarytest.mymodule.cameraandtrimdemo.cameraandtrimdemomodlue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.InputStream;

/**
 * Created by myourinsyo on 2014/08/14.
 */
public class OpenActivityManager {
    private static final String TAG = OpenActivityManager.class.getSimpleName();
    private static AlertDialog dialog = null;

    public static void OpenCamera(Activity _act) {
        saveImageButNoIntoGalleryDB(_act);
//        saveImageIntoGalleryDB(_act);
    }

    /*
     *  この方法でカメラで保存した写真は自動的にギャラリーに反映する
     */
    private static void saveImageIntoGalleryDB(Activity _act) {
        String photoName = FileManager.createPhotoFileName();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, photoName);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, photoName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATA, Environment.getExternalStorageDirectory().getAbsolutePath() + Constant.TEMP_PICTURE_DIR_NAME + photoName);
        Uri mImageUri = _act.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        MyApplication.getInstance().setPhotoUri(mImageUri);

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, MyApplication.getInstance().getPhotoUri());
        _act.startActivityForResult(intent, Constant.SYSTEM_CAMERA_REQUEST_CODE);
    }

    /*
     * この方法でカメラで保存した写真はギャラリーに反映しない
     * 自分でギャラリーDBに追加する必要がある
     */
    private static void saveImageButNoIntoGalleryDB(Activity _act) {
        String photoName = FileManager.createPhotoFileName();
        MyApplication.getInstance().setPhotoUri(photoName);

        Intent intent = new Intent();
        //Intent.ACTION_OPEN_DOCUMENT
        //Intent.ACTION_PICK
        //Intent.ACTION_GET_CONTENT
        //この三つの方法でギャラリーを開くことができる
        //ACTION_PICKを使って取得したUriは様々な端末の対応ができそう。
        //他のActionは端末によって、dataがnullになったりする。
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, MyApplication.getInstance().getPhotoUri());
        _act.startActivityForResult(intent, Constant.SYSTEM_CAMERA_REQUEST_CODE);
    }

    public static void OpenGallery(Activity _act) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType(Constant.INTENT_TYPE_IAMGE);
        _act.startActivityForResult(intent, Constant.GALLERY_REQUEST_CODE);
    }

    public static void openSaveImageAlert(final MainActivity _act,boolean isCropPhotoUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(_act);
        builder.setTitle(Constant.ALERT_DIALOG_TITLE);

        LinearLayout alertView = (LinearLayout) _act.getLayoutInflater().inflate(R.layout.save_alert, null);
        final RadioButton saveToDatabaseBtn = (RadioButton) alertView.findViewById(R.id.saveToDatabaseBtn);
        final RadioButton saveToGalleryBtn = (RadioButton) alertView.findViewById(R.id.saveToGalleryBtn);

        saveToGalleryBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OpenActivityManager.setDialogButtonStatus();
            }
        });

        saveToDatabaseBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                OpenActivityManager.setDialogButtonStatus();
            }
        });

        builder.setView(alertView);
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (saveToDatabaseBtn.isChecked()) {
                    _act.saveImageToDB();
                } else if (saveToGalleryBtn.isChecked()) {
                    if (MyApplication.getInstance().getIsCropPhotoUri()) {
                        FileManager.saveToGallery(MyApplication.getInstance().getCropPhotoUri(), _act);
                    } else {
                        FileManager.saveToGallery(MyApplication.getInstance().getPhotoUri(), _act);
                    }

                }
            }
        });
        builder.setNegativeButton("ギャンセル", null);

        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private static void setDialogButtonStatus() {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
    }
}
