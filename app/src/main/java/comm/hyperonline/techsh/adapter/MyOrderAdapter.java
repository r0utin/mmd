package comm.hyperonline.techsh.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.activity.OrderDetailActivity;
import comm.hyperonline.techsh.customview.textview.TextViewLight;
import comm.hyperonline.techsh.customview.textview.TextViewMedium;
import comm.hyperonline.techsh.customview.textview.TextViewRegular;
import comm.hyperonline.techsh.interfaces.OnItemClickListner;
import comm.hyperonline.techsh.model.Orders;
import comm.hyperonline.techsh.utils.BaseActivity;
import comm.hyperonline.techsh.utils.Constant;
import comm.hyperonline.techsh.utils.RequestParamUtils;
import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bhumi Shah on 11/7/2017.
 */

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.RecentViewHolde> {


    private List<Orders> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;
    private int width = 0, height = 0;

    public MyOrderAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;
        this.onItemClickListner = onItemClickListner;
    }

    public void addAll(List<Orders> list) {
        this.list = list;
        getWidthAndHeight();
        notifyDataSetChanged();
    }

    @Override
    public RecentViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_order, parent, false);

        return new RecentViewHolde(itemView);
    }

    @Override
    public void onBindViewHolder(RecentViewHolde holder, final int position) {

        holder.tvView.setBackgroundColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.SECOND_COLOR, Constant.SECONDARY_COLOR)));

        holder.tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.ORDERDETAIL = list.get(position);
                Intent intent = new Intent(activity, OrderDetailActivity.class);
                intent.putExtra(RequestParamUtils.ID, list.get(position).id);
                activity.startActivity(intent);
            }
        });

        if (list.get(position).lineItems.size() == 0 || list.get(position).lineItems.get(0).productImage.equals("")) {
            holder.ivImage.setVisibility(View.INVISIBLE);
        } else {
            holder.ivImage.setVisibility(View.VISIBLE);
            Picasso.get().load(list.get(position).lineItems.get(0).productImage).into(holder.ivImage);
        }


        String title = new String();
        for (int i = 0; i < list.get(position).lineItems.size(); i++) {
            if (i == 0) {
                title = list.get(position).lineItems.get(i).name;
            } else {
                title = title + " & " + list.get(position).lineItems.get(i).name;
            }
        }
//        holder.tvTitle.setText(title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            tvProductName.setText(categoryList.name + "");
            holder.tvTitle.setText(Html.fromHtml(title + "", Html.FROM_HTML_MODE_LEGACY));
        } else {
//            tvProductName.setText(categoryList.name + "");
            holder.tvTitle.setText(Html.fromHtml(title + ""));
        }

        String upperString = list.get(position).status.substring(0, 1).toUpperCase() + list.get(position).status.substring(1);
        holder.tvStatus.setText(upperString);
        holder.tvStatus.setTextColor(Color.parseColor(((BaseActivity) activity).getPreferences().getString(Constant.APP_COLOR, Constant.PRIMARY_COLOR)));
        String date = list.get(position).dateCreated;
        String idAndDate = activity.getString(R.string.on) + " " + getPersianTime(date.split("T")[1]) + " " + dateConverter(date.split("T")[0].split("-")) + "  " + activity.getString(R.string.order_id) + list.get(position).id;
        holder.tvOrderDateAndId.setText(idAndDate);
        String statusDesc = new String();
        if (list.get(position).status.toLowerCase().equals(RequestParamUtils.any)) {
            holder.tvStatusDesc.setText(R.string.delivered_soon);
        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.pending)) {
            holder.tvStatusDesc.setText(R.string.order_is_in_pending_state);
        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.processing)) {
            holder.tvStatusDesc.setText(R.string.order_is_under_processing);
        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.onHold)) {
            holder.tvStatusDesc.setText(R.string.order_is_on_hold);
        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.completed)) {
            holder.tvStatusDesc.setText(R.string.delivered);
        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.cancelled)) {
            holder.tvStatusDesc.setText(R.string.order_is_cancelled);
        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.refunded)) {
            holder.tvStatusDesc.setText(R.string.you_are_refunded_for_this_order);
        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.failed)) {
            holder.tvStatusDesc.setText(R.string.order_is_failed);
        } else if (list.get(position).status.toLowerCase().equals(RequestParamUtils.shipping)) {
            holder.tvStatusDesc.setText(R.string.delivered_soon);
        }
    }


    private String getPersianTime(String dateStr) {
//        String dateStr = "04:55";
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        df.setTimeZone(TimeZone.getTimeZone("GMT+3:30"));
        String formattedDate = df.format(date);

        return formattedDate;
    }
    private String dateConverter(String[] date) {
        int day = Integer.parseInt(date[2]);
        int mounth = Integer.parseInt(date[1]);
        int year = Integer.parseInt(date[0]);

        DateConverter dateConverter = new DateConverter();
        JalaliDate jalaliDate = dateConverter.gregorianToJalali(year, mounth, day);
        return jalaliDate.getDayOfWeek().getStringInPersian() + " " + jalaliDate.getDay() + " " + jalaliDate.getMonthPersian().getStringInPersian() + " " + jalaliDate.getYear();

//        JalaliCalendar jalaliDate = new JalaliCalendar(new GregorianCalendar(year, mounth, day));
//        return jalaliDate.getDayOfWeekString() + " " + jalaliDate.getDay() + " " + jalaliDate.getMonthString() + " " + jalaliDate.getYear();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void getWidthAndHeight() {
        int height_value = activity.getResources().getInteger(R.integer.height);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels / 2 - height_value * 2;
        height = width / 2 + height_value;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class RecentViewHolde extends RecyclerView.ViewHolder {

        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.ivImage)
        ImageView ivImage;

        @BindView(R.id.tvTitle)
        TextViewRegular tvTitle;

        @BindView(R.id.tvView)
        TextViewRegular tvView;

        @BindView(R.id.tvStatus)
        TextViewMedium tvStatus;

        @BindView(R.id.tvStatusDesc)
        TextViewLight tvStatusDesc;

        @BindView(R.id.tvOrderDateAndId)
        TextViewLight tvOrderDateAndId;


        public RecentViewHolde(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}