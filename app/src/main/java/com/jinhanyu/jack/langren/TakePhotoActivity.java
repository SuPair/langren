package com.jinhanyu.jack.langren;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by anzhuo on 2016/9/7.
 */
public class TakePhotoActivity extends Activity implements SurfaceHolder.Callback {

    SurfaceView cameraView;
    SurfaceHolder holder;
    Camera camera;
    ImageView iv_head;
    Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photo);

        cameraView = (SurfaceView) findViewById(R.id.camera);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.setFixedSize(176, 144);
        holder.setKeepScreenOn(true);
        //调用前置摄像头
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        camera.setDisplayOrientation(getPreviewDegree(this));





    }

    public void uploadHead(View view) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, output);
        //bm.recycle();//自由选择是否进行回收
        byte[] result = output.toByteArray();
        final ParseFile file = new ParseFile("head.jpg", result);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ParseObject testObject = new ParseObject("TestObject");
                testObject.put("name","周旺");
                testObject.put("head",file);
                testObject.saveInBackground();
            }
        });
    }

    public void takephoto(View view) {
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                if(bm!=null)
                    bm.recycle();
                bm = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                iv_head.setImageBitmap(bm);
                camera.stopPreview();
                camera.startPreview();

            }
        });

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    Camera.Parameters parameters;
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        parameters = camera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        parameters.setPreviewSize(width, height);
        parameters.setPreviewFrameRate(5);
        parameters.setPictureSize(width, height);
        parameters.setJpegQuality(80);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    public static int getPreviewDegree(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }



}
