package comm.hyperonline.techsh.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.customview.textview.TextViewBold;
import comm.hyperonline.techsh.customview.textview.TextViewMedium;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.Constant;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import com.squareup.picasso.Picasso;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductQuickDetailActivity extends BaseActivity {

    @BindView(R.id.tvSubTitle)
    TextViewMedium tvSubTitle;

    @BindView(R.id.ivProduct)
    ImageView ivProduct;

    @BindView(R.id.tvProductName)
    TextViewBold tvProductName;

    @BindView(R.id.tvDescription)
    HtmlTextView tvDescription;

    @BindView(R.id.wvDetail)
    WebView wvDetail;

    public static String changedHeaderHtml(String htmlText) {

        String head = "<html dir=\"rtl\"><head ><meta name=\"viewport\" content=\"width=device-width, user-scalable=yes\" />";
        String font = "<style type=\"text/css\">\n" +
                "@font-face {\n" +
                "    font-family: MyFont;\n" +
                "    src: url(\"file:///android_asset/font/iransans.ttf\")\n" +
                "}\n" +
                "body {\n" +
                "    font-family: MyFont;\n" +
                "    font-size: medium;\n" +
                "    text-align: right;\n" +
                "}\n" +
                "</style></head><body>";

        String closedTag = "</body></html>";
        String changeFontHtml = head + font + htmlText + closedTag;
        return changeFontHtml;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_quick_detail);
        ButterKnife.bind(this);
        setToolbarTheme();
        hideSearchNotification();
        setScreenLayoutDirection();

        String description = getIntent().getExtras().getString(RequestParamUtils.description);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            tvDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
//        } else {
//            tvDescription.setText(Html.fromHtml(description));
//        }
        tvDescription.setHtml(description,
                new HtmlHttpImageGetter(tvDescription));

        if (description != "") {
            wvDetail.setInitialScale(1);

            wvDetail.getSettings().setLoadsImagesAutomatically(true);
            wvDetail.getSettings().setUseWideViewPort(true);
            wvDetail.loadDataWithBaseURL(null , changedHeaderHtml(description), "text/html; charset=utf-8", "UTF-8" , null);
            wvDetail.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            wvDetail.getSettings().setBuiltInZoomControls(true);
        }

        String subTitle = getIntent().getExtras().getString(RequestParamUtils.title);
        String productImage = getIntent().getExtras().getString(RequestParamUtils.image);
        String productName = getIntent().getExtras().getString(RequestParamUtils.name);

        settvTitle(subTitle);
        tvSubTitle.setText(subTitle);
        tvProductName.setText(productName);
        tvProductName.setTextColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));

        if (productImage.length() > 0) {
            ivProduct.setVisibility(View.VISIBLE);
            Picasso.get().load(productImage).into(ivProduct);
        } else {
            ivProduct.setVisibility(View.INVISIBLE);
        }

        showBackButton();
    }


}
