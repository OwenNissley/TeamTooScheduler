package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;

//Note: filters are created "above" this class
public class Search
{
    private ArrayList<Filter> activeFilters;
    private ArrayList<Course> termFilteredCourses;
    private ArrayList<Course> generalFilteredCourses;
    private final ArrayList<Course> allCourses;

    public Search(ArrayList<Course> allCourses) {
        this.allCourses = allCourses;

        this.termFilteredCourses = new ArrayList<>();
        this.activeFilters = new ArrayList<>();
        this.generalFilteredCourses = new ArrayList<>();
    }

    //add classes to define termFilteredCourses right away (before other searches can be doen)
    //add checks to make sure term is selected prior to other searches
    public void filterBasedOnTerm(String term)
    {
        Filter termFilter = new FilterTerm(term);
        for (Course course : allCourses) {
            if (termFilter.filtersCourse(course))
                termFilteredCourses.add(course);
        }
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
            //for (Course course : termFilteredCourses) {
            //    generalFilteredCourses.add(course); // we don't need to deep copy course, since they don't change
            //}
            pointer = termFilteredCourses; //I think we can get away with this
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

    //does not return, only updates generalFilteredCourses from termFilteredCourses
    public void SearchGeneral(String searchTerm)
    {
        generalFilteredCourses.clear();
        //fill generalFilteredCourses.add() based on searchTerm
        if (termFilteredCourses.isEmpty()){
            System.out.println("\\u001B[31mERROR: no term filtered courses.\\u001B[0m\" ");
        }else {
            GeneralSearch genSearch = new GeneralSearch(termFilteredCourses);
            ArrayList<Course> results = genSearch.searchCourses(searchTerm);
            for (Course course : results) {
                generalFilteredCourses.add(course);
            }
        }
    }

}
