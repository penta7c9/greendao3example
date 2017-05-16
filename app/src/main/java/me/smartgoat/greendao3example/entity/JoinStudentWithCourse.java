package me.smartgoat.greendao3example.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class JoinStudentWithCourse {
    @Id
    private Long id;
    private Long studentId;
    private Long courseId;
    @Generated(hash = 1071939148)
    public JoinStudentWithCourse(Long id, Long studentId, Long courseId) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
    }
    @Generated(hash = 491688389)
    public JoinStudentWithCourse() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getStudentId() {
        return this.studentId;
    }
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    public Long getCourseId() {
        return this.courseId;
    }
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
