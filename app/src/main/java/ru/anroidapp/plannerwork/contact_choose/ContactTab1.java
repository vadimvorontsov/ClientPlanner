package ru.anroidapp.plannerwork.contact_choose;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import ru.anroidapp.plannerwork.R;
import ru.anroidapp.plannerwork.contact_choose.intface.PinnedHeaderAdapter;


/**
 * Created by Артём on 14.06.2015.
 */
public class ContactTab1 extends Fragment {

    ArrayList<String> mItems;

    ArrayList<Integer> mListSectionPos;

    ArrayList<String> mListItems;

    PinnedHeaderListView mListView;

    PinnedHeaderAdapter mAdaptor;

    EditText mSearchView;

    ProgressBar mLoadingView;

    TextView mEmptyView;

    private final String TAG = "ContactTab1";

    static String name;
    ArrayList<String> phonesToChoice = new ArrayList<>();
    ArrayList<String> emailsToChoice = new ArrayList<>();

    FragmentActivity fa;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fa = super.getActivity();

        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.contact_tab, container, false);
        mItems = new ArrayList<>();

        /*---------------------------------------------*/
        Cursor cursor = null;
        try {
            cursor = fa.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if (!name.isEmpty()) {
                    mItems.add(name);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        /*----------------------------------------------*/

        mSearchView = (EditText) relativeLayout.findViewById(R.id.search_view);
        mLoadingView = (ProgressBar) relativeLayout.findViewById(R.id.loading_view);
        mListView = (PinnedHeaderListView) relativeLayout.findViewById(R.id.list_view);
        mEmptyView = (TextView) relativeLayout.findViewById(R.id.empty_view);

        relativeLayout.findViewById(R.id.contact_tab);

        mListSectionPos = new ArrayList<Integer>();
        mListItems = new ArrayList<String>();

        // for handling configuration change
        if (savedInstanceState != null) {
            mListItems = savedInstanceState.getStringArrayList("mListItems");
            mListSectionPos = savedInstanceState.getIntegerArrayList("mListSectionPos");

            if (mListItems != null && mListItems.size() > 0 && mListSectionPos != null && mListSectionPos.size() > 0) {
                setListAdaptor();
            }

            String constraint = savedInstanceState.getString("constraint");
            if (constraint != null && constraint.length() > 0) {
                mSearchView.setText(constraint);
                setIndexBarViewVisibility(constraint);
            }

        } else {
            new Poplulate().execute(mItems);
        }

        setHasOptionsMenu(true);

        return relativeLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mSearchView.addTextChangedListener(filterTextWatcher);
        super.onActivityCreated(savedInstanceState);
    }


    private void setListAdaptor() {
        // create instance of PinnedHeaderAdapter and set adapter to list view
        mAdaptor = new PinnedHeaderAdapter(fa, mListItems, mListSectionPos);
        mListView.setAdapter(mAdaptor);

        LayoutInflater inflater = (LayoutInflater) fa.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // set header view
        View pinnedHeaderView = inflater.inflate(R.layout.section_row_view, mListView, false);
        mListView.setPinnedHeaderView(pinnedHeaderView);

        // set index bar view
        IndexBarView indexBarView = (IndexBarView) inflater.inflate(R.layout.index_bar_view, mListView, false);
        indexBarView.setData(mListView, mListItems, mListSectionPos);
        mListView.setIndexBarView(indexBarView);

        // set preview text view
        View previewTextView = inflater.inflate(R.layout.preview_view, mListView, false);
        mListView.setPreviewView(previewTextView);

        // for configure pinned header view on scroll change
        mListView.setOnScrollListener(mAdaptor);
        mListView.setOnItemClickListener(mClickListener);
    }

    AdapterView.OnItemClickListener mClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            name = mListItems.get(position).toString();
            Toast.makeText(fa.getApplicationContext(), "Выбран контакт " + name,
                    Toast.LENGTH_SHORT).show();
            getPhonesByName(fa, name);
            getEmailsByName(fa, name);
        }
    };

    private void getPhonesByName(Context context, String name) {

        Cursor cursor = null;

        try {
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" +
                    name + "%'";
            String[] column = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            cursor = context.getContentResolver().
                    query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, column,
                            selection, null, null);

            while (cursor.moveToNext()) {
                phonesToChoice.add(cursor.getString(cursor.getColumnIndex
                        (ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private void getEmailsByName(Context context, String name) {

        Cursor cursor = null;

        try {
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" +
                    name + "%'";
            String[] column = new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS};
            cursor = context.getContentResolver().
                    query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, column,
                            selection, null, null);

            while (cursor.moveToNext()) {
                emailsToChoice.add(cursor.getString(cursor.getColumnIndex
                        (ContactsContract.CommonDataKinds.Email.ADDRESS)));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (mAdaptor != null && str != null)
                (new ListFilter()).filter(str);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    };


    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread,
            // and
            // not the UI thread.
            String constraintStr = constraint.toString().toLowerCase(Locale.getDefault());
            Filter.FilterResults result = new FilterResults();

            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<String> filterItems = new ArrayList<>();

                synchronized (this) {
                    for (String item : mItems) {
                        if (item.toLowerCase(Locale.getDefault()).startsWith(constraintStr)) {
                            filterItems.add(item);
                        }
                    }
                    result.count = filterItems.size();
                    result.values = filterItems;
                }
            } else {
                synchronized (this) {
                    result.count = mItems.size();
                    result.values = mItems;
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<String> filtered = (ArrayList<String>) results.values;
            setIndexBarViewVisibility(constraint.toString());
            // sort array and extract sections in background Thread
            new Poplulate().execute(filtered);
        }

    }

    private void setIndexBarViewVisibility(String constraint) {
        // hide index bar for search results
        if (constraint != null && constraint.length() > 0) {
            mListView.setIndexBarVisibility(false);
        } else {
            mListView.setIndexBarVisibility(true);
        }
    }

    private class Poplulate extends AsyncTask<ArrayList<String>, Void, Void> {

        /**
         * вызывается до начала выполнения AsyncTask.Отображает только загруку
         *
         * @param contentView отображение содержимого
         * @param loadingView отображение загрузки
         * @param emptyView   отображение пустого списка
         */
        private void showLoading(View contentView, View loadingView, View emptyView) {
            contentView.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        /**
         * отображает только содержимое
         *
         * @param contentView
         * @param loadingView
         * @param emptyView
         */
        private void showContent(View contentView, View loadingView, View emptyView) {
            contentView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }

        /**
         * отображает пустой список
         *
         * @param contentView
         * @param loadingView
         * @param emptyView
         */
        private void showEmptyText(View contentView, View loadingView, View emptyView) {
            contentView.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            // show loading indicator
            showLoading(mListView, mLoadingView, mEmptyView);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(ArrayList<String>... params) {
            mListItems.clear();
            mListSectionPos.clear();
            ArrayList<String> items = params[0];
            if (mItems.size() > 0) {

                // NOT forget to sort array
                Collections.sort(items, new SortIgnoreCase());

                String prev_section = "";
                for (String current_item : items) {
                    String current_section = current_item.substring(0, 1).toUpperCase(Locale.getDefault());

                    if (!prev_section.equals(current_section)) {
                        mListItems.add(current_section);
                        mListItems.add(current_item);
                        // array list of section positions
                        mListSectionPos.add(mListItems.indexOf(current_section));
                        prev_section = current_section;
                    } else {
                        mListItems.add(current_item);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!isCancelled()) {
                if (mListItems.size() <= 0) {
                    showEmptyText(mListView, mLoadingView, mEmptyView);
                } else {
                    setListAdaptor();
                    showContent(mListView, mLoadingView, mEmptyView);
                }
            }
            super.onPostExecute(result);
        }
    }

    public class SortIgnoreCase implements Comparator<String> {
        public int compare(String s1, String s2) {
            return s1.compareToIgnoreCase(s2);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mListItems != null && mListItems.size() > 0) {
            outState.putStringArrayList("mListItems", mListItems);
        }
        if (mListSectionPos != null && mListSectionPos.size() > 0) {
            outState.putIntegerArrayList("mListSectionPos", mListSectionPos);
        }
        String searchText = mSearchView.getText().toString();
        if (searchText != null && searchText.length() > 0) {
            outState.putString("constraint", searchText);
        }
        super.onSaveInstanceState(outState);
    }

}
