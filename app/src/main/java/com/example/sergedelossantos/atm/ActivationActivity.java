package com.example.sergedelossantos.atm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivationActivity extends AppCompatActivity {

    private EditText txtCardcode, txtPassword, txtConfirmPassword;
    private Button btnActivate;
    private ProgressDialog pDialog;
    private RequestQueue queue;
    private Bundle bundle;
    private String cardid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        queue = Volley.newRequestQueue(this);

        txtCardcode = (EditText) findViewById(R.id.txtCardcode);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

        btnActivate = (Button) findViewById(R.id.btnActivate);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        btnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String password = txtPassword.getText().toString();
                final String confirmPassword = txtConfirmPassword.getText().toString();

                    if(password.equals(confirmPassword)){
                        ActivateCard(txtCardcode.getText().toString(), confirmPassword);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),
                                "Password did not match!",
                                Toast.LENGTH_LONG).show();

                        txtPassword.setText("");
                        txtConfirmPassword.setText("");
                        txtPassword.setFocusable(true);
                    }
            }
        });
    }

    private void ActivateCard(final String cardcode, final String password){
        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "http://unionbankphils.gear.host/api/UserCard/UpdateAccount?cardcode="+cardcode+"&password="+password,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing json object response
                            // response will be a json object
                            boolean IsSuccess = response.getBoolean("ResultBoolean");

                            if(IsSuccess){
                                Toast.makeText(getApplicationContext(),
                                        "Your card has been activated",
                                        Toast.LENGTH_LONG).show();

                                //then proceed to transaction
                                LogIn(cardcode, password);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),
                                        "Your card cannot be activate",
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
}
