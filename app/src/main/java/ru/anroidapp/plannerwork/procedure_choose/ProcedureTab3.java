package ru.anroidapp.plannerwork.procedure_choose;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smena.clientbase.procedures.Procedures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import ru.anroidapp.plannerwork.R;
import ru.anroidapp.plannerwork.contact_choose.IndexBarView;
import ru.anroidapp.plannerwork.contact_choose.PinnedHeaderListView;
import ru.anroidapp.plannerwork.contact_choose.intface.PinnedHeaderAdapter;


public class ProcedureTab3 extends Fragment {

    ArrayList<String> mProcedures;

    ArrayList<Integer> mListSectionPos;

    ArrayList<String> mListItems;

    PinnedHeaderListView mListView;

    PinnedHeaderAdapter mAdaptor;

    EditText mSearchView;

    ProgressBar mLoadingView;

    TextView mEmptyView;
    TextView mHistoryLabel;
    TextView mContactLabel;

    Switch mSwitcher;

    private final String TAG = "ProcedureTab3";

    static String procedureName;

    FragmentActivity fa;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fa = super.getActivity();

        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.procedure_tab, container, false);
        mProcedures = new ArrayList<>();

        Procedures procedures = new Procedures(fa);
        mProcedures = procedures.getAllProceduresNames();


        mSearchView = (EditText) relativeLayout.findViewById(R.id.search_proc_view);
        mLoadingView = (ProgressBar) relativeLayout.findViewById(R.id.loading_view);
        mListView = (PinnedHeaderListView) relativeLayout.findViewById(R.id.proc_list_view);
        mEmptyView = (TextView) relativeLayout.findViewById(R.id.empty_view);

        relativeLayout.findViewById(R.id.procedure_tab);

        mListSectionPos = new ArrayList<>();
        mListItems = new ArrayList<>();

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
            new Poplulate().execute(mProcedures);
        }

        setHasOptionsMenu(true);

        return relativeLayout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        /*меню*/

        super.onCreateOptionsMenu(menu, inflater);
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
            procedureName = mListItems.get(position);
            Toast.makeText(fa.getApplicationContext(), "Выбрана процедура " + procedureName,
                    Toast.LENGTH_SHORT).show();
        }
    };


    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (mAdaptor != null)
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

            if (constraint.toString().length() > 0) {
                ArrayList<String> filterItems = new ArrayList<>();

                synchronized (this) {
                    for (String item : mProcedures) {
                        if (item.toLowerCase(Locale.getDefault()).startsWith(constraintStr)) {
                            filterItems.add(item);
                        }
                    }
                    result.count = filterItems.size();
                    result.values = filterItems;
                }
            } else {
                synchronized (this) {
                    result.count = mProcedures.size();
                    result.values = mProcedures;
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

        private void showLoading(View contentView, View loadingView, View emptyView) {
            contentView.setVisibility(View.GONE);
            loadingView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        private void showContent(View contentView, View loadingView, View emptyView) {
            contentView.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }

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
            if (mProcedures.size() > 0) {

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
        if (searchText.length() > 0) {
            outState.putString("constraint", searchText);
        }
        super.onSaveInstanceState(outState);
    }

}
