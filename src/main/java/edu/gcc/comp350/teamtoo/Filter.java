package edu.gcc.comp350.teamtoo;

abstract public class Filter
{
    protected FilterType filterType;

    public Filter(FilterType filterType)
    {
        this.filterType = filterType;
    }

    public FilterType getFilterType()
    {
        return filterType;
    }

    //returns true if course is valid based on filter value
    public abstract boolean filtersCourse(Course course);
}
