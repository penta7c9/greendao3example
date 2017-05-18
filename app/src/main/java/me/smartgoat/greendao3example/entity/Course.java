package me.smartgoat.greendao3example.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.Date;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.ToOne;

@Entity
public class Course {
    @Id
    private Long id;

    @NotNull
    private String name;

    private Date startDate;

    private Date endDate;

    private String room;

    private Long instructorId;

    @ToOne(joinProperty = "instructorId")
    private Instructor instructor;

    @ToMany
    @JoinEntity(
            entity = JoinStudentWithCourse.class,
            sourceProperty = "courseId",
            targetProperty = "studentId"
    )
    private List<Student> students;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2063667503)
    private transient CourseDao myDao;

    @Generated(hash = 1731742807)
    private transient Long instructor__resolvedKey;

    @Generated(hash = 1637002947)
    public Course(Long id, @NotNull String name, Date startDate, Date endDate,
            String room, Long instructorId) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.room = room;
        this.instructorId = instructorId;
    }

    @Generated(hash = 1355838961)
    public Course() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRoom() {
        return this.room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Long getInstructorId() {
        return this.instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1555632920)
    public List<Student> getStudents() {
        if (students == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StudentDao targetDao = daoSession.getStudentDao();
            List<Student> studentsNew = targetDao._queryCourse_Students(id);
            synchronized (this) {
                if (students == null) {
                    students = studentsNew;
                }
            }
        }
        return students;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 238993120)
    public synchronized void resetStudents() {
        students = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 604667510)
    public Instructor getInstructor() {
        Long __key = this.instructorId;
        if (instructor__resolvedKey == null || !instructor__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            InstructorDao targetDao = daoSession.getInstructorDao();
            Instructor instructorNew = targetDao.load(__key);
            synchronized (this) {
                instructor = instructorNew;
                instructor__resolvedKey = __key;
            }
        }
        return instructor;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 924339613)
    public void setInstructor(Instructor instructor) {
        synchronized (this) {
            this.instructor = instructor;
            instructorId = instructor == null ? null : instructor.getId();
            instructor__resolvedKey = instructorId;
        }
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 94420068)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCourseDao() : null;
    }
}
