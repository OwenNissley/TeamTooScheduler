package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuickSchedule
{
    private int numCredits;
    private int currentCredits;
    private Schedule schedule;
    //private ArrayList<ArrayList<Course>> courses; // List of courses (each course is a list of time slots or options)

    //maps a course to an arraylist of courses, each of which maps a course to a priorty
    private ArrayList<Map<Course, Integer>> courses;
    private ArrayList<Boolean> coursesAdded; // List of booleans to track if a courses has been added


    //constructor
    public QuickSchedule(int numCredits) {
        // Initialize the QuickSchedule object
        this.numCredits = numCredits;
        this.currentCredits = 0;
        this.schedule = new Schedule();
        this.courses = new ArrayList<>();
        this.coursesAdded = new ArrayList<>(); // Initialize the list of booleans
    }

    //adds an arraylist of course
    public void addCourses(ArrayList<Course> course) {
        //add the courses
        Map <Course, Integer> courseMap = new HashMap<>();
        for (Course c : course) {
            courseMap.put(c, 0); // Initialize the priority to 0
        }
        courses.add(courseMap);
        coursesAdded.add(false);
    }

    //adds an arraylist of course into the given index, replacing what was there before
    public void addCourses(ArrayList<Course> course, int index) {
        // Add the courses at the specified index
        if (index >= 0 && index < courses.size()) {
            //add the courses
            Map <Course, Integer> courseMap = new HashMap<>();
            for (Course c : course) {
                courseMap.put(c, 0); // Initialize the priority to 0
            }
            courses.set(index, courseMap);
        } else {
            System.out.println("Index out of bounds");
        }
    }

    //calculate scheduel
    public int calculateSchedule() {
        // Calculate the schedule based on the added courses
        // This method should implement the logic to create a schedule
        // based on the courses added to the QuickSchedule

        System.out.println("Calculating schedule...");

        System.out.println("Checking for empty courseMaps");
        //zero, check for empty courseMaps
        for (int i = 0; i < courses.size() - 1; i++) {
            Map<Course, Integer> courseMap = courses.get(i);
            //if courseMap is empty, return i
            if (courseMap.isEmpty() && !coursesAdded.get(i)) {
                System.out.println("Course map is empty at index " + i + ", failing calculateSchedule");
                return i;
            }
        }

        //calculate priorities
        calculatePriorities();

        System.out.println("Adding courses to schedule (0 and only)");
        //second, calculate the schedule with required courses (courses besides last)
        //add priorities, starting at 0 to the schedule OR if the course is the only one of its kind
        for (int i = 0; i < courses.size() - 1; i++) {
            if (coursesAdded.get(i)) {
                continue; // Skip if the course has already been added
            }
            Map<Course, Integer> courseMap = courses.get(i);

            for (int entryIndex = 0; entryIndex < courseMap.size(); entryIndex++) {
                Map.Entry<Course, Integer> entry = (Map.Entry<Course, Integer>) courseMap.entrySet().toArray()[entryIndex];
                Course course = entry.getKey();
                int priority = entry.getValue();

                // Check if the course is the only one in the map
                if (courseMap.size() == 1 || priority == 0) {
                    // Check if the course can be added to the schedule
                    if (!schedule.hasConflict(course)) {
                        schedule.addCourse(course);
                        System.out.println("Adding course " + course.getName() + " to schedule");
                        currentCredits += course.getCredits();
                        coursesAdded.set(i, true); // Mark the course as added

                        // Cycle through all other courses and remove any conflicting courses from the map
                        for (int j = 0; j < courses.size() - 1; j++) {
                            if (j == i) {
                                continue; // Skip the same course
                            }
                            Map<Course, Integer> otherCourseMap = courses.get(j);
                            for (int k = 0; k < otherCourseMap.size(); k++) {
                                Course otherCourse = (Course) otherCourseMap.keySet().toArray()[k];
                                if (course.hasConflict(otherCourse)) {
                                    // Remove the course from the map
                                    otherCourseMap.remove(otherCourse);
                                    k--; // Adjust the index after removal to avoid skipping elements
                                }
                            }
                        }

                        //remove all courses from the map
                        courseMap.clear(); // Clear the map after adding the course

                        break;
                    } else {
                        System.out.println("Course " + course.getName() + " is conlicting, requesting less filters.");
                        return i; // Return the index of the course that caused the conflict
                        // Remove the course from the map
                        //courseMap.remove(course);
                        //entryIndex--; // Adjust the index after removal to avoid skipping elements
                    }
                }
            }
        }
        System.out.println("Adding priorities to schedule (1 and up)");
        //add priorities, starting at 1 and incrementing
        int priorityLevel = 0;
        while (!allCoursesAdded() && priorityLevel < 100) { // Limit to 100 iterations to prevent infinite loop
            priorityLevel++;
            for (int i = 0; i < courses.size() - 1; i++) {
                if (coursesAdded.get(i)) {
                    continue; // Skip if the course has already been added
                }
                Map<Course, Integer> courseMap = courses.get(i);

                // Check if the course map is empty
                if (courseMap.isEmpty()) {
                    return i; // Return the index of the empty course map
                }

                for (int entryIndex = 0; entryIndex < courseMap.size(); entryIndex++) {
                    Map.Entry<Course, Integer> entry = (Map.Entry<Course, Integer>) courseMap.entrySet().toArray()[entryIndex];
                    Course course = entry.getKey();
                    int priority = entry.getValue();

                    // Check if the course is the only one in the map
                    if (courseMap.size() == 1 || priority == priorityLevel) {
                        // Check if the course can be added to the schedule
                        if (!schedule.hasConflict(course)) {
                            schedule.addCourse(course);
                            System.out.println("Adding course " + course.getName() + " to schedule");
                            currentCredits += course.getCredits();
                            coursesAdded.set(i, true); // Mark the course as added

                            // Cycle through all other courses and remove any conflicting courses from the map
                            for (int j = 0; j < courses.size() - 1; j++) {
                                if (j == i) {
                                    continue; // Skip the same course
                                }
                                Map<Course, Integer> otherCourseMap = courses.get(j);
                                for (int otherEntryIndex = 0; otherEntryIndex < otherCourseMap.size(); otherEntryIndex++) {
                                    Map.Entry<Course, Integer> otherEntry = (Map.Entry<Course, Integer>) otherCourseMap.entrySet().toArray()[otherEntryIndex];
                                    Course otherCourse = otherEntry.getKey();
                                    if (course.hasConflict(otherCourse)) {
                                        // Remove the course from the map
                                        otherCourseMap.remove(otherCourse);
                                        otherEntryIndex--; // Adjust the index after removal to avoid skipping elements
                                    }
                                }
                            }

                            //remove all courses from the map
                            courseMap.clear(); // Clear the map after adding the course

                            break;
                        } else {
                            System.out.println("Course " + course.getName() + " is conflicted, requesting less filters.");
                            return i; // Return the index of the course that caused the conflict
                            // Remove the course from the map
                            //courseMap.remove(course);
                            //entryIndex--; // Adjust the index after removal to avoid skipping elements
                        }
                    }
                }
            }
        }

        System.out.println("Reviewing required schedule...");
        //if any courses werent added, return the index
        for (int i = 0; i < courses.size() - 1; i++) {
            if (!coursesAdded.get(i)) {
                System.out.println("Course " + i + " was not added to the schedule, failing.");
                return i; // Return the index of the course that wasn't added
            }
        }

        //third, calculate total credits from required courses (courses besides the last)
        //we only need to check one key in the map since they are all the same
        //for (Map<Course, Integer> courseMap : courses) {
        //    //if map has no entries, skip
        //    if (courseMap.isEmpty()) {
        //        continue;
        //    }
        //    // Get the first entry in the map
        //    Map.Entry<Course, Integer> entry = courseMap.entrySet().iterator().next();
        //    Course course = entry.getKey();
        //   currentCredits += course.getCredits();
        //}
        // Check if the current credits exceed the maximum allowed credits
        if (currentCredits > numCredits) {
            System.out.println("Required courses exceed maximum allowed credits, overriding");
            return -1;
        }
        else if (currentCredits == numCredits) {
            System.out.println("All required courses added, returning success");
            return -1; // Return -1 if all required courses have been added
        }

        //fourth, calculate the schedule with optional courses (last courses)
        //NOTE, this works best assuming the list of courses if filtered from the preferences
        //while the last map in the arraylist has next, and current credits is less than the max
        //for all elements in the last map in the arraylist (use standard for loop)
        System.out.println("Adding optional courses to schedule...");
        //print credits met and credits needed
        System.out.println("Current credits: " + currentCredits);
        System.out.println("Credits needed: " + numCredits);
        Map<Course, Integer> lastCourseMap = courses.get(courses.size() - 1);
        // Check if the last course map is empty
        if (lastCourseMap.isEmpty()) {
            System.out.println("Last course map is empty, failing.");
            return 7; // Return 7 if the last course map is empty
        }
        for (int entryIndex = 0; entryIndex < lastCourseMap.size(); entryIndex++) {
            Map.Entry<Course, Integer> entry = (Map.Entry<Course, Integer>) lastCourseMap.entrySet().toArray()[entryIndex];
            Course course = entry.getKey();

            // Check if the course can be added to the schedule
            if (!schedule.hasConflict(course)) {
                schedule.addCourse(course);
                System.out.println("Adding course " + course.getName() + " to schedule");
                currentCredits += course.getCredits();
                //coursesAdded.set(courses.size() - 1, true); // Mark the course as added
                //lastCourseMap.remove(course); // Remove the course from the map
                //entryIndex--; // Adjust the index after removal to avoid skipping elements
            }

            //print credits
            //System.out.println("Current credits: " + currentCredits);
            //System.out.println("Credits needed: " + numCredits);

            // Check if the current credits exceed the maximum allowed credits
            if (currentCredits > numCredits) {
                System.out.println("Optional courses exceed maximum allowed credits, overriding and returning success");
                return -1; // Return -1 if the current credits exceed the maximum allowed credits
            }
            else if (currentCredits == numCredits) {
                System.out.println("All courses added, returning success");
                return -1; // Return -1 if all courses have been added
            }
            else {
                continue;
            }
        }

        // Check if the current credits does not meet the maximum allowed credits
        if (currentCredits < numCredits) {
            System.out.println("Optional courses cannot meet maximum allowed credits, returning problem");
            return 7; // Return -1 if the current credits exceed the maximum allowed credits
        }

        System.out.println("All courses added, returning success (I dont think this should ever print");
        return -1; //success return value
    }

    //getter for current credits
    public int getCurrentCredits() {
        return currentCredits;
    }

    //getter for schedule
    public Schedule getSchedule() {
        return schedule;
    }

    //return true if all courses have been added (except for the last one)
    public boolean allCoursesAdded() {
        for (int i = 0; i < coursesAdded.size() - 1; i++) {
            if (!coursesAdded.get(i)) {
                return false;
            }
        }
        return true;
    }


    //calculate priorities
    public void calculatePriorities() {
        System.out.println("Calculating priorities...");
        //first, calculate priorities
        for (int i = 0; i < courses.size() - 1; i++) {
            Map<Course, Integer> courseMap = courses.get(i);

            for (Map.Entry<Course, Integer> entry : courseMap.entrySet()) {
                Course course = entry.getKey();
                int priority = entry.getValue();

                //cycle through all other courses and increment priority if time conflicts
                for (int j = 0; j < courses.size() - 1; j++) {
                    if (j == i) {
                        continue; // Skip the same course
                    }
                    Map<Course, Integer> otherCourseMap = courses.get(j);
                    for (Map.Entry<Course, Integer> otherEntry : otherCourseMap.entrySet()) {
                        Course otherCourse = otherEntry.getKey();
                        if (course.hasConflict(otherCourse)) {
                            priority++;
                        }
                    }
                }
                //cycle through all courses in the schedule and increment priority if time conflicts
                if (schedule.getCourses() != null || !schedule.getCourses().isEmpty()) {
                    if (schedule.hasConflict(course))
                    {
                        priority++;
                    }
                }
            }
        }
    }
}
