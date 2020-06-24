package com.example.yomamas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class dashboard extends AppCompatActivity implements ItemClickListener {
    private MaterialTextView order;
    private MaterialButton refresh;
    private RecyclerView recyclerView;
    apiInterfaceAdapter apiInterfaceAdapter;
    List<apiInterface> apiInterfaceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        order = findViewById(R.id.order_txt);
        recyclerView = findViewById(R.id.recycle_view);
        refresh = findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                finish();
                startActivity(intent);


            }
        });









        //Get order from api
        RestClient.get("http://yomamas.de/api/orders?_format=json",null,new JsonHttpResponseHandler( ){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i = 0;i<response.length();i++){
                    try {
                        JSONObject o = response.getJSONObject(i);
                        if (!TextUtils.isEmpty(o.getString("placed")) && TextUtils.isEmpty(o.getString("completed"))){

                            String store_name = o.getString("store_id");
                            //order.setText(store_name);
                            String order_id = o.getString("order_id");
                            String price = o.getString("total_price__number");
                            String contact = o.getString("field_del");
                            String delievery_loc = o.getString("field_del_location");
                            apiInterface api_interface = new apiInterface(store_name,price,order_id,contact,delievery_loc);
                            apiInterfaceList.add(api_interface);

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                apiInterfaceAdapter = new apiInterfaceAdapter(apiInterfaceList,dashboard.this);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(dashboard.this,LinearLayoutManager.VERTICAL,true);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(apiInterfaceAdapter);
                apiInterfaceAdapter.setClicklistener(dashboard.this::onClick);
                apiInterfaceAdapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(dashboard.this,"Check Internet Connection and try again",Toast.LENGTH_LONG);

            }
        });








    }


    @Override
    public void onClick(View view, int position) {
        String store_name = apiInterfaceList.get(position).getStore_id();
        String order_id = apiInterfaceList.get(position).getOrder_id();

        Intent i = new Intent(dashboard.this,storeLocation.class);
        i.putExtra("store",store_name);
        i.putExtra("order_id",order_id);
        i.putExtra("contact",apiInterfaceList.get(position).getField_del());
       // i.putExtra("de",apiInterfaceList.get(position).getField_del());
        startActivity(i);
        Toast.makeText(dashboard.this,store_name,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = new AlertDialog.Builder(dashboard.this).create();
        alertDialog.setTitle("Logout");
        alertDialog.setMessage("Are you sure you wan to logout");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dashboard.this.finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id==android.R.id.home) {
            AlertDialog alertDialog = new AlertDialog.Builder(dashboard.this).create();
            alertDialog.setTitle("Logout");
            alertDialog.setMessage("Are you sure you wan to logout");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dashboard.this.finish();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
