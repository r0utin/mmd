package comm.hyperonline.techsh.adapter;

import android.app.Activity;
import android.graphics.Color;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.model.CategoryList;
import comm.hyperonline.techsh.model.Variation;
import comm.hyperonline.techsh.utils.AdvancedView;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.Constant;
import comm.hyperonline.techsh.utils.CustomToast;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import comm.hyperonline.techsh.activity.ProductDetailActivity;
import comm.hyperonline.techsh.customview.textview.TextViewRegular;
import comm.hyperonline.techsh.interfaces.OnItemClickListner;
import comm.hyperonline.techsh.javaclasses.CheckIsVariationAvailable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class ProductColorAdapter extends RecyclerView.Adapter<ProductColorAdapter.ProductColorViewHolder> implements OnItemClickListner {

    public static int selectedpos;
    AlertDialog alertDialog;
    ProductVariationAdapter productVariationAdapter;
    private List<String> list = new ArrayList<>();
    private List<CategoryList.Attribute> dialogList;
    private List<Variation> variationList = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;
    private CustomToast toast;
    private String type;
    private int pos;
    private ProductSimpleAdapter productSimpleAdapter;

    public ProductColorAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
        toast = new CustomToast(activity);
    }

    public void setType(String type) {
        this.type = type;
    }


    public void setSimpleDialog(ProductSimpleAdapter productSimpleAdapter) {
        this.productSimpleAdapter = productSimpleAdapter;
        if (type != null && type.equals(RequestParamUtils.simple)) {
            showSimpleDialog(productSimpleAdapter);

        }
    }


    public void addAll(List<String> list) {
        this.list = list;
        getWidthAndHeight();
        notifyDataSetChanged();
    }

    public void addAllVariationList(List<Variation> variationList, ProductVariationAdapter productVariationAdapter) {
        this.variationList = variationList;
        this.productVariationAdapter = productVariationAdapter;
        showDialog(productVariationAdapter);
        notifyDataSetChanged();
    }

    public void getDialogList(List<CategoryList.Attribute> dialogList) {
        this.dialogList = dialogList;

    }


    @Override
    public ProductColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_color, parent, false);

        return new ProductColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductColorViewHolder holder, final int position) {
        holder.llMain.getLayoutParams().height = width;
        holder.llMain.getLayoutParams().width = width * 2;

        //GradientDrawable gd = (GradientDrawable) holder.flTransparent.getBackground();
        holder.advancedView.setColors(((BaseActivity) activity).getPreferences().getString(Constant.APP_TRANSPARENT, Constant.PRIMARY_COLOR));
        if (position == selectedpos) {
            holder.tik.setVisibility(View.VISIBLE);
        } else {
            holder.tik.setVisibility(View.GONE);
        }

        holder.llMain.setBackgroundColor(Color.WHITE);
        holder.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertDialog != null) {
                    if (type.equals(RequestParamUtils.variable)) {
                        alertDialog.show();
                        productVariationAdapter.notifyDataSetChanged();
                    } else if (type.equals(RequestParamUtils.simple)) {
                        alertDialog.show();
                        productSimpleAdapter.notifyDataSetChanged();

                    }

                }

            }
        });
        holder.tvName.setText(list.get(position));
        holder.tvName.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_TRANSPARENT, Constant.PRIMARY_COLOR)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onItemClick(int position, String value, int outerPos) {

    }

    public void getWidthAndHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels / 8;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void showDialog(final ProductVariationAdapter productVariationAdapter) {
        pos = selectedpos;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_product_variation, null);
        dialogBuilder.setView(dialogView);

        RecyclerView rvProductVariation = (RecyclerView) dialogView.findViewById(R.id.rvProductVariation);
        TextViewRegular tvDone = (TextViewRegular) dialogView.findViewById(R.id.tvDone);
        TextViewRegular tvCancel = (TextViewRegular) dialogView.findViewById(R.id.tvCancel);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        rvProductVariation.setLayoutManager(mLayoutManager);
        rvProductVariation.setAdapter(productVariationAdapter);
        rvProductVariation.setNestedScrollingEnabled(false);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
//        alertDialog.show();

        tvCancel.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvDone.setBackgroundColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

//                showDialog(productVariationAdapter);
//                selectedpos = pos;
                // notifyDataSetChanged();
            }
        });
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!new CheckIsVariationAvailable().isVariationAvailbale(ProductDetailActivity.combination, variationList, dialogList)) {
                    toast.showToast(activity.getResources().getString(R.string.combition));
                } else {
                    onItemClickListner.onItemClick(new CheckIsVariationAvailable().getVariationid(variationList, list), "", 0);
                    toast.cancelToast();
                    alertDialog.dismiss();
                    pos = selectedpos;
                    notifyDataSetChanged();
                }

            }
        });

    }

    public void showSimpleDialog(ProductSimpleAdapter productSimpleAdapter) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_product_variation, null);
        dialogBuilder.setView(dialogView);

        RecyclerView rvProductVariation = (RecyclerView) dialogView.findViewById(R.id.rvProductVariation);
        TextViewRegular tvDone = (TextViewRegular) dialogView.findViewById(R.id.tvDone);
        TextViewRegular tvCancel = (TextViewRegular) dialogView.findViewById(R.id.tvCancel);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        rvProductVariation.setLayoutManager(mLayoutManager);
        rvProductVariation.setAdapter(productSimpleAdapter);
        rvProductVariation.setNestedScrollingEnabled(false);
        productSimpleAdapter.addAll(dialogList);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        tvCancel.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvDone.setBackgroundColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
                selectedpos = pos;
                notifyDataSetChanged();
            }
        });
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                pos = selectedpos;
                notifyDataSetChanged();
            }
        });
    }

    public class ProductColorViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.asd)
        ConstraintLayout llMain;

        @BindView(R.id.flTransparent)
        FrameLayout flTransparent;

        @BindView(R.id.tvName)
        TextViewRegular tvName;

        @BindView(R.id.advancedView)
        AdvancedView advancedView;

        @BindView(R.id.tik)
        ImageView tik;

        public ProductColorViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}