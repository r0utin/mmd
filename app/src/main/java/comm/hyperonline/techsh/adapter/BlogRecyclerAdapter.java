package comm.hyperonline.techsh.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import comm.hyperonline.techsh.R;
import comm.hyperonline.techsh.model.BlogModel;
import comm.hyperonline.techsh.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.CustomHolder> {

    private ArrayList<BlogModel> dataList = new ArrayList<>();
    private Context context;
    private Typeface typeface;
    private OnItemCliclListener onItemClickListener;

    public void setOnItemClickListener(OnItemCliclListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemCliclListener{
        void onItemClick(BlogModel model);
    }

    public BlogRecyclerAdapter(Context context) {
        this.context = context;

        typeface = Utils.getFont(context);
    }

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blog_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHolder holder, int position) {
        BlogModel model = dataList.get(position);

        holder.txtTitle.setTypeface(typeface);
        holder.txtTitle.setText(model.title);
        if (model.image.startsWith("http"))
            Picasso.get().load(model.image).placeholder(R.drawable.no_image_available).into(holder.imgCover);
        else holder.imgCover.setImageDrawable(null);

        holder.frameClick.setOnClickListener(view -> {
            onItemClickListener.onItemClick(model);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addData(ArrayList<BlogModel> newData) {
        dataList.addAll(newData);
        notifyDataSetChanged();
    }

    class CustomHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cardView)
        ConstraintLayout cardView;
        @BindView(R.id.txtTitle)
        TextView txtTitle;
        @BindView(R.id.imgCover)
        ImageView imgCover;
        @BindView(R.id.frameClick)
        FrameLayout frameClick;

        public CustomHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
