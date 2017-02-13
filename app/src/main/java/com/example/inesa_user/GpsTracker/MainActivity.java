package com.example.inesa_user.GpsTracker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.inesa_user.sqlite.R;


public class MainActivity extends Activity  {

//    public static final String BROADCAST_STATUS = "com.map.ChangeButtonStatus";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String SAVE_STATUS_BTN = "SAVE_STATUS_BTN";
    private final String LOG_TAG = "myLogs";
    public SQLiteDatabase db;
    private Button btnStart,btnStop;
    private SharedPreferences sPref;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart=(Button) findViewById(R.id.btnStart);
        btnStop=(Button) findViewById(R.id.btnStop);
        loadStatusBtn();
    }
    public void onClickbtnStart(View v) {
        Log.d(LOG_TAG, "start");
        btnStop.setEnabled(true);
        saveStatusBtn(false);
        checkPermission();

    }
    public void saveStatusBtn(boolean status){
        sPref=getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor  btnStatus= sPref.edit();
        btnStatus.putBoolean(SAVE_STATUS_BTN,status);
        btnStatus.commit();
        loadStatusBtn();
    }
    public void loadStatusBtn(){
        sPref=getPreferences(MODE_PRIVATE);
        boolean btnStatus = sPref.getBoolean(SAVE_STATUS_BTN,true);
        btnStart.setEnabled(btnStatus);
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
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
                startService(new Intent(this,MyService.class));
            }
        }
        else
            Toast.makeText(this,"without permission",Toast.LENGTH_LONG).show();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void onClickbtnStop(View v) {
        btnStop.setEnabled(false);
        saveStatusBtn(true);
        db = DBHelper.getInstance(this).getWritableDatabase();
        Log.d(LOG_TAG, "--- Clear mytable: ---");
        //db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + "mytable" + "'");
        int clearCount =db.delete("mytable", null, null);
        Log.d(LOG_TAG, "deleted rows count = " + clearCount);
        DBHelper.getInstance(this).close();
        stopService(new Intent(this,MyService.class));
    }
    public void onClickbtnView(View v){
        startActivity(new Intent(this, MapActivity.class));
    }
}
