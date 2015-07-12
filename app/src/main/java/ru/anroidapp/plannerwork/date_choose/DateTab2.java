package ru.anroidapp.plannerwork.date_choose;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.example.smena.datechoose.DateTimePicker;

import ru.anroidapp.plannerwork.R;

public class DateTab2 extends Fragment {

    FragmentActivity fragmentActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentActivity = super.getActivity();

        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.date_tab, container, false);

        TimePicker timePicker = (TimePicker) relativeLayout.findViewById(R.id.timePicker);
        //set_timepicker_text_colour(timePicker);
        timePicker.setIs24HourView(true);

        DatePicker datePicker = (DatePicker) relativeLayout.findViewById(R.id.datePicker);

        relativeLayout.findViewById(R.id.date_tab);

        DateTimePicker picker = new DateTimePicker(timePicker, datePicker);
        picker.start();

        setHasOptionsMenu(true);
        return relativeLayout;

        /*----------Получение даты и времени-------
        DateTimePicker.year
        DateTimePicker.month
        DateTimePicker.day
        DateTimePicker.hour
        DateTimePicker.minute
        /*----------------------------------------*/
    }

//    private void set_timepicker_text_colour(TimePicker timePicker){
//        system = Resources.getSystem();
//        int hour_numberpicker_id = system.getIdentifier("hour", "id", "android");
//        int minute_numberpicker_id = system.getIdentifier("minute", "id", "android");
//        int ampm_numberpicker_id = system.getIdentifier("amPm", "id", "android");
//
//        NumberPicker hour_numberpicker = (NumberPicker) timePicker.findViewById(hour_numberpicker_id);
//        NumberPicker minute_numberpicker = (NumberPicker) timePicker.findViewById(minute_numberpicker_id);
//        NumberPicker ampm_numberpicker = (NumberPicker) timePicker.findViewById(ampm_numberpicker_id);
//
//        set_numberpicker_text_colour(hour_numberpicker);
//        set_numberpicker_text_colour(minute_numberpicker);
//        set_numberpicker_text_colour(ampm_numberpicker);
//    }
//
//    private void set_numberpicker_text_colour(NumberPicker number_picker){
//        final int count = number_picker.getChildCount();
//        final int color = fragmentActivity.getResources().getColor(R.color.colorPrimaryDark);
//
//        for(int i = 0; i < count; i++){
//            View child = number_picker.getChildAt(i);
//
//            try{
//                Field wheelpaint_field = number_picker.getClass().getDeclaredField("mSelectorWheelPaint");
//                wheelpaint_field.setAccessible(true);
//
//                ((Paint)wheelpaint_field.get(number_picker)).setColor(color);
//                ((EditText)child).setTextColor(color);
//                number_picker.invalidate();
//            }
//            catch(NoSuchFieldException e){
//                Log.w("setTimePickerTextColor", e);
//            }
//            catch(IllegalAccessException e){
//                Log.w("setTimePickerTextColor", e);
//            }
//            catch(IllegalArgumentException e){
//                Log.w("setTimePickerTextColor", e);
//            }
//        }
//    }

}
