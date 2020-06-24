package com.example.yomamas;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.ArrayList;

public class MarkerAnimation {
    private String y,n,ac;
    private int count=0;

    static GoogleMap map;
    boolean animFinish= false;
    ArrayList<LatLng> _trips = new ArrayList<>();
    Activity c;
    Marker _marker;
    String store;
    double end_lat,end_lng,current_lat,current_lng;
   // DatabaseReference DB;

    LatLngInterpolator _latLngInterpolator = new LatLngInterpolator.Spherical();

    public void animateLine(double current_lat,double current_lng, GoogleMap map, Marker marker, Activity current,String yesDialog,String noButtonText,String Activity,String store_name, double lat,double lng) {
        this.current_lat=current_lat;
        this.current_lng =current_lng;
        _marker = marker;
        c= current;
        y = yesDialog;
        n = noButtonText;
        ac=Activity;
        store = store_name;
        end_lat=lat;
        end_lng=lng;

       // DB=fb;
        //map=map;



        animateMarker();
    }


    public void animateMarker() {
        //final boolean onFinish_anim= false;

       /* TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return _latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");

        ObjectAnimator animator = ObjectAnimator.ofObject(_marker, property, typeEvaluator, _trips.get(0));

        //ObjectAnimator animator = ObjectAnimator.o(view, "alpha", 0.0f);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
                //  animDrawable.stop();



            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //  animDrawable.stop();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                //  animDrawable.stop();

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //  animDrawable.stop();
                if (_trips.size() > 1) {
                    _trips.remove(0);
                    animateMarker();




                }







                //Reached Location











                // Log.d("Trip sizes",String.valueOf(_trips.size()));



            }

        });



        animator.setDuration(300);



        animator.start();
        //   Log.d("Animation_paused",String.valueOf(animator.isRunning()));

        return animFinish;*/

        //Get distance between start and end position
        Location startPoint=new Location("locationA");
        startPoint.setLatitude( current_lat);
        startPoint.setLongitude(current_lng);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(end_lat);
        endPoint.setLongitude(end_lng);

        double distance=startPoint.distanceTo(endPoint);
        Log.d("distance",String.valueOf(distance));

        if(distance<20){

           // _trips.clear();



            if (count==0){
                MaterialDialog mDialog = new MaterialDialog.Builder(c)
                        .setTitle("Arrival")
                        .setMessage(y)
                        .setCancelable(false)
                        .setPositiveButton("Yes", R.drawable.ic_check_white_24dp, new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {

                                // Delete Operation
                                if (ac.equals("store")){
                                    Intent i = new Intent(c,customerLocation.class);
                                    i.putExtra("store",store);
                                    c.startActivity(i);

                                }


                                c.finish();

                                //TODO: Open Customer Location Map

                                dialogInterface.dismiss();
                            }

                        })
                        .setNegativeButton(n, R.drawable.ic_call_white_24dp, new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                //TODO: Send to dialer to call store
                            }

                        })
                        .build();

                if (!(c.isFinishing())){
                    // Show Dialog
                    mDialog.show();

                }

                count ++;


            }
            Log.d("marker_count",String.valueOf(count));













        }
    }
}
