package ru.anroidapp.plannerwork.contact_choose;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.Image;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.smena.clientbase.procedures.Clients;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import ru.anroidapp.plannerwork.MetaData;
import ru.anroidapp.plannerwork.R;
import ru.anroidapp.plannerwork.contact_choose.intface.PinnedHeaderAdapter;


public class ContactTab1 extends Fragment {

    MetaData mMetaData;

    ArrayList<String> mContacts;
    ArrayList<String> mClientsHistory;
    ArrayList<Integer> mListSectionPos;
    ArrayList<String> mListItems;
    PinnedHeaderListView mListView;
    PinnedHeaderAdapter mAdaptor;
    EditText mSearchView;
    ProgressBar mLoadingView;
    TextView mEmptyView;
    LinearLayout laySearch, layCanselSearch;
    FloatingActionButton fab;

    private final String TAG = "ContactTab1";
    private TextView lastChoose;

    FragmentActivity fa;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

//http://www.youtube.com/watch?v=i_HwX5CEL6g&list=PLIU76b8Cjem7x0Ot_d0Z1nIq1Mk3PUW_Q&index=3

        fa = super.getActivity();
        mMetaData = (MetaData) getArguments().getSerializable(MetaData.TAG);

        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.contact_tab, container, false);

        fab = (FloatingActionButton) relativeLayout.findViewById(R.id.fab);

        getContacts();
        getHistoryClients();

        mSearchView = (EditText) relativeLayout.findViewById(R.id.search_view);
        mLoadingView = (ProgressBar) relativeLayout.findViewById(R.id.loading_view);
        mListView = (PinnedHeaderListView) relativeLayout.findViewById(R.id.list_view);
        mEmptyView = (TextView) relativeLayout.findViewById(R.id.empty_view);
        laySearch = (LinearLayout) relativeLayout.findViewById(R.id.LaySearch);
        layCanselSearch = (LinearLayout) relativeLayout.findViewById(R.id.LayCanselSearch);

        fab.attachToListView(mListView);

        fab.setOnClickListener(oclFabClick);
        layCanselSearch.setOnClickListener(oclCloseSearch);

        laySearch.setVisibility(View.GONE);

        relativeLayout.findViewById(R.id.contact_tab);

        mListSectionPos = new ArrayList<>();
        mListItems = new ArrayList<>();

        // for handling configuration change
        if (savedInstanceState != null) {
            mListItems = savedInstanceState.getStringArrayList("mListItems");
            mListSectionPos = savedInstanceState.getIntegerArrayList("mListSectionPos");

            if (mListItems != null && mListItems.size() > 0 && mListSectionPos != null
                    && mListSectionPos.size() > 0) {
                setListAdaptor();
            }

            String constraint = savedInstanceState.getString("constraint");
            if (constraint != null && constraint.length() > 0) {
                mSearchView.setText(constraint);
                setIndexBarViewVisibility(constraint);
            }

        } else {
            new Poplulate().execute(mContacts);
        }

        setHasOptionsMenu(true);

        return relativeLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Poplulate().execute(getContacts());
    }

    View.OnClickListener oclFabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            laySearch.setVisibility(View.VISIBLE);
            fab.hide();
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(mSearchView.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);

            Toast.makeText(fa, "Проверка search", Toast.LENGTH_SHORT).show();

            mSearchView.setCursorVisible(false);

        }
    };

    View.OnClickListener oclCloseSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            laySearch.setVisibility(View.GONE);
            fab.show();
            Toast.makeText(fa, "Проверка close search", Toast.LENGTH_SHORT).show();

        }
    };

    private ArrayList<String> getContacts() {
        mContacts = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = fa.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                if (!name.isEmpty()) {
                    mContacts.add(name);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return mContacts;
    }

    private ArrayList<String> getHistoryClients() {
        mClientsHistory = new ArrayList<>();
        Clients clients = new Clients(fa);
        mClientsHistory = clients.getAllClientsNames();

        return mClientsHistory;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.client_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_client_item:
                final Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

                new MaterialDialog.Builder(fa)
                        .content("Чтобы вернуться нажмите 'Назад'")
                        .positiveText("Понял")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                fa.startActivity(intent);
                            }
                        })
                        .show();
                break;
            case R.id.watch_clients_mode:
                if (item.getTitle().equals(getResources().getString(R.string.history))) {
                    item.setTitle(getResources().getString(R.string.contacts));
                    new Poplulate().execute(mClientsHistory);
                } else if (item.getTitle().equals(getResources().getString(R.string.contacts))) {
                    item.setTitle(getResources().getString(R.string.history));
                    new Poplulate().execute(mContacts);
                }
                break;

        }

        return super.onOptionsItemSelected(item);
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

        TextView view = (TextView) mAdaptor.getView(0, mListView.getChildAt(0), mListView);
        view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_check_buttonless_on, 0, 0, 0);

        LayoutInflater inflater = (LayoutInflater) fa.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // set header view
       // View pinnedHeaderView = inflater.inflate(R.layout.section_row_view, mListView, false);
        //mListView.setPinnedHeaderView(pinnedHeaderView);

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
        mListView.setOnItemLongClickListener(mLongClickListener);
    }

    AdapterView.OnItemLongClickListener mLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            Resources resources = getResources();

            String nameTmp = mListItems.get(position);
            String allPhones = "";
            String allEmails = "";
            ArrayList<String> phonesTmp = getPhonesByName(fa, nameTmp);

            if (phonesTmp.isEmpty()) {
                allPhones = resources.getString(R.string.unknown) + "\n";
            } else {
                for (int i = 0; i < phonesTmp.size(); i++) {
                    allPhones += phonesTmp.get(i) + "\n";
                }
            }

            ArrayList<String> emailsTmp = getEmailsByName(fa, nameTmp);
            if (emailsTmp.isEmpty()) {
                allEmails = resources.getString(R.string.unknown) + "\n";
            } else {
                for (int i = 0; i < emailsTmp.size(); i++) {
                    allEmails += emailsTmp.get(i) + "\n";
                }
            }

            new MaterialDialog.Builder(fa)
                    .title(R.string.contact_inf)
                    .content(resources.getString(R.string.name) + " " + nameTmp + "\n" + "\n" +
                            resources.getString(R.string.phone) + "\n" + allPhones + "\n" +
                            resources.getString(R.string.email) + "\n" + allEmails)
                    .positiveText(R.string.back)
                    .show();
            //Toast.makeText(fa, "123", Toast.LENGTH_LONG).show();
            return true;
        }
    };

    AdapterView.OnItemClickListener mClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

            Resources resourses = getResources();
            String unknown = resourses.getString(R.string.unknown);

            String clientName = mListItems.get(position);
            mMetaData.setClientName(clientName);

            ArrayList<String> clientPhones = getPhonesByName(fa, clientName);
            if (clientPhones.isEmpty()) {
                clientPhones.add(unknown);
            }
            mMetaData.setClientPhones(clientPhones);

            ArrayList<String> clientEmails = getEmailsByName(fa, clientName);
            if (clientEmails.isEmpty()) {
                clientEmails.add(unknown);
            }
            mMetaData.setClientEmails(clientEmails);

            if (lastChoose != null) {
                lastChoose.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            TextView textView = (TextView) view;
            textView.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.btn_check_buttonless_on, 0, 0, 0);
            lastChoose = textView;
        }
    };

    private ArrayList<String> getPhonesByName(Context context, String name) {

        Cursor cursor = null;
        ArrayList<String> phonesByName = new ArrayList<>();

        try {
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" +
                    name + "%'";
            String[] column = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            cursor = context.getContentResolver().
                    query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, column,
                            selection, null, null);

            while (cursor.moveToNext()) {
                phonesByName.add(cursor.getString(cursor.getColumnIndex
                        (ContactsContract.CommonDataKinds.Phone.NUMBER)));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return phonesByName;

    }

    private ArrayList<String> getEmailsByName(Context context, String name) {

        Cursor cursor = null;
        ArrayList<String> emailsByName = new ArrayList<>();

        try {
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" +
                    name + "%'";
            String[] column = new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS};
            cursor = context.getContentResolver().
                    query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, column,
                            selection, null, null);

            while (cursor.moveToNext()) {
                emailsByName.add(cursor.getString(cursor.getColumnIndex
                        (ContactsContract.CommonDataKinds.Email.ADDRESS)));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return emailsByName;

    }

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
                    for (String item : mContacts) {
                        if (item.toLowerCase(Locale.getDefault()).startsWith(constraintStr)) {
                            filterItems.add(item);
                        }
                    }
                    result.count = filterItems.size();
                    result.values = filterItems;
                }
            } else {
                synchronized (this) {
                    result.count = mContacts.size();
                    result.values = mContacts;
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

    @SuppressWarnings("unchecked")
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
            if (mListItems != null)
                mListItems.clear();
            if (mListSectionPos != null)
                mListSectionPos.clear();

            ArrayList<String> items = params[0];
            if (mContacts.size() > 0) {

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
                if (mListItems != null && mListItems.size() <= 0) {
                    showEmptyText(mListView, mLoadingView, mEmptyView);
                } else {
                    if (mListItems != null)
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
