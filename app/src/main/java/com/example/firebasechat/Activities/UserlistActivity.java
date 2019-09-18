package com.example.firebasechat.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasechat.Adapter.RegisteredUserAdapter;
import com.example.firebasechat.ModelClass.LoginPojo;
import com.example.firebasechat.R;
import com.example.firebasechat.Utils.PrefManager;
import com.example.firebasechat.Utils.Temp_UserDetails;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class UserlistActivity extends AppCompatActivity {

    ArrayList<LoginPojo> al = new ArrayList<LoginPojo>();
    int totalUsers = 0;
    RecyclerView usersRv;
    Context context;
    TextView logoutBtn,ownId;
    RegisteredUserAdapter registeredUserAdapter;
    LinearLayoutManager manager;
    PrefManager prefManager;
    boolean doubleBackToExitPressedOnce = false;


    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_users);
        Firebase.setAndroidContext(this);
        manager = new LinearLayoutManager(this);
        logoutBtn = (TextView) findViewById(R.id.logoutBtn);
        ownId = (TextView) findViewById(R.id.ownid);

        prefManager = new PrefManager(this);

        final ProgressDialog pd = new ProgressDialog(UserlistActivity.this);
        pd.setMessage("Loading...");
        pd.show();
        context = this;

        usersRv = (RecyclerView) findViewById(R.id.rvmain);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                prefManager.deletePrefData();
                startActivity(i);
                finish();
            }
        });

        Firebase reference = new Firebase("https://fir-chat-d6c4d.firebaseio.com/UserDetails");
        ownId.setText("LOGGED IN AS : "+Temp_UserDetails.username);

        reference.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);

                LoginPojo loginPojo = new LoginPojo();
                JSONObject jsonObject = new JSONObject(map);
                for (int i = 0; i <= jsonObject.length(); i++) {

                    try {

                        loginPojo.setName(jsonObject.getString("Name"));
                        loginPojo.setUserName(jsonObject.getString("UserName"));
                        loginPojo.setPassword(jsonObject.getString("Password"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                try {
                    if (!jsonObject.getString("UserName").equals(Temp_UserDetails.username)) {
                        al.add(loginPojo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                registeredUserAdapter = new RegisteredUserAdapter(al, context);

                usersRv.setLayoutManager(manager);

                usersRv.setAdapter(registeredUserAdapter);

                registeredUserAdapter.notifyDataSetChanged();
                pd.dismiss();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
