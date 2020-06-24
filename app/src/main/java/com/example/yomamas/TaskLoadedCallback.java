package com.example.yomamas;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface TaskLoadedCallback {
    void onTaskDone(ArrayList<LatLng> values);

}
