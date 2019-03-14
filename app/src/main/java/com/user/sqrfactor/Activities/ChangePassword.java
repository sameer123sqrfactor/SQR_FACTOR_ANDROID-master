package com.user.sqrfactor.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.user.sqrfactor.Fragments.MDToast;
import com.user.sqrfactor.Pojo.TokenClass;
import com.user.sqrfactor.R;
import com.user.sqrfactor.Extras.UtilsClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ChangePassword extends AppCompatActivity {

    Toolbar toolbar;
    private EditText oldPassword,newPassword,confirmPassword;
    private Button save;
    int counter = 0;
    Thread t =null;
    ProgressDialog dialog = null;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_change_password);

            toolbar = (Toolbar) findViewById(R.id.change_password_toolbar);
            toolbar.setTitle("Profile");
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.back_arrow);

            save=(Button)findViewById(R.id.change_password_button);
            oldPassword =(EditText)findViewById(R.id.old_password_text);
            newPassword =(EditText)findViewById(R.id.new_password_text);
            confirmPassword =(EditText)findViewById(R.id.confirm_password_text);

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ValidateData();

                }
            });
        }

        public void ValidateData() {
            if (!TextUtils.isEmpty(oldPassword.getText().toString())&&!TextUtils.isEmpty(newPassword.getText().toString())&&!TextUtils.isEmpty(confirmPassword.getText().toString())) {
                saveDataToServer();
            } else {
                MDToast.makeText(this, "Your password field in empty", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
                return;
            }
        }
        public void saveDataToServer() {
            RequestQueue requestQueue1 = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UtilsClass.baseurl+"parse/changepassword",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Log.v("ResponseLike",s);
                            MDToast.makeText(getApplication(), "SUCCESS", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                oldPassword.setText("");
                                newPassword.setText("");
                                confirmPassword.setText("");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                        }
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Accept", "application/json");
                    params.put("Authorization", "Bearer "+TokenClass.Token);
                    return params;
                }
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("old_password",oldPassword.getText().toString());
                    params.put("new_password",newPassword.getText().toString());
                    params.put("new_password_confirmation",confirmPassword.getText().toString());
                    return params;
                }
            };

            //Adding request to the queue
            requestQueue1.add(stringRequest);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if(id==android.R.id.home){
                onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

}
