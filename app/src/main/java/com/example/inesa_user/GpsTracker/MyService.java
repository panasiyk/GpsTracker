package com.example.inesa_user.GpsTracker;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service{
    double x,y;
    private final String LOG_TAG = "myLogs";
    public SQLiteDatabase db;
    private ContentValues cv;
    MyLocationListener listener;
    public void onCreate() {
        super.onCreate();
        cv = new ContentValues();

        Log.d(LOG_TAG, "onCreate");
        listener = new MyLocationListener();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        listener.stopLocationUpdates();
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }
    public void addBD() {
        db = DBHelper.getInstance(this).getWritableDatabase();
        Log.d(LOG_TAG, "--- Insert in mytable: ---");
        cv.put("x", x);
        cv.put("y", y);
        long rowID = db.insert("mytable", null, cv);
        Log.d(LOG_TAG, "row inserted, ID = " + rowID);
    }

    public class MyLocationListener implements LocationListener {

        private LocationManager locationManager;

        private MyLocationListener() {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            //noinspection MissingPermission
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 5, this);
            //noinspection MissingPermission
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 5, this);
            Log.d(LOG_TAG, "MyLocationListenerCreate");
        }
        void stopLocationUpdates() {

            if(locationManager!=null){
                //noinspection MissingPermission
                locationManager.removeUpdates(this);
            }
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.d(LOG_TAG, "onLocationChanged");
            x = location.getLatitude();
            y = location.getLongitude();
            addBD();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        private void checkEnabled() {
            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    startActivity(new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            }
        }

    }
}
