package edu.gcc.comp350.teamtoo;


import java.lang.reflect.Array;
import java.util.ArrayList;
public class Schedule
{
    private ArrayList<Course> courses;
    private int scheduleID;

    //used for undo and redo
    private ScheduleHistory history;

    private static int idCounter = 1; // Unique ID generator for schedules

    public Schedule(){
        this.courses = new ArrayList<>();
        this.scheduleID = idCounter++;
        this.history = new ScheduleHistory(courses);
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public ArrayList<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public boolean hasCourse(Course course) {
        return courses.contains(course);
    }

    public void loadSchedule() {}
    public void saveSchedule() {}
    public void printSchedule() {
        if (courses.isEmpty()) {
            System.out.println("Schedule ID: " + scheduleID + " is empty.");
        } else {
            System.out.println("Schedule ID: " + scheduleID + " contains:");
            for (Course c : courses) {
                System.out.println("- " + c.getName());
            }
        }
    }
    public boolean hasConflict() {
        return false;
    }
    public void removeCourse(Course course) {
        if (courses.remove(course)) {
            history.updateHistory(courses); // call this at the end
        }
    }
    public void addCourse(Course course) {
        if (!hasCourse(course)) {
            courses.add(course);
            history.updateHistory(courses); // call this at the end
        }
    }
    public void setSchedule(ArrayList<Course> newCourses){
        this.courses = newCourses;
        history.updateHistory(courses);
    }
    public void undoAction() {history.getPrev(courses);}
    public void redoAction() {history.getNext(courses);}
}


