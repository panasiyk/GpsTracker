package com.example.inesa_user.sqlite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    private PolylineOptions polylineOptions;
    final String LOG_TAG = "myLogs";
    public SQLiteDatabase db;
    public GoogleMap map;
    int idColIndex,xColIndex,yColIndex;
    double x,y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onMapReady");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        polylineOptions=new PolylineOptions();
        db = DBHelper.getInstance(this).getWritableDatabase();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }
    public void paint() {
        Log.d(LOG_TAG, "--- Rows in mytable: ---");
        Cursor c = db.query("mytable", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            idColIndex = c.getColumnIndex("id");
            xColIndex = c.getColumnIndex("x");
            yColIndex = c.getColumnIndex("y");
            do {
                Log.d(LOG_TAG,
                        "ID = " + c.getInt(idColIndex) +
                                ", x = " + c.getDouble(xColIndex) +
                                ", y = " + c.getDouble(yColIndex));
                x=c.getDouble(xColIndex);
                y =  c.getDouble(yColIndex);
                polylineOptions.add(new LatLng(x,y))
                        .color(0xff000000)
                        .width(5);
                map.addPolyline(polylineOptions);
                if(xColIndex !=0&&yColIndex !=0)
                    determineCameraPosition(x,y);
                else
                    Toast.makeText(this,"denied",Toast.LENGTH_LONG).show();
            } while (c.moveToNext());
        } else {
            Log.d(LOG_TAG, "0 rows");
        }
        c.close();
    }

    private void determineCameraPosition(double x, double y) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(x,y))
                .zoom(10)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(LOG_TAG, "onMapReady");
        map=googleMap;
        paint();

    }
}











