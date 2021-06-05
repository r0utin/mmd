package comm.hyperonline.techsh.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalDividerItemDecoration extends DividerItemDecoration {
    /**
     * Creates a divider {@link DividerItemDecoration} that can be used with a
     * {@link LinearLayoutManager}.
     *
     * @param context     Current context, it will be used to access resources.
     */
    public VerticalDividerItemDecoration(Context context) {
        super(context, VERTICAL);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1 || parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 2) {
            super.getItemOffsets(outRect, view, parent, state);
        }
    }
}