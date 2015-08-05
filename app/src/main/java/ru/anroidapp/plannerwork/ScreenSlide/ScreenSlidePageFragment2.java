package ru.anroidapp.plannerwork.ScreenSlide;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.anroidapp.plannerwork.R;

/**
 * Created by user on 03.08.2015.
 */
public class ScreenSlidePageFragment2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanseState){
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide2, container, false);

        return rootView;
    }

}
