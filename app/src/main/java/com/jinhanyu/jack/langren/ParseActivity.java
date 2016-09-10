package com.jinhanyu.jack.langren;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

/**
 * Created by anzhuo on 2016/9/7.
 */
public class ParseActivity extends Activity {

    private Handler handler = new Handler();

    private ImageView iv_head;
    ImageView iv_pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_parse);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        iv_pic = (ImageView) findViewById(R.id.iv_pic);

    }

    public void test(View view){

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("TestObject");
        query.whereEqualTo("name","陈礼");

        new Thread(){
            @Override
            public void run() {
                try {
                    ParseObject a = query.getFirst();
                    ParseFile head = (ParseFile)a.get("head");
                    final Bitmap bp = BitmapFactory.decodeStream(head.getDataStream());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            iv_head.setImageBitmap(bp);
                        }
                    });

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }.start();

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
            iv_pic.setImageBitmap(bitmap);
        }
    }

    public void uploadHead(View view) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) iv_pic.getDrawable();
        if(bitmapDrawable!=null)
           bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, output);
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

    public void gotorecord(View view) {
        startActivity(new Intent(this,RecordActivity.class));
    }
}
