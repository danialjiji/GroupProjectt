package com.example.groupproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

public class WheelView extends View {

    private List<String> items = new ArrayList<>();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF wheelBounds;
    private float angle = 0, sweepAngle;
    private int[] colors = {
            getContext().getColor(R.color.blue),
            getContext().getColor(R.color.red),
            getContext().getColor(R.color.green),
            getContext().getColor(R.color.yellow),
            getContext().getColor(R.color.blue),
            getContext().getColor(R.color.red),
            getContext().getColor(R.color.green),
            getContext().getColor(R.color.yellow),
    };

    public WheelView(Context context, AttributeSet attributes){
        super(context, attributes);
        paint.setTextSize(55);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    public void setItems(List<String> newItems){
        items = newItems;
        sweepAngle = 360f / items.size();
        invalidate();
    }

    public void spinToIndex(int index){
        float target = 360 * 5 + (360 - (index * sweepAngle + sweepAngle / 2));
        ValueAnimator animator = ValueAnimator.ofFloat(angle, target);
        animator.setDuration(3000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            angle = (float) animation.getAnimatedValue();
            invalidate();
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                angle = angle % 360;
            }
        });
        animator.start();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (items == null || items.isEmpty()) {
            return;
        }

        //Wheel View
        int width = getWidth();
        int height = getHeight();
        float radius = Math.min(width, height) / 2f - 30;
        wheelBounds = new RectF(
                width / 2f - radius,
                height / 2f - radius,
                width / 2f + radius,
                height / 2f + radius
        );

        RectF textBounds = new RectF(
                wheelBounds.left + 85,
                wheelBounds.top + 85,
                wheelBounds.right - 85,
                wheelBounds.bottom - 85
        );

        canvas.save();
        canvas.rotate(angle % 360, width / 2f, height / 2f);

        for(int i = 0; i < items.size(); i++){
            paint.setColor(colors[i % colors.length]);
            canvas.drawArc(wheelBounds, i * sweepAngle, sweepAngle, true, paint);

            paint.setColor(Color.WHITE);
            Path path = new Path();
            path.addArc(textBounds, i * sweepAngle + sweepAngle / 4, sweepAngle / 2);
            canvas.drawTextOnPath(items.get(i), path, 0, 0, paint);
        }
        canvas.restore();

        paint.setColor(Color.WHITE);
        canvas.drawCircle(width / 2f, height / 2f, 60, paint);


        //Triangle Pointer
        float centerX = width / 2f;
        float centerY = height / 2f;

        float radiusTriangle = Math.min(width, height) / 2f - 30;

        float pointerLength = -40;
        float pointerTipX = centerX + radiusTriangle + pointerLength;
        float pointerBaseX = centerX + radiusTriangle + 10;
        float pointerTopY = centerY - 30;
        float pointerBottomY = centerY + 30;

        paint.setColor(Color.LTGRAY);
        Path triangle = new Path();
        triangle.moveTo(pointerTipX, centerY);
        triangle.lineTo(pointerBaseX, pointerTopY);
        triangle.lineTo(pointerBaseX, pointerBottomY);
        triangle.close();
        canvas.drawPath(triangle, paint);
    }
}
