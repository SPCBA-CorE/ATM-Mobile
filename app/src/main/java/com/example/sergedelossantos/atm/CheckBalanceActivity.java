package com.example.sergedelossantos.atm;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

public class CheckBalanceActivity extends AppCompatActivity {

    private TextView txtBalance, txtAccountNo;
    private ProgressDialog pDialog;
    private RequestQueue queue;
    private Bundle bundle;
    private String cardid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_balance);
        queue = Volley.newRequestQueue(this);

        bundle = getIntent().getExtras();
        cardid = bundle.getString("CardId");

        txtBalance = (TextView) findViewById(R.id.txtBalance);
        txtAccountNo = (TextView) findViewById(R.id.txtAccountNo);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        CheckBalance(cardid);
    }

    private void CheckBalance(final String cardid){

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "http://unionbankphils.gear.host/api/Bank/CheckBalance?cardid="+cardid,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing json object response
                            // response will be a json object
                            double balance = response.getDouble("Amount");
                            String accountNo = response.getString("AccountNo");

                            txtBalance.setText(String.valueOf(balance));
                            txtAccountNo.setText(accountNo);

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
