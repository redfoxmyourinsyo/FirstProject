package librarytest.mymodule.cameraandtrimdemo.cameraandtrimdemomodlue;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by myourinsyo on 2014/08/15.
 */
public class EditorActivity extends ActionBarActivity {

    private static final String TAG = EditorActivity.class.getSimpleName();

    private ImageView mImageView;
    private boolean initStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mImageView = (ImageView) findViewById(R.id.imageView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //ActionBarの左上のアイコンの非表示false・表示true
            getActionBar().setDisplayShowHomeEnabled(false);
        } else {
            Toast.makeText(this,"システムが古いため、ActionBar利用できません",Toast.LENGTH_SHORT).show();
        }

        MyApplication.getInstance().setIsCropPhotoUri(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (initStart) {
            showImageView(MyApplication.getInstance().getPhotoUri());
        }
    }

    public void onItemClicked(View v) {
        switch (v.getId()) {
            case R.id.rotateLeftBtn:

                break;
            case R.id.rotateRightBtn:

                break;
            case R.id.swapWBtn:

                break;
            case R.id.swapHBtn:

                break;
            case R.id.cropBtn:

                //トリミングした写真の保存先を設定する
                String cropPhotoName = FileManager.createCropPhotoFileName();
                MyApplication.getInstance().setCropPhotoUri(cropPhotoName);

                Intent intent = new Intent(Constant.ACTION_CROP);
                //URIから既存の写真を取得
                intent.setDataAndType(MyApplication.getInstance().getPhotoUri(), "image/*");

                intent.putExtra(MediaStore.EXTRA_OUTPUT, MyApplication.getInstance().getCropPhotoUri());

                intent.putExtra("scale", true);// トリミング中の枠を拡大縮小できるかどうか
                intent.putExtra("noFaceDetection", true);//顔認識機能

                /*
                 * outputX,outputY,aspectX,aspectYを全て設定しなければ、自由な形でトリミングできる
                 */
//                intent.putExtra("outputX", 256);// トリミング後の画像の幅(px)
//                intent.putExtra("outputY", 256);// トリミング後の画像の高さ(px)
//                intent.putExtra("aspectX", 1);// トリミング後の画像のアスペクト比(X)
//                intent.putExtra("aspectY", 1);// トリミング後の画像のアスペクト比(Y)

                /*
                 * トリミングしたデータを返却するかどうか
                 * trueにすると、onActivityResultでdata.getData()でトリミングした写真のUriを取得して、データが取得できる。
                 * falseにすると、onActivityResultでdataがnullですが、
                 * intent.putExtra(MediaStore.EXTRA_OUTPUT, MyApplication.getInstance().getCropPhotoUri());
                 * で設定した保存先（MyApplication.getInstance().getCropPhotoUri()）を利用して、データが取得できる。
                 *
                 * trueとfalseの使い分け
                 * 小さい写真の場合、trueを利用する。
                 * 大きい写真の場合、falseを利用する。
                 *
                 * outputX、outputY、aspectX、aspectYを設定するとき、trueを利用する。
                 * 自由な形でトリミングしたいさい、falseにする。
                 * 理由はtrueかつoutputX、outputY、aspectX、aspectYを設定しないでトリミングすると、
                 * 元の写真のサイズが大きい場合、dataに保存できない。トリミングができない。
                 *
                 * なるべく、falseを使った方がどんな写真でも利用できる。
                 */
                intent.putExtra("return-data", false);
                intent.putExtra("crop", "true");
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(intent, Constant.CROP_REQUEST_CODE);

                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

        if (resultCode == RESULT_OK) {
            //"return-data"=trueの場合
//            showImageView(data.getData());

            //"return-data"=falseの場合
            showImageView(MyApplication.getInstance().getCropPhotoUri());
            initStart = false;
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.OKBtn:
                if (initStart) {
                    MyApplication.getInstance().setIsCropPhotoUri(false);
                } else {
                    MyApplication.getInstance().setIsCropPhotoUri(true);
                }
                setResult(RESULT_OK,null);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
