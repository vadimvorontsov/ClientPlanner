package ru.anroidapp.clientplanner.record.procedure_choose;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smena.clientbase.procedures.Procedures;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import ru.anroidapp.clientplanner.MetaData;
import ru.anroidapp.clientplanner.R;
import ru.anroidapp.clientplanner.intface_procedure.ProcedureHeaderAdapter;
import ru.anroidapp.clientplanner.intface_procedure.ProcedureHeaderListView;


public class ProcedureTab3 extends Fragment {

    private final String TAG = "ProcedureTab3";
    private ArrayList<String> mProcedures;
    private ArrayList<Integer> mListSectionPosProcedure;
    private ArrayList<Integer> mColorProcedures;
    private ArrayList<String> mListItemsProcedure;
    private ProcedureHeaderListView mListViewProcedure;
    private ProcedureHeaderAdapter mAdaptorProcedure;
    private EditText mSearchViewProcedure;
    private ProgressBar mLoadingViewProcedure;
    private TextView mEmptyViewProcedure;
    private LinearLayout mSearchLayout, mCancelSearchLayout;
    private FloatingActionButton mFab;
    private FragmentActivity mContext;
    private MetaData mMetaData;
    private int mColorChoice;
    private int mPencilPosition;
    private long mProcedureIdTmp;
    private String[] data = {"one", "two", "three", "four"};
    private TextView lastChoose;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mContext = super.getActivity();
        mMetaData = (MetaData) getArguments().getSerializable(MetaData.TAG);
        mColorChoice = 0;
        mPencilPosition = 0;
        mProcedureIdTmp = 0;

        RelativeLayout relativeLayout = (RelativeLayout)
                inflater.inflate(R.layout.procedure_tab, container, false);

        mProcedures = new ArrayList<>();
        mColorProcedures = new ArrayList<>();
        getColorProcedures();
        getProcedures();

        mSearchViewProcedure = (EditText) relativeLayout.findViewById(R.id.SearchViewProc);
        mLoadingViewProcedure = (ProgressBar) relativeLayout.findViewById(R.id.loading_view);
        mListViewProcedure = (ProcedureHeaderListView) relativeLayout.findViewById(R.id.proc_list_view);
        mEmptyViewProcedure = (TextView) relativeLayout.findViewById(R.id.empty_view);
        mSearchLayout = (LinearLayout) relativeLayout.findViewById(R.id.LaySearchProc);
        mCancelSearchLayout = (LinearLayout) relativeLayout.findViewById(R.id.LayCanselSearchProc);

        mFab = (FloatingActionButton) relativeLayout.findViewById(R.id.fab_proc);
        mFab.attachToListView(mListViewProcedure);
        mFab.setOnClickListener(new OnFabClickListener());
        mCancelSearchLayout.setOnClickListener(new OnSearchCloseListener());

        mSearchLayout.setVisibility(View.GONE);

        relativeLayout.findViewById(R.id.procedure_tab);

        mListSectionPosProcedure = new ArrayList<>();
        mListItemsProcedure = new ArrayList<>();

        // for handling configuration change
        if (savedInstanceState != null) {
            mListItemsProcedure = savedInstanceState.getStringArrayList("mListItemsProcedure");
            mListSectionPosProcedure = savedInstanceState.
                    getIntegerArrayList("mListSectionPosProcedure");

            if (mListItemsProcedure != null && mListItemsProcedure.size() > 0
                    && mListSectionPosProcedure != null && mListSectionPosProcedure.size() > 0) {
                setListAdaptor();
            }

            String constraint = savedInstanceState.getString("constraint");
            if (constraint != null && constraint.length() > 0) {
                mSearchViewProcedure.setText(constraint);
                setIndexBarViewVisibility(constraint);
            }

        } else {
            new Populate().execute(mProcedures);
        }

        setHasOptionsMenu(true);

        if (mListItemsProcedure != null && mListItemsProcedure.size() < 6)
            mFab.setVisibility(View.GONE);

        return relativeLayout;
    }

    private void getProcedures() {
        Procedures procedures = new Procedures(mContext);
        mProcedures = procedures.getAllProceduresNames();
    }

    private void getColorProcedures() {
        Procedures procedures = new Procedures(mContext);
        mColorProcedures = procedures.getAllProceduresColor();
    }

    private void refreshList() {
        getProcedures();
        new Populate().execute(mProcedures);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.procedure_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.add_procedure, null);
        final EditText nameEditText = (EditText) view.findViewById(R.id.input_proc_name);
        final EditText priceEditText = (EditText) view.findViewById(R.id.input_proc_price);
        final EditText noteEditText = (EditText) view.findViewById(R.id.input_proc_note);

        Spinner spinner = (Spinner) view.findViewById(R.id.spin_proc_color);
        MyCustomAdapter adapter = new MyCustomAdapter(mContext, R.layout.row, data);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        switch (item.getItemId()) {
            case R.id.action_pencil:
                if (mPencilPosition == 0) {
                    Toast.makeText(mContext,
                            mContext.getResources().getString(R.string.select_service),
                            Toast.LENGTH_SHORT).show();
                    break;
                }

                Procedures procedures = new Procedures(mContext);
                String procNameTmp = mListItemsProcedure.get(mPencilPosition);
                mProcedureIdTmp = procedures.getProcedureID(procNameTmp);
                Object[] procInfoTmp = procedures.getProcedureInfo(mProcedureIdTmp);
                Integer procPriceTmp = (Integer) procInfoTmp[1];
                String procNoteTmp = (String) procInfoTmp[2];
                Integer procColorTmp = (Integer) procInfoTmp[3];

                spinner.setAdapter(adapter);
                spinner.setSelection(procColorTmp, true);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Toast toast = Toast.makeText(mContext,
                                mContext.getResources().getString(R.string.your_choice) + " "
                                        + position, Toast.LENGTH_SHORT);
                        mColorChoice = position;
                        toast.show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                nameEditText.setText(procNameTmp);
                priceEditText.setText(procPriceTmp + "");
                noteEditText.setText(procNoteTmp);
                mColorChoice = procColorTmp;

                builder.setView(view)
                        .setCancelable(true);
                builder.setPositiveButton(mContext.getResources().getString(R.string.change),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = nameEditText.getText().toString();
                        Integer price = 0;

                        if (!priceEditText.getText().toString().isEmpty())
                            price = Integer.parseInt(priceEditText.getText().toString());

                        String note = noteEditText.getText().toString();
                        if (note.isEmpty())
                            note = mContext.getResources().getString(R.string.no_notes);

                        Integer color = mColorChoice;

                        if (!name.isEmpty()) {
                            Procedures procedures_update = new Procedures(mContext);
                            int test = procedures_update.getUpdateProcedure(mProcedureIdTmp + "",
                                    name, price, note, color);
                            if (test == 1) {
                                Toast.makeText(mContext,
                                        mContext.getResources().getString(R.string.service_changed),
                                        Toast.LENGTH_SHORT).show();
                                refreshList();
                            }
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

                break;

            case R.id.add_procedure:

                spinner.setAdapter(adapter);
                spinner.setSelection(0, true);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Toast toast = Toast.makeText(mContext,
                                mContext.getResources().getString(R.string.your_choice) + " "
                                        + position, Toast.LENGTH_SHORT);
                        mColorChoice = position;
                        toast.show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                builder.setView(view)
                        .setCancelable(true);
                builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = nameEditText.getText().toString();
                        Integer price = 0;

                        if (!priceEditText.getText().toString().isEmpty())
                            price = Integer.parseInt(priceEditText.getText().toString());

                        String note = noteEditText.getText().toString();
                        if (note.isEmpty())
                            note = mContext.getResources().getString(R.string.no_notes);

                        if (!name.isEmpty()) {
                            long id = 0;
                            Procedures procedures = new Procedures(mContext);
                            id = procedures.addProcedure(name, price, note, mColorChoice);
                            if (id != 0) {
                                Toast.makeText(mContext,
                                        mContext.getResources().getString(R.string.service_added),
                                        Toast.LENGTH_SHORT).show();
                                refreshList();
                            }

                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mSearchViewProcedure.addTextChangedListener(new TextWatch());
        super.onActivityCreated(savedInstanceState);
    }

    private void setListAdaptor() {

        mAdaptorProcedure = new ProcedureHeaderAdapter(mContext, mListItemsProcedure,
                mListSectionPosProcedure, mColorProcedures);
        mListViewProcedure.setAdapter(mAdaptorProcedure);

        LayoutInflater inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View pinnedHeaderView = inflater.inflate(R.layout.section_row_view,
                mListViewProcedure, false);
        mListViewProcedure.setPinnedHeaderView(pinnedHeaderView);

        View previewTextView = inflater.inflate(R.layout.preview_view, mListViewProcedure, false);
        mListViewProcedure.setPreviewView(previewTextView);

        mListViewProcedure.setOnScrollListener(mAdaptorProcedure);
        mListViewProcedure.setOnItemClickListener(new OnItemClickListener());
        mListViewProcedure.setOnItemLongClickListener(new OnItemLongClickListener());
    }

    private void setIndexBarViewVisibility(String constraint) {
        if (constraint != null && constraint.length() > 0) {
            mListViewProcedure.setIndexBarVisibility(false);
        } else {
            mListViewProcedure.setIndexBarVisibility(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mListItemsProcedure != null && mListItemsProcedure.size() > 0) {
            outState.putStringArrayList("mListItemsProcedure", mListItemsProcedure);
        }
        if (mListSectionPosProcedure != null && mListSectionPosProcedure.size() > 0) {
            outState.putIntegerArrayList("mListSectionPosProcedure", mListSectionPosProcedure);
        }
        String searchText = mSearchViewProcedure.getText().toString();
        if (searchText.length() > 0) {
            outState.putString("constraint", searchText);
        }
        super.onSaveInstanceState(outState);
    }

    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourcedId, String[] objects) {
            super(context, textViewResourcedId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.color_circle);
            label.setText(data[position]);
            label.setVisibility(View.GONE);

            ImageView icon = (ImageView) row.findViewById(R.id.icon_circle);

            if (data[position].equals("one")) {
                icon.setImageResource(R.mipmap.ic_launcher_blue);
                //icon.setImageResource(R.mipmap.ic_launcher);
            } else if (data[position].equals("two")) {
                icon.setImageResource(R.mipmap.ic_launcher_orange);
                //icon.setImageResource(R.mipmap.ic_launcher);
            } else if (data[position].equals("three")) {
                icon.setImageResource(R.mipmap.ic_launcher_green);
                //icon.setImageResource(R.mipmap.ic_launcher);
            } else if (data[position].equals("four")) {
                icon.setImageResource(R.mipmap.ic_launcher_red);
                //icon.setImageResource(R.mipmap.ic_launcher);
            }
            return row;
        }
    }

    private class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String constraintStr = constraint.toString().toLowerCase(Locale.getDefault());
            Filter.FilterResults result = new FilterResults();

            if (constraint.toString().length() > 0) {
                ArrayList<String> filterItems = new ArrayList<>();

                synchronized (this) {
                    LOOP_FOR_PROCEDURES:
                    for (String item : mProcedures) {
                        String[] surNames = item.split(" ");
                        LOOP_FOR_SURNAMES:
                        for (String surName : surNames) {
                            if (surName.toLowerCase(Locale.getDefault()).startsWith(constraintStr)) {
                                filterItems.add(item);
                                break LOOP_FOR_SURNAMES;
                            }
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
            new Populate().execute(filtered);
        }
    }

    private class Populate extends AsyncTask<ArrayList<String>, Void, Void> {

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
            showLoading(mListViewProcedure, mLoadingViewProcedure, mEmptyViewProcedure);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(ArrayList<String>... params) {
            mListItemsProcedure.clear();
            mListSectionPosProcedure.clear();
            ArrayList<String> items = params[0];
            if (mProcedures.size() > 0) {

                // NOT forget to sort array
                Collections.sort(items, new SortIgnoreCase());

                String prev_section = "";
                for (String current_item : items) {
                    String current_section = current_item.substring(0, 1).toUpperCase(Locale.getDefault());

                    if (!prev_section.equals(current_section)) {
                        mListItemsProcedure.add(current_section);
                        mListItemsProcedure.add(current_item);
                        mListSectionPosProcedure.add(mListItemsProcedure.indexOf(current_section));
                        prev_section = current_section;
                    } else {
                        mListItemsProcedure.add(current_item);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!isCancelled()) {
                if (mListItemsProcedure.size() <= 0) {
                    showEmptyText(mListViewProcedure, mLoadingViewProcedure, mEmptyViewProcedure);
                } else {
                    setListAdaptor();
                    showContent(mListViewProcedure, mLoadingViewProcedure, mEmptyViewProcedure);
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

    private class OnFabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mSearchLayout.setVisibility(View.VISIBLE);
            mFab.hide();
            mSearchViewProcedure.requestFocus();
            InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(mSearchViewProcedure, 0);
        }
    }

    private class OnSearchCloseListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mSearchLayout.setVisibility(View.GONE);
            mFab.show();
            InputMethodManager inputManager = (InputMethodManager) getActivity().
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(mSearchViewProcedure.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class OnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            Resources resources = getResources();
            Procedures procedures = new Procedures(mContext);

            String procNameTmp = mListItemsProcedure.get(position);
            long procIdTmp = procedures.getProcedureID(procNameTmp);
            Object[] procInfoTmp = procedures.getProcedureInfo(procIdTmp);
            Integer procPriceTmp = (Integer) procInfoTmp[1];
            String procNoteTmp = (String) procInfoTmp[2];

            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.procedure_inf)
                    .setMessage(resources.getString(R.string.service) + ": " + procNameTmp + "\n" +
                            resources.getString(R.string.price) + ": " + procPriceTmp + "\n" +
                            resources.getString(R.string.note) + ": " + procNoteTmp)
                    .setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
            return true;
        }
    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mPencilPosition = position;
            Procedures procedures = new Procedures(mContext);

            String procedureName = mListItemsProcedure.get(position);
            mMetaData.setProcedureName(procedureName);
            long procedureId = procedures.getProcedureID(procedureName);
            Object[] procedureInfo = procedures.getProcedureInfo(procedureId);
            Integer procedurePrice = (Integer) procedureInfo[1];
            mMetaData.setProcedurePrice(procedurePrice);
            String procedureNote = (String) procedureInfo[2];
            mMetaData.setProcedureNote(procedureNote);

            if (lastChoose != null) {
                lastChoose.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        }
    }

    private class TextWatch implements TextWatcher {
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (mAdaptorProcedure != null)
                (new ListFilter()).filter(str);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    }

}
