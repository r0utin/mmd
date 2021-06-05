package comm.hyperonline.techsh.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.customview.textview.TextViewLight;
import comm.hyperonline.techsh.customview.textview.TextViewRegular;
import comm.hyperonline.techsh.interfaces.OnItemClickListner;
import comm.hyperonline.techsh.model.ProductReview;
import comm.hyperonline.techsh.utils.Constant;
import com.github.eloyzone.jalalicalendar.DateConverter;
import com.github.eloyzone.jalalicalendar.JalaliDate;

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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private List<ProductReview> list = new ArrayList<>();
    private Activity activity;
    private OnItemClickListner onItemClickListner;


    public ReviewAdapter(Activity activity, OnItemClickListner onItemClickListner) {
        this.activity = activity;

    }

    public void addAll(List<ProductReview> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);

        return new ReviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        holder.tvRatting.setText(Constant.setDecimalTwo((double) list.get(position).rating));
        holder.tvName.setText(list.get(position).name);
        holder.tvReview.setText(list.get(position).review);

        String date = list.get(position).dateCreated;
        holder.tvTime.setText(getPersianTime(date.split("T")[1]) + " " + dateConverter(date.split("T")[0].split("-")));


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

    public class ReviewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName)
        TextViewRegular tvName;

        @BindView(R.id.tvRatting)
        TextViewRegular tvRatting;

        @BindView(R.id.tvReview)
        TextViewRegular tvReview;

        @BindView(R.id.tvTime)
        TextViewLight tvTime;


        public ReviewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }


    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}