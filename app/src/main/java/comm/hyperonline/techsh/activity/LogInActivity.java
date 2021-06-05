package comm.hyperonline.techsh.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.customview.edittext.EditTextMedium;
import comm.hyperonline.techsh.customview.edittext.EditTextRegular;
import comm.hyperonline.techsh.customview.textview.TextViewLight;
import comm.hyperonline.techsh.customview.textview.TextViewRegular;
import comm.hyperonline.techsh.model.LogIn;
import comm.hyperonline.techsh.utils.APIS;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.Constant;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import comm.hyperonline.techsh.utils.Utils;
import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInActivity extends BaseActivity implements OnResponseListner {

    private static final String TAG = LogInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @BindView(R.id.tvNewUser)
    TextView tvNewUser;
    @BindView(R.id.etEmail)
    EditTextRegular etEmail;
    @BindView(R.id.ivLogo)
    ImageView ivLogo;
    @BindView(R.id.etPass)
    EditTextRegular etPass;
    @BindView(R.id.tvSignIn)
    TextViewLight tvSignIn;
    @BindView(R.id.tvForgetPassword)
    TextView tvForgetPassword;
    @BindView(R.id.ivBlackBackButton)
    ImageView ivBlackBackButton;

    AlertDialog alertDialog;
    AlertDialog alertDialog1;
    private String pin, email, password;
    private String facbookImageUrl;
    private JSONObject fbjsonObject;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);
        setScreenLayoutDirection();

        setColor();

        if (Constant.APPLOGO != null && !Constant.APPLOGO.equals("")) {
            Picasso.get().load(Constant.APPLOGO).error(R.drawable.logo).into(ivLogo);
        }


        ivBlackBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setColor() {
        tvNewUser.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvSignIn.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        tvForgetPassword.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        Drawable mDrawable = getResources().getDrawable(R.drawable.login);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)), PorterDuff.Mode.OVERLAY));

    }

    @OnClick(R.id.tvSignIn)
    public void tvSignInClick() {
        if (etEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.enter_email_address, Toast.LENGTH_SHORT).show();
        } else if (etPass.getText().toString().isEmpty()) {
            Toast.makeText(this, "رمز عبور خود را وارد کنید", Toast.LENGTH_SHORT).show();
        } else  {

//            String email_mobile=etEmail.getText().toString();
//            myUserLogin(email_mobile,etPass.getText().toString(),"mobile");
//

            sendDataToServer(etEmail.getText().toString() , etPass.getText().toString());

        }
    }

    private void sendDataToServer(String email , String password) {
        showProgress("");
        StringRequest request = new StringRequest(
                Request.Method.POST, APIS.APP_URL + "wp-json/digits/v1/login_user", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dismissProgress();
                try {
                    JSONObject jsonObject=new JSONObject(response);

                    if (jsonObject.getBoolean("success")){

                        JSONObject data = jsonObject.getJSONObject("data");

                        SharedPreferences.Editor pre = getPreferences().edit();
                        pre.putString(RequestParamUtils.CUSTOMER, "");
                        pre.putString(RequestParamUtils.ID, data.getString("user_id"));
                        pre.putString(RequestParamUtils.PASSWORD, etPass.getText().toString());
                        pre.putString(RequestParamUtils.SOCIAL_SIGNIN, "1");
                        pre.apply();

                        dismissProgress();
                        finish();
                    }else {
                        Toast.makeText(LogInActivity.this, "اطلاعات شما معتبر نیست", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LogInActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
                Toast.makeText(LogInActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            }
        }){
            Map<String,String> params=new HashMap<>();

            @Override
            public Map<String, String> getParams() {
                params.put("user",email);
                params.put("countrycode",Constant.MOBILE_COUNTRY_CODE);
                params.put("password",password);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(7000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(LogInActivity.this);
        requestQueue.add(request);

    }

    private void myUserLogin(final String mobile_email, final String password, final String type) {

        final StringRequest request = new StringRequest(Request.Method.POST, APIS.APP_URL + "app/login.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String status=jsonObject.getString("status");
                    if (status.equals("ok")){
                    //    Toast.makeText(LogInActivity.this, ""+jsonObject.getString("user_id"), Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor pre = getPreferences().edit();
                        pre.putString(RequestParamUtils.CUSTOMER, "");
                        pre.putString(RequestParamUtils.ID, jsonObject.getString("user_id") + "");
                        pre.putString(RequestParamUtils.PASSWORD, etPass.getText().toString());
                        pre.putString(RequestParamUtils.SOCIAL_SIGNIN, "1");
                        pre.commit();

//                                    Intent intent = new Intent(LogInActivity.this, AccountActivity.class);
//                                    startActivity(intent);

                        dismissProgress();
                        finish();
                    }else {
                        Toast.makeText(LogInActivity.this, "اطلاعات شما معتبر نیست", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LogInActivity.this,  "در حال بارگذاری", Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
                Toast.makeText(LogInActivity.this,  "در حال بارگذاری", Toast.LENGTH_SHORT).show();
                Log.i("sjkfhkhfkjs", error.toString());
            }
        }){
            Map<String,String> params=new HashMap<>();

            @Override
            public Map<String, String> getParams() {
                params.put("mobile_email",mobile_email);
                params.put("password",password);
                params.put("type",type);
                return params;
            }
        };



        request.setRetryPolicy(new DefaultRetryPolicy(7000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(LogInActivity.this);
        requestQueue.add(request);

    }


    public void userlogin() {

        if (Utils.isInternetConnected(this)) {
            showProgress("");
            PostApi postApi = new PostApi(LogInActivity.this, RequestParamUtils.login, this, getlanuage());
            JSONObject object = new JSONObject();
            try {
                object.put(RequestParamUtils.email, etEmail.getText().toString());
                //     object.put(RequestParamUtils.mobiles, etEmail.getText().toString());
                object.put(RequestParamUtils.PASSWORD, etPass.getText().toString());
                object.put(RequestParamUtils.deviceType, "2");
                String token = getPreferences().getString(RequestParamUtils.NOTIFICATION_TOKEN, "");
                object.put(RequestParamUtils.deviceToken, token);
                postApi.callPostApi(new URLS().LOGIN, object.toString());

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.tvSignInWithGoogle)
    public void tvSignInWithGoogleClick() {

    }


    public String getBitmap() {
        try {
            URL url = new URL(facbookImageUrl);
            try {


                Bitmap mIcon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mIcon.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();

                String encoded = Base64.encodeToString(b, Base64.DEFAULT);

                return encoded;
            } catch (IOException e) {
                Log.e("IOException ", e.getMessage());

            } catch (Exception e) {
                Log.e("Exception ", e.getMessage());

            }
            Log.e("Done", "Done");
        } catch (IOException e) {
            Log.e("Exception Url ", e.getMessage());
        }
        return null;
    }

    public void socialLogin(final JSONObject object) {
        if (Utils.isInternetConnected(LogInActivity.this)) {
            showProgress("");
            final PostApi postApi = new PostApi(LogInActivity.this, RequestParamUtils.socialLogin, LogInActivity.this, getlanuage());

            try {
                object.put(RequestParamUtils.deviceType, Constant.DEVICE_TYPE);

                String token = getPreferences().getString(RequestParamUtils.NOTIFICATION_TOKEN, "");
                object.put(RequestParamUtils.deviceToken, token);
                postApi.callPostApi(new URLS().SOCIAL_LOGIN, object.toString());

            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
        } else {
            Toast.makeText(LogInActivity.this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    // this part was missing thanks to wesely

    @OnClick(R.id.tvNewUser)
    public void tvNewUserClick() {
        Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.tvForgetPassword)
    public void tvForgetPassClick() {
        Intent intent = new Intent(LogInActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    public void showForgetPassDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_forget_password, null);
        dialogBuilder.setView(dialogView);

        TextViewRegular tvRequestPasswordReset = (TextViewRegular) dialogView.findViewById(R.id.tvRequestPasswordReset);
        tvRequestPasswordReset.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        final EditTextRegular etForgetPassEmail = (EditTextRegular) dialogView.findViewById(R.id.etForgetPassEmail);

        alertDialog1 = dialogBuilder.create();
        alertDialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = alertDialog1.getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        alertDialog1.getWindow().setAttributes(lp);
        alertDialog1.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        alertDialog1.show();

        tvRequestPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etForgetPassEmail.getText().toString().isEmpty()) {
                    Toast.makeText(LogInActivity.this, R.string.enter_email_address, Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.isValidEmail(etForgetPassEmail.getText().toString())) {
                        email = etForgetPassEmail.getText().toString();
                        forgetPassword();
                    } else {
                        Toast.makeText(LogInActivity.this, R.string.enter_valid_email_address, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void forgetPassword() {
        if (Utils.isInternetConnected(this)) {
            showProgress("");
            PostApi postApi = new PostApi(this, RequestParamUtils.forgotPassword, this, getlanuage());

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.email, email);
                postApi.callPostApi(new URLS().FORGET_PASSWORD, jsonObject.toString());

            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    public void showSetPassDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_forget_password_pin, null);
        dialogBuilder.setView(dialogView);

        final TextViewRegular tvSetNewPass = (TextViewRegular) dialogView.findViewById(R.id.tvSetNewPass);
        tvSetNewPass.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        final TextViewLight tvNowEnterPass = (TextViewLight) dialogView.findViewById(R.id.tvNowEnterPass);
        final EditTextMedium etPin = (EditTextMedium) dialogView.findViewById(R.id.etPin);
        final EditTextMedium etNewPassword = (EditTextMedium) dialogView.findViewById(R.id.etNewPassword);
        final EditTextMedium etConfirrmNewPassword = (EditTextMedium) dialogView.findViewById(R.id.etConfirrmNewPassword);

        alertDialog = dialogBuilder.create();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        alertDialog.show();

        etPin.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPin.getText().toString().equals(pin)) {
                    tvNowEnterPass.setVisibility(View.VISIBLE);
                    etNewPassword.setVisibility(View.VISIBLE);
                    etConfirrmNewPassword.setVisibility(View.VISIBLE);
                }
            }
        });


        tvSetNewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etPin.getText().toString().isEmpty()) {
                    Toast.makeText(LogInActivity.this, R.string.enter_pin, Toast.LENGTH_SHORT).show();
                } else {
                    if (etPin.getText().toString().equals(pin)) {
                        if (etNewPassword.getText().toString().isEmpty()) {
                            Toast.makeText(LogInActivity.this, R.string.enter_new_password, Toast.LENGTH_SHORT).show();
                        } else {
                            if (etConfirrmNewPassword.getText().toString().isEmpty()) {
                                Toast.makeText(LogInActivity.this, R.string.enter_confirm_password, Toast.LENGTH_SHORT).show();
                            } else {
                                if (etNewPassword.getText().toString().equals(etConfirrmNewPassword.getText().toString())) {
                                    //apicalls
                                    password = etNewPassword.getText().toString();
                                    updatePassword();

                                } else {
                                    Toast.makeText(LogInActivity.this, R.string.password_and_confirm_password_not_matched, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } else {
                        Toast.makeText(LogInActivity.this, R.string.enter_proper_detail, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void updatePassword() {

        if (Utils.isInternetConnected(this)) {
            showProgress("");
            PostApi postApi = new PostApi(this, RequestParamUtils.updatePassword, this, getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put(RequestParamUtils.email, email);
                jsonObject.put(RequestParamUtils.PASSWORD, password);
                jsonObject.put(RequestParamUtils.key, pin);

                postApi.callPostApi(new URLS().UPDATE_PASSWORD, jsonObject.toString());
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }

        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onResponse(final String response, final String methodName) {

        if (methodName.equals(RequestParamUtils.login) || methodName.equals(RequestParamUtils.socialLogin)) {

            dismissProgress();
            if (response != null && response.length() > 0) {
                try {
                    JSONObject jsonObj = new JSONObject(response);

                    String status = jsonObj.getString("status");
                    if (status.equals("success")) {
                        final LogIn loginRider = new Gson().fromJson(
                                response, new TypeToken<LogIn>() {
                                }.getType());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //set call here
                                if (loginRider.status.equals("success")) {

                                    SharedPreferences.Editor pre = getPreferences().edit();
                                    pre.putString(RequestParamUtils.CUSTOMER, "");
                                    pre.putString(RequestParamUtils.ID, loginRider.user.id + "");
                                    pre.putString(RequestParamUtils.PASSWORD, etPass.getText().toString());
                                    if (methodName.equals(RequestParamUtils.socialLogin)) {
                                        pre.putString(RequestParamUtils.SOCIAL_SIGNIN, "1");
                                    }
                                    pre.commit();

//                                    Intent intent = new Intent(LogInActivity.this, AccountActivity.class);
//                                    startActivity(intent);

                                    dismissProgress();
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.enter_proper_detail, Toast.LENGTH_SHORT).show(); //display in long period of time
                                }
                            }
                        });
                    } else {
                        String msg = jsonObj.getString("message");
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                    Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show(); //display in long period of time
                }
            }
        } else if (methodName.equals(RequestParamUtils.forgotPassword)) {
            dismissProgress();
            if (response != null && response.length() > 0) {
                try {
                    JSONObject jsonObj = new JSONObject(response);

                    String status = jsonObj.getString("status");
                    if (status.equals("success")) {
                        alertDialog1.dismiss();
                        pin = jsonObj.getString("key");
                        showSetPassDialog();
                    } else {
                        Toast.makeText(this, jsonObj.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                    Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show(); //display in long period of time
                }
            }
        } else if (methodName.equals(RequestParamUtils.updatePassword)) {
            dismissProgress();
            if (response != null && response.length() > 0) {
                try {
                    JSONObject jsonObj = new JSONObject(response);

                    String status = jsonObj.getString("status");
                    if (status.equals("success")) {
                        alertDialog.dismiss();
                        Toast.makeText(this, jsonObj.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, jsonObj.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                    Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show(); //display in long period of time
                }
            }
        }


    }

    class getBitmap extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            return getBitmap();
        }

        @Override
        protected void onPostExecute(String encoded) {
            super.onPostExecute(encoded);
            try {
                JSONObject jsonObject = new JSONObject();

                jsonObject.put(RequestParamUtils.data, encoded);
                jsonObject.put(RequestParamUtils.name, "image.jpg");

              /*  Log.e("name", fbjsonObject.getString("name"));
                Log.e("email", fbjsonObject.getString("email"));*/
                if (fbjsonObject.has(RequestParamUtils.gender)) {
                    Log.e("gender", fbjsonObject.getString(RequestParamUtils.gender));
                }

                fbjsonObject.put(RequestParamUtils.userImage, jsonObject);
                fbjsonObject.put(RequestParamUtils.socialId, fbjsonObject.getString("id"));
                socialLogin(fbjsonObject);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

