package com.example.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.livetvseries.MyApplication;
import com.example.livetvseries.R;
import com.example.livetvseries.SignInActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class DeleteAccount {

    ProgressDialog pDialog;
    Activity mActivity;
    MyApplication myApplication;

    public DeleteAccount(Activity activity) {
        this.mActivity = activity;
        myApplication = MyApplication.getInstance();
        pDialog = new ProgressDialog(mActivity);

        if (NetworkUtils.isConnected(mActivity)) {
            new AlertDialog.Builder(mActivity)
                    .setTitle(mActivity.getString(R.string.delete_account))
                    .setMessage(mActivity.getString(R.string.delete_account_msg))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            userDeleteAcc();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.mipmap.ic_launcher_round)
                    .show();
        } else {
            Toast.makeText(mActivity, mActivity.getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }
    }

    private void userDeleteAcc() {
        showProgressDialog();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "delete_user_account");
        jsObj.addProperty("user_id", myApplication.getUserId());
        params.put("data", API.toBase64(jsObj.toString()));

        client.post(Constant.API_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();
                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    Constant.GET_SUCCESS_MSG = mainJson.getInt(Constant.SUCCESS);
                    Toast.makeText(mActivity, mainJson.getString(Constant.MSG), Toast.LENGTH_SHORT).show();
                    if (Constant.GET_SUCCESS_MSG == 1) {
                        ActivityCompat.finishAffinity(mActivity);

                        myApplication.saveIsLogin(false);
                        Intent intent = new Intent(mActivity, SignInActivity.class);
                        intent.putExtra("isLogout", true);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mActivity.startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
            }
        });
    }

    private void showProgressDialog() {
        pDialog.setMessage(mActivity.getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void dismissProgressDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }
}
