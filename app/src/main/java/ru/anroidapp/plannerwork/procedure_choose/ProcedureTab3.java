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

    ArrayList<Integer> mListSectionPosProc;

    ArrayList<String> mListItemsProc;

    PinnedHeaderListView mListViewProc;

    PinnedHeaderAdapter mAdaptorProc;

    EditText mSearchViewProc;

    ProgressBar mLoadingViewProc;

    TextView mEmptyViewProc;

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

        mSearchViewProc = (EditText) relativeLayout.findViewById(R.id.search_proc_view);
        mLoadingViewProc = (ProgressBar) relativeLayout.findViewById(R.id.loading_view);
        mListViewProc = (PinnedHeaderListView) relativeLayout.findViewById(R.id.proc_list_view);
        mEmptyViewProc = (TextView) relativeLayout.findViewById(R.id.empty_view);

        relativeLayout.findViewById(R.id.procedure_tab);

        mListSectionPosProc = new ArrayList<>();
        mListItemsProc = new ArrayList<>();

        // for handling configuration change
        if (savedInstanceState != null) {
            mListItemsProc = savedInstanceState.getStringArrayList("mListItemsProc");
            mListSectionPosProc = savedInstanceState.getIntegerArrayList("mListSectionPosProc");

            if (mListItemsProc != null && mListItemsProc.size() > 0 && mListSectionPosProc != null && mListSectionPosProc.size() > 0) {
                setListAdaptor();
            }

            String constraint = savedInstanceState.getString("constraint");
            if (constraint != null && constraint.length() > 0) {
                mSearchViewProc.setText(constraint);
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
        inflater.inflate(R.menu.procedure_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mSearchViewProc.addTextChangedListener(filterTextWatcher);
        super.onActivityCreated(savedInstanceState);
    }


    private void setListAdaptor() {
        // create instance of PinnedHeaderAdapter and set adapter to list view
        mAdaptorProc = new PinnedHeaderAdapter(fa, mListItemsProc, mListSectionPosProc);
        mListViewProc.setAdapter(mAdaptorProc);

        LayoutInflater inflater = (LayoutInflater) fa.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // set header view
        View pinnedHeaderView = inflater.inflate(R.layout.section_row_view, mListViewProc, false);
        mListViewProc.setPinnedHeaderView(pinnedHeaderView);

        // set index bar view
        IndexBarView indexBarView = (IndexBarView) inflater.inflate(R.layout.index_bar_view, mListViewProc, false);
        indexBarView.setData(mListViewProc, mListItemsProc, mListSectionPosProc);
        mListViewProc.setIndexBarView(indexBarView);

        // set preview text view
        View previewTextView = inflater.inflate(R.layout.preview_view, mListViewProc, false);
        mListViewProc.setPreviewView(previewTextView);

        // for configure pinned header view on scroll change
        mListViewProc.setOnScrollListener(mAdaptorProc);
        mListViewProc.setOnItemClickListener(mClickListener);
    }

    AdapterView.OnItemClickListener mClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            procedureName = mListItemsProc.get(position);
            Toast.makeText(fa.getApplicationContext(), "Выбрана процедура " + procedureName,
                    Toast.LENGTH_SHORT).show();
        }
    };


    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (mAdaptorProc != null)
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
            mListViewProc.setIndexBarVisibility(false);
        } else {
            mListViewProc.setIndexBarVisibility(true);
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
            showLoading(mListViewProc, mLoadingViewProc, mEmptyViewProc);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(ArrayList<String>... params) {
            mListItemsProc.clear();
            mListSectionPosProc.clear();
            ArrayList<String> items = params[0];
            if (mProcedures.size() > 0) {

                // NOT forget to sort array
                Collections.sort(items, new SortIgnoreCase());

                String prev_section = "";
                for (String current_item : items) {
                    String current_section = current_item.substring(0, 1).toUpperCase(Locale.getDefault());

                    if (!prev_section.equals(current_section)) {
                        mListItemsProc.add(current_section);
                        mListItemsProc.add(current_item);
                        // array list of section positions
                        mListSectionPosProc.add(mListItemsProc.indexOf(current_section));
                        prev_section = current_section;
                    } else {
                        mListItemsProc.add(current_item);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!isCancelled()) {
                if (mListItemsProc.size() <= 0) {
                    showEmptyText(mListViewProc, mLoadingViewProc, mEmptyViewProc);
                } else {
                    setListAdaptor();
                    showContent(mListViewProc, mLoadingViewProc, mEmptyViewProc);
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
        if (mListItemsProc != null && mListItemsProc.size() > 0) {
            outState.putStringArrayList("mListItemsProc", mListItemsProc);
        }
        if (mListSectionPosProc != null && mListSectionPosProc.size() > 0) {
            outState.putIntegerArrayList("mListSectionPosProc", mListSectionPosProc);
        }
        String searchText = mSearchViewProc.getText().toString();
        if (searchText.length() > 0) {
            outState.putString("constraint", searchText);
        }
        super.onSaveInstanceState(outState);
    }

}
