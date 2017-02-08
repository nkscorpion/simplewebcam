package com.camera.simplewebcam2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main extends Activity {
	private static final String TAG = "Main";
	CameraPreview cp;
	Button btnCap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);

		setContentView(R.layout.main);
		
		cp = (CameraPreview) findViewById(R.id.cp);
        btnCap = (Button)findViewById(R.id.btnCapture);
        btnCap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bmp = cp.capture();
                if( bmp==null ) {
                    Log.e(TAG, "no data!");
                } else {
                    saveImageToGallery(v.getContext(), bmp);
                }
            }
        });
	}
    //[[ Copied from : http://blog.csdn.net/xu_fu/article/details/39158747
    private void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "ingdan");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String path = "";
        // 其次把文件插入到系统图库
        try {
            path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(path)));
    }
    //]] Copied from : http://blog.csdn.net/xu_fu/article/details/39158747
}
