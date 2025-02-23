package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;

//Note: filters are created "above" this class
public class Search
{
    private ArrayList<Filter> activeFilters;
    private ArrayList<Course> generalFilteredCourses;
    private final ArrayList<Course> allCourses;

    public Search(ArrayList<Course> allCourses) {
        this.allCourses = allCourses;

        this.activeFilters = new ArrayList<>();
        this.generalFilteredCourses = new ArrayList<>();
    }

    //clear generalFilteredCourses
    public void clearGeneralFilteredCourses() {
        generalFilteredCourses.clear();
    }

    //removes a filter
    public void removeFilter(Filter filter) {
        activeFilters.remove(filter);
    }

    //clears all filters
    public void clearFilters() {
        activeFilters.clear();
    }

    //a new filter is created when addFilter is called
    public void addFilter(Filter filter) {
        activeFilters.add(filter);
    }

    //removed, SearchAdvanced will always be called to return filter courses
    //public ArrayList<Course> getFilteredCourses() { return filteredCourses; }

    public ArrayList<Course> SearchAdvanced()
    {
        ArrayList<Course> advancedFilteredCourses = new ArrayList<>();
        ArrayList<Course> pointer = generalFilteredCourses;

        //this is inefficient and needs changed at some point
        if (generalFilteredCourses.isEmpty())
        {
            //for (Course course : allCourses) {
            //    generalFilteredCourses.add(course); // we don't need to deep copy course, since they don't change
            //}
            pointer = allCourses; //I think we can get away with this
        }

        if (activeFilters.isEmpty())
        {
            return generalFilteredCourses;
        }

        for(Filter filter : activeFilters)
        {
            for(Course course : pointer)
            {
                //filtersCourse returns true if the course is valid
                if (filter.filtersCourse(course))
                {
                    advancedFilteredCourses.add(course);
                }
            }
        }

        return advancedFilteredCourses;
    }

    //does not return, only updates generalFilteredCourses
    public void SearchGeneral(String searchTerm)
    {
        generalFilteredCourses.clear();

        //fill generalFilteredCourses.add() based on searchTerm
    }

}
