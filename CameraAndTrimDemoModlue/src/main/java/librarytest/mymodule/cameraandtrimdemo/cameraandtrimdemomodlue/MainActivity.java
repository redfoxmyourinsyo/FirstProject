package librarytest.mymodule.cameraandtrimdemo.cameraandtrimdemomodlue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ImageView mImageView;
    private BitmapToStringTask mBitmapToStringTask;
    private List<String> imageList;
    private int imageOrder = 0;
    //DBからBase64写真を順番に表示するために使う
    private Button leftButton;
    private Button rightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView);
        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);

        setButtonStatus(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //ActionBarの左上のアイコンの非表示false・表示true
            getActionBar().setDisplayShowHomeEnabled(false);
        } else {
            Toast.makeText(this, "システムが古いため、ActionBar利用できません", Toast.LENGTH_SHORT).show();
        }
    }

    private void setButtonStatus(boolean isVisible) {
        if (isVisible) {
            leftButton.setVisibility(View.VISIBLE);
            rightButton.setVisibility(View.VISIBLE);
        } else {
            leftButton.setVisibility(View.INVISIBLE);
            rightButton.setVisibility(View.INVISIBLE);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.cameraBtn:
                break;
            case R.id.fromDatabase:

                setButtonStatus(true);
                imageList = FileManager.getImageFromDB(this);
                Log.d("debug","imageList size = "+ imageList.size());
                imageOrder = 0;
                mImageView.setImageBitmap(base64ToBitmap(imageList.get(imageOrder)));

                break;
            case R.id.deleteImage:
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onItemClicked(View v) {
        switch (v.getId()) {
            case R.id.cameraBtn:
                setButtonStatus(false);
                OpenActivityManager.OpenCamera(this);
                break;
            case R.id.galleryBtn:
                setButtonStatus(false);
                OpenActivityManager.OpenGallery(this);
                break;
            case R.id.saveBtn:
                OpenActivityManager.openSaveImageAlert(this,MyApplication.getInstance().getIsCropPhotoUri());
                break;
            case R.id.leftButton:
                imageOrder--;
                if (imageOrder < 0) {
                    imageOrder = imageList.size() - 1;
                }
                mImageView.setImageBitmap(base64ToBitmap(imageList.get(imageOrder)));
                break;
            case R.id.rightButton:
                imageOrder++;
                if (imageOrder >= imageList.size()) {
                    imageOrder = 0;
                }
                mImageView.setImageBitmap(base64ToBitmap(imageList.get(imageOrder)));
                break;

            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        Intent intent;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.GALLERY_REQUEST_CODE:
                    if (data == null) {
                        return;
                    }

                    MyApplication.getInstance().setPhotoUri(data.getData());
                    intent = new Intent(this, EditorActivity.class);
                    startActivityForResult(intent, Constant.CROP_REQUEST_CODE);
                    break;

                case Constant.SYSTEM_CAMERA_REQUEST_CODE:
                    intent = new Intent(this, EditorActivity.class);
                    startActivityForResult(intent, Constant.CROP_REQUEST_CODE);
                    break;


                case Constant.CROP_REQUEST_CODE:
                    if (MyApplication.getInstance().getIsCropPhotoUri()) {
                        showImageView(MyApplication.getInstance().getCropPhotoUri());
                    } else {
                        showImageView(MyApplication.getInstance().getPhotoUri());
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void showImageView(Uri _photoUri) {

        InputStream input;
        Bitmap photoBitmap;
        try {
            /*
             * 通过Uri（decodeStream）或通过Path（decodeFile）获取照片并调整照片尺寸时，处理方式略有不同。
             * 使用Uri（decodeStream）时，当设置［inJustDecodeBounds = true］后，必须重新生成［InputStream］
             * 使用Path（decodeFile）时，当设置［inJustDecodeBounds = true］后，无需重新生成［InputStream］，只需恢复［inJustDecodeBounds = false］即可
             */

            //通过照片的Uri获取照片，并调整照片尺寸
            input = this.getContentResolver().openInputStream(_photoUri);
            BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
            boundsOptions.inJustDecodeBounds = true;
            photoBitmap = BitmapFactory.decodeStream(input, null, boundsOptions);

            //获取照片高和宽的最大值，保证处理后的最大值小于指定大小
            int max = Math.max(boundsOptions.outWidth, boundsOptions.outHeight);

            int imageSize = (int) (max / Constant.IMAGE_SIZE) + 1;

            if (imageSize < 1) {
                imageSize = 1;
            }

            //防止OOM问题，对图片使用下面的处理
            //input must be reset
            input = this.getContentResolver().openInputStream(_photoUri);
            BitmapFactory.Options buildOptions = new BitmapFactory.Options();
            buildOptions.inSampleSize = imageSize;
            //设置［inPurgeable = true］，当内存不够时可以回收该对象
            buildOptions.inPurgeable = true;
            photoBitmap = BitmapFactory.decodeStream(input, null, buildOptions);

            input.close();

            mImageView.setImageBitmap(photoBitmap);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //BitmapをBase64でデータベースに保存
    public void saveImageToDB() {
        InputStream input;
        try {
            //通过照片的Uri获取照片，并调整照片尺寸
            input = this.getContentResolver().openInputStream(MyApplication.getInstance().getCropPhotoUri());
            mBitmapToStringTask = new BitmapToStringTask(BitmapFactory.decodeStream(input), this);
            mBitmapToStringTask.execute("");
        } catch (Exception p) {
            Toast.makeText(this, "Bitmap == null", Toast.LENGTH_SHORT).show();
        }
    }


    //BitmapをBase64でデータベースに保存
    public void saveImageToDB(String _bitmapToString) {
        FileManager.saveImageToDB(_bitmapToString, this);
    }


    //データベースから取得したBase64をBitmapに変換する
    public Bitmap base64ToBitmap(String base64) {
        // 将字符串转换成Bitmap类型
        Bitmap _bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(base64, Base64.NO_WRAP);
            _bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.getInstance().init();
    }
}