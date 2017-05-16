package me.smartgoat.greendao3example;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import layout.CourseFragment;
import layout.InstructorFragment;
import layout.StudentFragment;
import me.smartgoat.greendao3example.entity.Student;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mContent;
    private CourseFragment courseFragment;
    private InstructorFragment instructorFragment;
    private StudentFragment studentFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_course:
                    getFragmentManager().beginTransaction()
                            .replace(R.id.content, courseFragment).commit();
                    return true;
                case R.id.navigation_teacher:
                    getFragmentManager().beginTransaction()
                            .replace(R.id.content, instructorFragment).commit();
                    return true;
                case R.id.navigation_student:
                    getFragmentManager().beginTransaction()
                            .replace(R.id.content, studentFragment).commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContent = (FrameLayout) findViewById(R.id.content);

        courseFragment = new CourseFragment();
        instructorFragment = new InstructorFragment();
        studentFragment = new StudentFragment();

        getFragmentManager().beginTransaction()
                .replace(R.id.content, courseFragment).commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
