package ru.anroidapp.plannerwork.procedure_choose;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.anroidapp.plannerwork.R;

/**
 * Created by Артём on 14.06.2015.
 */
public class ProcedureTab3 extends Fragment {

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.procedure_tab, container, false);
        setHasOptionsMenu(true);
        return v;
    }

}
