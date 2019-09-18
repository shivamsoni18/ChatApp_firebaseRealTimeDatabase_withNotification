package com.example.firebasechat.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
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
import com.example.firebasechat.R;
import com.example.firebasechat.Utils.EndPoints;
import com.example.firebasechat.Utils.PrefManager;
import com.example.firebasechat.Utils.SharedPrefManager;
import com.example.firebasechat.Utils.Temp_UserDetails;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.firebasechat.Utils.PrefManager.prefPASSWORD;
import static com.example.firebasechat.Utils.PrefManager.prefUSERNAME;

public class LoginActivity extends AppCompatActivity {
    TextView registerUser;
    EditText username, password;
    Button loginButton;
    String user, pass;
    String tkn;
    PrefManager prefManager;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prefManager = new PrefManager(this);
        tkn = FirebaseInstanceId.getInstance().getToken();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        registerUser = (TextView) findViewById(R.id.register);
        username = (EditText) findViewById(R.id.loginUsername);
        password = (EditText) findViewById(R.id.loginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            connected = true;

            if (prefManager.getPrefValue("Pusername") != null) {

                username.setText(prefManager.getPrefValue("Pusername"));
                password.setText(prefManager.getPrefValue("Ppassword"));

                StrinRequest();
            }


            registerUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                    finish();
                }
            });


            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user = username.getText().toString();
                    pass = password.getText().toString();


                    if (user.equals("")) {
                        username.setError("can't be blank");
                    } else if (pass.equals("")) {
                        password.setError("can't be blank");
                    } else {
                        String url = "https://fir-chat-d6c4d.firebaseio.com/RegisteredUsers.json";
                        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                        pd.setMessage("Loading...");
                        pd.show();

                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                if (s.equals("null")) {
                                    Toast.makeText(LoginActivity.this, "user not found", Toast.LENGTH_LONG).show();
                                } else {
                                    try {
                                        JSONObject obj = new JSONObject(s);

                                        if (!obj.has(user)) {
                                            Toast.makeText(LoginActivity.this, "user not found", Toast.LENGTH_LONG).show();
                                        } else if (obj.getJSONObject(user).getString("password").equals(pass)) {
                                            Temp_UserDetails.username = user;
                                            Temp_UserDetails.password = pass;

                                            prefManager.savePrefValue(prefUSERNAME, Temp_UserDetails.username);
                                            prefManager.savePrefValue(prefPASSWORD, Temp_UserDetails.password);

                                            startActivity(new Intent(LoginActivity.this, UserlistActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "incorrect password", Toast.LENGTH_LONG).show();
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

                        RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
                        rQueue.add(request);
                    }

                }
            });

        } else {

            connected = false;
            username.setText(prefManager.getPrefValue("Pusername"));
            password.setText(prefManager.getPrefValue("Ppassword"));

            Toast.makeText(this, "Please connect to the Internet", Toast.LENGTH_SHORT).show();

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(LoginActivity.this, "no network", Toast.LENGTH_SHORT).show();

                    StrinRequest();

                }
            });

        }

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
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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

    private void StrinRequest() {

        String url = "https://fir-chat-d6c4d.firebaseio.com/RegisteredUsers.json";

        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                try {
                    JSONObject obj = new JSONObject(s);

                    user = prefManager.getPrefValue("Pusername");
                    if (obj.getJSONObject(user).getString("password").equals(prefManager.getPrefValue("Ppassword"))) {

                        Temp_UserDetails.username = user;
                        Temp_UserDetails.password = pass;

                        sendTokenToServer();
                        startActivity(new Intent(LoginActivity.this, UserlistActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "incorrect password", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

        RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
        rQueue.add(request);
    }

}