package com.example.firebasechat.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.firebasechat.Utils.EndPoints;
import com.example.firebasechat.Utils.SharedPrefManager;
import com.example.firebasechat.R;
import com.example.firebasechat.Utils.Temp_UserDetails;
import com.firebase.client.Firebase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText name, username, pass;
    Button Add;
    String tkn;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.name);
        username = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);

        Add = (Button) findViewById(R.id.registerButton);
        Firebase.setAndroidContext(this);
        tkn = FirebaseInstanceId.getInstance().getToken();

        login = (TextView) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (username.getText().toString().equals("")) {
                    username.setError("can't be blank");
                } else if (pass.getText().toString().equals("")) {
                    pass.setError("can't be blank");
                } else if (!username.getText().toString().matches("[A-Za-z0-9]+")) {
                    username.setError("only alphabet or number allowed");
                } else if (username.getText().toString().length() < 5) {
                    username.setError("at least 5 characters long");
                } else if (pass.getText().toString().length() < 5) {
                    pass.setError("at least 5 characters long");
                } else {
                    final ProgressDialog pd = new ProgressDialog(RegistrationActivity.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    String url = "https://fir-chat-d6c4d.firebaseio.com/RegisteredUsers.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {

                            if (s.equals("null")) {
                                NewUserRegistration();

                            } else {
                                try {
                                    JSONObject obj = new JSONObject(s);
                                    if (!obj.has(username.getText().toString())) {
                                        NewUserRegistration();
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, "username already exists", Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(RegistrationActivity.this);
                    rQueue.add(request);

                }

            }


        });

    }

    private void NewUserRegistration() {
        Firebase reference = new Firebase("https://fir-chat-d6c4d.firebaseio.com/RegisteredUsers");
        Firebase reference2 = new Firebase("https://fir-chat-d6c4d.firebaseio.com/UserDetails");

        reference.child(username.getText().toString()).child("password").setValue(pass.getText().toString());

        Map<String, String> map = new HashMap<String, String>();

        Temp_UserDetails.MyName = name.getText().toString();
        Temp_UserDetails.MyUserId = username.getText().toString();
        Temp_UserDetails.Password = pass.getText().toString();

        map.put("Name", name.getText().toString());
        map.put("UserName", username.getText().toString());
        map.put("Password", pass.getText().toString());

        reference2.push().setValue(map);

        name.setText("");
        username.setText("");
        pass.setText("");

        Toast.makeText(RegistrationActivity.this, "registration successful", Toast.LENGTH_LONG).show();
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        finish();

        sendTokenToServer();
    }

    private void sendTokenToServer() {


        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        final String email = Temp_UserDetails.MyUserId;

        if (tkn == null) {
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, EndPoints.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegistrationActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("token", tkn);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public class NotifiactionSend extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            try {

                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=AAAAw3d5g5w:APA91bFY3DfuryPpHfPjSGCA7J_diTKWYcXHMB11gWcIhUwCQAeibbgeAJpLJwCQPXvUXBuBJjbQXkEBoQ6GuVMHAmL_lkDRpITXvPCDF48mXNZN4MPYwuu-NmjBQGnvkjobVztRfLWJ");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();

                json.put("to", tkn);

                JSONObject info = new JSONObject();
                info.put("title", "FireChat");   // Notification title
                info.put("body", "Welcome to FireChat " + Temp_UserDetails.MyName); // Notification body

                json.put("notification", info);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json.toString());
                wr.flush();
                conn.getInputStream();

            } catch (Exception e) {
                Log.d("Error", "" + e);
            }
            return null;
        }
    }

}
