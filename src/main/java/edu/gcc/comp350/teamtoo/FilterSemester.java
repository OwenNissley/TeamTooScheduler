package edu.gcc.comp350.teamtoo;

public class FilterSemester extends Filter {

    private final String term; // year_Spring/Fall

    public FilterSemester(String term) {
        super(FilterType.SEMESTER);
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
