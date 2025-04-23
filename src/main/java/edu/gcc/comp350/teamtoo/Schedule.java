package edu.gcc.comp350.teamtoo;


import java.util.ArrayList;
public class Schedule
{
    private ArrayList<Course> courses;
    private int scheduleID;

    //used for course conflicts
    private boolean isConflict; //true if two or more courses conflict
    private ArrayList<Course> conflictingCourses; //list of courses that conflict

    //used for undo and redo
    private ArrayList<Course> history;

    private static int idCounter = 1; // Unique ID generator for schedules  //Micah -  not sure if we need this

    public Schedule(){
        this.courses = new ArrayList<>();
        this.scheduleID = idCounter++;
        this.history = new ArrayList<>();
        isConflict = false;
        conflictingCourses = new ArrayList<>();
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public void setIdCounter(int value) {
        idCounter = value;
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

    public boolean removeCourse(Course course) {
        if (courses.remove(course)) {
            history.add(course); // call this at the end
            checkConflict();
            return true;
        }
        else {
            return false;
        }
    }
    public void removeCourse(int index) {
        if (index >= 0 && index < courses.size()) {
            removeCourse(courses.get(index));
        }
    }
    public boolean addCourse(Course course) {
            if (!courses.contains(course)) {
                courses.add(course);
                history.add(course); // call this at the end
                checkConflict(course);
                return true;
            }
            else {
                //System.out.println("Course already exists in schedule.");
                return false;
            }
    }

    //remove or replace
    public void setSchedule(ArrayList<Course> newCourses){
        this.courses = newCourses;
        //history.updateHistory(courses);
    }

    public void removeAllCourses() {
        //history.add(courses); // call this at the end

        courses.clear();

        // recheck conflicts
        checkConflict();

    }




    //-------------------------------------------------------------------------------------------------------------
    //THE FOLLOWING IS FOR UNDO AND REDO

    public void undoAdd()
    {
        if (!history.isEmpty()) {
            //remove last course in list
            if(removeCourse(history.get(history.size() - 1))) {
                //remove it twice because remove will add it back
                history.remove(history.size() - 1);
                history.remove(history.size() - 1);
            }
            else
                history.clear();
        }
    }

    public void undoRemove()
    {
        if (!history.isEmpty()) {
            //add last course in list
            if(addCourse(history.get(history.size() - 1))) {
                //remove it twice because add will add it back
                history.remove(history.size() - 1);
                history.remove(history.size() - 1);
            }
            else
                history.clear();
        }
    }

    public void clearUndoRedoHistory()
    {
        history.clear();
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
                //if course is not in conflictingCourses, add it
                if (!conflictingCourses.contains(c))
                {
                    conflictingCourses.add(c);
                }
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

    //added for QuickSchedule, but may have other uses
    public boolean hasConflict(Course course)
    {
        for (Course c : courses)
        {
            //if course conflicts with c or  c is the same course
            if (course.hasConflict(c) || course.equals(c))
            {
                return true;
                //if course is not in conflictingCourses, add it
            }
        }
        return false;
    }

    //getters
    public boolean getIsConflict() {return isConflict;}

    //get conflicting courses
    public ArrayList<Course> getConflictingCourses() {return conflictingCourses;}

    //END COURSE CONFLICTS
    //-------------------------------------------------------------------------------------------------------------

}


