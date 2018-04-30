package dev.brian.arcsoftdemo1;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/11/8
 * 描述:
 */

public class PermissionActivity extends Activity {
    public static int PERMISSION_REQ = 0x123456;

    private String[] mPermission = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private List<String> mRequestPermission = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            for (String one : mPermission) {
                if (PackageManager.PERMISSION_GRANTED != this.checkPermission(one, Process.myPid(), Process.myUid())) {
                    mRequestPermission.add(one);
                }
            }
            if (!mRequestPermission.isEmpty()) {
                this.requestPermissions(mRequestPermission.toArray(new String[mRequestPermission.size()]), PERMISSION_REQ);
                return;
            }
        }
        startActiviy();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 版本兼容
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }
        if (requestCode == PERMISSION_REQ) {
            for (int i = 0; i < grantResults.length; i++) {
                for (String one : mPermission) {
                    if (permissions[i].equals(one) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        mRequestPermission.remove(one);
                    }
                }
            }
            startActiviy();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQ) {
            if (resultCode == 0) {
                this.finish();
            }
        }
    }

    public void startActiviy() {
        if (mRequestPermission.isEmpty()) {
            final ProgressDialog mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setTitle("loading register data...");
            mProgressDialog.setCancelable(false);

           // mProgressDialog.show();
            //加载启动图片
            setContentView(R.layout.activity_welcome);
            //后台处理耗时任务
            Integer time = 2000;    //设置等待时间，单位为毫秒
            Handler handler = new Handler();
            //当计时结束时，跳转至主界面
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Application app = (Application) PermissionActivity.this.getApplicationContext();
                    app.mFaceDB.loadFaces();
                    PermissionActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mProgressDialog.cancel();
                            //跳转主界面
                            Intent intent = new Intent(PermissionActivity.this, DetecterActivity.class);
                            startActivityForResult(intent, PERMISSION_REQ);
                        }
                    });
                }
            }, time);

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Application app = (Application) PermissionActivity.this.getApplicationContext();
//                    app.mFaceDB.loadFaces();
//                    PermissionActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mProgressDialog.cancel();
//                            //跳转主界面
//                            Intent intent = new Intent(PermissionActivity.this, DetecterActivity.class);
//                            startActivityForResult(intent, PERMISSION_REQ);
//                        }
//                    });
//                }
//            }).start();
        } else {
            Toast.makeText(this, "PERMISSION DENIED!", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    PermissionActivity.this.finish();
                }
            }, 3000);
        }
    }
}
