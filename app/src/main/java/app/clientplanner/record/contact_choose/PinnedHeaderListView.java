package app.clientplanner.record.contact_choose;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import app.clientplanner.record.contact_choose.intface.IPinnedHeader;
import app.clientplanner.record.contact_choose.intface.PinnedHeaderAdapter;

public class PinnedHeaderListView extends ListView {

    private IPinnedHeader mAdapter;

    private View mHeaderView, mPreviewTextView;

    private boolean mHeaderVisibility = false;
    private boolean mPreviewVisibility = false;

    private Context mContext;

    // view height and width
    private int mHeaderViewWidth,
            mHeaderViewHeight,
            mPreviewTextViewWidth,
            mPreviewTextViewHeight;


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
        if (mHeaderView != null) {
            setFadingEdgeLength(0);
        }
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
        super.dispatchDraw(canvas);

        if (mHeaderView != null && mHeaderVisibility) {
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
        if (mPreviewTextView != null && mPreviewVisibility) {
            drawChild(canvas, mPreviewTextView, getDrawingTime());
        }
    }


}

