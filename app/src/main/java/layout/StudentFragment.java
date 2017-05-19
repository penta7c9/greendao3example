package layout;


import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.smartgoat.greendao3example.App;
import me.smartgoat.greendao3example.R;
import me.smartgoat.greendao3example.entity.Course;
import me.smartgoat.greendao3example.entity.CourseDao;
import me.smartgoat.greendao3example.entity.DaoSession;
import me.smartgoat.greendao3example.entity.JoinStudentWithCourse;
import me.smartgoat.greendao3example.entity.JoinStudentWithCourseDao;
import me.smartgoat.greendao3example.entity.Student;
import me.smartgoat.greendao3example.entity.StudentDao;

public class StudentFragment extends Fragment {

    private EditText txtStudentName;
    private Switch switchIsMale;
    private LinearLayout llCourseEnrollContainer;
    private Spinner spinnerCourses;
    private Button btnEnroll;
    private Button btnClear;
    private Button btnAdd;
    private Button btnSearch;
    private ListView listResult;

    private DaoSession mDaoSession;
    private Student mCurrentStudent;

    // btnAdd has 2 modes: Add and Update
    private boolean mIsUpdateMode;

    public StudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDaoSession = ((App) getActivity().getApplication()).getDaoSession();
    }

    @Override
    public void onResume() {
        super.onResume();
        fillCourseSpinner();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student, container, false);

        // Find view elements
        txtStudentName = (EditText) view.findViewById(R.id.txt_stu_name);
        switchIsMale = (Switch) view.findViewById(R.id.switch_sex);
        llCourseEnrollContainer = (LinearLayout) view.findViewById(R.id.course_enroll_container);
        spinnerCourses = (Spinner) view.findViewById(R.id.cb_course);
        btnEnroll = (Button) view.findViewById(R.id.btn_enroll);
        btnClear = (Button) view.findViewById(R.id.btn_clear);
        btnAdd = (Button) view.findViewById(R.id.btn_add);
        btnSearch = (Button) view.findViewById(R.id.btn_search);
        listResult = (ListView) view.findViewById(R.id.list_result);

        setControlEventListener();

        // Init entry UI
        switchButtonMode(false);

        return view;
    }

    private void setControlEventListener() {
        // Enroll current student to the selected course.
        btnEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentStudent != null) {
                    JoinStudentWithCourse jswc = new JoinStudentWithCourse();
                    jswc.setStudentId(mCurrentStudent.getId());

                    Course course = (Course) spinnerCourses.getSelectedItem();
                    jswc.setCourseId(course.getId());

                    JoinStudentWithCourseDao jswcDao = mDaoSession.getJoinStudentWithCourseDao();
                    jswcDao.insert(jswc);

                    // Need this for the enrollment takes effect
                    mCurrentStudent.resetAttendantCourses();

                    fillCourseSpinner();

                    Toast.makeText(getActivity(), "Course enrolled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Clear the form, the result list. Reset current instructor object.
        // And change to Add mode.
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtStudentName.setText("");
                switchIsMale.setChecked(false);
                mCurrentStudent = null;

                switchButtonMode(false);
                listResult.setAdapter(null);
            }
        });

        // Validate and Add/Update student to database.
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtStudentName.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Oops")
                            .setMessage("Student name cannot be empty")
                            .setNegativeButton("OK", null)
                            .show();
                } else {
                    if (mCurrentStudent == null) {
                        mCurrentStudent = new Student();
                    }
                    mCurrentStudent.setName(txtStudentName.getText().toString());
                    mCurrentStudent.setSex(switchIsMale.isChecked());

                    StudentDao studentDao = mDaoSession.getStudentDao();
                    String toastMsg;
                    if (mIsUpdateMode) {
                        studentDao.update(mCurrentStudent);
                        toastMsg = "Student updated";
                    } else {
                        studentDao.insert(mCurrentStudent);
                        toastMsg = "Student inserted";
                        switchButtonMode(true);
                    }

                    fillCourseSpinner();

                    Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Search by name and show result in the result list.
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentDao studentDao = mDaoSession.getStudentDao();
                List<Student> students = studentDao.queryBuilder().list();
                ListResultAdapter adapter = new ListResultAdapter(
                        getActivity(), R.layout.shared_list_item, students);
                listResult.setAdapter(adapter);
            }
        });

        // Fill the from with selected student's information.
        listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = (Student) listResult.getItemAtPosition(position);
                txtStudentName.setText(student.getName());
                switchIsMale.setChecked(student.getSex());
                mCurrentStudent = student;
                switchButtonMode(true);
                fillCourseSpinner();
            }
        });
    }

    // Switch the mode of Add button
    private void switchButtonMode(boolean toUpdateMode) {
        mIsUpdateMode = toUpdateMode;
        if (mIsUpdateMode) {
            btnAdd.setText("Update");
            llCourseEnrollContainer.setVisibility(View.VISIBLE);
        } else {
            btnAdd.setText("Add");
            llCourseEnrollContainer.setVisibility(View.GONE);
        }
    }

    // Fill the available course to the spinner.
    // The enrolled course(s) will not be filled.
    private void fillCourseSpinner() {
        CourseDao courseDao = mDaoSession.getCourseDao();
        List<Course> courses;
        if (mCurrentStudent != null) {
            List<Long> idList = new ArrayList<>();
            for (Course c : mCurrentStudent.getAttendantCourses()) {
                idList.add(c.getId());
            }
            courses = courseDao.queryBuilder().where(CourseDao.Properties.Id.notIn(idList)).list();
        } else {
            courses = courseDao.queryBuilder().list();
        }
        CourseSpinnerAdapter adapter = new CourseSpinnerAdapter(getActivity(), courses);
        spinnerCourses.setAdapter(adapter);
    }

    private class CourseSpinnerAdapter extends ArrayAdapter<Course> {

        CourseSpinnerAdapter(@NonNull Context context, @NonNull List<Course> courses) {
            super(context, R.layout.spinner_item, R.id.text_content, courses);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Course course = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.spinner_item, parent, false);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.text_content);
            textView.setText(course.getName());

            return textView;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Course course = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.spinner_item, parent, false);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.text_content);
            textView.setText(course.getName());

            return convertView;
        }
    }

    private class ListResultAdapter extends ArrayAdapter<Student> {

        private TextView txtID;
        private TextView txtName;
        private Button btnShow;
        private Button btnDelete;

        ListResultAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Student> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final Student student = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.shared_list_item, parent, false);
            }

            txtID = (TextView) convertView.findViewById(R.id.txt_id);
            txtName = (TextView) convertView.findViewById(R.id.txt_name);
            btnShow = (Button) convertView.findViewById(R.id.btn_show);
            btnDelete = (Button) convertView.findViewById(R.id.btn_delete);

            txtID.setText(student.getId().toString());
            txtName.setText(student.getName() + " (" + (student.getSex() ? "Male" : "Female") + ")");
            btnShow.setTag(student);
            btnDelete.setTag(student);

            setEventListener();

            return convertView;
        }

        private void setEventListener() {
            // Delete the student
            btnDelete.setOnClickListener(new View.OnClickListener() {
                Student localStudent;
                @Override
                public void onClick(View v) {
                    localStudent = (Student) v.getTag();
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Are you sure you want to delete this student?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteCourseEnrollmentOf(localStudent);
                                    localStudent.delete();
                                    remove(localStudent);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });

            // Show enrolled courses of the student
            btnShow.setOnClickListener(new View.OnClickListener() {
                Student localStudent;
                @Override
                public void onClick(View v) {
                    localStudent = (Student) v.getTag();
                    List<Course> courses = localStudent.getAttendantCourses();
                    if (courses.size() <= 0) {
                        Toast.makeText(getActivity(), "No course to show", Toast.LENGTH_SHORT).show();
                    } else {
                        // Extract the course name
                        List<String> courseNameList = new ArrayList<>();
                        for (int i = 0; i < courses.size(); i++) {
                            courseNameList.add(courses.get(i).getName());
                        }

                        // Populate the list and show it
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                getActivity(), android.R.layout.simple_list_item_1, courseNameList);
                        ListView listView = new ListView(getActivity());
                        listView.setAdapter(adapter);
                        new AlertDialog.Builder(getActivity()).setView(listView).show();
                    }
                }
            });
        }

        // Delete all record of the input student in join table
        private void deleteCourseEnrollmentOf(Student student) {
            JoinStudentWithCourseDao jswcDao = mDaoSession.getJoinStudentWithCourseDao();
            List<JoinStudentWithCourse> toBeDeleteList = jswcDao.queryBuilder().where(
                    JoinStudentWithCourseDao.Properties.StudentId.eq(student.getId()))
                    .list();
            jswcDao.deleteInTx(toBeDeleteList);
        }
    }
}
