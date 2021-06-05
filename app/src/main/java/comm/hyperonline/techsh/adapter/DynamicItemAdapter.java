package comm.hyperonline.techsh.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ciyashop.library.apicall.PostApi;
import com.ciyashop.library.apicall.URLS;
import com.ciyashop.library.apicall.interfaces.OnResponseListner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.activity.ProductDetailActivity;
import comm.hyperonline.techsh.customview.textview.TextViewRegular;
import comm.hyperonline.techsh.model.CategoryList;
import comm.hyperonline.techsh.model.Home;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.Constant;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import comm.hyperonline.techsh.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DynamicItemAdapter extends RecyclerView.Adapter<DynamicItemAdapter.MyViewHolder> implements OnResponseListner {

    public static final String TAG = "ChangeLanguageItemAdapter";
    private final LayoutInflater inflater;
    List<Home.Product> list;
    private Activity activity;
    private int width = 0, height = 0;
    private String[] strings = {"۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹", "۱۰", "۱۱", "۱۲", "۱۳", "۱۴", "۱۵"};
    private int remove;
    private int page;

    public DynamicItemAdapter(Activity activity) {
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
    }

    public void addAll(List<Home.Product> list) {
        this.list = list;
        getWidthAndHeight();
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_product_4, parent, false);
        MyViewHolder holer = new MyViewHolder(view);
        return holer;
    }

    @Override
    public void onBindViewHolder(final DynamicItemAdapter.MyViewHolder holder, final int position) {
        int fixedPosition_1 = position * 3;
        int fixedPosition_2 = (position * 3) + 1;
        int fixedPosition_3 = (position * 3) + 2;

        holder.main.getLayoutParams().width = (width * 159) / 100;
        holder.main.getLayoutParams().height = (height * 161) / 100;

        if (position != page) {
            holder.n1.setText(strings[fixedPosition_1]);
            holder.n2.setText(strings[fixedPosition_2]);
            holder.n3.setText(strings[fixedPosition_3]);

            if (Constant.IS_ADD_TO_CART_ACTIVE) {
                holder.item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int fixedPosition_1 = position * 3;
                        String productDetail = new Gson().toJson(list.get(fixedPosition_1));
                        CategoryList categoryListRider = new Gson().fromJson(
                                productDetail, new TypeToken<CategoryList>() {
                                }.getType());
                        Constant.CATEGORYDETAIL = categoryListRider;
                        if (categoryListRider.type.equals("external")) {

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(categoryListRider.externalUrl));
                            activity.startActivity(browserIntent);
                        } else {
                            Intent intent = new Intent(activity, ProductDetailActivity.class);
                            activity.startActivity(intent);
                        }
                    }
                });
                holder.item2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int fixedPosition_2 = (position * 3) + 1;
                        String productDetail = new Gson().toJson(list.get(fixedPosition_2));
                        CategoryList categoryListRider = new Gson().fromJson(
                                productDetail, new TypeToken<CategoryList>() {
                                }.getType());
                        Constant.CATEGORYDETAIL = categoryListRider;
                        if (categoryListRider.type.equals("external")) {

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(categoryListRider.externalUrl));
                            activity.startActivity(browserIntent);
                        } else {
                            Intent intent = new Intent(activity, ProductDetailActivity.class);
                            activity.startActivity(intent);
                        }
                    }
                });
                holder.item3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int fixedPosition_3 = (position * 3) + 2;
                        String productDetail = new Gson().toJson(list.get(fixedPosition_3));
                        CategoryList categoryListRider = new Gson().fromJson(
                                productDetail, new TypeToken<CategoryList>() {
                                }.getType());
                        Constant.CATEGORYDETAIL = categoryListRider;
                        if (categoryListRider.type.equals("external")) {

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(categoryListRider.externalUrl));
                            activity.startActivity(browserIntent);
                        } else {
                            Intent intent = new Intent(activity, ProductDetailActivity.class);
                            activity.startActivity(intent);
                        }
                    }
                });
            } else {
                holder.item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int fixedPosition_1 = position * 3;
                        getProductDetail(String.valueOf(list.get(fixedPosition_1).id));
                    }
                });
                holder.item2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int fixedPosition_2 = (position * 3) + 1;
                        getProductDetail(String.valueOf(list.get(fixedPosition_2).id));
                    }
                });
                holder.item3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int fixedPosition_3 = (position * 3) + 2;
                        getProductDetail(String.valueOf(list.get(fixedPosition_3).id));
                    }
                });
            }

            if (list.get(fixedPosition_1).image != null) {
                Picasso.get().load(list.get(fixedPosition_1).image)
                        .error(R.drawable.no_image_available)
                        .into(holder.ivImage);
            } else {
                holder.ivImage.setImageResource(R.drawable.no_image_available);
            }

            if (list.get(fixedPosition_2).image != null) {
                Picasso.get().load(list.get(fixedPosition_2).image)
                        .error(R.drawable.no_image_available)
                        .into(holder.ivImage2);
            } else {
                holder.ivImage2.setImageResource(R.drawable.no_image_available);
            }

            if (list.get(fixedPosition_3).image != null) {
                Picasso.get().load(list.get(fixedPosition_3).image)
                        .error(R.drawable.no_image_available)
                        .into(holder.ivImage3);
            } else {
                holder.ivImage3.setImageResource(R.drawable.no_image_available);
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                holder.tvName.setText(Html.fromHtml(list.get(fixedPosition_1).title + "", Html.FROM_HTML_MODE_LEGACY));
                holder.tvName2.setText(Html.fromHtml(list.get(fixedPosition_2).title + "", Html.FROM_HTML_MODE_LEGACY));
                holder.tvName3.setText(Html.fromHtml(list.get(fixedPosition_3).title + "", Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.tvName.setText(Html.fromHtml(list.get(fixedPosition_1).title + ""));
                holder.tvName2.setText(Html.fromHtml(list.get(fixedPosition_2).title + ""));
                holder.tvName3.setText(Html.fromHtml(list.get(fixedPosition_3).title + ""));
            }
        } else {
            switch (remove) {
                case 1:
                    holder.constraintLayout.removeView(holder.item3);

                    holder.n1.setText(strings[fixedPosition_1]);
                    holder.n2.setText(strings[fixedPosition_2]);

                    if (Constant.IS_ADD_TO_CART_ACTIVE) {
                        holder.item1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int fixedPosition_1 = position * 3;
                                String productDetail = new Gson().toJson(list.get(fixedPosition_1));
                                CategoryList categoryListRider = new Gson().fromJson(
                                        productDetail, new TypeToken<CategoryList>() {
                                        }.getType());
                                Constant.CATEGORYDETAIL = categoryListRider;
                                if (categoryListRider.type.equals("external")) {

                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(categoryListRider.externalUrl));
                                    activity.startActivity(browserIntent);
                                } else {
                                    Intent intent = new Intent(activity, ProductDetailActivity.class);
                                    activity.startActivity(intent);
                                }
                            }
                        });
                        holder.item2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int fixedPosition_2 = (position * 3) + 1;
                                String productDetail = new Gson().toJson(list.get(fixedPosition_2));
                                CategoryList categoryListRider = new Gson().fromJson(
                                        productDetail, new TypeToken<CategoryList>() {
                                        }.getType());
                                Constant.CATEGORYDETAIL = categoryListRider;
                                if (categoryListRider.type.equals("external")) {

                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(categoryListRider.externalUrl));
                                    activity.startActivity(browserIntent);
                                } else {
                                    Intent intent = new Intent(activity, ProductDetailActivity.class);
                                    activity.startActivity(intent);
                                }
                            }
                        });
                    } else {
                        holder.item1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int fixedPosition_1 = position * 3;
                                getProductDetail(String.valueOf(list.get(fixedPosition_1).id));
                            }
                        });
                        holder.item2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int fixedPosition_2 = (position * 3) + 1;
                                getProductDetail(String.valueOf(list.get(fixedPosition_2).id));
                            }
                        });
                    }

                    if (list.get(fixedPosition_1).image != null) {
                        Picasso.get().load(list.get(fixedPosition_1).image)
                                .error(R.drawable.no_image_available)
                                .into(holder.ivImage);
                    } else {
                        holder.ivImage.setImageResource(R.drawable.no_image_available);
                    }

                    if (list.get(fixedPosition_2).image != null) {
                        Picasso.get().load(list.get(fixedPosition_2).image)
                                .error(R.drawable.no_image_available)
                                .into(holder.ivImage2);
                    } else {
                        holder.ivImage2.setImageResource(R.drawable.no_image_available);
                    }

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        holder.tvName.setText(Html.fromHtml(list.get(fixedPosition_1).title + "", Html.FROM_HTML_MODE_LEGACY));
                        holder.tvName2.setText(Html.fromHtml(list.get(fixedPosition_2).title + "", Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        holder.tvName.setText(Html.fromHtml(list.get(fixedPosition_1).title + ""));
                        holder.tvName2.setText(Html.fromHtml(list.get(fixedPosition_2).title + ""));
                    }
                    break;
                case 2:
                    holder.constraintLayout.removeView(holder.item3);
                    holder.constraintLayout.removeView(holder.item2);

                    holder.n1.setText(strings[fixedPosition_1]);

                    if (Constant.IS_ADD_TO_CART_ACTIVE) {
                        holder.item1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int fixedPosition_1 = position * 3;
                                String productDetail = new Gson().toJson(list.get(fixedPosition_1));
                                CategoryList categoryListRider = new Gson().fromJson(
                                        productDetail, new TypeToken<CategoryList>() {
                                        }.getType());
                                Constant.CATEGORYDETAIL = categoryListRider;
                                if (categoryListRider.type.equals("external")) {

                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(categoryListRider.externalUrl));
                                    activity.startActivity(browserIntent);
                                } else {
                                    Intent intent = new Intent(activity, ProductDetailActivity.class);
                                    activity.startActivity(intent);
                                }
                            }
                        });
                    } else {
                        holder.item1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int fixedPosition_1 = position * 3;
                                getProductDetail(String.valueOf(list.get(fixedPosition_1).id));
                            }
                        });
                    }

                    if (list.get(fixedPosition_1).image != null) {
                        Picasso.get().load(list.get(fixedPosition_1).image)
                                .error(R.drawable.no_image_available)
                                .into(holder.ivImage);
                    } else {
                        holder.ivImage.setImageResource(R.drawable.no_image_available);
                    }

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        holder.tvName.setText(Html.fromHtml(list.get(fixedPosition_1).title + "", Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        holder.tvName.setText(Html.fromHtml(list.get(fixedPosition_1).title + ""));
                    }
                    break;
            }
        }
    }


    public void getWidthAndHeight() {
        int height_value = activity.getResources().getInteger(R.integer.height);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels / 2 - 10;
        height = width + height_value;
    }

    public void getProductDetail(String groupid) {
        if (Utils.isInternetConnected(activity)) {
            ((BaseActivity) activity).showProgress("");
            PostApi postApi = new PostApi(activity, RequestParamUtils.getProductDetail, this, ((BaseActivity) activity).getlanuage());
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(RequestParamUtils.INCLUDE, groupid);
                postApi.callPostApi(new URLS().PRODUCT_URL + ((BaseActivity) activity).getPreferences().getString(RequestParamUtils.CurrencyText, ""), jsonObject.toString());
            } catch (Exception e) {
                Log.e("Json Exception", e.getMessage());
            }
        } else {
            Toast.makeText(activity, R.string.internet_not_working, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() > 15) {
            return 5;
        } else {
            if (list.size() % 3 == 0) {
                return list.size() / 3;
            } else {
                page = (list.size() / 3);
                remove = 3 - list.size() % 3;
                return (int) Math.ceil(list.size() / 3d);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @SuppressLint("LongLogTag")
    @Override
    public void onResponse(String response, String methodName) {
        if (methodName.equals(RequestParamUtils.getProductDetail)) {
            if (response != null && response.length() > 0) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    CategoryList categoryListRider = new Gson().fromJson(
                            jsonArray.get(0).toString(), new TypeToken<CategoryList>() {
                            }.getType());
                    Constant.CATEGORYDETAIL = categoryListRider;

                    if (categoryListRider.type.equals(RequestParamUtils.external)) {

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(categoryListRider.externalUrl));
                        activity.startActivity(browserIntent);
                    } else {
                        Intent intent = new Intent(activity, ProductDetailActivity.class);
                        activity.startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.e(methodName + "Gson Exception is ", e.getMessage());
                }
                ((BaseActivity) activity).dismissProgress();
            }
        }


    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.main)
        ConstraintLayout main;

        @BindView(R.id.constraintLayout)
        ConstraintLayout constraintLayout;

        @BindView(R.id.item_1)
        ConstraintLayout item1;

        @BindView(R.id.item_2)
        ConstraintLayout item2;

        @BindView(R.id.item_3)
        ConstraintLayout item3;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.ivImage2)
        ImageView ivImage2;

        @BindView(R.id.ivImage3)
        ImageView ivImage3;

        @BindView(R.id.n_1)
        TextView n1;

        @BindView(R.id.n_2)
        TextView n2;

        @BindView(R.id.n_3)
        TextView n3;

        @BindView(R.id.tvName)
        TextViewRegular tvName;

        @BindView(R.id.tvName2)
        TextViewRegular tvName2;

        @BindView(R.id.tvName3)
        TextViewRegular tvName3;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
