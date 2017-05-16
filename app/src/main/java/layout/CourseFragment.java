package layout;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.smartgoat.greendao3example.MainActivity;
import me.smartgoat.greendao3example.R;

public class CourseFragment extends Fragment {

    private Calendar mCalendar;

    // Course view
    private EditText txtCourseName;
    private EditText txtCourseStart;
    private EditText txtCourseEnd;

    public CourseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCalendar = Calendar.getInstance();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_course, container, false);

        txtCourseName = (EditText) view.findViewById(R.id.txt_course_name);
        txtCourseStart = (EditText) view.findViewById(R.id.txt_course_start);
        txtCourseEnd = (EditText) view.findViewById(R.id.txt_course_end);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(txtCourseStart);
            }

        };

        txtCourseStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    private void updateLabel(EditText textField) {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textField.setText(sdf.format(mCalendar.getTime()));
    }

}
