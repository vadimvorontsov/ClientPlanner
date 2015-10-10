package ru.anroidapp.plannerwork;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.smena.clientbase.procedures.Procedures;
import com.example.smena.clientbase.procedures.Sessions;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import ru.anroidapp.plannerwork.contact_choose.IndexBarView;
import ru.anroidapp.plannerwork.contact_choose.PinnedHeaderListView;
import ru.anroidapp.plannerwork.contact_choose.intface.PinnedHeaderAdapter;


public class ProcedureActivity extends AppCompatActivity {

    ArrayList<String> mProcedures;

    ArrayList<Integer> mListSectionPosProc;

    ArrayList<String> mListItemsProc;

    PinnedHeaderListView mListViewProc;

    PinnedHeaderAdapter mAdaptorProc;

    EditText mSearchViewProc;

    ProgressBar mLoadingViewProc;

    TextView mEmptyViewProc;

    FloatingActionButton fab;

    LinearLayout laySearch, layCanselSearch;
    private final String TAG = "ProcedureActivity";

    //private TextView lastChoose;
    String[] data = {"one", "two", "three", "four"};

    Toolbar toolbar;
    Context cntxt;
    int choiceColor = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procedure);
        cntxt = this;

        toolbar = (Toolbar) findViewById(R.id.tool_bar_procedure);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mProcedures = new ArrayList<>();

        getProcedures();

        mSearchViewProc = (EditText) findViewById(R.id.act_search_proc_view);
        mLoadingViewProc = (ProgressBar) findViewById(R.id.loading_view);
        mListViewProc = (PinnedHeaderListView) findViewById(R.id.act_proc_list_view);
        mEmptyViewProc = (TextView) findViewById(R.id.empty_view);
        laySearch = (LinearLayout) findViewById(R.id.LaySearchProcAct);
        layCanselSearch = (LinearLayout) findViewById(R.id.LayCanselSearchProcAct);

        findViewById(R.id.procedure_tab);

        mListSectionPosProc = new ArrayList<>();
        mListItemsProc = new ArrayList<>();

        fab = (FloatingActionButton) findViewById(R.id.fab_proc_act);
        fab.attachToListView(mListViewProc);
        fab.setShadow(true);
        fab.setOnClickListener(oclFabClick);

        layCanselSearch.setOnClickListener(oclCloseSearch);
        laySearch.setVisibility(View.GONE);

        // for handling configuration change
        if (savedInstanceState != null) {
            mListItemsProc = savedInstanceState.getStringArrayList("mListItemsProc");
            mListSectionPosProc = savedInstanceState.getIntegerArrayList("mListSectionPosProc");

            if (mListItemsProc != null && mListItemsProc.size() > 0 &&
                    mListSectionPosProc != null && mListSectionPosProc.size() > 0) {
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

        mSearchViewProc.addTextChangedListener(filterTextWatcher);

        //hide fab if procedure < 5
        if (mListItemsProc.size() < 6 )
            fab.setVisibility(View.GONE);
        //

    }

    View.OnClickListener oclFabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Animation openSearch = AnimationUtils.loadAnimation(cntxt, R.anim.s_down);
            laySearch.startAnimation(openSearch);
            laySearch.setVisibility(View.VISIBLE);
            Animation hideFab = AnimationUtils.loadAnimation(cntxt, R.anim.s_down);
            fab.startAnimation(hideFab);
            fab.setVisibility(View.GONE);
            mSearchViewProc.requestFocus();
            InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(mSearchViewProc, 0);

            Toast.makeText(cntxt, "Проверка fab", Toast.LENGTH_SHORT).show();

        }
    };

    View.OnClickListener oclCloseSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //////
            //mSearchViewProc.setText("");
            laySearch.setVisibility(View.GONE);
            //Animation showFab = AnimationUtils.loadAnimation(cntxt, R.anim.s_up);
            //fab.startAnimation(showFab);
            fab.setVisibility(View.VISIBLE);
            //mSearchViewProc.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(mSearchViewProc.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            Toast.makeText(cntxt, "Проверка close search proc", Toast.LENGTH_SHORT).show();

        }
    };

    public class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourcedId, String[] objects){
            super(context, textViewResourcedId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent){
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);
            TextView label = (TextView) row.findViewById(R.id.color_circle);
            label.setText(data[position]);
            label.setVisibility(View.GONE);

            ImageView icon = (ImageView) row.findViewById(R.id.icon_circle);

            if (data[position] == "one"){
                icon.setImageResource(R.mipmap.ic_launcher_blue);
            }
            else if(data[position] == "two"){
                icon.setImageResource(R.mipmap.ic_launcher_orange);
            }
            else if(data[position] == "three"){
                icon.setImageResource(R.mipmap.ic_launcher_green);
            }
            else if(data[position] == "four"){
                icon.setImageResource(R.mipmap.ic_launcher_red);
            }
            return row;
        }
    }


    private void getProcedures() {
        Procedures procedures = new Procedures(ProcedureActivity.this);
        mProcedures = procedures.getAllProceduresNames();
    }

    private void refreshList() {
        getProcedures();
        new Populate().execute(mProcedures);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_procedure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        switch (item.getItemId()) {
            case R.id.add_procedure:
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.add_procedure, null);
                final EditText nameEditText = (EditText) view.findViewById(R.id.input_proc_name);
                final EditText priceEditText = (EditText) view.findViewById(R.id.input_proc_price);
                final EditText noteEditText = (EditText) view.findViewById(R.id.input_proc_note);

                //#######__adapter__#########

                Spinner spinner = (Spinner) view.findViewById(R.id.spin_proc_color);
                MyCustomAdapter adapter = new MyCustomAdapter(cntxt, R.layout.row, data);
                spinner.setAdapter(adapter);
                spinner.setSelection(0,true);
                //обработчик нажатия
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Ваш выбор: " + position, Toast.LENGTH_SHORT);
                        choiceColor = position;
                        toast.show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                //#######################

                AlertDialog.Builder builder = new AlertDialog.Builder(ProcedureActivity.this);
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

                        Integer color = choiceColor;

                        if (!name.isEmpty()) {
                            long id = 0;
                            Procedures procedures = new Procedures(ProcedureActivity.this);
                            id = procedures.addProcedure(name, price, note, color);
                            if (id != 0) {
                                Toast.makeText(ProcedureActivity.this, "Процедура добавлена", Toast.LENGTH_SHORT).show();
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

    private void setListAdaptor() {
        // create instance of PinnedHeaderAdapter and set adapter to list view
        mAdaptorProc = new PinnedHeaderAdapter(ProcedureActivity.this, mListItemsProc, mListSectionPosProc);
        mListViewProc.setAdapter(mAdaptorProc);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // set header view
        View pinnedHeaderView = inflater.inflate(R.layout.contact_section_row_view, mListViewProc, false);
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
        mListViewProc.setOnItemLongClickListener(mLongClickListener);
    }

    AdapterView.OnItemLongClickListener mLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            Resources resources = getResources();
            Procedures procedures = new Procedures(ProcedureActivity.this);

            String procNameTmp = mListItemsProc.get(position);
            long procIdTmp = procedures.getProcedureID(procNameTmp);
            Object[] procInfoTmp = procedures.getProcedureInfo(procIdTmp);
            Integer procPriceTmp = (Integer) procInfoTmp[1];
            String procNoteTmp = (String) procInfoTmp[2];

            new MaterialDialog.Builder(ProcedureActivity.this)
                    .title(R.string.procedure_inf)
                    .content(resources.getString(R.string.procedure) + ": " + procNameTmp + "\n" +
                            resources.getString(R.string.price) + ": " + procPriceTmp + "\n" +
                            resources.getString(R.string.note) + ": " + procNoteTmp)
                    .positiveText(R.string.back)
                    .show();
            return true;
        }
    };

    AdapterView.OnItemClickListener mClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Procedures procedures = new Procedures(ProcedureActivity.this);


            Toast.makeText(ProcedureActivity.this.getApplicationContext(), "Проверка",
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
            new Populate().execute(filtered);
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
