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

import java.util.List;

import me.smartgoat.greendao3example.App;
import me.smartgoat.greendao3example.R;
import me.smartgoat.greendao3example.entity.DaoSession;
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

                    Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = (Student) listResult.getItemAtPosition(position);
                txtStudentName.setText(student.getName());
                switchIsMale.setChecked(student.getSex());
                mCurrentStudent = student;
                switchButtonMode(true);
            }
        });
    }

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

    private class ListResultAdapter extends ArrayAdapter<Student> {

        public ListResultAdapter(@NonNull Context context,
                                 @LayoutRes int resource,
                                 @NonNull List<Student> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Student student = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.shared_list_item, parent, false);
            }

            TextView txtID = (TextView) convertView.findViewById(R.id.txt_id);
            TextView txtName = (TextView) convertView.findViewById(R.id.txt_name);
            final Button btnDelete = (Button) convertView.findViewById(R.id.btn_delete);

            txtID.setText(student.getId().toString());
            txtName.setText(student.getName() + " (" + (student.getSex() ? "Male" : "Female") + ")");
            btnDelete.setTag(student);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Are you sure you want to delete this student?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Student stu = (Student) btnDelete.getTag();
                                    stu.delete();
                                    remove(stu);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });

            return convertView;
        }
    }
}
