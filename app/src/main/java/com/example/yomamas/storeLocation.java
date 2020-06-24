package com.example.yomamas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.example.easywaylocation.draw_path.DirectionUtil;
import com.example.easywaylocation.draw_path.PolyLineDataBean;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.JsonHttpResponseHandler;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static com.example.easywaylocation.EasyWayLocation.LOCATION_SETTING_REQUEST_CODE;


public class storeLocation extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback, Listener, DirectionUtil.DirectionCallBack {
    GoogleMap mMap;
    MarkerOptions store_loc,dispatch_loc;
    private final LatLng mDefaultLocation = new LatLng(5.631099, -0.094119);
    private static final int DEFAULT_ZOOM = 14;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private CameraPosition mCameraPosition;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    private double store_loc_lat,store_loc_lng,dispatch_loc_lat,dispatch_loc_lng;
    private Marker dispatch_location;
    private int count=0;
    private float v;
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private ValueAnimator valueAnimator;
    private RelativeLayout cancel;
    private EasyWayLocation easyWayLocation;
    private LatLng service_loc;
    private String apiKey;
    private Polyline route;
    private int i=0;

    ArrayList<LatLng> _trips = new ArrayList<>();
    Marker _marker;
    LatLngInterpolator _latLngInterpolator = new LatLngInterpolator.Spherical();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {

            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_store_location);

        apiKey = getResources().getString(R.string.map_key);

        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Initialize EasyWay Location
        easyWayLocation = new EasyWayLocation(this,request,false,this);

        //Build the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.storeLocation_map);
        mapFragment.getMapAsync(storeLocation.this);

        //TODO: Get current location of device
        //Dispatch Marker
      //  dispatch_loc = new MarkerOptions().position(new LatLng(5.627145, -0.090820)).title("Dispatch Location");




    }


    @Override
    public void onTaskDone(final ArrayList<LatLng> values) {
        if (values != null) {



            if (i==0){

                route = mMap.addPolyline(new PolylineOptions().addAll(values).color(R.color.black));
                i++;
            }else if (i>0){
               // Log.d("second_route","works");
                route.remove();
                route = mMap.addPolyline(new PolylineOptions().addAll(values).color(R.color.black));

            }





           //MarkerAnimation markerAnimation = new MarkerAnimation();

            //NOTE: pass db reference here to be able to update database on animation marker events
         //   markerAnimation.animateLine(values,mMap,dispatch_location,storeLocation.this,"Have you picked up the product?","Call Store","store",getIntent().getStringExtra("store"),store_loc_lat,store_loc_lng);


        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        //TODO: Choose Map Style to implement
       // MapStyleOptions mapStyleOptions = MapStyleOptions.loadRawResourceStyle(this,R.raw.mapstyle);
        //mMap.setMapStyle(mapStyleOptions);



        // Prompt the user for permission.
        getLocationPermission();









        //mMap.addMarker(artisan_loc);
       // dispatch_location = mMap.addMarker(dispatch_loc);




    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle

        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());


            super.onSaveInstanceState(outState);
        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.map_key);
        return url;
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    /**
     * Gets the location of the service to be provided.
     */
    private void getLocation(int count) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.

         */

            try {
                if (mLocationPermissionGranted) {
                    //Log.d("lat",String.valueOf(store_loc_lat));


                    //Get lat and lng for picked

                    //Get Store location
                    RestClient.get("http://yomamas.de/api/stores?_format=json",null,new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                            for (int i =0;i<response.length();i++){
                                try {
                                    JSONObject o = response.getJSONObject(i);
                                    if(o.getString("name").equals(getIntent().getStringExtra("store"))){

                                        if(!TextUtils.isEmpty(o.getString("field_location"))){
                                            String locs = o.getString("field_location");
                                            //Remove the all /n
                                            locs = locs.replaceAll("\n","");

                                            //Remove all white spaces
                                            locs= locs.replaceAll("\\s+","");
                                            //   Log.d("locs",locs);
                                            String[] latlng_array = locs.split(",");
                                            store_loc_lat = Double.parseDouble(latlng_array[0]);

                                            store_loc_lng = Double.parseDouble(latlng_array[1]);
                                            // Log.d("lng",String.valueOf(loc_lng));

                                            //Store Location marker
                                            store_loc= new MarkerOptions().position(new LatLng(store_loc_lat, store_loc_lng)).title("Store Location");
                                            mMap.addMarker(store_loc);

                                            // dispatch_loc = new MarkerOptions().position(new LatLng(dispatch_loc_lat,dispatch_loc_lng)).title("Dispatch Location").icon(bitmapDescriptorFromVector(storeLocation.this, R.drawable.dispatch_marker));
                                            //dispatch_location=  mMap.addMarker(dispatch_loc);




                                            Log.d("dispatchLoc_get",String.valueOf(dispatch_loc_lat));

                                            if (count==0){
                                                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(dispatch_loc_lat,dispatch_loc_lng)));
                                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dispatch_loc_lat,dispatch_loc_lng), DEFAULT_ZOOM));
                                                String url = getUrl(new LatLng(dispatch_loc_lat,dispatch_loc_lng), store_loc.getPosition(), "driving");
                                                new FetchURL(storeLocation.this).execute(url, "driving");


                                            }else if (count>0){
                                                Log.d("second_getLoc","works");
                                                String url = getUrl(new LatLng(dispatch_loc_lat,dispatch_loc_lng), store_loc.getPosition(), "driving");
                                                new FetchURL(storeLocation.this).execute(url, "driving");


                                            }

                                        }else {
                                            Toast.makeText(storeLocation.this,"Store Location not Found. Enter Store Location on Store Page",Toast.LENGTH_LONG);


                                        }






                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                            Toast.makeText(storeLocation.this,"Check Internet Connection and try again",Toast.LENGTH_LONG);

                        }
                    });



                    //mMap.animateCamera(CameraUpdateFactory.newLatLng(mDefaultLocation));
                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));





                    /**Getting current location **/
                    //TODO: To be transfered to the location_input page when Use current loction button is selected
              /*  Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {

                            mLastKnownLocation = location;

                           currentPosititonLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            //Artisan Marker
                            job_loc = new MarkerOptions().position(currentPosititonLatLng).icon(bitmapDescriptorFromVector(customer_choice.this,R.drawable.ic_map_arrow));
                            mMap.addMarker(job_loc);
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(currentPosititonLatLng));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosititonLatLng, DEFAULT_ZOOM));
                            String url = getUrl(currentPosititonLatLng, place.getPosition(), "driving");
                            new FetchURL(customer_choice.this).execute(url, "driving");


                        } else {
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(mDefaultLocation));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));

                        }


                    }

                });*/

                }
            } catch (SecurityException e) {
                Log.e("Exception: %s", e.getMessage());
            }

        }





    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void locationOn() {

    }

    @Override
    public void currentLocation(Location location) {
        Log.d("current_lat",String.valueOf(location.getLatitude()));
        dispatch_loc_lat=location.getLatitude();
        dispatch_loc_lng = location.getLongitude();
       // _trips.add(new LatLng(location.getLatitude(),location.getLongitude()));

        Log.d("store_latCurrent",String.valueOf(store_loc_lat));


        getLocation(count);
        count++;





        MarkerAnimation markerAnimation = new MarkerAnimation();
        markerAnimation.animateLine(location.getLatitude(),location.getLongitude(), mMap, dispatch_location, storeLocation.this, "Have you picked up the product?", "Call Store", "store", getIntent().getStringExtra("store"), store_loc_lat, store_loc_lng);




     /*   if (_trips.size()>2 && store_loc_lat!=0 && store_loc_lng!=0) {
            MarkerAnimation markerAnimation = new MarkerAnimation();
            markerAnimation.animateLine(_trips, mMap, dispatch_location, storeLocation.this, "Have you picked up the product?", "Call Store", "store", getIntent().getStringExtra("store"), store_loc_lat, store_loc_lng);

        }*/

        // Get the location of the service to be provided.
      //  int count =0;
       // Log.d("Count",String.valueOf(count++));






       
                
        



    }

    private void updateDipatcherMArker() {
        Log.d("update","running");
        //Dispatcher marker
        Log.d("dispatch_loc_lat",String.valueOf(dispatch_loc_lat));
        dispatch_loc = new MarkerOptions().position(new LatLng(dispatch_loc_lat,dispatch_loc_lng)).title("Dispatch Location").icon(bitmapDescriptorFromVector(storeLocation.this, R.drawable.dispatch_marker));
        dispatch_location=  mMap.addMarker(dispatch_loc);
        dispatch_location.remove();
        dispatch_location = mMap.addMarker(dispatch_loc);
    }

    @Override
    public void locationCancelled() {

    }


    @Override
    public void pathFindFinish(HashMap<String, PolyLineDataBean> hashMap) {

    }

    /**
     * Convert Vector to Bitmap
     * */
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        easyWayLocation.startLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        easyWayLocation.endUpdates();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOCATION_SETTING_REQUEST_CODE:
                easyWayLocation.onActivityResult(resultCode);
                break;
        }
    }


}
