package edu.gcc.comp350.teamtoo;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;

public class searchController {

    private final coreTest core;

    public searchController(coreTest core) {
        this.core = core;
    }

    public void registerRoutes(Javalin app) {
        app.get("/getSearchResults", this::getSearchResults);
        app.post("/excuteGeneralSearch", this::excuteGeneralSearch);
        app.post("/addCourse", this::addCourse);
         app.post("/excuteDayFilterSearch", this::excuteDayFilterSearch);
         app.post("/excuteTimeFilterSearch", this::excuteTimeFilterSearch);
         app.post("/clearFilters", this::clearFilters);
       // app.post("/removeCourse", this::removeCourse);
    }

    private void clearFilters(Context ctx) {
        boolean genSearchEcuxted = core.getGeneralSearchExecuted();
        if (genSearchEcuxted) {
            core.setGeneralSearchExecuted(false);
        }
        if (!core.getActiveFilters().isEmpty()) {
            core.clearAllFilters();
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
