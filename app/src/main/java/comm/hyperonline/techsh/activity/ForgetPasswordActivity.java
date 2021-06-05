package comm.hyperonline.techsh.activity;

import androidx.appcompat.app.AlertDialog;

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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.javaclasses.SyncWishList;
import comm.hyperonline.techsh.utils.APIS;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.Constant;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPasswordActivity extends BaseActivity {

    @BindView(R.id.etMobile)
    EditText etMobile;
    @BindView(R.id.etNewPass)
    EditText etNewPass;
    @BindView(R.id.tvSubmit)
    TextView tvSubmit;
    @BindView(R.id.ivBlackBackButton)
    ImageView ivBlackBackButton;
    @BindView(R.id.ivLogo)
    ImageView ivLogo;


    private Handler timerHandler = new Handler();
    private final int TOTAL_SECOND = 60;
    private int time = TOTAL_SECOND;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
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
        tvSubmit.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));
        Drawable mDrawable = getResources().getDrawable(R.drawable.login);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)), PorterDuff.Mode.OVERLAY));
    }

    @OnClick(R.id.tvSubmit)
    public void tvSignInClick() {
        if (etMobile.getText().toString().isEmpty()) {
            Toast.makeText(this, "شماره همراه را وارد کنید", Toast.LENGTH_SHORT).show();
        } else if (etNewPass.getText().toString().isEmpty()) {
            Toast.makeText(this, "رمز عبور جدید را وارد کنید", Toast.LENGTH_SHORT).show();
        } else {

//            String email_mobile=etEmail.getText().toString();
//            myUserLogin(email_mobile,etPass.getText().toString(),"mobile");
//

            sendOtp(etMobile.getText().toString(), etNewPass.getText().toString());

        }
    }

    private void sendOtp(String mobile, String newPassword) {
        showProgress("");
        StringRequest request = new StringRequest(
                Request.Method.POST, APIS.APP_URL + "wp-json/digits/v1/send_otp", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dismissProgress();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("code").equals("1")) {

                        getCodeActivate(mobile, newPassword);

                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ForgetPasswordActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
                Toast.makeText(ForgetPasswordActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            }
        }) {
            Map<String, String> params = new HashMap<>();

            @Override
            public Map<String, String> getParams() {
                params.put("countrycode", Constant.MOBILE_COUNTRY_CODE);
                params.put("mobileNo", mobile);
                params.put("type", "resetpass");
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(7000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(ForgetPasswordActivity.this);
        requestQueue.add(request);
    }

    private void getCodeActivate(String mobile, String newPassword) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPasswordActivity.this);
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
                    sendOtp(mobile
                            , newPassword
                    );
                    alertDialog.dismiss();
                }
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtCode.getText().toString().trim().equals("")) {
                    Toast.makeText(ForgetPasswordActivity.this, "لطفا کد فعال سازی را وارد کنید", Toast.LENGTH_SHORT).show();
                    edtCode.requestFocus();
                } else {
                    checkOtp(mobile, edtCode.getText().toString().trim(), newPassword);
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

    private void checkOtp(String mobile, String otpCode, String newPassword) {
        showProgress("");
        StringRequest request = new StringRequest(
                Request.Method.POST, APIS.APP_URL + "wp-json/digits/v1/recovery", new Response.Listener<String>() {
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
                        pre.putString(RequestParamUtils.PASSWORD, newPassword);
                        pre.commit();


                        new SyncWishList(ForgetPasswordActivity.this).syncWishList(getPreferences().getString(RequestParamUtils.ID, ""), false);

//                        Intent intent = new Intent(ForgetPasswordActivity.this, AccountActivity.class);
//                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, getHtmlText(data.getString("msg")), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ForgetPasswordActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissProgress();
                Toast.makeText(ForgetPasswordActivity.this, "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
            }
        }) {
            Map<String, String> params = new HashMap<>();

            @Override
            public Map<String, String> getParams() {
                params.put("countrycode", Constant.MOBILE_COUNTRY_CODE);
                params.put("user", mobile);
                params.put("otp", otpCode);
                params.put("password", newPassword);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(7000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(ForgetPasswordActivity.this);
        requestQueue.add(request);
    }

    private String getHtmlText(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }
}