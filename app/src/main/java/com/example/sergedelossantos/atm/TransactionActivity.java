package com.example.sergedelossantos.atm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class TransactionActivity extends AppCompatActivity {

    private Button btnCheckBalance;
    private Button btnWithdraw;
    private Bundle bundle;
    private Switch blockCard;
    private String cardid;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        queue = Volley.newRequestQueue(this);

        bundle = getIntent().getExtras();
        cardid = bundle.getString("CardId");

        btnCheckBalance = (Button) findViewById(R.id.btnCheckBalance);
        btnWithdraw = (Button) findViewById(R.id.btnWithdraw);
        blockCard = (Switch) findViewById(R.id.switchLock);

        //attach a listener to check for changes in state
        CheckIfCardBlocked(cardid);

        blockCard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    BlockCard(cardid, true);
                }else{
                    BlockCard(cardid, false);
                }
            }
        });

        btnCheckBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),CheckBalanceActivity.class);
                bundle = new Bundle();
                bundle.putString("CardId", cardid);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),WithdrawalActivity.class);
                bundle = new Bundle();
                bundle.putString("CardId", cardid);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    private void BlockCard(String cardid, boolean isblocked){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "http://unionbankphils.gear.host/api/Bank/BlockCard?cardid="+cardid+"&isblocked="+isblocked,
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
                                        "Card blocked",
                                        Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),
                                        "Card un-blocked",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        queue.add(jsonObjReq);
    }

    private void CheckIfCardBlocked(String cardid){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                "http://unionbankphils.gear.host/api/Bank/CheckifCardisBlock?cardid="+cardid,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Parsing json object response
                            // response will be a json object
                            boolean IsBlocked = response.getBoolean("ResultBoolean");

                            if(IsBlocked){
                                blockCard.setChecked(IsBlocked);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to request queue
        queue.add(jsonObjReq);
    }
}
