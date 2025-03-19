package edu.gcc.comp350.teamtoo;

import java.sql.SQLOutput;
import java.util.ArrayList;

//Note: filters are created "above" this class
public class Search
{
    private ArrayList<Filter> activeFilters;
    private ArrayList<Course> semesterFilteredCourses;
    private ArrayList<Course> generalFilteredCourses;
    private final ArrayList<Course> allCourses;
    private boolean generalSearchExecuted; //used to determine if general search has been executed

    //constructor for empty
    public Search () {
        this.allCourses = new ArrayList<>();
        this.semesterFilteredCourses = new ArrayList<>();
        this.activeFilters = new ArrayList<>();
        this.generalFilteredCourses = new ArrayList<>();
        this.generalSearchExecuted = false; //set to false since no search has been executed
    }

    public Search(ArrayList<Course> allCourses) {
        this.allCourses = allCourses;

        this.semesterFilteredCourses = new ArrayList<>();
        this.activeFilters = new ArrayList<>();
        this.generalFilteredCourses = new ArrayList<>();

        //TESTING PURPOSES ONLY
        System.out.println("All Course Size: " + allCourses.size());
    }

    //add classes to define semesterFilteredCourses right away (before other searches can be doen)
    //add checks to make sure term is selected prior to other searches
    public void filterBasedOnSemester(Filter semesterFilter)
    {
        semesterFilteredCourses.clear();
        //Filter termFilter = new FilterSemester(semesterFilter);
        for (Course course : allCourses) {
            if (semesterFilter.filtersCourse(course))
                semesterFilteredCourses.add(course);
        }

        //TESTING PERPOSES ONLY
        System.out.println("Semester Filtered Size: " + semesterFilteredCourses.size());
    }

    //clear generalFilteredCourses
    private void clearGeneralFilteredCourses() {
        generalFilteredCourses.clear();
    }

    //removes a filter
    public void removeFilter(Filter filter) {
        activeFilters.remove(filter);
    }

    //clears all filters
    private void clearFilters() {
        activeFilters.clear();
    }

    //a new filter is created when addFilter is called
    public void addFilter(Filter filter) {
        activeFilters.add(filter);
    }

    //removed, SearchAdvanced will always be called to return filter courses
    //public ArrayList<Course> getFilteredCourses() { return filteredCourses; }

    public ArrayList<Course> searchAdvanced()
    {
        ArrayList<Course> advancedFilteredCourses = new ArrayList<>();
        ArrayList<Course> pointer = generalFilteredCourses;

        //this is inefficient and needs changed at some point
        if (generalFilteredCourses.isEmpty() && !generalSearchExecuted)
        {
            //for (Course course : semesterFilteredCourses) {
            //    generalFilteredCourses.add(course); // we don't need to deep copy course, since they don't change
            //}
            pointer = semesterFilteredCourses; //I think we can get away with this
        }

        if (activeFilters.isEmpty())
        {
            //TESTING PURPOSES ONLY
            System.out.println("NO FILTERS...Size: " + pointer.size());

            //the following is to prevent error
            //deep copy pointer to advancedFilteredCourses
            for (Course course : pointer) {
                advancedFilteredCourses.add(course);
            }
            clearGeneralFilteredCourses();

            return advancedFilteredCourses;
        }

        boolean firstFilter = true;
        for(Filter filter : activeFilters)
        {
            if (firstFilter) {
                firstFilter = false;

                for (Course course : pointer) {
                    //filtersCourse returns true if the course is valid
                    if (filter.filtersCourse(course)) {
                        advancedFilteredCourses.add(course);
                    }
                }
            }
            else
            {
                ArrayList<Course> tempFilteredCourses = new ArrayList<>();
                for (Course course : advancedFilteredCourses) {
                    if (filter.filtersCourse(course)) {
                        tempFilteredCourses.add(course);
                    }
                }
                advancedFilteredCourses = tempFilteredCourses;
            }
        }

        //clear generalSearchExecuted so that it can be used for the next search
        generalSearchExecuted = false;

        //for now, may be changed later
        clearFilters();

        //the following is for errors
        clearGeneralFilteredCourses();

        //TESTING PURPOSES ONLY
        System.out.println("Advanced Search Size: " + advancedFilteredCourses.size());

        return advancedFilteredCourses;
    }

    //does not return, only updates generalFilteredCourses from semesterFilteredCourses
    public void searchGeneral(String searchTerm)
    {
        generalFilteredCourses.clear();
        //fill generalFilteredCourses.add() based on searchTerm
        if (semesterFilteredCourses.isEmpty()){
            System.out.println("\\u001B[31mERROR: no term filtered courses.\\u001B[0m\" ");
        }else {
            GeneralSearch genSearch = new GeneralSearch(semesterFilteredCourses);
            ArrayList<Course> results = genSearch.searchCourses(searchTerm);
            for (Course course : results) {
                generalFilteredCourses.add(course);
            }
        }

        generalSearchExecuted = true; //set to true so that searchAdvanced can use generalFilteredCourses

        //TESTING PURPOSES ONLY
        System.out.println("General Search Size: " + generalFilteredCourses.size());
    }

}
