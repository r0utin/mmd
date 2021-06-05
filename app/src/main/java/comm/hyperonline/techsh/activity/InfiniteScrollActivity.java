package comm.hyperonline.techsh.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ciyashop.library.apicall.Ciyashop;
import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import comm.hyperonline.techsh.adapter.HomeTopCategoryAdapter;
import comm.hyperonline.techsh.adapter.InfiniteScrollAdapter;
import comm.hyperonline.techsh.adapter.NavigationDrawerAdapter;
import comm.hyperonline.techsh.helper.DatabaseHelper;
import comm.hyperonline.techsh.model.CategoryList;
import comm.hyperonline.techsh.model.Home;
import comm.hyperonline.techsh.model.NavigationList;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.Config;
import comm.hyperonline.techsh.utils.Constant;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import comm.hyperonline.techsh.utils.Utils;
import com.pnikosis.materialishprogress.ProgressWheel;
import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.customview.CustomLinearLayoutManager;
import comm.hyperonline.techsh.customview.GridSpacingItemDecoration;
import comm.hyperonline.techsh.customview.edittext.EditTextRegular;
import comm.hyperonline.techsh.customview.textview.TextViewRegular;
import comm.hyperonline.techsh.interfaces.OnItemClickListner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InfiniteScrollActivity extends BaseActivity implements OnItemClickListner, OnResponseListner {

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @BindView(R.id.ivBack)
    ImageView ivDrawer;

    @BindView(R.id.llMain)
    LinearLayout llMain;

    @BindView(R.id.ivNotification)
    ImageView ivNotification;

    @BindView(R.id.svHome)
    NestedScrollView svHome;

    @BindView(R.id.crMain)
    CoordinatorLayout crMain;

    @BindView(R.id.rvViewAllProductList)
    RecyclerView rvViewAllProductList;

    @BindView(R.id.llProgress)
    LinearLayout llProgress;

    @BindView(R.id.progress_wheel)
    ProgressWheel progress_wheel;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.llTopCategory)
    LinearLayout llTopCategory;

    @BindView(R.id.rvTopCategory)
    RecyclerView rvTopCategory;

    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout mShimmerViewContainer;

    @BindView(R.id.etSearch)
    EditTextRegular etSearch;
    //Todo: Global Parametere
    int CurrentPage = 1;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    ArrayList<CategoryList> viewProductList = new ArrayList<>();
    Home homeRider;
    List<CategoryList>      categoryLists   = new ArrayList<>();
    Boolean                 setNoItemFound  = false;


    //    @BindView(R.id.etSearch)
//    EditTextRegular etSearch;
    Boolean isSaleCallOver = false;
    private comm.hyperonline.techsh.adapter.InfiniteScrollAdapter InfiniteScrollAdapter;
    private HomeTopCategoryAdapter homeTopCategoryAdapter;
    private boolean                 loading      = true;
    private TextView[]              dots;
    private View                    listHeaderView;
    private boolean                 ishead       = false;
    private NavigationDrawerAdapter navigationDrawerAdapter;
    private TextViewRegular         tvName;
    private ActionBarDrawerToggle   actionBarDrawerToggle;
    private DatabaseHelper databaseHelper;
    private boolean                 isAutoScroll = false, isSpecialDeal = false;
    private long    mBackPressed;
    private Handler handler;
    private int     page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infinite_scroll);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        ButterKnife.bind(this);

        Config.IS_RTL = getPreferences().getBoolean(Constant.RTL, false);
        setHomecolorTheme(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR));
        setScreenLayoutDirection();
        ivDrawer.setImageDrawable(getResources().getDrawable(R.drawable.ic_drawer));
        // Get token and Save Notification Token
        String token = "";
        SharedPreferences.Editor pre = getPreferences().edit();
        pre.putString(RequestParamUtils.NOTIFICATION_TOKEN, token);

        pre.commit();
        etSearch.setHint(getResources().getString(R.string.search));

        getHomeData();
        settvImage();
        showNotification();
        showCart();
        swipeView();
        setCategoryList();
        setToolbarTheme();
        settvImage();
        setHomeCategoryData();
    }

    public void setHomeCategoryData() {
        homeTopCategoryAdapter = new HomeTopCategoryAdapter(this, this);
        CustomLinearLayoutManager mLayoutManager = new CustomLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvTopCategory.setLayoutManager(mLayoutManager);
        rvTopCategory.setAdapter(homeTopCategoryAdapter);
        rvTopCategory.setNestedScrollingEnabled(false);
        ViewCompat.setNestedScrollingEnabled(rvTopCategory, false);
        rvTopCategory.setHasFixedSize(true);
        rvTopCategory.setItemViewCacheSize(20);
        rvTopCategory.setDrawingCacheEnabled(true);
        rvTopCategory.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @OnClick(R.id.etSearch)
    public void etSearchClick() {
        Intent intent = new Intent(InfiniteScrollActivity.this, SearchFromHomeActivity.class);
        startActivity(intent);
    }


    public void getHomeData() {
        if (Utils.isInternetConnected(this)) {
            //showProgress("");
            if (Config.SHIMMER_VIEW) {
                mShimmerViewContainer.startShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.VISIBLE);
            } else {
                mShimmerViewContainer.setVisibility(View.GONE);
                showProgress("");
            }
            try {
                PostApi postApi = new PostApi(this, RequestParamUtils.getHomeData, this, getlanuage());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.appVersion, URLS.version);

                postApi.callPostApi(new URLS().HOME_SCROLLING + getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());
            } catch (Exception e) {
                Log.e("Home", e.getMessage());
            }

        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onResponse(final String response, String methodName) {
        if (Config.SHIMMER_VIEW) {
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
        } else {
            dismissProgress();
        }
        if (methodName.equals(RequestParamUtils.getHomeData)) {
            if (response != null && response.length() > 0) {
                swipeContainer.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    //Convert json response into gson and made model class
                    Gson gson = new GsonBuilder().serializeNulls().create();
                    homeRider = gson.fromJson(
                            jsonObject.toString(), new TypeToken<Home>() {
                            }.getType());

                    if (jsonObject.has("pgs_woo_api_add_to_cart_option") && jsonObject.getString("pgs_woo_api_add_to_cart_option").equals("enable")) {
                        Constant.IS_ADD_TO_CART_ACTIVE = true;
                    } else {
                        Constant.IS_ADD_TO_CART_ACTIVE = false;
                    }

                    if (jsonObject.has("pgs_woo_api_catalog_mode_option") && jsonObject.getString("pgs_woo_api_catalog_mode_option").equals("enable")) {
                        Config.IS_CATALOG_MODE_OPTION = true;
                        showCart();
                    } else {
                        Config.IS_CATALOG_MODE_OPTION = false;
                    }


                    if (jsonObject.has("pgs_woo_api_web_view_pages")) {
                        Constant.WEBVIEWPAGES = new ArrayList<>();
                        if (homeRider.webViewPages != null && homeRider.webViewPages.size() > 0) {
                            Constant.WEBVIEWPAGES.addAll(homeRider.webViewPages);
                        }
                    }

                    checkReview(jsonObject);
                    runOnUiThread(new Runnable() {
                        @SuppressLint("NewApi")
                        @Override
                        public void run() {

                            llMain.setVisibility(View.VISIBLE);

                            if (homeRider != null) {
                                if (homeRider.isAppValidation != null) {
                                    new Ciyashop(InfiniteScrollActivity.this).setFlag(homeRider.isAppValidation, false);
                                }

                                //set all constatnt value from the respones
                                setConstantValue();

                                //set theme color from the reponse
                                setThemeIconColor();

                                //set  all color from the response into preferences
                                setColorPreferences(homeRider.appColor.primaryColor, homeRider.appColor.secondaryColor, homeRider.appColor.headerColor);

                                //set color into toolbar of home activity
                                setHomecolorTheme(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR));

                                //set lungage into local
                                setLocale(homeRider.siteLanguage);

                                //CheckOut URLs get
                                if (homeRider.checkoutRedirectUrl != null && homeRider.checkoutRedirectUrl.size() > 0) {
                                    setCheckoutURL(homeRider.checkoutRedirectUrl);
                                }

                                //set bottambar
                                setBottomBar("home", svHome);

                                //set lungage list  from the response
                                if (homeRider.isWpmlActive != null && homeRider.isWpmlActive) {
                                    if (homeRider.wpmlLanguages != null) {
                                        Constant.LANGUAGELIST = (homeRider.wpmlLanguages);
                                    }
                                } else {
                                    SharedPreferences.Editor pre = getPreferences().edit();
                                    pre.putString(RequestParamUtils.LANGUAGE, "");
                                    pre.commit();
                                }
                                //set currency list from resposne
                                setCurrency(response);
                            }
                            for (int i = 0; i < homeRider.allCategories.size(); i++) {
                                if (homeRider.allCategories.get(i).name.equals("Uncategorized")) {
                                    homeRider.allCategories.remove(i);
                                }
                            }
                            Constant.MAINCATEGORYLIST.clear();
                            Constant.MAINCATEGORYLIST.addAll(homeRider.allCategories);

                            //set main category list from response
                            setMainCategoryList(homeRider.mainCategory);
                            SharedPreferences.Editor editor = getPreferences().edit();
                            editor.putString(Constant.APPLOGO, homeRider.appLogo);
                            editor.putString(Constant.APPLOGO_LIGHT, homeRider.appLogoLight);
                            editor.commit();
                            settvImage();
                        }
                    });
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
                page = 1;
                setNoItemFound = false;
                InfiniteScrollAdapter.clearList();
                getCategoryListData(true);
            }
        } else if (methodName.equals(RequestParamUtils.getCategoryListData)) {
            if (response != null && response.length() > 0) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    categoryLists = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String jsonResponse = jsonArray.get(i).toString();
                        CategoryList categoryListRider = new Gson().fromJson(
                                jsonResponse, new TypeToken<CategoryList>() {
                                }.getType());
                        categoryLists.add(categoryListRider);

                    }
                    InfiniteScrollAdapter.addAll(categoryLists);

                    if (Config.SHIMMER_VIEW) {
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    } else {
                        dismissProgress();
                    }
                    //  dismissProgress();
                    loading = true;
                } catch (Exception e) {
                    //dismissProgress();
                    if (Config.SHIMMER_VIEW) {
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    } else {
                        dismissProgress();
                    }
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("message").equals(getString(R.string.no_product_found))) {
                            setNoItemFound = true;
                        }
                    } catch (JSONException e1) {
                        Log.e("noproductJSONException", e1.getMessage());
                    }

                }
            } else {

            }
            llProgress.setVisibility(View.GONE);
            //dismissProgress();
            if (Config.SHIMMER_VIEW) {
                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);
            } else {
                dismissProgress();
            }
        } else if (methodName.equals(RequestParamUtils.addWishList) || methodName.equals(RequestParamUtils.removeWishList)) {
            dismissProgress();
        }
    }

    private void setCheckoutURL(List<String> checkoutRedirectUrl) {
        Constant.CheckoutURL.clear();
        Constant.CheckoutURL = new ArrayList<>();
        Constant.CheckoutURL.addAll(checkoutRedirectUrl);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setThemeIconColor() {

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor(getPreferences().getString(Constant.PRIMARY_COLOR, Constant.PRIMARY_COLOR)));
        gradientDrawable.setCornerRadius(5);
    }

    public void swipeView() {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHomeData();
                if (InfiniteScrollAdapter != null && InfiniteScrollAdapter.handler != null) {
                    InfiniteScrollAdapter.handler.removeCallbacksAndMessages(null);
                    InfiniteScrollAdapter.handler = null;

                }
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.orange,
                R.color.red,
                R.color.blue
        );
    }

    public void setConstantValue() {
        if (homeRider.pgsAppContactInfo != null) {
            if (homeRider.pgsAppContactInfo.addressLine1 != null) {
                Constant.ADDRESS_LINE1 = homeRider.pgsAppContactInfo.addressLine1;
            }
            if (homeRider.pgsAppContactInfo.addressLine2 != null) {
                Constant.ADDRESS_LINE2 = homeRider.pgsAppContactInfo.addressLine2;
            }
            if (homeRider.pgsAppContactInfo.email != null) {
                Constant.EMAIL = homeRider.pgsAppContactInfo.email;
            }
            if (homeRider.pgsAppContactInfo.phone != null) {
                Constant.PHONE = homeRider.pgsAppContactInfo.phone;
            }
            if (homeRider.pgsAppContactInfo.whatsappNo != null) {
                Constant.WHATSAPP = homeRider.pgsAppContactInfo.whatsappNo;
            }
            if (homeRider.pgsAppContactInfo.whatsappFloatingButton != null) {
                Constant.WHATSAPPENABLE = homeRider.pgsAppContactInfo.whatsappFloatingButton;
                //Constant.WHATSAPPENABLE = "disable";
            }
            if (homeRider.priceFormateOptions.currencyCode != null) {
                Constant.CURRENCYCODE = Html.fromHtml(homeRider.priceFormateOptions.currencyCode).toString();
            }

        }
        if (homeRider.isCurrencySwitcherActive != null) {
            Constant.IS_CURRENCY_SWITCHER_ACTIVE = homeRider.isCurrencySwitcherActive;
        }
        if (homeRider.isGuestCheckoutActive != null) {
            Constant.IS_GUEST_CHECKOUT_ACTIVE = homeRider.isGuestCheckoutActive;
        }
        if (homeRider.isWpmlActive != null) {
            Constant.IS_WPML_ACTIVE = homeRider.isWpmlActive;
        }
        if (homeRider.isOrderTrackingActive != null) {
            Constant.IS_ORDER_TRACKING_ACTIVE = homeRider.isOrderTrackingActive;
        }

        if (homeRider.isRewardPointsActive != null) {
            Constant.IS_REWARD_POINT_ACTIVE = homeRider.isRewardPointsActive;
        }

        if (homeRider.isWishlistActive != null) {
            Constant.IS_WISH_LIST_ACTIVE = homeRider.isWishlistActive;
        }
        if (homeRider.isWishlistActive != null) {
            Constant.IS_YITH_FEATURED_VIDEO_ACTIVE = homeRider.isYithFeaturedVideoActive;
        }

        if (homeRider.pgsAppSocialLinks != null) {
            Constant.SOCIALLINK = homeRider.pgsAppSocialLinks;
        }
        if (homeRider.priceFormateOptions != null) {
            if (homeRider.priceFormateOptions.currencyPos != null) {
                Constant.CURRENCYSYMBOLPOSTION = homeRider.priceFormateOptions.currencyPos;
            }
            if (homeRider.priceFormateOptions.currencySymbol != null) {
                Constant.CURRENCYSYMBOL = Html.fromHtml(homeRider.priceFormateOptions.currencySymbol).toString();
                SharedPreferences.Editor pre = getPreferences().edit();
                pre.putString(Constant.CURRENCYSYMBOLPref, Html.fromHtml(homeRider.priceFormateOptions.currencySymbol).toString());
                pre.commit();
            }
            if (homeRider.priceFormateOptions.decimals != null) {
                Constant.Decimal = homeRider.priceFormateOptions.decimals;
            }
            if (homeRider.priceFormateOptions.decimalSeparator != null) {
                Constant.DECIMALSEPRETER = homeRider.priceFormateOptions.decimalSeparator;
            }
            if (homeRider.priceFormateOptions.thousandSeparator != null) {
                Constant.THOUSANDSSEPRETER = homeRider.priceFormateOptions.thousandSeparator;
            }
        }

        if (homeRider.appLogo != null) {
            Constant.APPLOGO = homeRider.appLogo;
        }

        if (homeRider.appLogoLight != null) {
            Constant.APPLOGO_LIGHT = homeRider.appLogoLight;
        }

        if (getPreferences().getString(RequestParamUtils.LANGUAGE, "").equals("")) {
            if (homeRider.isRtl != null) {
                Config.IS_RTL = homeRider.isRtl;

//                Config.IS_RTL =true;
                getPreferences().edit().putBoolean(Constant.RTL, Config.IS_RTL).commit();
            }

        } else {
            Config.IS_RTL = getPreferences().getBoolean(Constant.RTL, false);
        }

        if (homeRider.pgsWooApiDeliverPincode != null) {
            if (homeRider.pgsWooApiDeliverPincode.status != null && homeRider.pgsWooApiDeliverPincode.status.equals("enable")) {
                Config.WOO_API_DELIVER_PINCODE = true;
            } else {
                Config.WOO_API_DELIVER_PINCODE = false;
            }

            if (homeRider.pgsWooApiDeliverPincode.settingOptions == null) {
                Home.SettingOptions settingOptions = new Home().getSettingOption();
                settingOptions.availableatText = getString(R.string.available_text);
                settingOptions.codAvailableMsg = getString(R.string.cod_available_msg);
                settingOptions.codDataLabel = getString(R.string.cod_data_label);
                settingOptions.codHelpText = getString(R.string.cod_help_text);
                settingOptions.codNotAvailableMsg = getString(R.string.cod_not_available_msg);
                settingOptions.delDataLabel = getString(R.string.del_data_label);
                settingOptions.delHelpText = getString(R.string.del_help_text);
                settingOptions.delSaturday = getString(R.string.del_saturday);
                settingOptions.delSunday = getString(R.string.del_sunday);
                settingOptions.errorMsgBlank = getString(R.string.error_msg_blank);
                settingOptions.errorMsgCheckPincode = getString(R.string.error_msg_check_pincode);
                settingOptions.pincodePlaceholderTxt = getString(R.string.pincode_placeholder_txt);
                Constant.settingOptions = settingOptions;
            } else {
                Constant.settingOptions = homeRider.pgsWooApiDeliverPincode.settingOptions;
            }


        }

    }

    public void setColorPreferences(String primaryColor, String secondaryColor, String HeaderColor) {
        String colorSubString = (primaryColor.substring(primaryColor.lastIndexOf("#") + 1));
        SharedPreferences.Editor editor = getPreferences().edit();

        if (!primaryColor.equals("")) {
            editor.putString(Constant.APP_COLOR, primaryColor);
            editor.putString(Constant.APP_TRANSPARENT, "#aa" + colorSubString);
            editor.putString(Constant.APP_TRANSPARENT_VERY_LIGHT, "#44" + colorSubString);
        }
        if (!secondaryColor.equals("")) {
            editor.putString(Constant.SECOND_COLOR, secondaryColor);
        }
        if (!HeaderColor.equals("")) {
            editor.putString(Constant.HEADER_COLOR, HeaderColor);
        }
        editor.commit();
    }

    public void setMainCategoryList(List<Home.MainCategory> list) {
        if (list != null) {
            List<Home.MainCategory> mainCategoryList = new ArrayList<>();
            if (list.size() > 0) {
                if (list.size() > 4) {
                    for (int i = 0; i <= 4; i++) {
                        mainCategoryList.add(list.get(i));
                    }
                } else {
                    mainCategoryList.addAll(list);
                }
                Home.MainCategory mainCategory = new Home().getInstranceMainCategory();
                mainCategory.mainCatName = getString(R.string.more);
                mainCategoryList.add(mainCategory);
                homeTopCategoryAdapter.addAll(mainCategoryList);
                llTopCategory.setVisibility(View.VISIBLE);


            } else {
                llTopCategory.setVisibility(View.GONE);
            }
            navigationDrawerAdapter.setSepreter(mainCategoryList.size() - 1);
            List<Home.MainCategory> drawerList = new ArrayList<Home.MainCategory>();
            drawerList.addAll(mainCategoryList);
            for (int i = 0; i < NavigationList.getInstance(this).getImageList().size(); i++) {
                Home.MainCategory mainCategory = new Home().getInstranceMainCategory();
                mainCategory.mainCatName = NavigationList.getInstance(this).getTitleList().get(i);
                mainCategory.mainCatImage = NavigationList.getInstance(this).getImageList().get(i) + "";
                mainCategory.mainCatId = i + "";
                drawerList.add(mainCategory);
            }
            navigationDrawerAdapter.addAll(drawerList);
        } else {
            navigationDrawerAdapter.setSepreter(0);
            List<Home.MainCategory> drawerList = new ArrayList<Home.MainCategory>();
            for (int i = 0; i < NavigationList.getInstance(this).getImageList().size(); i++) {
                Home.MainCategory mainCategory = new Home().getInstranceMainCategory();
                mainCategory.mainCatName = NavigationList.getInstance(this).getTitleList().get(i);
                mainCategory.mainCatImage = NavigationList.getInstance(this).getImageList().get(i) + "";
                mainCategory.mainCatId = i + "";
                drawerList.add(mainCategory);
            }
            navigationDrawerAdapter.addAll(drawerList);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        showCart();
        if (Constant.IS_CURRENCY_SET) {
            getHomeData();
                databaseHelper.clearRecentItem();
                databaseHelper.clearCart();
            Constant.IS_CURRENCY_SET = false;
        }

    }


    public void setCurrency(String response) {
        if (Constant.IS_CURRENCY_SWITCHER_ACTIVE) {
            try {
                JSONObject jsonObj = new JSONObject(response);
                JSONObject currency_switcher = jsonObj.getJSONObject(RequestParamUtils.currencySwitcher);
                Constant.CurrencyList = new ArrayList<>();
                JSONArray namearray = currency_switcher.names();  //<<< get all keys in JSONArray
                for (int i = 0; i < namearray.length(); i++) {
                    JSONObject c = currency_switcher.getJSONObject(namearray.get(i).toString());
                    String name = c.getString(RequestParamUtils.name);
                    String symbol = c.getString(RequestParamUtils.symbol);
                    JSONObject obj = new JSONObject();
                    obj.put(RequestParamUtils.NAME, name);
                    obj.put(RequestParamUtils.SYMBOL, symbol);

                    // adding contact to contact list
                    Constant.CurrencyList.add(String.valueOf(obj));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void setLocale(String lang) {
        if (!getPreferences().getString(RequestParamUtils.LANGUAGE, "").equals("")) {
            lang = getPreferences().getString(RequestParamUtils.LANGUAGE, "");
        }
        String languageToLoad = lang; // your language
        if (lang.contains("-")) {
            String[] array = lang.split("-");
            if (array.length > 0) {
                languageToLoad = array[0];
            } else {
                languageToLoad = lang;
            }
        } else {
            languageToLoad = lang;
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        if (!getPreferences().getString(RequestParamUtils.DEFAULTLANGUAGE, "").equals("") && !getPreferences().getString(RequestParamUtils.DEFAULTLANGUAGE, "").equals(languageToLoad)) {
            recreate();
            getPreferences().edit().putBoolean(RequestParamUtils.iSSITELANGUAGECALLED, false).commit();
        }
        if (getPreferences().getString(RequestParamUtils.LANGUAGE, "").isEmpty()) {
            if (!getPreferences().getBoolean(RequestParamUtils.iSSITELANGUAGECALLED, false)) {
                getPreferences().edit().putBoolean(RequestParamUtils.iSSITELANGUAGECALLED, true).commit();
                (InfiniteScrollActivity.this).getPreferences().edit().putString(RequestParamUtils.DEFAULTLANGUAGE, languageToLoad).commit();
                recreate();
            }
        }
        setScreenLayoutDirection();
    }


    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Snackbar.make(llMain, getResources().getString(R.string.exitformapp), Snackbar.LENGTH_LONG).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    public void getCategoryListData(boolean isDialogShow) {
        if (Utils.isInternetConnected(this)) {
            if (isDialogShow) {
                //showProgress("");
                if (Config.SHIMMER_VIEW) {
                    mShimmerViewContainer.startShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.VISIBLE);
                } else {
                    mShimmerViewContainer.setVisibility(View.GONE);
                    showProgress("");
                }
            }
            PostApi postApi = new PostApi(InfiniteScrollActivity.this, RequestParamUtils.getCategoryListData, this, getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.PAGE, page);
                jsonObject.put(RequestParamUtils.PERPAGE, 15);
                jsonObject.put(RequestParamUtils.LODADED, InfiniteScrollAdapter.getIds());
                postApi.callPostApi(new URLS().RANDOMLIST + getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());

            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }

    public void setCategoryList() {
        InfiniteScrollAdapter = new InfiniteScrollAdapter(this, this);
        final LinearLayoutManager mLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        rvViewAllProductList.setLayoutManager(mLayoutManager);
        rvViewAllProductList.setAdapter(InfiniteScrollAdapter);
        rvViewAllProductList.setNestedScrollingEnabled(false);
        rvViewAllProductList.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(3), true));
        //pagination on get category data
        svHome.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {
                        //code to fetch more data for endless scrolling
                        if (loading) {
                            if (setNoItemFound != true) {
                                loading = false;
                                page = page + 1;
                                Log.e("End ", "Last Item Wow  and page no:- " + page);
                                llProgress.setVisibility(View.VISIBLE);
                                progress_wheel.setBarColor(Color.parseColor(getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
                                getCategoryListData(false);
                                //Do pagination.. i.e. fetch new data
                            }
                        }
                    }
                }
            }
        });
    }

    public void setToolbarTheme() {

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(Color.parseColor(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getPreferences().getString(Constant.HEADER_COLOR, Constant.HEAD_COLOR)));
        }
    }

    @Override
    public void onItemClick(int position, String value, int outerPos) {
        String userid = getPreferences().getString(RequestParamUtils.ID, "");
        if (!userid.equals("")) {
            if (value.equals(RequestParamUtils.delete)) {
                removeWishList(true, userid, position + "");
            } else if (value.equals(RequestParamUtils.insert)) {
                addWishList(true, userid, position + "");
            }
        }
    }

    public void addWishList(boolean isDialogShow, String userid, String productid) {
        if (Utils.isInternetConnected(this)) {
            if (isDialogShow) {
                showProgress("");
            }
            PostApi postApi = new PostApi(InfiniteScrollActivity.this, RequestParamUtils.addWishList, this, getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.USER_ID, userid);
                jsonObject.put(RequestParamUtils.PRODUCT_ID, productid);
                postApi.callPostApi(new URLS().ADD_TO_WISHLIST + getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }

    public void removeWishList(boolean isDialogShow, String userid, String productid) {
        if (Utils.isInternetConnected(this)) {
            if (isDialogShow) {
                showProgress("");
            }

            PostApi postApi = new PostApi(InfiniteScrollActivity.this, RequestParamUtils.removeWishList, this, getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.USER_ID, userid);
                jsonObject.put(RequestParamUtils.PRODUCT_ID, productid);
                postApi.callPostApi(new URLS().REMOVE_FROM_WISHLIST, jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(this, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }

    }
}