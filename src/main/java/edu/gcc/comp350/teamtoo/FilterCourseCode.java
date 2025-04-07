package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;
import java.util.Objects;

// Subclass extending the abstract class "Filter"
public class FilterCourseCode extends Filter {

    private final int courseCode;

    public FilterCourseCode(int courseCode) {
        super(FilterType.COURSE_CODE);
        this.courseCode = courseCode;
    }

    @Override
    public boolean filtersCourse(Course course) {
        return course.getNumber() == courseCode;
    }

    public static void main(String[] args) {
        ArrayList<Course> filteredCourses = new ArrayList<>();
        CourseRegistry registry = new CourseRegistry();
        registry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json"); // Load courses

        Filter codeFilter = new FilterCourseCode(350);
        for (Course course : registry.getCourses()) {
            if (codeFilter.filtersCourse(course)) {
                filteredCourses.add(course);
            }
        }

        // Print filtered courses
        for (Course course : filteredCourses) {
            System.out.println(course.getName() + ":" + course.getNumber() );
        }
    }
}
