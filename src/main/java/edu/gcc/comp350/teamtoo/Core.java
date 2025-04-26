package edu.gcc.comp350.teamtoo;

import io.javalin.plugin.bundled.RouteOverviewPlugin;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class Core {
    private ArrayList<Schedule> schedules;
    private int selectedSchedule;
    private CourseRegistry courseRegistry;
    private ArrayList<Course> searchResults;
    private FileReadWriter FRW;
    private ArrayList<Course> conflictingCourses = new ArrayList<>();

    //fixing schedules
    private String semester;
    //make a  map of semester to an arraylist of schedules
    private Map<String, ArrayList<Schedule>> semesterSchedules;


    public Core() {

        //init course registry
        courseRegistry = new CourseRegistry();
        courseRegistry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");

        //initializes 'schedules' from the file 'StoredSchedules.txt' if it exists,
        // otherwise initializes it as empty.
        FRW = new FileReadWriter(courseRegistry.getCourses());
        try{
            semesterSchedules = FRW.readScheduleFromFile("StoredSchedules.txt");

            //check for conflicts in every schedule
            //for (Map.Entry<String, ArrayList<Schedule>> entry : semesterSchedules.entrySet()) {
            for (String semesterKey : semesterSchedules.keySet()) {
                //String semesterKey = entry.getKey();
                schedules = semesterSchedules.get(semesterKey);
                for (Schedule s : schedules) {
                    s.checkConflict();
                }
            }
        }
        catch(Exception e){
            //make an empty semesterSchedule
            System.out.println("StoredSchedules.txt not found, creating new schedules.");
            semesterSchedules = new HashMap<>();
        }

        //initialize semester
        semester = "2023_Fall"; //default semester
        updateSemester(semester);

        //set selected schedule to the first schedule
        selectedSchedule = 0;

        //init search
        search = new Search();


    }

    public String getClosestSuggestion(String searchTerm) {
        String closestMatch = null;
        int closestDistance = Integer.MAX_VALUE;

        for (Course course : courseRegistry.getCourses(semester)) {
            String courseName = course.getName();
            int distance = calculateLevenshteinDistance(searchTerm.toLowerCase(), courseName.toLowerCase());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestMatch = courseName; // Return the full course name
            }
        }

        return closestMatch;
    }

    private int calculateLevenshteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                        dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1),
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
                    );
                }
            }
        }

        return dp[a.length()][b.length()];
    }

    public Map<String, Object> parseCourseInformation(String courseId) {
        Course course = courseRegistry.getCourseById(courseId); // Ensure courseRegistry has this method
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }

        Map<String, Object> courseDetails = new HashMap<>();
        courseDetails.put("name", course.getName());
        courseDetails.put("credits", course.getCredits());
        courseDetails.put("faculty", course.getFaculty());
        courseDetails.put("location", course.getLocation());
        courseDetails.put("openSeats", course.getOpenSeats());
        courseDetails.put("totalSeats", course.getTotalSeats());
        courseDetails.put("times", course.getTimes());
        courseDetails.put("subject", course.getSubject());
        courseDetails.put("number", course.getNumber());
        courseDetails.put("section", course.getSection());
        courseDetails.put("semester", course.getSemester());

        return courseDetails;
    }

    /*public ArrayList<Map<String, Object>> parseCourseInformation() {
        ArrayList<Course> courses = courseRegistry.getCourses(semester); // Retrieve courses for the current semester
        ArrayList<Map<String, Object>> detailedCourses = new ArrayList<>();

        for (Course course : courses) {
            Map<String, Object> courseDetails = new HashMap<>();
            courseDetails.put("name", course.getName());
            courseDetails.put("credits", course.getCredits());
            courseDetails.put("faculty", course.getFaculty());
            courseDetails.put("location", course.getLocation());
            courseDetails.put("openSeats", course.getOpenSeats());
            courseDetails.put("totalSeats", course.getTotalSeats());
            courseDetails.put("times", course.getTimes());
            courseDetails.put("subject", course.getSubject());
            courseDetails.put("number", course.getNumber());
            courseDetails.put("section", course.getSection());
            courseDetails.put("semester", course.getSemester());
            detailedCourses.add(courseDetails);
        }

        return detailedCourses; // Return the detailed course list
    }
    */

    //writes 'schedules' into the file 'StoredSchedules.txt'
    //saving the current schedules the user has
    public void saveSchedulesIntoFile(){
        FRW.readScheduleIntoFile("StoredSchedules.txt", semesterSchedules);
    }

    public void addCourse(Course course) {
        if (selectedSchedule < schedules.size()) {
            Schedule schedule = schedules.get(selectedSchedule);
            schedule.addCourse(course);
            updateConflictingCoursesInSearchResults(course);
        }
    }

    public void addCourse(int courseIndex) {
        //if (selectedSchedule < schedules.size()) {
            //Schedule schedule = schedules.get(selectedSchedule);
            schedules.get(selectedSchedule).addCourse(searchResults.get(courseIndex));
            //searchResults.remove(courseIndex);
            updateConflictingCoursesInSearchResults(searchResults.get(courseIndex));
        //}
    }

    public void removeCourse(Course course) {
        if (selectedSchedule < schedules.size()) {
            Schedule schedule = schedules.get(selectedSchedule);
            schedule.removeCourse(course);
        }
    }

    public void removeCourse(int courseIndex) {
        schedules.get(selectedSchedule).removeCourse(courseIndex);
    }

    public void removeAllCourses() {
        schedules.get(selectedSchedule).removeAllCourses();
    }

    public ArrayList<Course> getConflictingCourses() {
        return schedules.get(selectedSchedule).getConflictingCourses();
    }

    //write code to return current schedule
    public ArrayList<Course> getSchedule() {
        return schedules.get(selectedSchedule).getCourses();
    }

    //creates a new Schedule for the user to work with leaves
    //the old on in a separate spot in 'schedules' effectively saving it
    public void newSchedule() {
        Schedule newSchedule = new Schedule();
        schedules.add(newSchedule);
        selectedSchedule = schedules.indexOf(newSchedule);
    }

    public void loadSchedule(Schedule schedule){
        selectedSchedule = schedules.indexOf(schedule);
    }

    public Schedule getCurrentSchedule() {
        return schedules.get(selectedSchedule);
    }



    public ArrayList<Schedule> getSavedSchedules() {
        return schedules;
    }

    //update the semester
    //semester is a String such as "2023_Fall"
    public void updateSemester(String semester) {
        this.semester = semester;

        //if the semester is not already in the map, create a new entry
        if (!semesterSchedules.containsKey(semester)) {
            semesterSchedules.put(semester, new ArrayList<>());
        }
        schedules = semesterSchedules.get(semester);

        //print confirmation
        System.out.println("Semester updated to: " + semester);

        //if schedules is empty, add a new schedule
        if(schedules.isEmpty()){
            schedules.add(new Schedule());
        }

        selectedSchedule = 0; //reset selected schedule to the first one

        //print confirmation
        System.out.println("Selected schedule updated to: " + selectedSchedule);
    }






    //-------------------------------------------------------------------------------------------------------------
    //HERE DOWN IS FOR SEARCHING

    //creates search and maintains search, eventually returning search results
    private Search search;

    public void addFilter(Filter filter) {
            search.addFilter(filter);
    }

    public void removeFilter(Filter filter) {
        search.removeFilter(filter);
    }

    public void searchGeneral(String searchTerm)
    {
        search.searchGeneral(searchTerm, courseRegistry.getCourses(semester));
    }

    public void searchAdvanced()
    {
        searchResults = search.searchAdvanced(courseRegistry.getCourses(semester));
        //calculateConflictingCoursesInSearchResults();

    }

    public ArrayList<Course> getSearchResults() {
        return searchResults;
    }

    //END SEARCHING
    //-------------------------------------------------------------------------------------------------------------





    //-------------------------------------------------------------------------------------------------------------
    //THE FOLLOWING IS FOR CONFLICTING SCHEDULES

    //check if the current schedule has any conflicts
    public boolean checkConflicts()
    {
        return schedules.get(selectedSchedule).getIsConflict();
    }

    //returns a list of conflicting courses in searchResults
    public ArrayList<Course> getConflictingCoursesInSearchResults()
    {
        return conflictingCourses;
    }

    //monkeys return for conflicting courses - BRUUUUUUUUU it was suposed to be nonConflciting,
    // IT WAS NAMED getNonConflictingCourses
    //then you retuend conflicting courses - bruuu
    // - Crashed th whole ahhh app, cuase used to fetch- BRUUUUUUU
    // Your gonna get hurt
    public ArrayList<Course> getNonConflictingCourses()
    {
        //return conflictingCourses; - NO NO NO ON NO
        // get all courses in the selected schedule
        //and remove the conflicting courses

        ArrayList<Course> nonConflictingCourses = new ArrayList<>(schedules.get(selectedSchedule).getCourses());
        nonConflictingCourses.removeAll(schedules.get(selectedSchedule).getConflictingCourses());

        return nonConflictingCourses;


    }



    //calculates a list of conflicting courses in searchResults
    public void calculateConflictingCoursesInSearchResults()
    {
        conflictingCourses.clear();
        for (Course c : searchResults)
        {
            for (Course s : schedules.get(selectedSchedule).getCourses())
            {
                //if course conflicts with s and s is not the same course
                if (c.hasConflict(s) && !c.equals(s))
                {
                    conflictingCourses.add(c);
                }
            }
        }
    }

    public void updateConflictingCoursesInSearchResults(Course s)
    {
        for (Course c : searchResults)
        {
            //if course conflicts with s and s is not the same course
            if (c.hasConflict(s) && !c.equals(s))
            {
                conflictingCourses.add(c);
            }
        }
    }


    //END CONFLICTING SCHEDULES
    //-------------------------------------------------------------------------------------------------------------



    //-------------------------------------------------------------------------------------------------------------
    //THE FOLLOWING IS FOR UNDO/REDO
    public void undoAdd() {
        schedules.get(selectedSchedule).undoAdd();
    }

    public void undoRemove() {
        schedules.get(selectedSchedule).undoRemove();
    }

    public void clearUndoRedoHistory() {
        schedules.get(selectedSchedule).clearUndoRedoHistory();
    }

    //END UNDO/REDO
    //-------------------------------------------------------------------------------------------------------------


    //Monk frontend additions
    public int getNumOfSchedules() {
        return schedules.size();
    } //addded

    public void setSelectedSchedule(int selectedSchedule) {
        this.selectedSchedule = selectedSchedule;
    } //added
    public int getSelectedSchedule() {
        return selectedSchedule;
    } //added

    // get current semester
    public String getSemester() {
        return semester;
    } //added


    //changed
    public int deleteSchedule() {
        if(schedules.size() == 1){
            schedules.remove(0);
            newSchedule();
            return 0; //changed 1 -> 0
        }else {
            int oldSelectedSchedule = selectedSchedule; //added
            schedules.remove(selectedSchedule);
            if (oldSelectedSchedule ==0){
                selectedSchedule = 0; //added
            }else {
                selectedSchedule = oldSelectedSchedule - 1; //added
            }
        }
        return selectedSchedule; //added
    }

    // get generalSearchExecuted
    public boolean getGeneralSearchExecuted() {
        return search.getGeneralSearchExecuted();
    }

    //set generalSearchExecuted
    public void setGeneralSearchExecuted(boolean generalSearchExecuted) {
        search.setGeneralSearchExecuted(generalSearchExecuted);
    }

    //ADDED by Monk
    // Get active filters on search
    public ArrayList<Filter> getActiveFilters() {
        return search.getActiveFilters();
    }
    // clear all filters
    public void clearAllFilters() {
        search.clearFilters();
    }


    //-------------------------------------------------------------------------------------------------------------
    //THE FOLLOWING IS FOR QUICK SCHEDULE

    public boolean quickSchedule(String day, String startTime, String endTime, int numCredits, ArrayList<String> requiredCourses, int recursion)
    {
        System.out.println("Quick schedule called with recursion level: " + recursion);
        if (recursion > 2) {
            System.out.println("Recursion level too high, failing");
            return false;
        }
        boolean addedDayFilter = false;
        boolean addedTimeFilter = false;
        Filter dayFilter = null; //for some boshi error
        Filter timeFilter = null; //for some boshi error

        //clear all filters
        clearAllFilters();

        //create a filter for the day
        if (!(recursion > 0)) {
            if (day.equals("MWF") || day.equals("TR")) {
                dayFilter = new FilterDaysOfWeek(day);
                addFilter(dayFilter);
                addedDayFilter = true;
            }
        }

        //create a filter for the time
        if (!(recursion > 1)) {
            timeFilter = new FilterTime(startTime, endTime);
            addFilter(timeFilter);
            addedTimeFilter = true;
        }

        //create variables we need
        QuickSchedule qs = new QuickSchedule(numCredits);

        System.out.println("Searching based on searchTerms");
        //begin adding courses to potentialCourses based on required courses
        for (String searchTerm : requiredCourses) {
            //general search if search term is not empty
            if (!searchTerm.isEmpty()) {
                searchGeneral(searchTerm);
                qs.addCourses(search.searchAdvanced(courseRegistry.getCourses(semester)));
            }
        }

        //add courses to potentialCourses based on filters
        searchGeneral("");
        //add filters if not added already
        //create a filter for the day
        if (!addedDayFilter) {
            if (day.equals("MWF") || day.equals("TR")) {
                dayFilter = new FilterDaysOfWeek(day);
                addFilter(dayFilter);
            }
        }
        //create a filter for the time
        if (!addedTimeFilter) {
            timeFilter = new FilterTime(startTime, endTime);
            addFilter(timeFilter);
        }
        qs.addCourses(search.searchAdvanced(courseRegistry.getCourses(semester)));
        //if filters were only created for the general filtered classes, remove them
        if (!addedDayFilter) {
            removeFilter(dayFilter);
        }
        if (!addedTimeFilter) {
            removeFilter(timeFilter);
        }

        //try to calculate the schedule, if fail, then remove filters for search terms
        ArrayList<Integer> qsFailValue = new ArrayList<>();
        qsFailValue.add(qs.calculateSchedule()); //success as -1
        while (qsFailValue.get(qsFailValue.size()-1) != -1)
        {
            //qsFailValue has a maximum value of 7
            //if qsFailValue is 7, we cannot fully create a schedule, an should break out of while loop
            if (qsFailValue.get(qsFailValue.size()-1) == 7) {
                System.out.println("Quick schedule cannot meet credit limit OR no other classes meet filter requirements, generating schedule anyways");
                break;
            }

            //if the last qsFailValue has occurred 3 times or more, break out of loop
            int failCount = 0;
            for (int i = 0; i < qsFailValue.size(); i++) {
                if (qsFailValue.get(i) == qsFailValue.get(qsFailValue.size()-1)) {
                    failCount++;
                }
            }

            //if failCount >= 3, we cannot add required courses
            if (failCount >= 3) {
                System.out.println("Quick schedule cannot add required courses, retrying Quick schedule with reduced filters");
                return quickSchedule(day, startTime, endTime, numCredits, requiredCourses, recursion + 1);
            }
            //if failcount is 2, remove the day filter
            else if (failCount == 1) {
                System.out.println("Quick schedule cannot add required courses, removing day filter");
                if (addedDayFilter) {
                    removeFilter(dayFilter);
                    //addedDayFilter = false;
                }
            }
            //if failcount is 1, remove the time filter
            else if (failCount == 2) {
                System.out.println("Quick schedule cannot add required courses, removing time filter");
                //remove the time filter
                if (addedTimeFilter) {
                    removeFilter(timeFilter);
                    //addedTimeFilter = false;
                }
                //update search term and research

            }
            //failcount 0 should not be possible
            else {
                System.out.println("Quick schedule calculation returned 0, this should not be possible, failing");
                return false;
            }

            //recalculate the schedule
            String searchTerm = requiredCourses.get(qsFailValue.get(qsFailValue.size()-1));
            if (!searchTerm.isEmpty()) {
                searchGeneral(searchTerm);
                qs.addCourses(search.searchAdvanced(courseRegistry.getCourses(semester)), qsFailValue.get(qsFailValue.size()-1));
            }
            qsFailValue.add(qs.calculateSchedule());

            //add back the filters if removed and not already in list
            if (addedDayFilter && !getActiveFilters().contains(dayFilter)) {
                //add back the day filter
                //if (!(recursion > 0)) {
                    //if (day.equals("MWF") || day.equals("TR")) {
                        addFilter(dayFilter);
                        //addedDayFilter = true;
                    //}
                //}
            }

            //add back the time filter if removed
            if (addedTimeFilter && !getActiveFilters().contains(timeFilter)) {
                //add back the time filter
                //add back the time filter
                //if (!(recursion > 1)) {
                    addFilter(timeFilter);
                    addedTimeFilter = true;
                //}
            }
        }

        //clear all filters
        search.clearFilters();
        //Print success
        System.out.println("Quick schedule success");

        //add schedule as a new schedule and update to the new schedule
        //if current schedule is empty, replace its contents
        if (schedules.get(selectedSchedule).getCourses().isEmpty()) {
            schedules.set(selectedSchedule, qs.getSchedule());
        } else {
            schedules.add(qs.getSchedule());
        }
        selectedSchedule = schedules.indexOf(qs.getSchedule());

        return true;
    }

    public void quickSchedule(String day, String startTime, String endTime, int numCredits, ArrayList<String> requiredCourses)
    {
        quickSchedule(day, startTime, endTime, numCredits, requiredCourses, 0);
    }

    public void quickSchedule(String day, String startTime, String endTime, int numCredits)
    {
        //call quickSchedule with empty required courses
        ArrayList<String> requiredCourses = new ArrayList<>();
        quickSchedule(day, startTime, endTime, numCredits, requiredCourses);
    }

    //quickschedule test
    public static void main(String[] args) {
        Core core = new Core();
        core.updateSemester("2023_Fall");
        core.newSchedule();
        ArrayList<String> requiredCourses = new ArrayList<>();
        requiredCourses.add("COMP141");
        requiredCourses.add("acct200");
        requiredCourses.add("writ");
        requiredCourses.add("journalism");
        core.quickSchedule("MWF", "12:00 PM", "3:00 PM", 12, requiredCourses);

        // Print the courses in the selected schedule
        System.out.println("Courses in the selected schedule:");
        for (Course course : core.getSchedule()) {
            System.out.println(course.getName());
        }
    }


    //END QUICK SCHEDULE
    //-------------------------------------------------------------------------------------------------------------
}