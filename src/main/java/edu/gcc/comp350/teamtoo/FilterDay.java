package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;

// Subclass extending the abstract class "Filter"
public class FilterDay extends Filter {

    private String dayPattern;

    public FilterDay(String dayPattern) {
        super(FilterType.DAY_OF_WEEK);
        if (isValidDayPattern(dayPattern)) {
            this.dayPattern = dayPattern;
        } else {
            throw new RuntimeException("Invalid day format, must be a valid pattern like MWF or TR");
        }
    }

    @Override
    public boolean filtersCourse(Course course) {
        String courseDays = course.daysString();
        for (char day : dayPattern.toCharArray()) {
            if (courseDays.contains(String.valueOf(day))) {
                return true;
            }
        }
        return false;
    }
    private boolean isValidDayPattern(String dayPattern) {
        return dayPattern.matches("(?i)(MWF|TR)");
    }

    public static void main(String[] args) {
        ArrayList<Course> filteredCourses = new ArrayList<>();
        CourseRegistry registry = new CourseRegistry();
        registry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json"); // Load courses

        Filter dayFilter = new FilterDay("MWF");
        for (Course course : registry.getCourses()) {
            if (dayFilter.filtersCourse(course)) {
                filteredCourses.add(course);
            }
        }

        // Print filtered courses
        for (Course course : filteredCourses) {
            System.out.println(course.getName() + " - Meets on " + course.daysString());
        }
    }
}
