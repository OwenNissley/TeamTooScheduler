package edu.gcc.comp350.teamtoo;


import java.lang.reflect.Array;
import java.util.ArrayList;
public class Schedule
{
    private ArrayList<Course> courses;
    private int scheduleID;

    //used for course conflicts
    private boolean isConflict; //true if two or more courses conflict
    private ArrayList<Course> conflictingCourses; //list of courses that conflict

    //used for undo and redo
    private ScheduleHistory history;

    private static int idCounter = 1; // Unique ID generator for schedules  //Micah -  not sure if we need this

    public Schedule(){
        this.courses = new ArrayList<>();
        this.scheduleID = idCounter++;
        this.history = new ScheduleHistory(courses);
        isConflict = false;
        conflictingCourses = new ArrayList<>();
    }

    public Schedule(ArrayList<Course> courses){
        this.courses = new ArrayList<>(courses);
        this.scheduleID = idCounter++;
        this.history = new ScheduleHistory(courses);
        isConflict = false;
        conflictingCourses = new ArrayList<>();
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
            checkConflict();
        }
    }
    public void removeCourse(int index) {
        if (index >= 0 && index < courses.size()) {
            courses.remove(index);
            history.updateHistory(courses); // call this at the end
            checkConflict();
        }
    }
    public void addCourse(Course course) {
        if (!hasCourse(course)) {
            courses.add(course);
            checkConflict(course);
            history.updateHistory(courses); // call this at the end
        }
    }
    public void setSchedule(ArrayList<Course> newCourses){
        this.courses = newCourses;
        history.updateHistory(courses);
    }

    public void clearSchedule() {
        courses.clear();
        history.updateHistory(courses); // call this at the end
    }

    public void printHistory(){
        history.printHistory();
    }




    //-------------------------------------------------------------------------------------------------------------
    //THE FOLLOWING IS FOR UNDO AND REDO

    public void undoAdd()
    {
        history.getPrev(courses);
        courses = new ArrayList<>(history.getCurrentNodeData()); //I anticipate this not working
    }

    public void undoRemove()
    {
        history.getPrev(courses);
        courses = new ArrayList<>(history.getCurrentNodeData()); //I anticipate this not working
    }

    public void redoAdd()
    {
        history.getNext(courses);
        courses = new ArrayList<>(history.getCurrentNodeData()); //I anticipate this not working
    }

    public void redoRemove()
    {
        history.getNext(courses);
        courses = new ArrayList<>(history.getCurrentNodeData()); //I anticipate this not working
    }

    public void undoAction() {
        history.getPrev(courses);
        courses = new ArrayList<>(history.getCurrentNodeData());
    }
    public void redoAction() {
        history.getNext(courses);
        courses = new ArrayList<>(history.getCurrentNodeData());
    }

    //END UNDO AND REDO
    //-------------------------------------------------------------------------------------------------------------




    //-------------------------------------------------------------------------------------------------------------
    //THE FOLLOWING IS FOR COURSE CONFLICTS

    //check if course conflicts with any other course in the schedule
    //used with adding (NOTE: course should still be added to schedule)
    public void checkConflict(Course course)
    {
        for (Course c : courses)
        {
            //if course conflicts with c and c is not the same course
            if (course.hasConflict(c) && !course.equals(c))
            {
                isConflict = true;
                conflictingCourses.add(c);
                //if course not in conflictingCourses, add it
                if (!conflictingCourses.contains(course))
                {
                    conflictingCourses.add(course);
                }
            }
        }
    }

    //check if any course conflicts with any other course in the schedule
    //used anywhere
    public void checkConflict()
    {
        isConflict = false;
        conflictingCourses.clear();

        for (Course c1 : courses)
        {
            for (Course c2 : courses)
            {
                //if c1 and c2 are the same course, skip
                if (c1.hasConflict(c2) && !c1.equals(c2))
                {
                    isConflict = true;

                    //if course is not in conflictingCourses, add it
                    if (!conflictingCourses.contains(c1))
                    {
                        conflictingCourses.add(c1);
                    }
                    if (!conflictingCourses.contains(c2))
                    {
                        conflictingCourses.add(c2);
                    }
                }
            }
        }
    }

    //getters
    public boolean getIsConflict() {return isConflict;}

    //get conflicting courses
    public ArrayList<Course> getConflictingCourses() {return conflictingCourses;}

    //END COURSE CONFLICTS
    //-------------------------------------------------------------------------------------------------------------

}


