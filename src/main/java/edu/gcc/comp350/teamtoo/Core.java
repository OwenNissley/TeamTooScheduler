package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;

public class Core {
    private ArrayList<Schedule> schedules;
    private int selectedSchedule;
    private CourseRegistry courseRegistry;
    private ArrayList<Course> searchResults;

    public Core() {
        schedules = new ArrayList<>();


        //eventually this will be replaced with a method to load schedules from a file if they exist, otherwise create a new schedule
        schedules.add(new Schedule());


        selectedSchedule = 0;

        //init course registry
        courseRegistry = new CourseRegistry();
        courseRegistry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");

        //init search
        search = new Search(courseRegistry.getCourses());

        //For testing
        schedules.get(selectedSchedule).addCourse(courseRegistry.getCourses().get(0));
        schedules.get(selectedSchedule).addCourse(courseRegistry.getCourses().get(1));
        schedules.get(selectedSchedule).addCourse(courseRegistry.getCourses().get(2));
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
        for (Course course : schedules.get(selectedSchedule).getCourses()) {
            schedules.get(selectedSchedule).removeCourse(course);
        }
    }

    //write code to return current schedule
    public ArrayList<Course> getSchedule() {
        return schedules.get(selectedSchedule).getCourses();
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

}