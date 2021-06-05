package comm.hyperonline.techsh.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import comm.hyperonline.techsh.R;

import java.util.ArrayList;

import java.util.List;

public class AdvancedView extends AppCompatImageView {

    private final RectF rectF_1 = new RectF();
    private final Paint paint_1 = new Paint();

    private List<Integer> colors = new ArrayList<>();

    public AdvancedView(Context context) {
        super(context);
    }

    public AdvancedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AdvancedView);

        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            if (a.getIndex(i) == R.styleable.AdvancedView_colors) {
                String[] hex_color = a.getString(R.styleable.AdvancedView_colors).split(",");
                for (String color : hex_color) {
                    colors.add(Color.parseColor(color.trim()));
                }
            }
        }

        a.recycle();
    }

    public void setColors(String... colors) {
        this.colors.clear();
        for (String color : colors) {
            this.colors.add(Color.parseColor(color.trim()));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setRectFs();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setRectFs();
        drawCanvas(canvas);

        invalidate();
    }

    private void setRectFs() {
        rectF_1.set(0, 0, getWidth(), getHeight());
    }

    private void drawCanvas(Canvas canvas) {
        paint_1.setStyle(Paint.Style.FILL);
        paint_1.setColor(getColor(0));
        paint_1.setAntiAlias(true);

        float roundRadius = getHeight() * 0.5f;

        canvas.drawRoundRect(rectF_1, roundRadius, roundRadius, paint_1);
    }

    private int getColor(int i) {
        return colors.size() == 0 ? Color.parseColor("#39B54A") : colors.get(i);
    }
}
