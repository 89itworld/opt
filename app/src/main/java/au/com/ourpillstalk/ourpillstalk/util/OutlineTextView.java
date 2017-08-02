package au.com.ourpillstalk.ourpillstalk.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class OutlineTextView extends TextView
{
    public OutlineTextView(Context context)
    {
        super(context);
        init();
    }

    public OutlineTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public OutlineTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        int textColor = getTextColors().getDefaultColor();
        setTextColor(Color.BLACK);
        getPaint().setStrokeWidth(10);
        getPaint().setStyle(Paint.Style.STROKE);
        super.onDraw(canvas);
        setTextColor(textColor);
        getPaint().setStrokeWidth(0);
        getPaint().setStyle(Paint.Style.FILL);
        super.onDraw(canvas);
    }

    private void init()
    {

    }
}