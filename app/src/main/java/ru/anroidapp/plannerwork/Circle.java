package ru.anroidapp.plannerwork;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Артём on 30.08.2015.
 */
public class Circle extends View{

        Paint paint;
        int X;
        int Y;
        final static int Radius = 5;

        public Circle(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            X = 10;
            Y = 10;
        }

        @Override
        protected void onDraw(Canvas canvas)// метод OnDraw вызвается Андроидом
        // тогда, когда нужно отрисовать данный
        // View
        {
            canvas.drawCircle(X, Y, Radius, paint);
            invalidate();// invalidate() нужен для того, чтобы оповестить Android,
            // что нужно выполнить метод OnDraw снова, без него View
            // не будет перериcовываться.
        }
}

