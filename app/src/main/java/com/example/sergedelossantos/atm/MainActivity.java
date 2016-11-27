package com.example.sergedelossantos.atm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText txtCardId;
    private EditText txtPassword;
    private ProgressDialog pDialog;
    private RequestQueue queue;
    private Bundle bundle;
    private String cardid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);

        txtCardId = (EditText) findViewById(R.id.txtCardid);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IsActivatedByCardCode(txtCardId.getText().toString(), txtPassword.getText().toString());
            }
        });
    }

    //Check if card is activated
    private void IsActivatedByCardCode(final String cardcode, final String passsword) {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "http://unionbankphils.gear.host/api/UserCard/IsActivatedByCardCode?cardcode="+cardcode,
                null,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Parsing json object response
                    // response will be a json object
                    boolean IsActivated = response.getBoolean("ResultBoolean");

                    if(IsActivated){
                        //Proceed to log in cardid and password
                        LogIn(cardcode, passsword);
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "This card is not yet activated",
                                Toast.LENGTH_LONG).show();

                        //proceed to activation
                        Intent i = new Intent(getApplicationContext(),ActivationActivity.class);
                        startActivity(i);

                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void LogIn(final String cardcode, final String password){
        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "http://unionbankphils.gear.host/api/UserCard/Login?cardcode="+cardcode+"&password="+password,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing json object response
                            // response will be a json object
                            boolean AccessGranted = response.getBoolean("ResultBoolean");
                            cardid = response.getString("CardId");

                            if(AccessGranted){
                                //Proceed to log in cardid and password
                                Intent i = new Intent(getApplicationContext(),TransactionActivity.class);
                                bundle = new Bundle();
                                bundle.putString("CardId", cardid);
                                i.putExtras(bundle);
                                startActivity(i);

                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),
                                        "Access Denied!",
                                        Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        hidepDialog();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
