package edu.gcc.comp350.teamtoo;

import com.fasterxml.jackson.databind.JsonNode;
import io.javalin.Javalin;
import io.javalin.http.Context;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class searchController {

    private final Core core;

    public searchController(Core core) {
        this.core = core;
    }

    public void registerRoutes(Javalin app) {
        app.get("/getSearchResults", this::getSearchResults);
        app.post("/excuteGeneralSearch", this::excuteGeneralSearch);
        app.post("/addCourse", this::addCourse);
         app.post("/excuteDayFilterSearch", this::excuteDayFilterSearch);
         app.post("/excuteTimeFilterSearch", this::excuteTimeFilterSearch);
         app.post("/clearFilters", this::clearFilters);
         app.post("/removeCourse", this::removeCourse);
         app.get("/getCoursesToDisplay", this::getCoursesToDisplay);
         app.post("/removeAllCourses", this::removeAllCourses);
            app.post("/clearSearch", this::clearSearch);
            app.post("/clearDayFormat", this::clearDayFormat);
            app.post("/clearTimeRange", this::clearTimeRange);
            app.post("/undoAdd", this::undoAdd);
            app.post("/undoRemoveCourse", this::undoRemoveCourse);
            app.post("/parseCourseInformation", this::parseCourseInformation);
        app.post("/getConflictingCoursesInSearchResults", this::getConflictingCoursesInSearchResults);
        app.post("/getAllCourses", this::getAllCourses);
        app.post("/createNewScheduleManual", this::createNewScheduleManual);
        app.post("/createNewScheduleSkip", this::createNewScheduleSkip);
        app.post("/getSuggestions", this::getSuggestions);
    }

    private void createNewScheduleManual(Context ctx) {

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(ctx.body());
            // Extract the values from the JSON body
            String dayFormat = jsonNode.get("dayFormat").asText();
            String startTime = jsonNode.get("startTime").asText();
            String endTime = jsonNode.get("endTime").asText();
            int totalCredits = jsonNode.get("credits").asInt();
            ArrayList<String> fields = new ArrayList<>();
            for (JsonNode field : jsonNode.get("courses")) {
                fields.add(field.asText());
            }



            // Output the received course data (for debugging)
            System.out.println("Received course data: Day Format: " + dayFormat + ", Start Time: " + startTime + ", End Time: " + endTime + ", Total Credits: " + totalCredits);
            // print the fields
            System.out.println("Fields: " + fields);
            // Call the Core method to create a new schedule

            core.quickSchedule(dayFormat, startTime, endTime, totalCredits, fields);

        } catch (Exception e) {
            ctx.status(500).result("Error parsing course information: " + e.getMessage());
        }
    }
    private void createNewScheduleSkip(Context ctx) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(ctx.body());
            // Extract the values from the JSON body
            String dayFormat = jsonNode.get("dayFormat").asText();
            String startTime = jsonNode.get("startTime").asText();
            String endTime = jsonNode.get("endTime").asText();
            int totalCredits = jsonNode.get("credits").asInt();



            // Output the received course data (for debugging)
            System.out.println("Received course data: Day Format: " + dayFormat + ", Start Time: " + startTime + ", End Time: " + endTime + ", Total Credits: " + totalCredits);
            // print the fields

            // Call the Core method to create a new schedule

            core.quickSchedule(dayFormat, startTime, endTime, totalCredits);
        } catch (Exception e) {
            ctx.status(500).result("Error parsing course information: " + e.getMessage());
        }
    }
    private void getAllCourses(Context ctx) {
        ArrayList<Course> allCourses = new ArrayList<>(core.getConflictingCourses());
        allCourses.addAll(core.getNonConflictingCourses());
        ctx.json(allCourses);
    }
    private void getConflictingCoursesInSearchResults(Context ctx) {
        ctx.json(core.getConflictingCoursesInSearchResults());
    }

    private void getSuggestions(Context ctx) {
        try {
            String searchTerm = ctx.queryParam("searchTerm");
            if (searchTerm == null || searchTerm.isEmpty()) {
                ctx.status(400).result("Missing searchTerm parameter");
                return;
            }

            String closestSuggestion = core.getClosestSuggestion(searchTerm);
            ctx.json(closestSuggestion != null ? List.of(closestSuggestion) : List.of());
        } catch (Exception e) {
            ctx.status(500).result("Error fetching suggestions: " + e.getMessage());
        }
    }

    private void parseCourseInformation(Context ctx) {
        try {
            // Extract courseId from the request parameters
            String courseId = ctx.queryParam("courseId");
            if (courseId == null || courseId.isEmpty()) {
                ctx.status(400).result("Missing courseId parameter");
                return;
            }

            // Call the Core method to parse course information
            Map<String, Object> parsedCourse = core.parseCourseInformation(courseId);

            // Return the parsed course information as JSON
            ctx.json(parsedCourse);
        } catch (Exception e) {
            ctx.status(500).result("Error parsing course information: " + e.getMessage());
        }
    }

    private void undoRemoveCourse(Context ctx) {
        core.undoRemove();
        ctx.json(core.getNonConflictingCourses());
    }

    private void undoAdd(Context ctx) {
        // MAke sure undo takes out of search results
       core.undoAdd();
       ctx.json(core.getSearchResults());
    }

    private void removeAllCourses(Context ctx) {
        core.removeAllCourses();
        ctx.json(core.getNonConflictingCourses());
    }

    private void getCoursesToDisplay(Context ctx) {
        ArrayList<Course> courses = core.getNonConflictingCourses();
        ArrayList<Course> conflictingCourses = core.getConflictingCourses();
        ArrayList<Course> allCourses = new ArrayList<>();
        allCourses.addAll(courses);
        allCourses.addAll(conflictingCourses);
        ctx.json(allCourses);
    }

    private void removeCourse(Context ctx) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(ctx.body());

            // Extract the values from the JSON body
            String name = jsonNode.get("name").asText();
            int number = jsonNode.get("number").asInt();
            int credits = jsonNode.get("credits").asInt();

            // Output the received course data (for debugging)
            System.out.println("Received course to remove: " + name + ", Number: " + number + ", Credits: " + credits);

            // check which course object matches, name, number, credits
            ArrayList<Course> courses = core.getNonConflictingCourses();
            ArrayList<Course> conflictingCourses = core.getConflictingCourses();
            courses.addAll(conflictingCourses);
            Course selectedCourse = null;
            for (Course course : courses) {
                System.out.println("Course: " + course.getName() + ", Number: " + course.getNumber() + ", Credits: " + course.getCredits());
                if (course.getName().equals(name) && course.getNumber() == number && course.getCredits() == credits) {
                    selectedCourse = course;
                    break;
                }
            }

            core.removeCourse(selectedCourse);
            ctx.json(core.getNonConflictingCourses());

        } catch (Exception e) {
            ctx.status(400).result("Invalid course data");
        }
    }

    // Clear all filters and reset the search

    private void clearFilters(Context ctx) {
        boolean genSearchEcuxted = core.getGeneralSearchExecuted();
        if (genSearchEcuxted) {
            core.setGeneralSearchExecuted(false);
            core.searchGeneral("");
        }
        if (!core.getActiveFilters().isEmpty()) {
            core.clearAllFilters();
        }
        core.searchAdvanced();
        ctx.json(core.getSearchResults());
    }
    private void clearSearch(Context ctx) {
       // boolean genSearchEcuxted = core.getGeneralSearchExecuted();
     //   if (genSearchEcuxted) {
       //     core.setGeneralSearchExecuted(false);
     //   }
        core.searchGeneral("");
        core.searchAdvanced();
        ctx.json(core.getSearchResults());
    }
    private void clearDayFormat(Context ctx) {
        ArrayList<Filter> activeFilters = new ArrayList<>(core.getActiveFilters());

// Remove all FilterDaysOfWeek filters safely
        activeFilters.removeIf(filter -> filter instanceof FilterDaysOfWeek);

// Clear and re-add the safe filters back to core
        core.clearAllFilters();
        for (Filter filter : activeFilters) {
            core.addFilter(filter);
        }
        core.searchAdvanced();
        ctx.json(core.getSearchResults());
    }
    private void clearTimeRange(Context ctx) {
        // Only keep non-day filters, then reset the filters
        ArrayList<Filter> activeFilters = new ArrayList<>(core.getActiveFilters());

// Remove all FilterTime filters safely
        activeFilters.removeIf(filter -> filter instanceof FilterTime);

// Clear and re-add the safe filters back to core
        core.clearAllFilters();
        for (Filter filter : activeFilters) {
            core.addFilter(filter);
        }
        core.searchAdvanced();
        ctx.json(core.getSearchResults());
    }

    private void excuteTimeFilterSearch(Context ctx) {
        // check to only have one time filter at a time
        // Only keep non-day filters, then reset the filters
        ArrayList<Filter> activeFilters = new ArrayList<>(core.getActiveFilters());

// Remove all FilterTime filters safely
        activeFilters.removeIf(filter -> filter instanceof FilterTime);

// Clear and re-add the safe filters back to core
        core.clearAllFilters();
        for (Filter filter : activeFilters) {
            core.addFilter(filter);
        }

        String startTime = ctx.queryParam("startTime");
        String endTime = ctx.queryParam("endTime");
        System.out.println("Start time: " + startTime);
        System.out.println("End time: " + endTime);
        if (startTime.equals("") || endTime.equals("")) {
            ctx.status(400).result("Missing startTime or endTime");
            return;
        }
        Filter timeFilter = new FilterTime(startTime, endTime);
        core.addFilter(timeFilter);
        core.searchAdvanced();
        ctx.json(core.getSearchResults());
    }


    private void excuteDayFilterSearch(Context ctx) {
        // check to only have one time filter at a time
        // Only keep non-day filters, then reset the filters
        ArrayList<Filter> activeFilters = new ArrayList<>(core.getActiveFilters());

// Remove all FilterDaysOfWeek filters safely
        activeFilters.removeIf(filter -> filter instanceof FilterDaysOfWeek);

// Clear and re-add the safe filters back to core
        core.clearAllFilters();
        for (Filter filter : activeFilters) {
            core.addFilter(filter);
        }

        String day = ctx.queryParam("dayFormatChoice");
        Filter dayFilter = new FilterDaysOfWeek(day);
        core.addFilter(dayFilter);
        core.searchAdvanced();
        ctx.json(core.getSearchResults());
    }

    private void addCourse(Context ctx) {
        int courseIndex = Integer.parseInt(ctx.queryParam("courseIndex"));
        System.out.println("Adding course at index: " + courseIndex);
        core.addCourse(courseIndex);
        ctx.json(core.getSearchResults());
    }

    private void getSearchResults(Context ctx) {
        ctx.json(core.getSearchResults());
    }

    private void excuteGeneralSearch(Context ctx) {
        // dont need check to only have one generalSearch going at a time - build in i think??
        String searchTerm = ctx.queryParam("searchTerm");
        core.searchGeneral(searchTerm);
        core.searchAdvanced();
        ctx.json(core.getSearchResults());
    }


}
