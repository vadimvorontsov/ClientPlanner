package ru.anroidapp.plannerwork.contact_choose.intface;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import ru.anroidapp.plannerwork.CircularImageView;
import ru.anroidapp.plannerwork.R;
import ru.anroidapp.plannerwork.contact_choose.PinnedHeaderListView;

public class PinnedHeaderAdapter extends BaseAdapter implements AbsListView.OnScrollListener, IPinnedHeader, Filterable {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SECTION = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SECTION + 1;

    LayoutInflater mLayoutInflater;
    int mCurrentSectionPosition = 0, mNextSectionPosition = 0;

    // array list to store section positions
    ArrayList<Integer> mListSectionPos;

    // array list to store list view data
    ArrayList<String> mListItems;

    // context object
    Context mContext;


    public PinnedHeaderAdapter(Context context, ArrayList<String> listItems,
                               ArrayList<Integer> listSectionPos) {

        this.mContext = context;
        this.mListItems = listItems;
        this.mListSectionPos = listSectionPos;

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return !mListSectionPos.contains(position);
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return mListSectionPos.contains(position) ? TYPE_SECTION : TYPE_ITEM;
    }

    @Override
    public Object getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mListItems.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            int type = getItemViewType(position);

            switch (type) {
                case TYPE_ITEM:
                    convertView = mLayoutInflater.inflate(R.layout.contact_row_view, null);
//                    holder.contactPhoto = (CircularImageView) convertView.findViewById(R.id.contact_circle);
//                    Drawable drawable = mContext.getDrawable(R.drawable.header);
//                    holder.contactPhoto.setImageDrawable(drawable);
                    convertView.setOnLongClickListener(qqq);
                    convertView.setOnClickListener(eee);
                    break;
                case TYPE_SECTION:
                    convertView = mLayoutInflater.inflate(R.layout.contact_section_row_view, null);
                    break;
            }

            holder.textView = (TextView) convertView.findViewById(R.id.row_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(mListItems.get(position).toString());

        return convertView;
    }

    View.OnLongClickListener qqq = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            //Toast.makeText(mContext, "долгий клик", Toast.LENGTH_SHORT).show();
            return false;
        }
    };

    View.OnClickListener eee = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Toast.makeText(mContext, "обычный клик", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public int getPinnedHeaderState(int position) {
        // hide pinned header when items count is zero OR position is less than
        // zero OR
        // there is already a header in list view
        if (getCount() == 0 || position < 0 || mListSectionPos.indexOf(position) != -1) {
            return PINNED_HEADER_GONE;
        }

        // the header should get pushed up if the top item shown
        // is the last item in a section for a particular letter.
        mCurrentSectionPosition = getCurrentSectionPosition(position);
        mNextSectionPosition = getNextSectionPosition(mCurrentSectionPosition);
        if (mNextSectionPosition != -1 && position == mNextSectionPosition - 1) {
            return PINNED_HEADER_PUSHED_UP;
        }

        return PINNED_HEADER_VISIBLE;
    }

    public int getCurrentSectionPosition(int position) {
        String listChar = mListItems.get(position).toString().substring(0, 1).toUpperCase(Locale.getDefault());
        return mListItems.indexOf(listChar);
    }

    public int getNextSectionPosition(int currentSectionPosition) {
        int index = mListSectionPos.indexOf(currentSectionPosition);
        if ((index + 1) < mListSectionPos.size()) {
            return mListSectionPos.get(index + 1);
        }
        return mListSectionPos.get(index);
    }

    @Override
    public void configurePinnedHeader(View v, int position) {
        // set text in pinned header
        TextView header = (TextView) v;
        mCurrentSectionPosition = getCurrentSectionPosition(position);
        header.setText(mListItems.get(mCurrentSectionPosition));
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public static class ViewHolder {
        public TextView textView;
        public CircularImageView contactPhoto;
    }
}