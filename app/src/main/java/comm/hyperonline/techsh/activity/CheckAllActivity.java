package comm.hyperonline.techsh.activity;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.ciyashop.library.apicall.GetApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.adapter.ReviewAdapter;
import comm.hyperonline.techsh.model.CategoryList;
import comm.hyperonline.techsh.model.ProductReview;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.Constant;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import comm.hyperonline.techsh.utils.Utils;
import comm.hyperonline.techsh.interfaces.OnItemClickListner;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckAllActivity extends BaseActivity implements OnResponseListner, OnItemClickListner {

    @BindView(R.id.rvAllReview)
    RecyclerView rvAllReview;

    private ReviewAdapter reviewAdapter;
    private CategoryList categoryList = Constant.CATEGORYDETAIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_all);
        ButterKnife.bind(this);
        setToolbarTheme();
        hideSearchNotification();
//        setScreenLayoutDirection();
        settvTitle(getResources().getString(R.string.review));
        showBackButton();
        getReview();
        setReviewData();
    }

    public void setReviewData() {
        reviewAdapter = new ReviewAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvAllReview.setLayoutManager(mLayoutManager);
        rvAllReview.setAdapter(reviewAdapter);
        rvAllReview.setNestedScrollingEnabled(false);
    }

    public void getReview() {
        if (Utils.isInternetConnected(this)) {
            GetApi getApi = new GetApi(this, RequestParamUtils.getReview, this, getlanuage());
            getApi.callGetApi(new URLS().WOO_MAIN_URL + new URLS().WOO_PRODUCT_URL + "/" + categoryList.id + "/" + new URLS().WOO_REVIEWS);
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResponse(String response, String methodName) {

        dismissProgress();
        if (methodName.equals(RequestParamUtils.getReview)) {
            if (response != null && response.length() > 0) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    List<ProductReview> reviewList = new ArrayList<>();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String jsonResponse = jsonArray.get(i).toString();
                            ProductReview productReviewRider = new Gson().fromJson(
                                    jsonResponse, new TypeToken<ProductReview>() {
                                    }.getType());
                            reviewList.add(productReviewRider);

                        }
                    }
                    reviewAdapter.addAll(reviewList);

                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
            }
        }


    }

    @Override
    public void onItemClick(int position, String value, int outerpos) {

    }
}
