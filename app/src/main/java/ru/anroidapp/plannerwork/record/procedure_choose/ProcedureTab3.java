package ru.anroidapp.plannerwork.record.procedure_choose;

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

import ru.anroidapp.plannerwork.MetaData;
import ru.anroidapp.plannerwork.R;
import ru.anroidapp.plannerwork.intface_procedure.ProcedureHeaderAdapter;
import ru.anroidapp.plannerwork.intface_procedure.ProcedureHeaderListView;


public class ProcedureTab3 extends Fragment {

    private final String TAG = "ProcedureTab3";
    ArrayList<String> mProcedures;
    ArrayList<Integer> mListSectionPosProc;
    ArrayList<Integer> mColorProcedures;
    ArrayList<String> mListItemsProc;
    ProcedureHeaderListView mListViewProc;
    ProcedureHeaderAdapter mAdaptorProc;
    EditText mSearchViewProc;
    ProgressBar mLoadingViewProc;
    TextView mEmptyViewProc;
    LinearLayout laySearch, layCanselSearch;
    FloatingActionButton fab;
    FragmentActivity fa;
    MetaData mMetaData;
    int choiceColor = 0;
    int position_pencil = 0;
    long procIdTmp = 0;
    String[] data = {"one", "two", "three", "four"};
    View.OnClickListener oclFabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            laySearch.setVisibility(View.VISIBLE);
            fab.hide();
            mSearchViewProc.requestFocus();
            InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(mSearchViewProc, 0);
            Toast.makeText(fa, "Проверка fab", Toast.LENGTH_SHORT).show();
        }
    };
    View.OnClickListener oclCloseSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            laySearch.setVisibility(View.GONE);
            fab.show();
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(mSearchViewProc.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        }
    };
    AdapterView.OnItemLongClickListener mLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            Resources resources = getResources();
            Procedures procedures = new Procedures(fa);

            String procNameTmp = mListItemsProc.get(position);
            long procIdTmp = procedures.getProcedureID(procNameTmp);
            Object[] procInfoTmp = procedures.getProcedureInfo(procIdTmp);
            Integer procPriceTmp = (Integer) procInfoTmp[1];
            String procNoteTmp = (String) procInfoTmp[2];

//            new MaterialDialog.Builder(fa)
//                    .title(R.string.procedure_inf)
//                    .content(resources.getString(R.string.procedure) + ": " + procNameTmp + "\n" +
//                            resources.getString(R.string.price) + ": " + procPriceTmp + "\n" +
//                            resources.getString(R.string.note) + ": " + procNoteTmp)
//                    .positiveText(R.string.back)
//                    .show();
            new AlertDialog.Builder(fa)
                    .setTitle(R.string.procedure_inf)
                    .setMessage(resources.getString(R.string.procedure) + ": " + procNameTmp + "\n" +
                            resources.getString(R.string.price) + ": " + procPriceTmp + "\n" +
                            resources.getString(R.string.note) + ": " + procNoteTmp)
                    .setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
            return true;
        }
    };
    private TextView lastChoose;
    AdapterView.OnItemClickListener mClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            position_pencil = position;
            Procedures procedures = new Procedures(fa);

            String procedureName = mListItemsProc.get(position);
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

            //         TextView textView = (TextView) view;
            //          textView.setCompoundDrawablesWithIntrinsicBounds
            //                  (R.drawable.btn_check_buttonless_on, 0, 0, 0);
            //          lastChoose = textView;

            Toast.makeText(fa.getApplicationContext(), "Выбрана процедура " + procedureName + "\n"
                            + "цена " + procedurePrice + "\n" + "примечание " + procedureNote,
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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fa = super.getActivity();
        mMetaData = (MetaData) getArguments().getSerializable(MetaData.TAG);

        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.procedure_tab, container, false);

        mProcedures = new ArrayList<>();
        mColorProcedures = new ArrayList<>();
        getColorProcedures();
        getProcedures();

        mSearchViewProc = (EditText) relativeLayout.findViewById(R.id.SearchViewProc);
        mLoadingViewProc = (ProgressBar) relativeLayout.findViewById(R.id.loading_view);
        mListViewProc = (ProcedureHeaderListView) relativeLayout.findViewById(R.id.proc_list_view);
        mEmptyViewProc = (TextView) relativeLayout.findViewById(R.id.empty_view);
        laySearch = (LinearLayout) relativeLayout.findViewById(R.id.LaySearchProc);
        layCanselSearch = (LinearLayout) relativeLayout.findViewById(R.id.LayCanselSearchProc);

        fab = (FloatingActionButton) relativeLayout.findViewById(R.id.fab_proc);
        fab.attachToListView(mListViewProc);
        fab.setOnClickListener(oclFabClick);
        layCanselSearch.setOnClickListener(oclCloseSearch);

        laySearch.setVisibility(View.GONE);

        relativeLayout.findViewById(R.id.procedure_tab);

        mListSectionPosProc = new ArrayList<>();
        mListItemsProc = new ArrayList<>();

        // for handling configuration change
        if (savedInstanceState != null) {
            mListItemsProc = savedInstanceState.getStringArrayList("mListItemsProc");
            mListSectionPosProc = savedInstanceState.getIntegerArrayList("mListSectionPosProc");

            if (mListItemsProc != null && mListItemsProc.size() > 0
                    && mListSectionPosProc != null && mListSectionPosProc.size() > 0) {
                setListAdaptor();
            }

            String constraint = savedInstanceState.getString("constraint");
            if (constraint != null && constraint.length() > 0) {
                mSearchViewProc.setText(constraint);
                setIndexBarViewVisibility(constraint);
            }

        } else {
            new Populate().execute(mProcedures);
        }

        setHasOptionsMenu(true);

        //hide fab if procedure < 5
        if (mListItemsProc != null && mListItemsProc.size() < 6)
            fab.setVisibility(View.GONE);

        return relativeLayout;
    }

    private void getProcedures() {
        Procedures procedures = new Procedures(fa);
        mProcedures = procedures.getAllProceduresNames();
    }

    private void getColorProcedures() {
        Procedures procedures = new Procedures(fa);
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

        LayoutInflater inflater = (LayoutInflater) fa.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.add_procedure, null);
        final EditText nameEditText = (EditText) view.findViewById(R.id.input_proc_name);
        final EditText priceEditText = (EditText) view.findViewById(R.id.input_proc_price);
        final EditText noteEditText = (EditText) view.findViewById(R.id.input_proc_note);

        Spinner spinner = (Spinner) view.findViewById(R.id.spin_proc_color);
        MyCustomAdapter adapter = new MyCustomAdapter(fa, R.layout.row, data);

        AlertDialog.Builder builder = new AlertDialog.Builder(fa);

        switch (item.getItemId()) {
            case R.id.action_pencil:
                //###########
                if (position_pencil == 0) {
                    Toast.makeText(fa, "Выберите процедуру", Toast.LENGTH_SHORT).show();
                    break;
                }

                Resources resources = getResources();
                Procedures procedures = new Procedures(fa);
                String procNameTmp = mListItemsProc.get(position_pencil);
                procIdTmp = procedures.getProcedureID(procNameTmp);
                Object[] procInfoTmp = procedures.getProcedureInfo(procIdTmp);
                Integer procPriceTmp = (Integer) procInfoTmp[1];
                String procNoteTmp = (String) procInfoTmp[2];
                Integer procColorTmp = (Integer) procInfoTmp[3];
                //###########

                //#######__adapter__#########
                spinner.setAdapter(adapter);
                spinner.setSelection(procColorTmp, true);
                //обработчик нажатия
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Toast toast = Toast.makeText(fa,
                                "Ваш выбор: " + position, Toast.LENGTH_SHORT);
                        choiceColor = position;
                        toast.show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                //#######################

                nameEditText.setText(procNameTmp);
                priceEditText.setText(procPriceTmp + "");
                noteEditText.setText(procNoteTmp);
                choiceColor = procColorTmp;

                builder.setView(view)
                        .setCancelable(true);
                builder.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = nameEditText.getText().toString();
                        Integer price = 0;

                        if (!priceEditText.getText().toString().isEmpty())
                            price = Integer.parseInt(priceEditText.getText().toString());

                        String note = noteEditText.getText().toString();
                        if (note.isEmpty())
                            note = "Примечаний нет";

                        Integer color = choiceColor;

                        if (!name.isEmpty()) {
                            Procedures procedures_update = new Procedures(fa);
                            int test = procedures_update.getUpdateProcedure(procIdTmp + "", name, price, note, color);
                            if (test == 1) {
                                Toast.makeText(fa, "Процедура изменена", Toast.LENGTH_SHORT).show();
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
                //#######__adapter__#########

                spinner.setAdapter(adapter);
                spinner.setSelection(0, true);
                //обработчик нажатия
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Toast toast = Toast.makeText(fa,
                                "Ваш выбор: " + position, Toast.LENGTH_SHORT);
                        choiceColor = position;
                        toast.show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                //#######################

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
                            note = "Примечаний нет";

                        if (!name.isEmpty()) {
                            long id = 0;
                            Procedures procedures = new Procedures(fa);
                            id = procedures.addProcedure(name, price, note, choiceColor);
                            if (id != 0) {
                                Toast.makeText(fa, "Процедура добавлена", Toast.LENGTH_SHORT).show();
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
        mSearchViewProc.addTextChangedListener(filterTextWatcher);
        super.onActivityCreated(savedInstanceState);
    }

    private void setListAdaptor() {
        // create instance of PinnedHeaderAdapter and set adapter to list view
        mAdaptorProc = new ProcedureHeaderAdapter(fa, mListItemsProc, mListSectionPosProc, mColorProcedures);
        mListViewProc.setAdapter(mAdaptorProc);

        LayoutInflater inflater = (LayoutInflater) fa.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // set header view
        View pinnedHeaderView = inflater.inflate(R.layout.section_row_view, mListViewProc, false);
        mListViewProc.setPinnedHeaderView(pinnedHeaderView);

        // set index bar view
        // IndexBarView indexBarView = (IndexBarView) inflater.inflate(R.layout.index_bar_view, mListViewProc, false);
        // indexBarView.setData(mListViewProc, mListItemsProc, mListSectionPosProc);
        //mListViewProc.setIndexBarView(indexBarView);

        // set preview text view
        View previewTextView = inflater.inflate(R.layout.preview_view, mListViewProc, false);
        mListViewProc.setPreviewView(previewTextView);

        // for configure pinned header view on scroll change
        mListViewProc.setOnScrollListener(mAdaptorProc);
        mListViewProc.setOnItemClickListener(mClickListener);
        mListViewProc.setOnItemLongClickListener(mLongClickListener);
    }

    private void setIndexBarViewVisibility(String constraint) {
        // hide index bar for search results
        if (constraint != null && constraint.length() > 0) {
            mListViewProc.setIndexBarVisibility(false);
        } else {
            mListViewProc.setIndexBarVisibility(true);
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
            LayoutInflater inflater = (LayoutInflater) fa.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.color_circle);
            label.setText(data[position]);
            label.setVisibility(View.GONE);

            ImageView icon = (ImageView) row.findViewById(R.id.icon_circle);

            if (data[position] == "one") {
                icon.setImageResource(R.mipmap.ic_launcher_blue);
                //icon.setImageResource(R.mipmap.ic_launcher);
            } else if (data[position] == "two") {
                icon.setImageResource(R.mipmap.ic_launcher_orange);
                //icon.setImageResource(R.mipmap.ic_launcher);
            } else if (data[position] == "three") {
                icon.setImageResource(R.mipmap.ic_launcher_green);
                //icon.setImageResource(R.mipmap.ic_launcher);
            } else if (data[position] == "four") {
                icon.setImageResource(R.mipmap.ic_launcher_red);
                //icon.setImageResource(R.mipmap.ic_launcher);
            }
            return row;
        }
    }

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
                    LOOP_FOR_PROCEDURES:
                    for (String item : mProcedures) {
                        String[] subNames = item.split(" ");
                        LOOP_FOR_SUBNAMES:
                        for (String subName : subNames) {
                            if (subName.toLowerCase(Locale.getDefault()).startsWith(constraintStr)) {
                                filterItems.add(item);
                                break LOOP_FOR_SUBNAMES;
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
            // sort array and extract sections in background Thread
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

}
