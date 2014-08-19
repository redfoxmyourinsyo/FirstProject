
package librarytest.mymodule.cameraandtrimdemo.cameraandtrimdemomodlue;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * 写真をBase64に変換する
 */
public class BitmapToStringTask extends
        AsyncTask<String, Integer, String> {

    private MainActivity mActivity;
    private Bitmap mBitmap;
    private ProgressDialog mProgressDialog;

    public BitmapToStringTask(Bitmap bitmap, MainActivity activity) {
        mActivity = activity;
        mBitmap = bitmap;
    }

    // タスクを実行した直後にコールされる
    @Override
    protected void onPreExecute() {
        // プログレスバーを表示する
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage("変換中のため、お待ちください。。");
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        mBitmap.compress(CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    // onProgressUpdate方法用于更新进度信息
    @Override
    protected void onProgressUpdate(Integer... progresses) {

    }

    // onPostExecute方法用于在执行完后台任务后更新UI,显示结果
    @Override
    protected void onPostExecute(String _bitmapToString) {
        mProgressDialog.dismiss();
        mActivity.saveImageToDB(_bitmapToString);
    }

}
