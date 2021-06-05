package comm.hyperonline.techsh.utils;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int horizontalSpaceWidth;

    public HorizontalSpaceItemDecoration(Resources resource, int horizontalSpaceWidth) {
        this.horizontalSpaceWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                horizontalSpaceWidth,
                resource.getDisplayMetrics()
        );
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.left = horizontalSpaceWidth;
        }
    }
}
