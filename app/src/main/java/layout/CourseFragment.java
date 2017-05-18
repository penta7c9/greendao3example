package layout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.smartgoat.greendao3example.App;
import me.smartgoat.greendao3example.R;
import me.smartgoat.greendao3example.entity.Course;
import me.smartgoat.greendao3example.entity.CourseDao;
import me.smartgoat.greendao3example.entity.DaoSession;
import me.smartgoat.greendao3example.entity.Instructor;
import me.smartgoat.greendao3example.entity.InstructorDao;

public class CourseFragment extends Fragment {

    private Calendar mCalendar;

    private EditText txtCourseName;
    private EditText txtCourseStart;
    private EditText txtCourseEnd;
    private EditText txtRoom;
    private Spinner spinnerInstructor;
    private Button btnClear;
    private Button btnAdd;
    private Button btnSearch;
    private ListView listResult;

    private Course mCurrentCourse;
    private DaoSession mDaoSession;

    private boolean mIsUpdateMode;

    public CourseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCalendar = Calendar.getInstance();
        mDaoSession = ((App) getActivity().getApplication()).getDaoSession();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        txtCourseName = (EditText) view.findViewById(R.id.txt_course_name);
        txtCourseStart = (EditText) view.findViewById(R.id.txt_course_start);
        txtCourseEnd = (EditText) view.findViewById(R.id.txt_course_end);
        txtRoom = (EditText) view.findViewById(R.id.txt_room);
        spinnerInstructor = (Spinner) view.findViewById(R.id.cb_instructor);
        btnClear = (Button) view.findViewById(R.id.btn_clear);
        btnAdd = (Button) view.findViewById(R.id.btn_add);
        btnSearch = (Button) view.findViewById(R.id.btn_search);
        listResult = (ListView) view.findViewById(R.id.list_result);

        setControlEventListener();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fillInstructorSpinner();
    }

    private void setControlEventListener() {
        txtCourseStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, monthOfYear);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateText(txtCourseStart);
                    }

                };
                new DatePickerDialog(getActivity(), dateSetListener,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        txtCourseEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, monthOfYear);
                        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateText(txtCourseEnd);
                    }

                };
                new DatePickerDialog(getActivity(), dateSetListener,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtCourseName.setText("");
                txtCourseStart.setText("");
                txtCourseEnd.setText("");
                txtRoom.setText("");
                spinnerInstructor.setSelection(0);
                mCurrentCourse = null;

                switchButtonMode(false);
                listResult.setAdapter(null);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtCourseName.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Oops")
                            .setMessage("Course name cannot be empty")
                            .setNegativeButton("OK", null)
                            .show();
                } else {
                    if (mCurrentCourse == null) {
                        mCurrentCourse = new Course();
                    }
                    mCurrentCourse.setName(txtCourseName.getText().toString());
                    mCurrentCourse.setStartDate(
                            convertStringToDate(txtCourseStart.getText().toString()));
                    mCurrentCourse.setEndDate(
                            convertStringToDate(txtCourseEnd.getText().toString()));
                    mCurrentCourse.setRoom(txtRoom.getText().toString());
                    mCurrentCourse.setInstructorId(getSelectedInstructorId());

                    CourseDao courseDao = mDaoSession.getCourseDao();
                    String toastMsg;
                    if (mIsUpdateMode) {
                        courseDao.update(mCurrentCourse);
                        toastMsg = "Course updated";
                    } else {
                        courseDao.insert(mCurrentCourse);
                        toastMsg = "Course inserted";
                        switchButtonMode(true);
                    }

                    Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseDao courseDao = mDaoSession.getCourseDao();
                List<Course> courses = courseDao.queryBuilder().list();
                ListResultAdapter adapter = new ListResultAdapter(
                        getActivity(), R.layout.course_list_item, courses);
                listResult.setAdapter(adapter);
            }
        });

        listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course course = (Course) listResult.getItemAtPosition(position);
                txtCourseName.setText(course.getName());
                txtCourseStart.setText(convertDateToString(course.getStartDate()));
                txtCourseEnd.setText(convertDateToString(course.getEndDate()));
                txtRoom.setText(course.getRoom());

                InstructorSpinnerAdapter adapter = (InstructorSpinnerAdapter)spinnerInstructor.getAdapter();
                int pos = adapter.getPosition(course.getInstructor());
                spinnerInstructor.setSelection(pos);

                mCurrentCourse = course;
                switchButtonMode(true);
            }
        });
    }

    private Long getSelectedInstructorId() {
        Instructor instructor = (Instructor) spinnerInstructor.getSelectedItem();
        return instructor.getId();
    }

    private Date convertStringToDate(String input) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        try {
            Date date = format.parse(input);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertDateToString(Date input) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        return format.format(input);
    }

    private void switchButtonMode(boolean toUpdateMode) {
        mIsUpdateMode = toUpdateMode;
        if (mIsUpdateMode) {
            btnAdd.setText("Update");
        } else {
            btnAdd.setText("Add");
        }
    }

    private void updateText(EditText textField) {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        textField.setText(sdf.format(mCalendar.getTime()));
    }

    private void fillInstructorSpinner() {
        InstructorDao instructorDao = mDaoSession.getInstructorDao();
        List<Instructor> instructors = instructorDao.queryBuilder().list();
        InstructorSpinnerAdapter adapter = new InstructorSpinnerAdapter(getActivity(), instructors);
        spinnerInstructor.setAdapter(adapter);
    }

    private class InstructorSpinnerAdapter extends ArrayAdapter<Instructor> {

        public InstructorSpinnerAdapter(@NonNull Context context,
                                        @NonNull List<Instructor> instructors) {
            super(context, R.layout.spinner_item, R.id.text_content, instructors);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Instructor instructor = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.spinner_item, parent, false);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.text_content);
            textView.setText(instructor.getDisplayName());

            return textView;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Instructor instructor = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.spinner_item, parent, false);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.text_content);
            textView.setText(instructor.getDisplayName());

            return convertView;
        }
    }

    private class ListResultAdapter extends ArrayAdapter<Course> {

        private TextView txtID;
        private TextView txtName;
        private TextView txtTime;
        private Button btnDelete;

        public ListResultAdapter(@NonNull Context context,
                                 @LayoutRes int resource,
                                 @NonNull List<Course> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Course course = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.course_list_item, parent, false);
            }

            txtID = (TextView) convertView.findViewById(R.id.txt_id);
            txtName = (TextView) convertView.findViewById(R.id.txt_name);
            txtTime = (TextView) convertView.findViewById(R.id.txt_time);
            btnDelete = (Button) convertView.findViewById(R.id.btn_delete);

            txtID.setText(course.getId().toString());
            txtName.setText(course.getName() + " - Room: " + course.getRoom());
            txtTime.setText(getCourseDuration(course.getStartDate(), course.getEndDate()));
            btnDelete.setTag(course);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Are you sure you want to delete this course?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Course crs = (Course) btnDelete.getTag();
                                    crs.delete();
                                    remove(crs);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });

            return convertView;
        }

        private String getCourseDuration(Date start, Date end) {
            return convertDateToString(start) + " - " + convertDateToString(end);
        }
    }
}
