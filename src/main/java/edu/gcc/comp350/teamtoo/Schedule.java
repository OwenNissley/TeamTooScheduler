package edu.gcc.comp350.teamtoo;


import java.lang.reflect.Array;
import java.util.ArrayList;
public class Schedule
{
    private ArrayList<Course> courses;
    private int scheduleID;

    //used for undo and redo
    private ScheduleHistory history;


    public Schedule(){
        courses = new ArrayList<Course>();
        // set schedule ID here
        history = new ScheduleHistory(courses);
    }


    public void loadSchedule() {}
    public void saveSchedule() {}
    public void printSchedule() {}
    public boolean hasConflict() {
        return false;
    }
    public void removeCourse() {
        //do all of the needed stuff
        history.updateHistory(courses); //call this at the end
    }
    public void addCourse() {
        //do all of the needed stuff
        history.updateHistory(courses); //call this at the end
    }
    public void setSchedule(ArrayList<Course> newCourses){
        courses = newCourses;
    }
    public void undoAction() {history.getPrev(courses);}
    public void redoAction() {history.getNext(courses);}
}


