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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import me.smartgoat.greendao3example.App;
import me.smartgoat.greendao3example.R;
import me.smartgoat.greendao3example.entity.Course;
import me.smartgoat.greendao3example.entity.CourseDao;
import me.smartgoat.greendao3example.entity.DaoSession;
import me.smartgoat.greendao3example.entity.Instructor;
import me.smartgoat.greendao3example.entity.InstructorDao;

public class InstructorFragment extends Fragment {

    private EditText txtInstructorName;
    private Spinner spinnerInstructorTitle;
    private Button btnClear;
    private Button btnAdd;
    private Button btnSearch;
    private ListView listResult;

    private Instructor mCurrentInstructor;
    private DaoSession mDaoSession;

    // btnAdd has 2 modes: Add and Update
    private boolean mIsUpdateMode;

    public InstructorFragment() {
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
        View view = inflater.inflate(R.layout.fragment_instructor, container, false);

        // Find view elements
        txtInstructorName = (EditText) view.findViewById(R.id.txt_ins_name);
        spinnerInstructorTitle = (Spinner) view.findViewById(R.id.cb_title);
        btnClear = (Button) view.findViewById(R.id.btn_clear);
        btnAdd = (Button) view.findViewById(R.id.btn_add);
        btnSearch = (Button) view.findViewById(R.id.btn_search);
        listResult = (ListView) view.findViewById(R.id.list_result);

        setControlEventListener();

        return view;
    }

    private void setControlEventListener() {
        // Clear the form, the result list. Reset current instructor object.
        // And change to Add mode.
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtInstructorName.setText("");
                spinnerInstructorTitle.setSelection(0);
                mCurrentInstructor = null;

                switchButtonMode(false);
                listResult.setAdapter(null);
            }
        });

        // Validate and Add/Update instructor to database.
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtInstructorName.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Oops")
                            .setMessage("Instructor name cannot be empty")
                            .setNegativeButton("OK", null)
                            .show();
                } else {
                    if (mCurrentInstructor == null) {
                        mCurrentInstructor = new Instructor();
                    }
                    mCurrentInstructor.setName(txtInstructorName.getText().toString());
                    mCurrentInstructor.setTitle(spinnerInstructorTitle.getSelectedItem().toString());

                    InstructorDao instructorDao = mDaoSession.getInstructorDao();
                    String toastMsg;
                    if (mIsUpdateMode) {
                        instructorDao.update(mCurrentInstructor);
                        toastMsg = "Instructor updated";
                    } else {
                        instructorDao.insert(mCurrentInstructor);
                        toastMsg = "Instructor inserted";
                        switchButtonMode(true);
                    }

                    Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Search by name and show result in the result list.
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstructorDao instructorDao = mDaoSession.getInstructorDao();
                List<Instructor> instructors = instructorDao.queryBuilder()
                        .where(InstructorDao.Properties.Name.like(
                                "%" + txtInstructorName.getText().toString() + "%"
                        )).list();
                ListResultAdapter adapter = new ListResultAdapter(
                        getActivity(), R.layout.shared_list_item, instructors);
                listResult.setAdapter(adapter);
            }
        });

        // Fill the from with selected instructor's information.
        listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Instructor instructor = (Instructor) listResult.getItemAtPosition(position);
                txtInstructorName.setText(instructor.getName());
                spinnerInstructorTitle.setSelection(getTitleIndex(instructor.getTitle()));

                mCurrentInstructor = instructor;
                switchButtonMode(true);
            }
        });
    }

    // Switch the mode of Add button
    private void switchButtonMode(boolean toUpdateMode) {
        mIsUpdateMode = toUpdateMode;
        if (mIsUpdateMode) {
            btnAdd.setText("Update");
        } else {
            btnAdd.setText("Add");
        }
    }

    // Get the index of a title in the spinner, so that we can select that title.
    private int getTitleIndex(String title) {
        String[] titles = getResources().getStringArray(R.array.array_title);
        int index = -1;
        for (int i = 0; i < titles.length; i++) {
            if (titles[i].equalsIgnoreCase(title)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private class ListResultAdapter extends ArrayAdapter<Instructor> {

        private TextView txtID;
        private TextView txtName;
        private Button btnDelete;
        private Button btnShow;

        ListResultAdapter(@NonNull Context context,
                          @LayoutRes int resource,
                          @NonNull List<Instructor> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Instructor instructor = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.shared_list_item, parent, false);
            }

            txtID = (TextView) convertView.findViewById(R.id.txt_id);
            txtName = (TextView) convertView.findViewById(R.id.txt_name);
            btnDelete = (Button) convertView.findViewById(R.id.btn_delete);
            btnShow = (Button) convertView.findViewById(R.id.btn_show);

            txtID.setText(instructor.getId().toString());
            txtName.setText(instructor.getDisplayName());
            btnDelete.setTag(instructor);
            btnShow.setTag(instructor);

            setEventListener();

            return convertView;
        }

        private void setEventListener() {
            // Delete an instructor.
            btnDelete.setOnClickListener(new View.OnClickListener() {
                Instructor localInstructor;
                @Override
                public void onClick(View v) {
                    // Get the instructor to be deleted.
                    localInstructor = (Instructor) v.getTag();

                    // Check if there is any assigned course of this instructor.
                    // We won't delete the instructor if he/she has assigned course.
                    CourseDao courseDao = mDaoSession.getCourseDao();
                    long numOfEntry = courseDao.queryBuilder()
                            .where(CourseDao.Properties.InstructorId.eq(localInstructor.getId()))
                            .count();
                    if (numOfEntry > 0) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage("This instructor has assigned course(s). Please remove the course first!")
                                .setPositiveButton("Ok", null)
                                .show();
                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setMessage("Are you sure you want to delete this instructor?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        localInstructor.delete();
                                        remove(localInstructor);
                                        notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                }
            });

            // Show assigned courses of the instructor
            btnShow.setOnClickListener(new View.OnClickListener() {
                Instructor localInstructor;
                @Override
                public void onClick(View v) {
                    localInstructor = (Instructor) v.getTag();
                    CourseDao courseDao = mDaoSession.getCourseDao();
                    List<Course> courses = courseDao.queryBuilder()
                            .where(CourseDao.Properties.InstructorId.eq(localInstructor.getId()))
                            .list();
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
                        new AlertDialog.Builder(getActivity())
                                .setView(listView)
                                .show();
                    }
                }
            });
        }
    }
}
