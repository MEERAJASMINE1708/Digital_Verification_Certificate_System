package com.dcvs.model;

import java.time.LocalDateTime;

/**
 * POJO representing a skill-based course offered by the organization.
 */
public class Course {

    private int           courseId;
    private String        courseName;
    private String        category;
    private String        description;
    private String        duration;
    private boolean       active;
    private LocalDateTime createdAt;

    public Course() {}

    public Course(int courseId, String courseName, String category,
                  String description, String duration, boolean active,
                  LocalDateTime createdAt) {
        this.courseId   = courseId;
        this.courseName = courseName;
        this.category   = category;
        this.description = description;
        this.duration   = duration;
        this.active     = active;
        this.createdAt  = createdAt;
    }

    public int           getCourseId()   { return courseId; }
    public String        getCourseName() { return courseName; }
    public String        getCategory()   { return category; }
    public String        getDescription(){ return description; }
    public String        getDuration()   { return duration; }
    public boolean       isActive()      { return active; }
    public LocalDateTime getCreatedAt()  { return createdAt; }

    public void setCourseId(int v)            { courseId = v; }
    public void setCourseName(String v)       { courseName = v; }
    public void setCategory(String v)         { category = v; }
    public void setDescription(String v)      { description = v; }
    public void setDuration(String v)         { duration = v; }
    public void setActive(boolean v)          { active = v; }
    public void setCreatedAt(LocalDateTime v) { createdAt = v; }

    @Override
    public String toString() { return courseName; } // used in JComboBox
}
