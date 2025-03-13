package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;

public class Core {
    private ArrayList<Schedule> schedules;
    private int selectedSchedule;
    private CourseRegistry courseRegistry;

    public Core() {
        schedules = new ArrayList<>();
        selectedSchedule = 0;

        //init course registry
        courseRegistry = new CourseRegistry();
        courseRegistry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");

        //init search
        search = new Search(courseRegistry.getCourses());
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
        ArrayList<Course> searchResults = search.searchAdvanced();
    }

    public void hasConflict() {}


}
