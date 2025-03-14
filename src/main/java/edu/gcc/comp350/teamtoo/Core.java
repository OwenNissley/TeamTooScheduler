package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;

public class Core {
    //private static Core instance;

    private ArrayList<Schedule> schedules;
    private int selectedSchedule;
    private CourseRegistry courseRegistry;

    /*private Core() {
        this.schedules = new ArrayList<>();
        this.selectedSchedule = 0; // Default selection
        this.courseRegistry = new CourseRegistry();
    }

    public static Core getInstance() {
        if (instance == null) {
            instance = new Core();
        }
        return instance;
    }*/

    public void searchGeneral() {
    }

    public void quickSchedule() {
    }

    public void searchCourse() {
    }

    public void hasConflict() {
    }

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