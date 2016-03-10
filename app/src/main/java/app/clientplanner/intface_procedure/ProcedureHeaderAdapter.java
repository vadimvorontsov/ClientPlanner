package app.clientplanner.intface_procedure;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.clientplanner.R;
import lib.clientbase.procedures.Procedures;

public class ProcedureHeaderAdapter extends BaseAdapter implements AbsListView.OnScrollListener,
        IPinnedHeaderProcedure, Filterable {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SECTION = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SECTION + 1;

    LayoutInflater mLayoutInflater;
    int mCurrentSectionPosition = 0, mNextSectionPosition = 0;

    // array list to store section positions
    ArrayList<Integer> mListSectionPos;

    // array list to store list view data
    List<String> mListItems;

    // context object
    Context mContext;

    int testColor = 0;
    int testPrice = 0;

    ArrayList<Integer> mColorProcedures;

    int[] arColorProcedures = {R.mipmap.ic_launcher_blue, R.mipmap.ic_launcher_orange,
            R.mipmap.ic_launcher_green, R.mipmap.ic_launcher_red};


    public ProcedureHeaderAdapter(Context context, List<String> listItems,
                                  ArrayList<Integer> listSectionPos, ArrayList<Integer> colorProcedures) {
        this.mContext = context;
        this.mListItems = listItems;
        this.mListSectionPos = listSectionPos;
        this.mColorProcedures = colorProcedures;

        //mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutInflater = LayoutInflater.from(mContext);
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

        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            int type = getItemViewType(position);

            switch (type) {
                case TYPE_ITEM:
                    convertView = mLayoutInflater.inflate(R.layout.procedure_row_view, null);

                    //holder.textView = (TextView) convertView.findViewById(R.id.row_title);
                    holder.textPrice = (TextView) convertView.findViewById(R.id.contact_status);
                    holder.colorRes = (ImageView) convertView.findViewById(R.id.procedure_circle);
                    break;
                case TYPE_SECTION:
                    convertView = mLayoutInflater.inflate(R.layout.section_row_view, null);
                    break;
            }
            holder.textView = (TextView) convertView.findViewById(R.id.row_title);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(mListItems.get(position).toString());
        int length = mListItems.get(position).toString().length();

        if (length > 1) {
            String name = mListItems.get(position).toString();
            testPrice = getPriceProcedureName(name);
            testColor = getColorProceduresName(name);
            holder.textPrice.setText("Цена: " + testPrice);
            holder.colorRes.setImageResource(arColorProcedures[testColor]);
        }

        return convertView;
    }

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
        if (view instanceof ProcedureHeaderListView) {
            ((ProcedureHeaderListView) view).configureHeaderView(firstVisibleItem);
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

    private int getColorProceduresName( String name ) {
        Procedures procedures = new Procedures(mContext);
        int color = procedures.getColorProcedureByName(name);
        return color;
    }

    private int getPriceProcedureName ( String name ){
        Procedures procedures = new Procedures(mContext);
        int price = procedures.getPriceProcedureByName(name);
        return price;
    }

    public static class ViewHolder {
        public TextView textView;
        public TextView textPrice;
        public ImageView colorRes;
    }

}