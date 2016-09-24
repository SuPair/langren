

package com.jinhanyu.jack.langren.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

/**
 * Created by anzhuo on 2016/9/7.
 */
public class UserHeadActivity extends Activity implements View.OnClickListener {

    ImageView cameraView;
    private ImageView system_camera;
    private ImageView photo;
    private Button complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_head);

        cameraView = (ImageView) findViewById(R.id.surfaceview);
        system_camera = (ImageView) findViewById(R.id.camera);
        photo = (ImageView) findViewById(R.id.photo);
        complete = (Button) findViewById(R.id.complete);

        system_camera.setOnClickListener(this);
        photo.setOnClickListener(this);
        complete.setOnClickListener(this);



    }







    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera://摄像头
                gototakephoto(v);
                break;
            case R.id.photo://相册
                gotosystempic(v);

                break;
            case R.id.complete://完成 跳转到主页面或者玩家设置页面
                uploadHead(v);
        }

    }

    private void gotosystempic(View v) {
    }

    public void gototakephoto(View view) {
        //startActivity(new Intent(this,TakePhotoActivity.class));
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            cameraView.setImageBitmap(bitmap);
        }
    }

    public void uploadHead(View view) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) cameraView.getDrawable();
        if(bitmapDrawable!=null)
            bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, output);
        //bm.recycle();//自由选择是否进行回收
        byte[] result = output.toByteArray();
        final ParseFile file = new ParseFile("head.jpg", result);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e!=null){
                    e.printStackTrace();
                    Toast.makeText(UserHeadActivity.this, "上传头像失败", Toast.LENGTH_SHORT).show();
                }else {
                    ParseUser.getCurrentUser().put("head",file);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null) {
                                Toast.makeText(UserHeadActivity.this, "上传头像成功！", Toast.LENGTH_SHORT).show();
                                ParseFile parseFile = (ParseFile) ParseUser.getCurrentUser().get("head");
                                if(parseFile!=null)
                                    MainApplication.userInfo.setHead(parseFile.getUrl());
                                finish();
                            }
                        }
                    });

                }

            }
        });
    }
}
