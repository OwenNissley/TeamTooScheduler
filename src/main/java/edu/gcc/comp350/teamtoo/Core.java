package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;

public class Core {
    private ArrayList<Schedule> schedules;
    private int selectedSchedule;
    private CourseRegistry courseRegistry;
    private ArrayList<Course> searchResults;
    private FileReadWriter FRW;

    public Core() {

        //initializes 'schedules' from the file 'StoredSchedules.txt' if it exists,
        // otherwise initializes it as empty.
        FRW = new FileReadWriter();
        try{
            schedules = FRW.readScheduleFromFile("StoredSchedules.txt");
        }
        catch(Exception e){
            schedules = new ArrayList<>();
        }

        //if schedules is empty, add a new schedule
        if(schedules.isEmpty()){
            schedules.add(new Schedule());
        }



        selectedSchedule = 0;

        //init course registry
        courseRegistry = new CourseRegistry();
        courseRegistry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");

        //init search
        search = new Search(courseRegistry.getCourses());

        //For testing
        //schedules.get(selectedSchedule).addCourse(courseRegistry.getCourses().get(0));
        //schedules.get(selectedSchedule).addCourse(courseRegistry.getCourses().get(1));
        //schedules.get(selectedSchedule).addCourse(courseRegistry.getCourses().get(2));
    }
    //writes 'schedules' into the file 'StoredSchedules.txt'
    //saving the current schedules the user has
    public void saveSchedulesIntoFile(){
        FRW.readScheduleIntoFile("StoredSchedules.txt", schedules);
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