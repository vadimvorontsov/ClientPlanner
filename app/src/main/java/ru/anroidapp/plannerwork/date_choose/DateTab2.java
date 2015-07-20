package ru.anroidapp.plannerwork.date_choose;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.smena.datechoose.DateTimePicker;

import ru.anroidapp.plannerwork.R;


public class DateTab2 extends Fragment {

    FragmentActivity fragmentActivity;

    Button btnTime1,btnTime2 , btnDate;
    int time_hour, time_min, time_year, time_month, time_day;
    TextView textDate, textTime, textSelec;
    LinearLayout layDate, layTime, LayReturnDate;
    String time ,time1, time2, strHour, strMin;

    String arrMouth[] = {"января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "декабря"};



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentActivity = super.getActivity();

        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.date_tab, container, false);

        btnTime1 = (Button) relativeLayout.findViewById(R.id.BtnTimeHour);
        btnTime2   = (Button) relativeLayout.findViewById(R.id.BtnTimeMin);
        btnDate = (Button) relativeLayout.findViewById(R.id.BtnDate);

        textDate = (TextView) relativeLayout.findViewById(R.id.TextDate);
        textTime = (TextView) relativeLayout.findViewById(R.id.TextTime);
        textSelec = (TextView )relativeLayout.findViewById(R.id.textSelection);

        layDate = (LinearLayout) relativeLayout.findViewById(R.id.LinDatePick);
        layTime = (LinearLayout) relativeLayout.findViewById(R.id.LinTimePick);
        LayReturnDate = (LinearLayout) relativeLayout.findViewById(R.id.cancelLayDate);

        layTime.setVisibility(View.GONE);
        btnTime1.setVisibility(View.GONE);
        btnTime2.setVisibility(View.GONE);
        LayReturnDate.setVisibility(View.GONE);

        time1 = ""; time2 = "";

        View.OnClickListener oclBtnTime1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                time_hour = DateTimePicker.hour;

                if( time_hour < 10)
                    strHour = "0" + Integer.toString(time_hour);
                else
                    strHour = Integer.toString(time_hour);

                time_min = DateTimePicker.minute;

                if( time_min < 10)
                    strMin = "0" + Integer.toString(time_min);
                else
                    strMin = Integer.toString(time_min);

                time1 = "с " + strHour + ":" +  strMin ;
                time  = time1 + time2;
                Toast.makeText(fragmentActivity, time1, Toast.LENGTH_SHORT).show();
                textTime.setText(time);
            }
        };
        btnTime1.setOnClickListener(oclBtnTime1);

        View.OnClickListener oclBtnTime2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                time_hour = DateTimePicker.hour;

                if( time_hour < 10)
                    strHour = "0" + Integer.toString(time_hour);
                else
                    strHour = Integer.toString(time_hour);

                time_min = DateTimePicker.minute;

                if( time_min < 10)
                    strMin = "0" + Integer.toString(time_min);
                else
                    strMin = Integer.toString(time_min);

                time2 = " по " + strHour + ":" +  strMin ;
                time = time1 + time2;
                Toast.makeText(fragmentActivity, time2, Toast.LENGTH_SHORT).show();
                textTime.setText(time);
            }
        };
        btnTime2.setOnClickListener(oclBtnTime2);

        View.OnClickListener oclBtnDate = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                time_day = DateTimePicker.day;
                time_month = DateTimePicker.month;
                time_year = DateTimePicker.year;

                layDate.setVisibility(View.GONE);
                btnDate.setVisibility(View.GONE);
                layTime.setVisibility(View.VISIBLE);
                btnTime1.setVisibility(View.VISIBLE);
                btnTime2.setVisibility(View.VISIBLE);
                LayReturnDate.setVisibility(View.VISIBLE);

                textSelec.setText("Выберите время");
                time = Integer.toString(time_day) + " " + arrMouth[time_month] + " " + Integer.toString(time_year);
                textDate.setText(time);

            }
        };

        btnDate.setOnClickListener(oclBtnDate);


        View.OnClickListener oclLayReturnDate = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                layDate.setVisibility(View.VISIBLE);
                btnDate.setVisibility(View.VISIBLE);
                btnTime1.setVisibility(View.GONE);
                btnTime2.setVisibility(View.GONE);
                layTime.setVisibility(View.GONE);
                LayReturnDate.setVisibility(View.GONE);

                textSelec.setText("Выберите дату");


                Toast.makeText(fragmentActivity, "OK", Toast.LENGTH_SHORT).show();

            }
        };
        LayReturnDate.setOnClickListener(oclLayReturnDate);


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
