package edu.gcc.comp350.teamtoo;
//NOTES FOR MICAH"S RETSARTED ASSS
//View My cahnges - I Need all Funciotnalkty - DO NOT REMOVE functionality
//
//
//
//
//
//
//
// -------------------------------------------------------------------------------------------------------------



import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class coreTest {
    private ArrayList<Schedule> schedules;
    private int selectedSchedule;
    private CourseRegistry courseRegistry;
    private ArrayList<Course> searchResults;
    private FileReadWriter FRW;

    //fixing schedules
    private String semester;
    //make a  map of semester to an arraylist of schedules
    private Map<String, ArrayList<Schedule>> semesterSchedules;


    public coreTest() {

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

        }
    }

    public void addCourse(int courseIndex) {
        //if (selectedSchedule < schedules.size()) {
        //Schedule schedule = schedules.get(selectedSchedule);
        schedules.get(selectedSchedule).addCourse(searchResults.get(courseIndex));
        searchResults.remove(courseIndex);
        //}
    }

    // All added or changed methods below
    //------------------------------------------------------------------------------------
    // Added
    public ArrayList<Course> getNonConflictingCourses() {
    ArrayList<Course> nonConflictingCourses = new ArrayList<>();
        for (Course course : this.getSchedule()) {
            if (!this.getConflictingCourses().contains(course)) {
                nonConflictingCourses.add(course);
            }
        }
        return nonConflictingCourses;
    }
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



    //------------------------------------------------------------------------------------






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

    public void searchCourse()
    {


    }


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
        //remove courses that are already in the schedule
        //CANT DO THIS
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
