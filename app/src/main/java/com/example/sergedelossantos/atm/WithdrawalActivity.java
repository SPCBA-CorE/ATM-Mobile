package com.example.sergedelossantos.atm;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

public class WithdrawalActivity extends AppCompatActivity {

    private EditText txtAmountToWithdraw;
    private Button btnWithdraw;
    private ProgressDialog pDialog;
    private RequestQueue queue;
    private Bundle bundle;
    private String cardid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);
        queue = Volley.newRequestQueue(this);

        bundle = getIntent().getExtras();
        cardid = bundle.getString("CardId");

        txtAmountToWithdraw = (EditText) findViewById(R.id.txtAmountToWithdraw);
        btnWithdraw = (Button) findViewById(R.id.btnWithdraw);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        WithdrawalActivity.this);

                alertDialogBuilder.setTitle("Receipt");
                alertDialogBuilder
                        .setMessage("Do you want some receipt?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Withdraw(Double.valueOf(txtAmountToWithdraw.getText().toString()), cardid, true);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Withdraw(Double.valueOf(txtAmountToWithdraw.getText().toString()), cardid, false);
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }



    private void Withdraw(final double amount, final String cardid, final boolean wantReceipt){

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "http://unionbankphils.gear.host/api/Bank/Withdraw?amount="+amount+"&cardid="+cardid+"&providereceipt="+wantReceipt,
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
                                        "Thank you for banking with us",
                                        Toast.LENGTH_LONG).show();

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

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
