package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Core {
    private ArrayList<Schedule> schedules;
    private int selectedSchedule;
    private CourseRegistry courseRegistry;
    private ArrayList<Course> searchResults;
    private FileReadWriter FRW;
    private ArrayList<Course> conflictingCourses = new ArrayList<>();

    //fixing schedules
    private String semester;
    //make a  map of semester to an arraylist of schedules
    private Map<String, ArrayList<Schedule>> semesterSchedules;


    public Core() {

        //init course registry
        courseRegistry = new CourseRegistry();
        courseRegistry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");

        //initializes 'schedules' from the file 'StoredSchedules.txt' if it exists,
        // otherwise initializes it as empty.
        FRW = new FileReadWriter(courseRegistry.getCourses());
        try{
            semesterSchedules = FRW.readScheduleFromFile("StoredSchedules.txt");

            //check for conflicts in every schedule
            //for (Map.Entry<String, ArrayList<Schedule>> entry : semesterSchedules.entrySet()) {
            for (String semesterKey : semesterSchedules.keySet()) {
                //String semesterKey = entry.getKey();
                schedules = semesterSchedules.get(semesterKey);
                for (Schedule s : schedules) {
                    s.checkConflict();
                }
            }
        }
        catch(Exception e){
            //make an empty semesterSchedule
            System.out.println("StoredSchedules.txt not found, creating new schedules.");
            semesterSchedules = new HashMap<>();
        }

        //initialize semester
        semester = "2023_Fall"; //default semester
        updateSemester(semester);

        //set selected schedule to the first schedule
        selectedSchedule = 0;

        //init search
        search = new Search();


    }

    public Map<String, Object> parseCourseInformation(String courseId) {
        Course course = courseRegistry.getCourseById(courseId); // Ensure courseRegistry has this method
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }

        Map<String, Object> courseDetails = new HashMap<>();
        courseDetails.put("name", course.getName());
        courseDetails.put("credits", course.getCredits());
        courseDetails.put("faculty", course.getFaculty());
        courseDetails.put("location", course.getLocation());
        courseDetails.put("openSeats", course.getOpenSeats());
        courseDetails.put("totalSeats", course.getTotalSeats());
        courseDetails.put("times", course.getTimes());
        courseDetails.put("subject", course.getSubject());
        courseDetails.put("number", course.getNumber());
        courseDetails.put("section", course.getSection());
        courseDetails.put("semester", course.getSemester());

        return courseDetails;
    }

    /*public ArrayList<Map<String, Object>> parseCourseInformation() {
        ArrayList<Course> courses = courseRegistry.getCourses(semester); // Retrieve courses for the current semester
        ArrayList<Map<String, Object>> detailedCourses = new ArrayList<>();

        for (Course course : courses) {
            Map<String, Object> courseDetails = new HashMap<>();
            courseDetails.put("name", course.getName());
            courseDetails.put("credits", course.getCredits());
            courseDetails.put("faculty", course.getFaculty());
            courseDetails.put("location", course.getLocation());
            courseDetails.put("openSeats", course.getOpenSeats());
            courseDetails.put("totalSeats", course.getTotalSeats());
            courseDetails.put("times", course.getTimes());
            courseDetails.put("subject", course.getSubject());
            courseDetails.put("number", course.getNumber());
            courseDetails.put("section", course.getSection());
            courseDetails.put("semester", course.getSemester());
            detailedCourses.add(courseDetails);
        }

        return detailedCourses; // Return the detailed course list
    }
    */

    //writes 'schedules' into the file 'StoredSchedules.txt'
    //saving the current schedules the user has
    public void saveSchedulesIntoFile(){
        FRW.readScheduleIntoFile("StoredSchedules.txt", semesterSchedules);
    }

    public void addCourse(Course course) {
        if (selectedSchedule < schedules.size()) {
            Schedule schedule = schedules.get(selectedSchedule);
            schedule.addCourse(course);
            updateConflictingCoursesInSearchResults(course);
        }
    }

    public void addCourse(int courseIndex) {
        //if (selectedSchedule < schedules.size()) {
            //Schedule schedule = schedules.get(selectedSchedule);
            schedules.get(selectedSchedule).addCourse(searchResults.get(courseIndex));
            //searchResults.remove(courseIndex);
            updateConflictingCoursesInSearchResults(searchResults.get(courseIndex));
        //}
    }

    public void removeCourse(Course course) {
        if (selectedSchedule < schedules.size()) {
            Schedule schedule = schedules.get(selectedSchedule);
            schedule.removeCourse(course);
        }
    }

    public void removeCourse(int courseIndex) {
        schedules.get(selectedSchedule).removeCourse(courseIndex);
    }

    public void removeAllCourses() {
        schedules.get(selectedSchedule).removeAllCourses();
    }

    public ArrayList<Course> getConflictingCourses() {
        return schedules.get(selectedSchedule).getConflictingCourses();
    }

    //write code to return current schedule
    public ArrayList<Course> getSchedule() {
        return schedules.get(selectedSchedule).getCourses();
    }

    //creates a new Schedule for the user to work with leaves
    //the old on in a separate spot in 'schedules' effectively saving it
    public void newSchedule() {
        Schedule newSchedule = new Schedule();
        schedules.add(newSchedule);
        selectedSchedule = schedules.indexOf(newSchedule);
    }

    public void loadSchedule(Schedule schedule){
        selectedSchedule = schedules.indexOf(schedule);
    }

    public Schedule getCurrentSchedule() {
        return schedules.get(selectedSchedule);
    }

    public ArrayList<Schedule> getSavedSchedules() {
        return schedules;
    }

    //update the semester
    //semester is a String such as "2023_Fall"
    public void updateSemester(String semester) {
        this.semester = semester;

        //if the semester is not already in the map, create a new entry
        if (!semesterSchedules.containsKey(semester)) {
            semesterSchedules.put(semester, new ArrayList<>());
        }
        schedules = semesterSchedules.get(semester);

        //print confirmation
        System.out.println("Semester updated to: " + semester);

        //if schedules is empty, add a new schedule
        if(schedules.isEmpty()){
            schedules.add(new Schedule());
        }

        selectedSchedule = 0; //reset selected schedule to the first one

        //print confirmation
        System.out.println("Selected schedule updated to: " + selectedSchedule);
    }



    public void quickSchedule() {}



    //-------------------------------------------------------------------------------------------------------------
    //HERE DOWN IS FOR SEARCHING

    //creates search and maintains search, eventually returning search results
    private Search search;

    public void addFilter(Filter filter) {
            search.addFilter(filter);
    }

    public void removeFilter(Filter filter) {
        search.removeFilter(filter);
    }

    public void searchGeneral(String searchTerm)
    {
        search.searchGeneral(searchTerm, courseRegistry.getCourses(semester));
    }

    public void searchAdvanced()
    {
        searchResults = search.searchAdvanced(courseRegistry.getCourses(semester));
        calculateConflictingCoursesInSearchResults();

    }

    public ArrayList<Course> getSearchResults() {
        return searchResults;
    }

    //END SEARCHING
    //-------------------------------------------------------------------------------------------------------------





    //-------------------------------------------------------------------------------------------------------------
    //THE FOLLOWING IS FOR CONFLICTING SCHEDULES

    //check if the current schedule has any conflicts
    public boolean checkConflicts()
    {
        return schedules.get(selectedSchedule).getIsConflict();
    }

    //returns a list of conflicting courses in searchResults
    public ArrayList<Course> getConflictingCoursesInSearchResults()
    {
        return conflictingCourses;
    }

    //monkeys return for conflicting courses - BRUUUUUUUUU it was suposed to be nonConflciting,
    // IT WAS NAMED getNonConflictingCourses
    //then you retuend conflicting courses - bruuu
    // - Crashed th whole ahhh app, cuase used to fetch- BRUUUUUUU
    // Your gonna get hurt
    public ArrayList<Course> getNonConflictingCourses()
    {
        //return conflictingCourses; - NO NO NO ON NO
        // get all courses in the selected schedule
        //and remove the conflicting courses

        ArrayList<Course> nonConflictingCourses = new ArrayList<>(schedules.get(selectedSchedule).getCourses());
        nonConflictingCourses.removeAll(schedules.get(selectedSchedule).getConflictingCourses());

        return nonConflictingCourses;


    }



    //calculates a list of conflicting courses in searchResults
    public void calculateConflictingCoursesInSearchResults()
    {
        conflictingCourses.clear();
        for (Course c : searchResults)
        {
            for (Course s : schedules.get(selectedSchedule).getCourses())
            {
                //if course conflicts with s and s is not the same course
                if (c.hasConflict(s) && !c.equals(s))
                {
                    conflictingCourses.add(c);
                }
            }
        }
    }

    public void updateConflictingCoursesInSearchResults(Course s)
    {
        for (Course c : searchResults)
        {
            //if course conflicts with s and s is not the same course
            if (c.hasConflict(s) && !c.equals(s))
            {
                conflictingCourses.add(c);
            }
        }
    }


    //END CONFLICTING SCHEDULES
    //-------------------------------------------------------------------------------------------------------------



    //-------------------------------------------------------------------------------------------------------------
    //THE FOLLOWING IS FOR UNDO/REDO
    public void undoAdd() {
        schedules.get(selectedSchedule).undoAdd();
    }

    public void undoRemove() {
        schedules.get(selectedSchedule).undoRemove();
    }

    public void clearUndoRedoHistory() {
        schedules.get(selectedSchedule).clearUndoRedoHistory();
    }

    //END UNDO/REDO
    //-------------------------------------------------------------------------------------------------------------


    //Monk frontend additions
    public int getNumOfSchedules() {
        return schedules.size();
    } //addded

    public void setSelectedSchedule(int selectedSchedule) {
        this.selectedSchedule = selectedSchedule;
    } //added
    public int getSelectedSchedule() {
        return selectedSchedule;
    } //added


    //changed
    public int deleteSchedule() {
        if(schedules.size() == 1){
            schedules.remove(0);
            newSchedule();
            return 0; //changed 1 -> 0
        }else {
            int oldSelectedSchedule = selectedSchedule; //added
            schedules.remove(selectedSchedule);
            if (oldSelectedSchedule ==0){
                selectedSchedule = 0; //added
            }else {
                selectedSchedule = oldSelectedSchedule - 1; //added
            }
        }
        return selectedSchedule; //added
    }

    // get generalSearchExecuted
    public boolean getGeneralSearchExecuted() {
        return search.getGeneralSearchExecuted();
    }

    //set generalSearchExecuted
    public void setGeneralSearchExecuted(boolean generalSearchExecuted) {
        search.setGeneralSearchExecuted(generalSearchExecuted);
    }

    //ADDED by Monk
    // Get active filters on search
    public ArrayList<Filter> getActiveFilters() {
        return search.getActiveFilters();
    }
    // clear all filters
    public void clearAllFilters() {
        search.clearFilters();
    }
}