package edu.gcc.comp350.teamtoo;

public class FilterSemester extends Filter {

    private String term; // year_Spring/Fall
    //private final String year;
    //private final String semester;

    public FilterSemester(String year, String semester) {
        super(FilterType.SEMESTER);
        this.term = year + "_" + semester;

        if (!term.matches("\\d{4}_(Fall|Spring)")) {
            throw new IllegalArgumentException("Invalid term format. Expected format: YYYY_Season (e.g., 2023_Fall)");
        }
        this.term = term;
    }

    //returns true if course is valid based on filter value
    public boolean filtersCourse(Course course) {
        boolean doesFilterFit = false;
        // Search for courses matching the term
        if (course.getSemester().equals(term)) {
            doesFilterFit = true;
        }
        return doesFilterFit;
    }
}
