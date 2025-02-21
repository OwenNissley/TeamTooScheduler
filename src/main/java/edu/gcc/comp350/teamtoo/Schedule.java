package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;

public class Schedule
{
    private ArrayList<Course> courses;
    private int scheduleID;
    private Course lastAdded;
    private Course lastRemoved;

    public void loadSchedule() {}
    public void saveSchedule() {}
    public void printSchedule() {}
    public boolean hasConflict() {
        return false;
    }
    public void removeCourse() {}
    public void addCourse() {}
    public void undoLastAdd() {}
    public void undoLastRemove() {}
}
