package edu.gcc.comp350.teamtoo;

abstract public class Filter
{
    protected String filterType;

    public Filter(String filterType) {
        this.filterType = filterType;
    }

    public abstract String getType();
    public abstract String getValue();
}
