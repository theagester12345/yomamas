package com.example.yomamas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signUp extends AppCompatActivity {
    private LinearLayout sign_in;
    private TextInputEditText username,password,fname,lname,address,tele,re_password;
    private AppCompatSpinner region,area;
    private DatabaseReference db,newpost;
    private MaterialButton sign_up;
    private SpinKitView wave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wave = findViewById(R.id.spin_kitWave);

        //Post Sign up details to db
        sign_up= findViewById(R.id.sign_up_btn);
        username = findViewById(R.id.username);
        re_password = findViewById(R.id.re_password);
        password= findViewById(R.id.password);
        region = findViewById(R.id.region);
        area =findViewById(R.id.area);



        fname = findViewById(R.id.f_name);
        lname = findViewById(R.id.l_name);
        address = findViewById(R.id.address);
        tele = findViewById(R.id.tele);

        db = FirebaseDatabase.getInstance().getReference().child("dispatcher");


        //Sign up Button Clicked
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wave.setVisibility(View.VISIBLE);

                if (TextUtils.equals(re_password.getText().toString().trim(),password.getText().toString().trim())){
                    //Post of db
                    newpost = db.push();
                    newpost.child("first_name").setValue(fname.getText().toString());
                    newpost.child("last_name").setValue(lname.getText().toString());
                    newpost.child("address").setValue(address.getText().toString());
                    newpost.child("tele").setValue(tele.getText().toString());
                    newpost.child("username").setValue(username.getText().toString());
                    newpost.child("password").setValue(password.getText().toString());
                    newpost.child("region").setValue(region.getSelectedItem().toString());
                    newpost.child("area_of_operation").setValue(area.getSelectedItem().toString());


                }else {

                    Toast.makeText(signUp.this,"Password does not match",Toast.LENGTH_LONG);
                }






                signUp.this.finish();



            }
        });






        //Sign in layout in sng up page
        sign_in = findViewById(R.id.signin_layout);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(signUp.this,login.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



}
