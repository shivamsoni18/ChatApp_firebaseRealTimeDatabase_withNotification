package com.example.firebasechat.Activities;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.firebasechat.R;
import com.example.firebasechat.Utils.EndPoints;
import com.example.firebasechat.Utils.MyVolley;
import com.example.firebasechat.Utils.Temp_UserDetails;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton, backbtn;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    TextView usernameTv;
    String OtherUserid, OtherName, NotiFromUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        OtherUserid = getIntent().getStringExtra("OtherUserid");
        OtherName = getIntent().getStringExtra("OtherName");
        NotiFromUser = getIntent().getStringExtra("notiFrom");

        Temp_UserDetails.chatWith = OtherUserid;

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        backbtn = (ImageView) findViewById(R.id.backbtn);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        usernameTv = (TextView) findViewById(R.id.username);


        Firebase.setAndroidContext(this);

        if (NotiFromUser != null) {
            usernameTv.setText(NotiFromUser);
            reference1 = new Firebase("https://fir-chat-d6c4d.firebaseio.com/OneToOneChat/" + Temp_UserDetails.username + "_" + NotiFromUser);
            reference2 = new Firebase("https://fir-chat-d6c4d.firebaseio.com/OneToOneChat/" + NotiFromUser + "_" + Temp_UserDetails.username);

        } else {
            usernameTv.setText(OtherName);
            reference1 = new Firebase("https://fir-chat-d6c4d.firebaseio.com/OneToOneChat/" + Temp_UserDetails.username + "_" + Temp_UserDetails.chatWith);
            reference2 = new Firebase("https://fir-chat-d6c4d.firebaseio.com/OneToOneChat/" + Temp_UserDetails.chatWith + "_" + Temp_UserDetails.username);
        }
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", Temp_UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    sendSinglePush();

                    messageArea.setText("");
                }

            }
        });


        reference1.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);

                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if (userName.equals(Temp_UserDetails.username)) {
                    addMessageBox(message, 1);

                } else {
                    addMessageBox(message, 2);
                }


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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);
        textView.setElevation(5);
        textView.setTextSize(2, 16);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 10.0f;

        if (type == 1) {
            lp2.gravity = Gravity.RIGHT;
            lp2.setMargins(200, 20, 20, 10);
            textView.setPadding(30, 25, 100, 25);
            textView.setTextColor(Color.DKGRAY);
            textView.setBackgroundColor(Color.WHITE);

        } else {
            lp2.gravity = Gravity.LEFT;
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.GRAY);
            lp2.setMargins(20, 20, 200, 10);
            textView.setPadding(100, 25, 30, 25);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private void sendSinglePush() {
        final String title = Temp_UserDetails.username;
        final String message = messageArea.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_SEND_SINGLE_PUSH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(ChatActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);

                if (NotiFromUser != null) {
                    params.put("email", NotiFromUser);
                } else {
                    params.put("email", Temp_UserDetails.chatWith);
                }
                return params;
            }
        };

        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }
}