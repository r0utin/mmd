package comm.hyperonline.techsh.activity;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.adapter.SellerReviewAdapter;
import comm.hyperonline.techsh.model.SellerData;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import comm.hyperonline.techsh.interfaces.OnItemClickListner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SellerReviewActivity extends BaseActivity implements OnItemClickListner {

    @BindView(R.id.rvReview)
    RecyclerView rvReview;

    private SellerReviewAdapter sellerReviewAdapter;

    List<SellerData.SellerInfo.ReviewList> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_review);
        ButterKnife.bind(this);
        settvTitle(RequestParamUtils.vendorReview);
        showBackButton();
        setToolbarTheme();

        String listData = getIntent().getExtras().getString(RequestParamUtils.sellerInfo);

        SellerData sellerDataRider = new Gson().fromJson(
                listData, new TypeToken<SellerData>() {
                }.getType());

        list.addAll(sellerDataRider.sellerInfo.reviewList);

        setReviewData();
    }

    public void setReviewData() {
        sellerReviewAdapter = new SellerReviewAdapter(this, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvReview.setLayoutManager(mLayoutManager);
        rvReview.setAdapter(sellerReviewAdapter);
        rvReview.setNestedScrollingEnabled(false);
        sellerReviewAdapter.addAll(list);
    }

    @Override
    public void onItemClick(int position, String value, int outerPos) {

    }
}
