package com.example.yomamas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
    private MaterialButton login_btn;
    private LinearLayout signup;
    private MaterialTextView dispatch_tv;
    private TextInputEditText username,password;
    private DatabaseReference dispatcher;
    private SpinKitView wave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Login to App
        username = findViewById(R.id.username_login);
       dispatch_tv = findViewById(R.id.dispather_tv);

        password = findViewById(R.id.password_login);
        wave = findViewById(R.id.spin_kitWave_login);
        login_btn = findViewById(R.id.login_btn);

        //Initialize DB
        dispatcher = FirebaseDatabase.getInstance().getReference().child("dispatcher");
        //Log.d("dispatch",dispatcher.toString());
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Handle field not empty
                if (TextUtils.isEmpty(username.getText().toString()) || TextUtils.isEmpty(password.getText().toString())){



                    Toast.makeText(login.this,"Please Fill all field",Toast.LENGTH_LONG).show();

                }else {
                    //Show wave loader
                    wave.setVisibility(View.VISIBLE);
                    //Perform Query on db for dipatcher login
                    String u_name =username.getText().toString();
                   Query queryon_dispatcher = dispatcher.orderByChild("username").equalTo(username.getText().toString().trim());
                   Log.d("username",username.getText().toString());
                   queryon_dispatcher.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         if (dataSnapshot.exists()){
                             for (DataSnapshot credentials : dataSnapshot.getChildren()){
                                 Log.d("password_db",credentials.child("password").getValue(String.class));
                                 Log.d("password_app",password.getText().toString().trim());
                                 if(TextUtils.equals(credentials.child("password").getValue(String.class),password.getText().toString().trim())){


                                     Intent i = new Intent(login.this,dashboard.class);
                                     startActivity(i);
                                     wave.setVisibility(View.GONE);
                                 }else {
                                     wave.setVisibility(View.GONE);
                                     Toast.makeText(login.this,"Invalid Cedentials",Toast.LENGTH_LONG).show();
                                     Log.d("login_problem","password");

                                 }
                             }

                         }else {
                             wave.setVisibility(View.GONE);
                             Toast.makeText(login.this,"Invalid Cedentials",Toast.LENGTH_LONG).show();
                         }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });




                }



            }
        });




        signup = findViewById(R.id.signup_layout);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(login.this,signUp.class);
                startActivity(i);
            }
        });





    }
}
