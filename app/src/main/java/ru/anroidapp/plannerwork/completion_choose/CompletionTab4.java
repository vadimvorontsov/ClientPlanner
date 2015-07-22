package ru.anroidapp.plannerwork.completion_choose;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.smena.clientbase.procedures.Procedures;

import ru.anroidapp.plannerwork.R;

/**
 * Created by Àðò¸ì on 22.07.2015.
 */
public class CompletionTab4 extends Fragment {

    Button btnClickOK;
    FragmentActivity fa;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.completion_tab4, container, false);

        fa = super.getActivity();
        btnClickOK = (Button) relativeLayout.findViewById(R.id.BtnCompletionOK);

        View.OnClickListener oclBtnOK = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        };
        btnClickOK.setOnClickListener(oclBtnOK);

        setHasOptionsMenu(true);
        return relativeLayout;
    }
}
