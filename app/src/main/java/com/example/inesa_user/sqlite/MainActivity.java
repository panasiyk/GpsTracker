package com.example.inesa_user.sqlite;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity  {

    public static final String BROADCAST_STATUS = "com.map.ChangeButtonStatus";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private final String LOG_TAG = "myLogs";
    public SQLiteDatabase db;
    private Button btnStart,btnStop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart=(Button) findViewById(R.id.btnStart);
        btnStop=(Button) findViewById(R.id.btnStop);
        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    Log.d(LOG_TAG, "bntStartStatusChange");
                    btnStart.setEnabled(intent.getBooleanExtra("status", false));
                    btnStop.setEnabled(true);
            }
        };
        IntentFilter intFilter = new IntentFilter();
        intFilter.addAction(BROADCAST_STATUS);
        registerReceiver(br, intFilter);
    }
    public void onClickbtnStart(View v) {
        Log.d(LOG_TAG, "start");
        checkPermission();

    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            Log.d(LOG_TAG, "зразу всьо ок");
            startService(new Intent(this,MyService.class));
            }
        else {
            if(Build.VERSION.SDK_INT>=23) {
                Log.d(LOG_TAG, "Build.VERSION.SDK_INT>=23");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if( requestCode==PERMISSION_REQUEST_CODE){
            Log.d(LOG_TAG, "requestCode==PERMISSION_REQUEST_CODE");
            if ( grantResults[0] == PackageManager.PERMISSION_GRANTED){
            checkPermission();
            }
        }
        else
            Toast.makeText(this,"denied",Toast.LENGTH_LONG).show();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void onClickbtnStop(View v) {

        btnStop.setEnabled(false);
        btnStart.setEnabled(true);
        db = DBHelper.getInstance(this).getWritableDatabase();
        Log.d(LOG_TAG, "--- Clear mytable: ---");
        int clearCount =db.delete("mytable", null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + "mytable" + "'");
        Log.d(LOG_TAG, "deleted rows count = " + clearCount);
        DBHelper.getInstance(this).close();
        stopService(new Intent(this,MyService.class));
    }
    public void onClickbtnView(View v){
        startActivity(new Intent(this, MapActivity.class));
    }
}
