package edu.gcc.comp350.teamtoo;

import java.sql.SQLOutput;
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
        search = new Search(courseRegistry.getCourses());


    }

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
        schedules.get(selectedSchedule).clearSchedule();
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

    //deletes the currently selected schedule
    //from the list of schedules
    public int deleteSchedule() {
        if(schedules.size() == 1){
            return 1;
        }
        schedules.remove(selectedSchedule);
        if(selectedSchedule >= schedules.size()){
            selectedSchedule = schedules.size() - 1;
        }
        return 0;
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

        //if schedules is empty, add a new schedule
        if(schedules.isEmpty()){
            schedules.add(new Schedule());
        }

        selectedSchedule = 0; //reset selected schedule to the first one
    }



    public void quickSchedule() {}



    //-------------------------------------------------------------------------------------------------------------
    //HERE DOWN IS FOR SEARCHING

    //creates search and maintains search, eventually returning search results
    private Search search;

    public void addFilter(Filter filter) {
        if (filter.getFilterType() == FilterType.SEMESTER) {
            search.filterBasedOnSemester(filter);
        }
        else
        {
            search.addFilter(filter);
        }
    }

    public void removeFilter(Filter filter) {
        search.removeFilter(filter);
    }

    public void searchGeneral(String searchTerm)
    {
        search.searchGeneral(searchTerm);
    }

    public void searchAdvanced()
    {
        searchResults = search.searchAdvanced();
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

    public void redoAdd() {
        schedules.get(selectedSchedule).redoAdd();
    }

    public void redoRemove() {
        schedules.get(selectedSchedule).redoRemove();
    }
    //END UNDO/REDO
    //-------------------------------------------------------------------------------------------------------------

}