package com.gosproj.gosproject.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PaintView extends View
{
    private static final float STROKE_WIDTH = 5f;
    /** Need to track this so the dirty region can accommodate the stroke. **/
    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
    private Paint paint = new Paint();
    private Path path = new Path();
    /**
     * Optimizes painting by invalidating the smallest possible area.
     */
    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF();

    Bitmap bitmap = null;

    public CustomScrollView scrollView;

    private String lastEvent = "";

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAlpha(0);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(STROKE_WIDTH);
    }
    /**
     * Erases the signature.
     */
    public void clear() {
        path.reset();
        bitmap = null;
        // Repaints the entire view.
        invalidate();
    }

    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap(int width, int height)
    {
        Bitmap b = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        layout(getLeft(), getTop(), getRight(), getBottom());
        draw(c);
        return b;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (bitmap != null)
        {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }

        canvas.drawPath(path, paint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                // There is no end point yet, so don't waste cycles invalidating.
                Log.d("EVENTS", "DOWN_EVENT");
                return true;
            case MotionEvent.ACTION_MOVE:
                scrollView.setEnableScrolling(false);
                lastEvent = "move";
                Log.d("EVENTS", "MOVE");
            case MotionEvent.ACTION_UP:
                if (lastEvent.equals("up"))
                {
                    scrollView.setEnableScrolling(true);
                }
                lastEvent = "up";
                // Start tracking the dirty region.
                resetDirtyRect(eventX, eventY);
                // When the hardware tracks events faster than they are delivered, the
                // event will contain a history of those skipped points.
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    path.lineTo(historicalX, historicalY);
                }
                // After replaying history, connect the line to the touch point.
                path.lineTo(eventX, eventY);
                Log.d("EVENTS", "UP_EVENt");
                break;
            default:
                Log.d("EVENTS", "DEF");
                //debug("Ignored touch event: " + event.toString());
                return false;
        }
        // Include half the stroke width to avoid clipping.
        invalidate(
                (int) (dirtyRect.left - HALF_STROKE_WIDTH),
                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
        lastTouchX = eventX;
        lastTouchY = eventY;
        return true;
    }
    /**
     * Called when replaying history to ensure the dirty region includes all
     * points.
     */
    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }
        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }
    /**
     * Resets the dirty region when the motion event occurs.
     */
    private void resetDirtyRect(float eventX, float eventY) {
        // The lastTouchX and lastTouchY were set when the ACTION_DOWN
        // motion event occurred.
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }
}
