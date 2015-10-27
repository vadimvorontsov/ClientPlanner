package ru.anroidapp.plannerwork.record.contact_choose;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ru.anroidapp.plannerwork.record.contact_choose.intface.IIndexBarFilter;
import ru.anroidapp.plannerwork.record.contact_choose.intface.IPinnedHeader;
import ru.anroidapp.plannerwork.record.contact_choose.intface.PinnedHeaderAdapter;
import ru.anroidapp.plannerwork.R;

/*
 * A ListView that maintains a header pinned at the top of the list. The
 * pinned header can be pushed up and dissolved as needed.
 *
 */
public class PinnedHeaderListView extends ListView implements IIndexBarFilter {

    // interface object that configure pinned header view position in list view
    IPinnedHeader mAdapter;

    // view objects
    View mHeaderView, mIndexBarView, mPreviewTextView;

    // flags that decide view visibility
    boolean mHeaderVisibility = false;
    boolean mPreviewVisibility = false;
    // initially show index bar view with it's content
    boolean mIndexBarVisibility = true;

    // context object
    Context mContext;

    // view height and width
    int mHeaderViewWidth,
            mHeaderViewHeight,
            mIndexBarViewWidth,
            mIndexBarViewHeight,
            mIndexBarViewMargin,
            mPreviewTextViewWidth,
            mPreviewTextViewHeight;

    // touched index bar Y axis position used to decide preview text view position
    float mIndexBarY;


    public PinnedHeaderListView(Context context) {
        super(context);
        this.mContext = context;
    }


    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }


    public PinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }


    @Override
    public void setAdapter(ListAdapter adapter) {
        this.mAdapter = (PinnedHeaderAdapter) adapter;
        super.setAdapter(adapter);
    }


    public void setPinnedHeaderView(View headerView) {
        this.mHeaderView = headerView;
        // Disable vertical fading when the pinned header is present
        // TODO change ListView to allow separate measures for top and bottom fading edge;
        // in this particular case we would like to disable the top, but not the bottom edge.
        if (mHeaderView != null) {
            setFadingEdgeLength(0);
        }
    }


    public void setIndexBarView(View indexBarView) {
        mIndexBarViewMargin = (int) mContext.getResources().getDimension(R.dimen.index_bar_view_margin);
        this.mIndexBarView = indexBarView;
    }


    public void setPreviewView(View previewTextView) {
        this.mPreviewTextView = previewTextView;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }

        if (mIndexBarView != null && mIndexBarVisibility) {
            measureChild(mIndexBarView, widthMeasureSpec, heightMeasureSpec);
            mIndexBarViewWidth = mIndexBarView.getMeasuredWidth();
            mIndexBarViewHeight = mIndexBarView.getMeasuredHeight();
        }

        if (mPreviewTextView != null && mPreviewVisibility) {
            measureChild(mPreviewTextView, widthMeasureSpec, heightMeasureSpec);
            mPreviewTextViewWidth = mPreviewTextView.getMeasuredWidth();
            mPreviewTextViewHeight = mPreviewTextView.getMeasuredHeight();
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mHeaderView != null) {
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
            configureHeaderView(getFirstVisiblePosition());
        }

        if (mIndexBarView != null && mIndexBarVisibility) {
            mIndexBarView.layout(getMeasuredWidth() - mIndexBarViewMargin - mIndexBarViewWidth, mIndexBarViewMargin
                    , getMeasuredWidth() - mIndexBarViewMargin, getMeasuredHeight() - mIndexBarViewMargin);
        }

        if (mPreviewTextView != null && mPreviewVisibility) {
            mPreviewTextView.layout(mIndexBarView.getLeft() - mPreviewTextViewWidth, (int) mIndexBarY - (mPreviewTextViewHeight / 2)
                    , mIndexBarView.getLeft(), (int) (mIndexBarY - (mPreviewTextViewHeight / 2)) + mPreviewTextViewHeight);
        }
    }


    public void setIndexBarVisibility(Boolean isVisible) {
        if (isVisible) {
            mIndexBarVisibility = true;
        } else {
            mIndexBarVisibility = false;
        }
    }


    private void setPreviewTextVisibility(Boolean isVisible) {
        if (isVisible) {
            mPreviewVisibility = true;
        } else {
            mPreviewVisibility = false;
        }
    }


    public void configureHeaderView(int position) {
        if (mHeaderView == null) {
            return;
        }

        int state = mAdapter.getPinnedHeaderState(position);

        switch (state) {

            case IPinnedHeader.PINNED_HEADER_GONE:
                mHeaderVisibility = false;
                break;
            case IPinnedHeader.PINNED_HEADER_VISIBLE:
                if (mHeaderView.getTop() != 0) {
                    mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                }
                mAdapter.configurePinnedHeader(mHeaderView, position);
                mHeaderVisibility = true;
                break;
            case IPinnedHeader.PINNED_HEADER_PUSHED_UP:
                View firstView = getChildAt(0);
                int bottom = firstView.getBottom();
                // int itemHeight = firstView.getHeight();
                int headerHeight = mHeaderView.getHeight();
                int y;
                if (bottom < headerHeight) {
                    y = (bottom - headerHeight);
                } else {
                    y = 0;
                }

                if (mHeaderView.getTop() != y) {
                    mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
                }
                mAdapter.configurePinnedHeader(mHeaderView, position);
                mHeaderVisibility = true;
                break;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);// draw list view elements (zIndex == 1)

        if (mHeaderView != null && mHeaderVisibility) {
            drawChild(canvas, mHeaderView, getDrawingTime()); // draw pinned header view (zIndex == 2)
        }
        if (mIndexBarView != null && mIndexBarVisibility) {
            drawChild(canvas, mIndexBarView, getDrawingTime()); // draw index bar view (zIndex == 3)
        }
        if (mPreviewTextView != null && mPreviewVisibility) {
            drawChild(canvas, mPreviewTextView, getDrawingTime()); // draw preview text view (zIndex == 4)
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIndexBarView != null && (mIndexBarView).onTouchEvent(ev)) {
            setPreviewTextVisibility(true);
            return true;
        } else {
            setPreviewTextVisibility(false);
            return super.onTouchEvent(ev);
        }
    }


    @Override
    public void filterList(float indexBarY, int position, String previewText) {
        this.mIndexBarY = indexBarY;

        if (mPreviewTextView instanceof TextView)
            ((TextView) mPreviewTextView).setText(previewText);

        setSelection(position);
    }
}

