package com.dcvs.service;

import com.dcvs.dao.AuditLogDAO;
import com.dcvs.dao.CourseDAO;
import com.dcvs.model.AuditLog;
import com.dcvs.model.Course;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Business logic for course management.
 */
public class CourseService {

    private final CourseDAO   courseDAO = new CourseDAO();
    private final AuditLogDAO auditDAO  = new AuditLogDAO();

    public boolean addCourse(String name, String category, String description, String duration) {
        Course c = new Course(0, name, category, description, duration, true, LocalDateTime.now());
        boolean ok = courseDAO.insert(c);
        if (ok) audit("ADD_COURSE", name, "Category: " + category);
        return ok;
    }

    public boolean updateCourse(Course course) {
        boolean ok = courseDAO.update(course);
        if (ok) audit("UPDATE_COURSE", course.getCourseName(), "Updated");
        return ok;
    }

    public boolean deleteCourse(int courseId) {
        Optional<Course> c = courseDAO.findById(courseId);
        boolean ok = courseDAO.delete(courseId);
        if (ok) c.ifPresent(course -> audit("DELETE_COURSE", course.getCourseName(), "Deleted"));
        return ok;
    }

    public List<Course> findAll()    { return courseDAO.findAll(); }
    public List<Course> findActive() { return courseDAO.findActive(); }
    public Optional<Course> findById(int id) { return courseDAO.findById(id); }

    private void audit(String action, String target, String details) {
        String actor = SessionManager.getInstance().getUsername();
        auditDAO.insert(new AuditLog(0, action, actor, target, details, LocalDateTime.now()));
    }
}
