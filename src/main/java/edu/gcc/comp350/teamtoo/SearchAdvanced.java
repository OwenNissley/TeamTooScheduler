package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;

public class SearchAdvanced
{
    private ArrayList<Filter> activeFilters;
    private ArrayList<Course> filteredCourses;
    private ArrayList<Course> allCourses;

    public SearchAdvanced(ArrayList<Course> allCourses) {
        this.allCourses = allCourses;
    }

    public void removeFilter() {}

    public void refreshCourses() {}
    public void addFilter(Filter filter) {}
}
