package comm.hyperonline.techsh.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.model.StoreFinder;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import comm.hyperonline.techsh.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class StoreFinderActivity extends BaseActivity implements  OnResponseListner {
    // Google Map

    // Latitude & Longitude
    private Double Latitude = 0.00;
    private Double Longitude = 0.00;

    List<StoreFinder.Datum> location = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_finder);
        ButterKnife.bind(this);
        settvImage();
        hideSearchNotification();
        setToolbarTheme();
        showBackButton();
        setScreenLayoutDirection();

        getStore();


        // *** Focus & Zoom


    }

    public void getStore() {
        if (Utils.isInternetConnected(this)) {
            showProgress("");
            PostApi postApi = new PostApi(this, RequestParamUtils.getBlog, this, getlanuage());

            postApi.callPostApi(new URLS().GET_STORES + getPreferences().getString(RequestParamUtils.CurrencyText, ""), "");
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onResponse(String response, String methodName) {
        dismissProgress();
        if (response != null && response.length() > 0) {

            Gson gson = new GsonBuilder().serializeNulls().create();
            StoreFinder storeFinderRider = gson.fromJson(
                    response, new TypeToken<StoreFinder>() {
                    }.getType());

            location.addAll(storeFinderRider.data);


        }

    }
}