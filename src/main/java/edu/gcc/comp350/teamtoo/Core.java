package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;

public class Core {
    private ArrayList<Schedule> schedules;
    private int selectedSchedule;
    private CourseRegistry courseRegistry;

    public void searchGeneral() {}
    public void quickSchedule() {}
    public void searchCourse() {}
    public void hasConflict() {}

    public void addCourse(Course course) {
        if (selectedSchedule < schedules.size()) {
            Schedule schedule = schedules.get(selectedSchedule);
            schedule.addCourse(course);

        }
    }

    public void removeCourse(Course course) {
        if (selectedSchedule < schedules.size()) {
            Schedule schedule = schedules.get(selectedSchedule);
            schedule.removeCourse(course);
        }
    }
}
