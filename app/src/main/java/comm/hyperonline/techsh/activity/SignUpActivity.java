package comm.hyperonline.techsh.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import comm.hyperonline.techsh.utils.APIS;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.customview.edittext.EditTextRegular;
import comm.hyperonline.techsh.customview.textview.TextViewLight;
import comm.hyperonline.techsh.javaclasses.SyncWishList;
import comm.hyperonline.techsh.model.LogIn;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.Constant;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import comm.hyperonline.techsh.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends BaseActivity implements OnResponseListner {


    @BindView(R.id.etFirstName)
    EditTextRegular etFirstName;

//    @BindView(R.id.etUsername)
//    EditTextRegular etUsername;
//
//    @BindView(R.id.etEmail)
//    EditTextRegular etEmail;

    @BindView(R.id.etContact)
    EditTextRegular etContact;

    @BindView(R.id.etPass)
    EditTextRegular etPass;

    @BindView(R.id.etConfirmPass)
    EditTextRegular etConfirmPass;

    @BindView(R.id.ivLogo)
    ImageView ivLogo;

    @BindView(R.id.tvAlreadyAccount)
    TextViewLight tvAlreadyAccount;

    @BindView(R.id.tvSignInNow)
    TextViewLight tvSignInNow;

    @BindView(R.id.tvSignUp)
    TextViewLight tvSignUp;


//    @BindView(R.id.ccp)
//    CountryCodePicker ccp;


    Thread thread;

    private Handler timerHandler = new Handler();
    private final int TOTAL_SECOND = 60;
    private int time = TOTAL_SECOND;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_ups);
        ButterKnife.bind(this);
        setScreenLayoutDirection();
        setThemeColor();
    }

    @OnClick(R.id.tvSignInNow)
    public void tvSignInNowClick() {
        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.ivBlackBackButton)
    public void ivBackClick() {
        onBackPressed();
    }


    public void setThemeColor() {

        if (Constant.APPLOGO != null && !Constant.APPLOGO.equals("")) {
            Picasso.get().load(Constant.APPLOGO).error(R.drawable.logo).into(ivLogo);
        }
        Drawable mDrawable = getResources().getDrawable(R.drawable.login);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)), PorterDuff.Mode.OVERLAY));


        tvAlreadyAccount.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvSignInNow.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvSignUp.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
    }

    @OnClick(R.id.tvSignUp)
    public void tvSignUpClick() {
        if (etFirstName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, R.string.first_name, Toast.LENGTH_SHORT).show();
            return;
        }
//        if (etUsername.getText().toString().isEmpty()) {
//            Toast.makeText(this, R.string.enter_username, Toast.LENGTH_SHORT).show();
//        } else {

        if (etContact.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.enter_contact_number, Toast.LENGTH_SHORT).show();
        } else {
            if (etPass.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.enter_password, Toast.LENGTH_SHORT).show();
            } else {
                if (etConfirmPass.getText().toString().isEmpty()) {
                    Toast.makeText(this, R.string.enter_confirm_password, Toast.LENGTH_SHORT).show();
                } else {
                    if (etPass.getText().toString().equals(etConfirmPass.getText().toString())) {
                        //  registerUser();

                        sendOtp(etFirstName.getText().toString().trim()
                                , etContact.getText().toString().trim()
                                , etPass.getText().toString().trim()
//                                , etUsername.getText().toString().trim()
                                , ""
                        );
                        //     sendNumber(etContact.getText().toString().trim());
                    } else {
                        Toast.makeText(this, R.string.password_and_confirm_password_not_matched, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        //  }
    }

    private void sendOtp(
            String name
            , String mobile
            , String password
            , String email
    ) {
        showProgress("");
        StringRequest request = new StringRequest(
                Request.Method.POST, APIS.APP_URL + "wp-json/digits/v1/send_otp", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dismissProgress();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("code").equals("1")) {

                        getCodeActivate(name, mobile, password, email);

                    } else {
                        Toast.makeText(SignUpActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUpActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
                Toast.makeText(SignUpActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            }
        }) {
            Map<String, String> params = new HashMap<>();

            @Override
            public Map<String, String> getParams() {
                params.put("countrycode", Constant.MOBILE_COUNTRY_CODE);
                params.put("mobileNo", mobile);
                params.put("type", "register");
                params.put("username ", mobile);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(7000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);
        requestQueue.add(request);
    }

    public void getCodeActivate(
            String name
            , String mobile
            , String password
//            , String username
            , String email
    ) {

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_get_code_activate, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView btnOk = view.findViewById(R.id.btn_code_dialog);
        EditText edtCode = view.findViewById(R.id.edt_code_dialog);
        TextView txtTimer = view.findViewById(R.id.txt_timer_dialog);

        time = TOTAL_SECOND;
        startTimer(txtTimer);
        txtTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time == 0) {
                    sendOtp(name
                            , mobile
                            , password
//                            , username
                            , email
                    );
                    alertDialog.dismiss();
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtCode.getText().toString().trim().equals("")) {
                    Toast.makeText(SignUpActivity.this, "لطفا کد فعال سازی را وارد کنید", Toast.LENGTH_SHORT).show();
                    edtCode.requestFocus();
                } else {
                    registerUserWithServer(
                            name, mobile, password, email, edtCode.getText().toString().trim());
                }
            }
        });
    }

    private void startTimer(TextView txtTimer) {

        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (time == 0) {
                    txtTimer.setText("ارسال مجدد کد فعال سازی");
                    timerHandler.removeCallbacks(this);
                    return;
                }
                time--;
                txtTimer.setText(time + " ثانیه دیگر صبر کنید.");
                timerHandler.postDelayed(this, 1000);
            }
        };

        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.postDelayed(timerRunnable, 1000);
    }



    private void registerUserWithServer(
            String name
            , String mobile
            , String password
//            , String username
            , String email
            , String otpCode
    ) {
        showProgress("");
        StringRequest request = new StringRequest(
                Request.Method.POST, APIS.APP_URL + "wp-json/digits/v1/create_user", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dismissProgress();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONObject data = jsonObject.getJSONObject("data");
                    if (jsonObject.getBoolean("success")) {


                        SharedPreferences.Editor pre = getPreferences().edit();
                        pre.putString(RequestParamUtils.CUSTOMER, "");
                        pre.putString(RequestParamUtils.ID, data.getString("user_id"));
                        pre.putString(RequestParamUtils.PASSWORD, etPass.getText().toString());
                        pre.commit();


                        new SyncWishList(SignUpActivity.this).syncWishList(getPreferences().getString(RequestParamUtils.ID, ""), false);

//                                    Intent intent = new Intent(SignUpActivity.this, AccountActivity.class);
//                                    startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(SignUpActivity.this, getHtmlText(data.getString("msg")), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUpActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
                Toast.makeText(SignUpActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            }
        }) {
            Map<String, String> params = new HashMap<>();

            @Override
            public Map<String, String> getParams() {
                params.put("digits_reg_name", name);
                params.put("digits_reg_countrycode", Constant.MOBILE_COUNTRY_CODE);
                params.put("digits_reg_mobile", mobile);
                params.put("digits_reg_password", password);
                params.put("digits_reg_username", mobile);
                params.put("digits_reg_email", email);
                params.put("otp", otpCode);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(7000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);
        requestQueue.add(request);
    }

    private String getHtmlText(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }

    public void registerUser() {
        if (Utils.isInternetConnected(this)) {
            showProgress("");
            PostApi postApi = new PostApi(this, "create_customer", this, getlanuage());
            JSONObject object = new JSONObject();
            try {

//                object.put(RequestParamUtils.email, etEmail.getText().toString());
                object.put(RequestParamUtils.email, etContact.getText().toString().trim());
//                object.put(RequestParamUtils.username, etUsername.getText().toString());
                object.put(RequestParamUtils.mobile, etContact.getText().toString().trim());
                object.put(RequestParamUtils.PASSWORD, etPass.getText().toString());
                object.put(RequestParamUtils.deviceType, Constant.DEVICE_TYPE);

                String token = getPreferences().getString(RequestParamUtils.NOTIFICATION_TOKEN, "");
                object.put(RequestParamUtils.deviceToken, token);

                postApi.callPostApi(new URLS().CREATE_CUSTOMER, object.toString());

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(this, R.string.something_went_wrong_try_after_somtime, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onResponse(final String response, String methodName) {

        if (methodName.equals(RequestParamUtils.createCustomer)) {
            if (response != null && response.length() > 0) {
                try {
                    final LogIn loginRider = new Gson().fromJson(
                            response, new TypeToken<LogIn>() {
                            }.getType());

                    JSONObject jsonObj = new JSONObject(response);
                    String status = jsonObj.getString("status");

                    if (status.equals("error")) {
                        Toast.makeText(getApplicationContext(), jsonObj.getString("message"), Toast.LENGTH_SHORT).show(); //display in long period of time
                        dismissProgress();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //set call here
                                if (loginRider.status.equals("success")) {

                                    SharedPreferences.Editor pre = getPreferences().edit();
                                    pre.putString(RequestParamUtils.CUSTOMER, "");
                                    pre.putString(RequestParamUtils.ID, loginRider.user.id + "");
                                    pre.putString(RequestParamUtils.PASSWORD, etPass.getText().toString());
                                    pre.commit();

                                    new SyncWishList(SignUpActivity.this).syncWishList(getPreferences().getString(RequestParamUtils.ID, ""), false);

//                                    Intent intent = new Intent(SignUpActivity.this, AccountActivity.class);
//                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.enter_proper_detail, Toast.LENGTH_SHORT).show(); //display in long period of time
                                }
                            }
                        });
                        dismissProgress();
                    }
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                    Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show(); //display in long period of time
                }
            }
        }
    }

    private void sendNumber(final String phoneNumber) {
        showProgress("");
        final StringRequest request = new StringRequest(Request.Method.POST, APIS.APP_URL + "app/sendSms.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dismissProgress();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("ok")) {
                        getCodeActivate(jsonObject.getString("code"), phoneNumber);
                    } else if (status.equals("user_name_exist")) {
                        Toast.makeText(SignUpActivity.this, "نام کاربری شما قبلا استفاده شده است", Toast.LENGTH_SHORT).show();
                    } else if (status.equals("mobile_exist")) {
                        Toast.makeText(SignUpActivity.this, "شماره همراه شما قبلا استفاده شده است", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "در حال بارگذاری", Toast.LENGTH_SHORT).show();
                    }

                    Log.i("sjkfhkhfkjs", status);
                } catch (JSONException e) {
                    e.printStackTrace();
                    dismissProgress();
                    Toast.makeText(SignUpActivity.this, "در حال بارگذاری", Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
                Toast.makeText(SignUpActivity.this, "در حال بارگذاری", Toast.LENGTH_SHORT).show();
                Log.i("sjkfhkhfkjs", error.toString());
            }
        }) {
            Map<String, String> params = new HashMap<>();

            @Override
            public Map<String, String> getParams() {
                params.put("phone_number", phoneNumber);
//                params.put("user_name", etUsername.getText().toString());
                return params;
            }
        };


        request.setRetryPolicy(new DefaultRetryPolicy(7000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(SignUpActivity.this);
        requestQueue.add(request);
    }

    public void getCodeActivate(final String codeMain, final String phoneNumber) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
//        View view = getLayoutInflater().inflate(R.layout.dialog_get_code_activate, null);
//        builder.setView(view);
//        final AlertDialog alertDialog = builder.create();
//        alertDialog.setCanceledOnTouchOutside(false);
//        alertDialog.show();
//        Button btnOk = view.findViewById(R.id.btn_code_dialog);
//        final EditText edtCode = view.findViewById(R.id.edt_code_dialog);
//        ConstraintLayout clCode = view.findViewById(R.id.constrate_code_dialog);
//        final TextView txtTimer = view.findViewById(R.id.txt_timer_dialog);
//        timer(txtTimer);
//        txtTimer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (txtTimer.getText().toString().equals("ارسال مجدد کد")) {
//                    sendNumber(phoneNumber);
//                    alertDialog.dismiss();
//                    showProgress("");
//                }
//            }
//        });
//
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (edtCode.getText().toString().equals(codeMain)) {
//                    registerUser();
//                } else {
//                    Toast.makeText(SignUpActivity.this, "کد وارد شده صحیح نیست", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }


}


//package com.example.ciyashop.activity;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffColorFilter;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.ciyashop.library.apicall.PostApi;
//import com.ciyashop.library.apicall.URLS;
//import com.ciyashop.library.apicall.interfaces.OnResponseListner;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.FirebaseException;
//import com.google.firebase.FirebaseTooManyRequestsException;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.PhoneAuthCredential;
//import com.google.firebase.auth.PhoneAuthProvider;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.example.ciyashop.R;
//import com.example.ciyashop.customview.edittext.EditTextRegular;
//import com.example.ciyashop.customview.textview.TextViewLight;
//import com.example.ciyashop.customview.textview.TextViewRegular;
//import com.example.ciyashop.javaclasses.SyncWishList;
//import com.example.ciyashop.model.LogIn;
//import com.example.ciyashop.utils.BaseActivity;
//import com.example.ciyashop.utils.Config;
//import com.example.ciyashop.utils.Constant;
//import com.example.ciyashop.utils.CustomToast;
//import com.example.ciyashop.utils.RequestParamUtils;
//import com.example.ciyashop.utils.Utils;
//import com.rilixtech.CountryCodePicker;
//import com.squareup.picasso.Picasso;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.concurrent.TimeUnit;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
//public class SignUpActivity extends BaseActivity implements OnResponseListner {
//
//
//    private static final String TAG = SignUpActivity.class.getSimpleName();
//
//    @BindView(R.id.etUsername)
//    EditTextRegular etUsername;
//
//    @BindView(R.id.etEmail)
//    EditTextRegular etEmail;
//
//    @BindView(R.id.etContact)
//    EditTextRegular etContact;
//
//    @BindView(R.id.etPass)
//    EditTextRegular etPass;
//
//    @BindView(R.id.etConfirmPass)
//    EditTextRegular etConfirmPass;
//
//    @BindView(R.id.ivLogo)
//    ImageView ivLogo;
//
//    @BindView(R.id.tvAlreadyAccount)
//    TextViewLight tvAlreadyAccount;
//
//    @BindView(R.id.tvSignInNow)
//    TextViewLight tvSignInNow;
//
//    @BindView(R.id.tvSignUp)
//    TextViewLight tvSignUp;
//
//    @BindView(R.id.ccp)
//    CountryCodePicker ccp;
//    AlertDialog alertDialog;
//    private FirebaseAuth                                          mAuth;
//    private PhoneAuthProvider.ForceResendingToken                 mResendToken;
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
//    private String                                                mVerificationId;
//    private CustomToast                                           toast;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_ups);
//        ButterKnife.bind(this);
//        toast = new CustomToast(this);
//        mAuth = FirebaseAuth.getInstance();
//        setScreenLayoutDirection();
//        setThemeColor();
//
//        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//            @Override
//            public void onVerificationCompleted(PhoneAuthCredential credential) {
//                Log.e(TAG, "onVerificationCompleted:" + credential);
//                signInWithPhoneAuthCredential(credential);
//            }
//
//            @Override
//            public void onVerificationFailed(FirebaseException e) {
//                // This callback is invoked in an invalid request for verification is made,
//                // for instance if the the phone number format is not valid.
//                Log.e(TAG, "onVerificationFailed", e);
//
//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                    // Invalid request
//                    // [START_EXCLUDE]
//                    if (alertDialog != null) {
//                        alertDialog.dismiss();
//                    }
//                    Toast.makeText(SignUpActivity.this, getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show();
//                    // [END_EXCLUDE]
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    // The SMS quota for the project has been exceeded
//                    // [START_EXCLUDE]
//                    Toast.makeText(SignUpActivity.this, getString(R.string.quoto_exceeded), Toast.LENGTH_SHORT).show();
//                    // [END_EXCLUDE]
//                }
//
//               /* // Show a message and update the UI
//                // [START_EXCLUDE]
//                updateUI(STATE_VERIFY_FAILED);
//                // [END_EXCLUDE]*/
//            }
//
//            @Override
//            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
//                // The SMS verification code has been sent to the provided phone number, we
//                // now need to ask the user to enter the code and then construct a credential
//                // by combining the code with a verification ID.
//                Log.e(TAG, "onCodeSent:" + verificationId);
//                if (alertDialog != null) {
//                    alertDialog.show();
//                }
//                // Save verification ID and resending token so we can use them later
//                mVerificationId = verificationId;
//                mResendToken = token;
//            }
//        };
//    }
//
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.e(TAG, "signInWithCredential:success");
//                            FirebaseUser user = task.getResult().getUser();
//
//                            if (alertDialog != null) {
//                                alertDialog.dismiss();
//                            }
//                            registerUser();
//                        } else {
//                            // Sign in failed, display a message and update the UI
//                            Log.e(TAG, "signInWithCredential:failure", task.getException());
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                                // [START_EXCLUDE silent]
//                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                // [END_EXCLUDE]
//                            }
//                           /* // [START_EXCLUDE silent]
//                            // Update UI
//                            updateUI(STATE_SIGNIN_FAILED);
//                            // [END_EXCLUDE]*/
//                        }
//                    }
//                });
//    }
//
//    @OnClick(R.id.tvSignInNow)
//    public void tvSignInNowClick() {
//        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    @OnClick(R.id.ivBlackBackButton)
//    public void ivBackClick() {
//        onBackPressed();
//    }
//
//
//    public void setThemeColor() {
//
//        if (Constant.APPLOGO != null && !Constant.APPLOGO.equals("")) {
//            Picasso.with(this).load(Constant.APPLOGO).error(R.drawable.logo).into(ivLogo);
//        }
//        Drawable mDrawable = getResources().getDrawable(R.drawable.login);
//        mDrawable.setColorFilter(new
//                PorterDuffColorFilter(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)), PorterDuff.Mode.OVERLAY));
//
//
//        tvAlreadyAccount.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
//        tvSignInNow.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
//        tvSignUp.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
//    }
//
//    @OnClick(R.id.tvSignUp)
//    public void tvSignUpClick() {
//        if (etUsername.getText().toString().length() == 0) {
//            Toast.makeText(this, R.string.enter_username, Toast.LENGTH_SHORT).show();
//        } else if (etEmail.getText().toString().length() == 0) {
//            Toast.makeText(this, R.string.enter_email_address, Toast.LENGTH_SHORT).show();
//        } else if (!Utils.isValidEmail(etEmail.getText().toString())) {
//            Toast.makeText(this, R.string.enter_valid_email_address, Toast.LENGTH_SHORT).show();
//        } else if (etContact.getText().toString().isEmpty()) {
//            Toast.makeText(this, R.string.enter_contact_number, Toast.LENGTH_SHORT).show();
//        } else if (etPass.getText().toString().isEmpty()) {
//            Toast.makeText(this, R.string.enter_password, Toast.LENGTH_SHORT).show();
//        } else if (etConfirmPass.getText().toString().isEmpty()) {
//            Toast.makeText(this, R.string.enter_confirm_password, Toast.LENGTH_SHORT).show();
//        } else if (etPass.getText().toString().equals(etConfirmPass.getText().toString())) {
//            if (Config.OTPVerification) {
//                String number = ccp.getSelectedCountryCodeWithPlus() + etContact.getText().toString().trim();
//                Log.e("Otp :-", number);
//                ShowDialogForOTP(number);
//            } else {
//                registerUser();
//            }
//
//        } else {
//            Toast.makeText(this, R.string.password_and_confirm_password_not_matched, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void ShowDialogForOTP(final String number) {
//
//        // [START start_phone_auth]
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                number,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks);        // OnVerificationStateChangedCallbacks
//        // [END start_phone_auth]
//
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SignUpActivity.this);
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.dialog_otp_verification, null);
//        dialogBuilder.setView(dialogView);
//
//        final EditTextRegular etOTP = (EditTextRegular) dialogView.findViewById(R.id.etOTP);
//        TextViewRegular tvVerificationText = dialogView.findViewById(R.id.tvVerificationText);
//
//        TextViewRegular tvDone = (TextViewRegular) dialogView.findViewById(R.id.tvDone);
//
//        TextViewRegular tvResend = (TextViewRegular) dialogView.findViewById(R.id.tvResend);
//
//        tvVerificationText.setText(getResources().getString(R.string.please_type_verification_code_sent_to_in) + etContact.getText().toString());
//        alertDialog = dialogBuilder.create();
//        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
//
//
//        tvDone.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
//        tvResend.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
//        tvDone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String code = etOTP.getText().toString();
//                if (etOTP.getText().toString().length() == 0) {
//                    toast.showToast(getString(R.string.enter_verificiation_code));
//                    toast.showBlackbg();
////                    etOTP.setError("Enter Verification Code");
//                    return;
//                }
//                verifyPhoneNumberWithCode(mVerificationId, code);
//            }
//        });
//
//        tvResend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Toast.makeText(SignUpActivity.this,
//                        getString(R.string.otp_sent_again) + number, Toast.LENGTH_SHORT).show();
//
//                resendVerificationCode(number, mResendToken);
//            }
//        });
//
//    /*    rvProductVariation = (RecyclerView) dialogView.findViewById(R.id.rvProductVariation);
//        TextViewRegular tvDone = (TextViewRegular) dialogView.findViewById(R.id.tvDone);
//        TextViewRegular tvCancel = (TextViewRegular) dialogView.findViewById(R.id.tvCancel);*/
//
//    }
//
//    private void verifyPhoneNumberWithCode(String mVerificationId, String code) {
//
//        // [START verify_with_code]
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
//        // [END verify_with_code]
//        signInWithPhoneAuthCredential(credential);
//    }
//
//    private void resendVerificationCode(String number, PhoneAuthProvider.ForceResendingToken token) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                number,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks,         // OnVerificationStateChangedCallbacks
//                token);             // ForceResendingToken from callbacks
//    }
//
//    public void registerUser() {
//        if (Utils.isInternetConnected(this)) {
//            showProgress("");
//            PostApi postApi = new PostApi(this, "create_customer", this, getlanuage());
//            JSONObject object = new JSONObject();
//            try {
//
//                object.put(RequestParamUtils.email, etEmail.getText().toString());
//                object.put(RequestParamUtils.username, etUsername.getText().toString());
//                object.put(RequestParamUtils.mobile, ccp.getSelectedCountryCodeWithPlus() + etContact.getText().toString().trim());
//                object.put(RequestParamUtils.PASSWORD, etPass.getText().toString());
//                object.put(RequestParamUtils.deviceType, Constant.DEVICE_TYPE);
//
//                String token = getPreferences().getString(RequestParamUtils.NOTIFICATION_TOKEN, "");
//                object.put(RequestParamUtils.deviceToken, token);
//
//                postApi.callPostApi(new URLS().CREATE_CUSTOMER, object.toString());
//
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                Toast.makeText(this, R.string.something_went_wrong_try_after_somtime, Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
//        }
//
//
//    }
//
//    @Override
//    public void onResponse(final String response, String methodName) {
//
//        if (methodName.equals(RequestParamUtils.createCustomer)) {
//            if (response != null && response.length() > 0) {
//                try {
//                    final LogIn loginRider = new Gson().fromJson(
//                            response, new TypeToken<LogIn>() {
//                            }.getType());
//
//                    JSONObject jsonObj = new JSONObject(response);
//                    String status = jsonObj.getString("status");
//
//                    if (status.equals("error")) {
//                        Toast.makeText(getApplicationContext(), jsonObj.getString("message"), Toast.LENGTH_SHORT).show(); //display in long period of time
//                        dismissProgress();
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                //set call here
//                                if (loginRider.status.equals("success")) {
//
//                                    SharedPreferences.Editor pre = getPreferences().edit();
//                                    pre.putString(RequestParamUtils.CUSTOMER, "");
//                                    pre.putString(RequestParamUtils.ID, loginRider.user.id + "");
//                                    pre.putString(RequestParamUtils.PASSWORD, etPass.getText().toString());
//                                    pre.commit();
//
//
//                                    new SyncWishList(SignUpActivity.this).syncWishList(getPreferences().getString(RequestParamUtils.ID, ""), false);
//
////                                    Intent intent = new Intent(SignUpActivity.this, AccountActivity.class);
////                                    startActivity(intent);
//                                    finish();
//                                } else {
//                                    Toast.makeText(getApplicationContext(), R.string.enter_proper_detail, Toast.LENGTH_SHORT).show(); //display in long period of time
//                                }
//                            }
//                        });
//                        dismissProgress();
//                    }
//                } catch (Exception e) {
//                    Log.e(methodName + "Gson Exception is ", e.getMessage());
//                    Toast.makeText(getApplicationContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show(); //display in long period of time
//                }
//            }
//        }
//    }
//
//
//}
