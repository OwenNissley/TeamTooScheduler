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


    public Schedule(){
        courses = new ArrayList<Course>();
        // set schedule ID here
        history = new ScheduleHistory(courses);
        isConflict = false;
    }


    public void loadSchedule() {}
    public void saveSchedule() {}
    public void printSchedule() {}
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

    //END COURSE CONFLICTS
    //-------------------------------------------------------------------------------------------------------------

}


