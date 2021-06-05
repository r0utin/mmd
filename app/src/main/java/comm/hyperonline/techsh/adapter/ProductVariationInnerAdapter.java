package comm.hyperonline.techsh.adapter;

import android.app.Activity;
import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.customview.textview.TextViewRegular;
import comm.hyperonline.techsh.interfaces.OnItemClickListner;
import comm.hyperonline.techsh.model.CategoryList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class ProductVariationInnerAdapter extends RecyclerView.Adapter<ProductVariationInnerAdapter.ProductColorViewHolder> {

    public int previousSelectionPosition;
    public int outerPosition, outerPreviousSelectedPosition, outerListSize;
    private String outListId;
    private List<String> list = new ArrayList<>();
    private List<CategoryList.NewOption> newOptionList = new ArrayList<>();
    private List<String> variationList = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;


    public ProductVariationInnerAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
    }


    public void getSizePosition(int outerListSize, int outerPreviousSelectedPosition) {
        this.outerListSize = outerListSize;
        this.outerPreviousSelectedPosition = outerPreviousSelectedPosition;

    }

    public void setOutListId(String outListId) {
        this.outListId = outListId;
    }

    public void setOuterPosition(int outerPosition) {
        this.outerPosition = outerPosition;
    }

    public void addAll(List<String> list, List<CategoryList.NewOption> newOptionList) {
        this.list = list;
        this.newOptionList = newOptionList;
        getWidthAndHeight();
        notifyDataSetChanged();
    }

    public void addAllVariationList(List<String> variationList) {
        this.variationList = variationList;
        notifyDataSetChanged();
    }

    @Override
    public ProductColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_variation, parent, false);

        return new ProductColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProductColorViewHolder holder, final int position) {

        //GradientDrawable gd = (GradientDrawable) holder.flTransparent.getBackground();
        //holder.flTransparent.setBackgroundColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_TRANSPARENT, Constant.PRIMARY_COLOR)));

//        if (variationList != null && variationList.size() > 0) {
////            if (!variationList.contains(list.get(position)) && outerPosition != 0) {
////                gd = (GradientDrawable) holder.llMain.getBackground();
////                gd.setStroke(5, Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_TRANSPARENT_VERY_LIGHT, Constant.PRIMARY_COLOR)));
////
////                holder.tvNa me.setTextColor(activity.getResources().getColor(R.color.gray_table));
////            } else {
//            holder.advancedView.setColors(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR));
//            holder.tvName.setTextColor(activity.getResources().getColor(R.color.white));
////            }
//        } else {
//            if (outerPosition == 0) {
//                //holder.llMain.setBackgroundResource(R.drawable.primary_strok_button);
//                holder.tvName.setTextColor(activity.getResources().getColor(R.color.white));
//            }
//        }
        if (previousSelectionPosition == position) {
            holder.tik.setVisibility(View.VISIBLE);
        } else {
            holder.tik.setVisibility(View.GONE);
        }

//        holder.asd.getLayoutParams().height = (width * 8) / 10;
//        holder.asd.getLayoutParams().width = width * 2;

        holder.asd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousSelectionPosition = position;
                onItemClickListner.onItemClick(previousSelectionPosition, outListId + "->" + list.get(position), outerPosition);
                notifyItemChanged(position);
            }
        });

        if (newOptionList != null) {
            if (newOptionList.get(position).image != null && !newOptionList.get(position).image.equals("")) {

                holder.ivImg.setVisibility(View.VISIBLE);
                Picasso.get().load(newOptionList.get(position).image)
                        .error(R.drawable.no_image_available)
                        .into(holder.ivImg);
                // holder.tvName.setText(newOptionList.get(position).image);
            } else if (newOptionList.get(position).color != null && !newOptionList.get(position).color.equals("")) {
                holder.tvName.setText("");
                holder.tvName.setBackgroundColor(Color.parseColor(newOptionList.get(position).color));
            } else {
                holder.tvName.setText(list.get(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void getWidthAndHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels / 7;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ProductColorViewHolder extends RecyclerView.ViewHolder {

        //        @BindView(R.id.advancedView)
//        AdvancedView advancedView;
//
        @BindView(R.id.asd)
        LinearLayout asd;
//
//        @BindView(R.id.flTransparent)
//        FrameLayout flTransparent;

        @BindView(R.id.tvName)
        TextViewRegular tvName;

        @BindView(R.id.ivImg)
        ImageView ivImg;

        @BindView(R.id.tik)
        ImageView tik;

        public ProductColorViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}