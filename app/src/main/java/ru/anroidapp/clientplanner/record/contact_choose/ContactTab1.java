package ru.anroidapp.clientplanner.record.contact_choose;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smena.clientbase.procedures.Clients;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ru.anroidapp.clientplanner.MetaData;
import ru.anroidapp.clientplanner.R;
import ru.anroidapp.clientplanner.record.contact_choose.intface.PinnedHeaderAdapter;


public class ContactTab1 extends Fragment implements LoaderCallbacks<ArrayList<String>> {

    private static boolean isContactLoaded = true;
    private static ArrayList<String> mContacts;
    private static ArrayList<String> mClients;
    private final String TAG = "ContactTab1";
    private final int GET_CONTACT = 0;
    private final int GET_CLIENT = 1;
    private MetaData mMetaData;
    private ArrayList<Integer> mListSectionPos;
    private ArrayList<String> mListItems;
    private PinnedHeaderListView mListView;
    private PinnedHeaderAdapter mAdaptor;
    private EditText mSearchView;
    private ProgressBar mLoadingView;
    private TextView mEmptyView;
    private LinearLayout mSearchLayout, mCancelSearchLayout;
    private FloatingActionButton mFab;
    View.OnClickListener onFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSearchLayout.setVisibility(View.VISIBLE);
            mFab.hide();
            mSearchView.requestFocus();
            final InputMethodManager keyboard = (InputMethodManager) getActivity().
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(mSearchView, 0);

            mSearchView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_ENTER:
                                keyboard.hideSoftInputFromWindow(mSearchView.getWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);
                                return true;
                            default:
                                break;
                        }
                    }
                    return false;
                }
            });
        }
    };
    private FragmentActivity mContext;
    AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            Resources resources = getResources();

            String nameTmp = mListItems.get(position);
            String allPhones = "";
            String allEmails = "";
            ArrayList<String> phonesTmp = getPhonesByName(nameTmp);

            if (phonesTmp == null || phonesTmp.isEmpty()) {
                allPhones = resources.getString(R.string.unknown) + "\n";
            } else {
                for (int i = 0; i < phonesTmp.size(); i++) {
                    if (i != phonesTmp.size() - 1) {
                        allPhones += phonesTmp.get(i) + "\n";
                    } else {
                        allPhones += phonesTmp.get(i);
                    }
                }
            }

            ArrayList<String> emailsTmp = getEmailsByName(nameTmp);
            if (emailsTmp == null || emailsTmp.isEmpty()) {
                allEmails = resources.getString(R.string.unknown) + "\n";
            } else {
                for (int i = 0; i < emailsTmp.size(); i++) {
                    if (i != emailsTmp.size() - 1) {
                        allEmails += emailsTmp.get(i) + "\n";
                    } else {
                        allEmails += emailsTmp.get(i);
                    }
                }
            }

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View contactInfo = inflater.inflate(R.layout.contact_info, null);
            final TextView contactNameTextView = (TextView) contactInfo.findViewById(R.id.contact_info_name_input);
            contactNameTextView.setText(nameTmp);
            final TextView contactPhoneTextView = (TextView) contactInfo.findViewById(R.id.contact_info_phone_input);
            contactPhoneTextView.setText(allPhones);
            final TextView contactEmailTextView = (TextView) contactInfo.findViewById(R.id.contact_info_email_input);
            contactEmailTextView.setText(allEmails);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(contactInfo)
                    .setCancelable(true)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.show();
            return true;
        }
    };
    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

            Resources resources = getResources();
            String unknown = resources.getString(R.string.unknown);

            String clientName = mListItems.get(position);
            mMetaData.setClientName(clientName);

            ArrayList<String> clientPhones = getPhonesByName(clientName);
            if (clientPhones == null || clientPhones.isEmpty()) {
                clientPhones = new ArrayList<>();
                clientPhones.add(unknown);
            }
            mMetaData.setClientPhones(clientPhones);

            ArrayList<String> clientEmails = getEmailsByName(clientName);
            if (clientEmails == null || clientEmails.isEmpty()) {
                clientEmails = new ArrayList<>();
                clientEmails.add(unknown);
            }
            mMetaData.setClientEmails(clientEmails);

        }
    };
    View.OnClickListener onCloseSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSearchLayout.setVisibility(View.GONE);
            mFab.show();
            InputMethodManager inputManager = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(mSearchView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

            if (isContactLoaded)
                showResults(mContacts);
            else
                showResults(mClients);

        }
    };
    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (mAdaptor != null) {
                if (isContactLoaded)
                    (new ListFilter(mContacts)).filter(str);
                else
                    (new ListFilter(mClients)).filter(str);
            }

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    private static void ignoreDuplicateStrings(List listWithDuplicate) {
        Set<String> listWithoutDuplicate = new HashSet<>();
        listWithoutDuplicate.addAll(listWithDuplicate);
        listWithDuplicate.clear();
        listWithDuplicate.addAll(listWithoutDuplicate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mContext = super.getActivity();
        mMetaData = (MetaData) getArguments().getSerializable(MetaData.TAG);
        mContacts = new ArrayList<>();
        mClients = new ArrayList<>();
        getLoaderManager().initLoader(GET_CONTACT, null, ContactTab1.this);
        getLoaderManager().initLoader(GET_CLIENT, null, ContactTab1.this);

        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.contact_tab,
                container, false);

        mFab = (FloatingActionButton) relativeLayout.findViewById(R.id.fab);

        mSearchView = (EditText) relativeLayout.findViewById(R.id.search_view);
        mLoadingView = (ProgressBar) relativeLayout.findViewById(R.id.loading_view);
        mListView = (PinnedHeaderListView) relativeLayout.findViewById(R.id.list_view);
        mEmptyView = (TextView) relativeLayout.findViewById(R.id.empty_view);
        mSearchLayout = (LinearLayout) relativeLayout.findViewById(R.id.search_layout);
        mCancelSearchLayout = (LinearLayout) relativeLayout.findViewById(R.id.cancel_search_layout);

        mFab.attachToListView(mListView);
        mFab.setOnClickListener(onFabClickListener);
        mCancelSearchLayout.setOnClickListener(onCloseSearchListener);

        mSearchLayout.setVisibility(View.GONE);

        relativeLayout.findViewById(R.id.contact_tab);

        mListSectionPos = new ArrayList<>();
        mListItems = new ArrayList<>();

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
            }

        } else {
            showLoading(mListView, mLoadingView, mEmptyView);
            getLoaderManager().getLoader(GET_CONTACT).forceLoad();
        }

        setHasOptionsMenu(true);

        return relativeLayout;
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
                mContext.startActivity(intent);
                break;
            case R.id.watch_clients_mode:
                if (item.getTitle().equals(getResources().getString(R.string.history))) {
                    item.setTitle(getResources().getString(R.string.contacts));
                    showLoading(mListView, mLoadingView, mEmptyView);
                    getLoaderManager().getLoader(GET_CLIENT).forceLoad();
                } else if (item.getTitle().equals(getResources().getString(R.string.contacts))) {
                    item.setTitle(getResources().getString(R.string.history));
                    showLoading(mListView, mLoadingView, mEmptyView);
                    getLoaderManager().getLoader(GET_CONTACT).forceLoad();
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

        mAdaptor = new PinnedHeaderAdapter(mContext, mListItems, mListSectionPos);
        mListView.setAdapter(mAdaptor);
        mListView.setOnItemClickListener(clickListener);
        mListView.setOnItemLongClickListener(longClickListener);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View pinnedHeaderView = inflater.inflate(R.layout.section_row_view, mListView, false);
        mListView.setPinnedHeaderView(pinnedHeaderView);

        View previewTextView = inflater.inflate(R.layout.preview_view, mListView, false);
        mListView.setPreviewView(previewTextView);

        mListView.setOnScrollListener(mAdaptor);

    }

    private ArrayList<String> getPhonesByName(String name) {

        Cursor cursor = null;
        ArrayList<String> phonesByName = new ArrayList<>();

        try {
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" +
                    name + "%'";
            String[] column = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            cursor = mContext.getContentResolver().
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

        phonesByName = ignoreDuplicatePhones(phonesByName);

        return phonesByName;

    }

    private ArrayList<String> getEmailsByName(String name) {

        Cursor cursor = null;
        ArrayList<String> emailsByName = new ArrayList<>();

        try {
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" +
                    name + "%'";
            String[] column = new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS};
            cursor = mContext.getContentResolver().
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

    private ArrayList<String> ignoreDuplicatePhones(ArrayList<String> phones) {

        ArrayList<String> goodPhones = new ArrayList<>();
        for (int i = 0; i < phones.size(); i++) {
            goodPhones.add(reformatPhones(phones.get(i)));
        }

        int size = goodPhones.size();
        switch (size) {
            case 0:
                return null;
            case 1:
                return goodPhones;
            default:
                Set<String> originalPhones = new HashSet<>();
                originalPhones.addAll(goodPhones);
                goodPhones.clear();
                goodPhones.addAll(originalPhones);

                return goodPhones;
        }
    }

    private String reformatPhones(String oldPhone) {

        if (oldPhone.length() < 11)
            return oldPhone;

        if (oldPhone.startsWith("8")) {
            oldPhone = "7" + oldPhone.substring(1, oldPhone.length());
        }

        String newPhone = oldPhone.replaceAll("\\D", "");
        newPhone.replace(" ", "");

        newPhone = "+" + newPhone.substring(0, 1) + " (" + newPhone.substring(1, 4) + ") " +
                newPhone.substring(4, 7) + "-" + newPhone.substring(7, 9) +
                "-" + newPhone.substring(9, newPhone.length());
        if (newPhone.length() == 18) {
            return newPhone;
        } else {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.phone) + " "
                            + oldPhone + " " + mContext.getResources().getString(R.string.incorrect_record),
                    Toast.LENGTH_SHORT).show();
            return null;
        }
    }

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
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case GET_CONTACT:
                return new ContactLoader(mContext);
            case GET_CLIENT:
                return new ClientLoader(mContext);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
        if (isContactLoaded)
            mContacts = data;
        else
            mClients = data;

        showResults(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    private void showResults(ArrayList<String> data) {
        if (mListItems != null)
            mListItems.clear();
        if (mListSectionPos != null)
            mListSectionPos.clear();

        String prev_section = "";

        for (String current_item : data) {
            String current_section = current_item.substring(0, 1).toUpperCase(Locale.getDefault());
            if (!prev_section.equals(current_section)) {
                mListItems.add(current_section);
                mListSectionPos.add(mListItems.indexOf(current_section));
                prev_section = current_section;
            }
            mListItems.add(current_item);
        }

        if (mListItems != null) {
            if (mListItems.size() > 0) {
                setListAdaptor();
                showContent(mListView, mLoadingView, mEmptyView);
            } else {
                showEmptyText(mListView, mLoadingView, mEmptyView);
            }
        }
    }

    public static class SortIgnoreCase implements Comparator<String> {
        public int compare(String s1, String s2) {
            return s1.compareToIgnoreCase(s2);
        }
    }

    private static class ContactLoader extends AsyncTaskLoader<ArrayList<String>> {
        private Context context;

        public ContactLoader(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public ArrayList<String> loadInBackground() {

            ArrayList<String> contacts = new ArrayList<>();
            isContactLoaded = true;
            if (contacts.isEmpty()) {
                Cursor cursor = context.getContentResolver().
                        query(ContactsContract.Contacts.CONTENT_URI,
                                null, null, null, null);

                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    if (!name.isEmpty()) {
                        contacts.add(name);
                    }
                }

                if (contacts.size() > 1) {
                    ignoreDuplicateStrings(contacts);
                    Collections.sort(contacts, new SortIgnoreCase());
                }
            }

            int a = 0;
            return contacts;
        }
    }


    private static class ClientLoader extends AsyncTaskLoader<ArrayList<String>> {
        private Context context;

        public ClientLoader(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public ArrayList<String> loadInBackground() {

            ArrayList<String> clients = new ArrayList<>();

            isContactLoaded = false;
            if (clients.isEmpty()) {
                clients = new Clients(context).getAllClientsNames();

                if (clients.size() > 1) {
                    ignoreDuplicateStrings(clients);
                    Collections.sort(clients, new SortIgnoreCase());
                }
            }

            int a = 0;
            return clients;
        }
    }

    private class ListFilter extends Filter {
        ArrayList<String> list;

        public ListFilter(ArrayList<String> list) {
            this.list = list;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String constraintStr = constraint.toString().toLowerCase(Locale.getDefault());
            Filter.FilterResults result = new FilterResults();

            if (constraint.toString().length() > 0) {
                ArrayList<String> filterItems = new ArrayList<>();

                synchronized (this) {
                    LOOP_FOR_CONTACTS:
                    for (String item : list) {
                        String[] surnames = item.split(" ");
                        LOOP_FOR_SURNAMES:
                        for (String subName : surnames) {
                            if (subName.toLowerCase(Locale.getDefault()).startsWith(constraintStr)) {
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
                    result.count = list.size();
                    result.values = list;
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<String> filtered = (ArrayList<String>) results.values;
            showResults(filtered);
        }
    }

}