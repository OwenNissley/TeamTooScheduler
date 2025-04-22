package edu.gcc.comp350.teamtoo;
/**
 * MICAH YOU MUST COME SEEEE ME BEFORE YOU REDSEIGN< TAKE NOTE WHERE I CHANGED ------ I NEED THEM
 */

import java.sql.SQLOutput;
import java.util.ArrayList;

// Monk only changed two things and they are labeled

//Note: filters are created "above" this class
public class Search
{
    private ArrayList<Filter> activeFilters;
    //private ArrayList<Course> semesterFilteredCourses;
    private ArrayList<Course> generalFilteredCourses;
    //private final ArrayList<Course> allCourses;
    private boolean generalSearchExecuted; //used to determine if general search has been executed

    //constructor for empty
    public Search () {
        //this.allCourses = new ArrayList<>();
        //this.semesterFilteredCourses = new ArrayList<>();
        this.activeFilters = new ArrayList<>();
        this.generalFilteredCourses = new ArrayList<>();
        this.generalSearchExecuted = false; //set to false since no search has been executed
    }

    //removes a filter
    public void removeFilter(Filter filter) {
        activeFilters.remove(filter);
    }


    //a new filter is created when addFilter is called
    public void addFilter(Filter filter) {
        activeFilters.add(filter);
    }

    //removed, SearchAdvanced will always be called to return filter courses
    //public ArrayList<Course> getFilteredCourses() { return filteredCourses; }

    //-------------------------------------------------------------------------------
    //Monk
    //added HEHHEHHEHHEHHEHEHEHHHEHHEH HAAA
    //Get filters method
    public ArrayList<Filter> getActiveFilters() {
        return activeFilters;
    }
    //clears all filters - Monk made public
    public void clearFilters() {
        activeFilters.clear();
    }

    //Monk added thsi get genealEarchExuited in order to reset from backed end core
    public boolean getGeneralSearchExecuted() {
        return generalSearchExecuted;
    }

    //set generalSearchExecuted - monk
    public void setGeneralSearchExecuted(boolean generalSearchExecuted) {
        this.generalSearchExecuted = generalSearchExecuted;
    }

//--------------------------------------------------------------------------------------------------


    public ArrayList<Course> searchAdvanced(ArrayList<Course> semesterFilteredCourses)
    {
        //FOR TESTING PURPOSES ONLY
        //print out size of semesterFilteredCourses
        try {
            System.out.println("Semester Filtered Courses Size: " + semesterFilteredCourses.size());
        } catch (NullPointerException e) {
            System.out.println("\\u001B[31mERROR: no term filtered courses.\\u001B[0m\" ");
            return null;
        }

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
            //remove this following line??? - Monk commented line out
           // generalFilteredCourses.clear();

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
        // Testing pursposes only - iam not copying entire boshi just to comment out line - bum ahh gaydos heheheh
       // generalSearchExecuted = false;

        //for now, may be changed later,
        // Testing pursposes only - iam not copying entire boshi just to comment out line - bum ahh gaydos heheheh
      //  clearFilters();

        //the following is for errors
        // Testing pursposes only - iam not copying entire boshi just to comment out line - bum ahh gaydos heheheh
      //  generalFilteredCourses.clear();

        //TESTING PURPOSES ONLY
        System.out.println("Advanced Search Size: " + advancedFilteredCourses.size());

        return advancedFilteredCourses;
    }

    //does not return, only updates generalFilteredCourses from semesterFilteredCourses
    public void searchGeneral(String searchTerm, ArrayList<Course> semesterFilteredCourses)
    {
        //FOR TESTING PURPOSES ONLY
        //print out size of semesterFilteredCourses
        System.out.println("Semester Filtered Courses Size: " + semesterFilteredCourses.size());

        //Monk to get it to clear with an empty search term, to fufill condion in search advanced wich
        // requires false booleans and empty gen earch results
        if (searchTerm.isEmpty()) {
            generalFilteredCourses.clear();
            generalSearchExecuted = false;
            return;
        }

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
