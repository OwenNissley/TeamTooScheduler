package edu.gcc.comp350.teamtoo;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class searchController {

    private final coreTest core;

    public searchController(coreTest core) {
        this.core = core;
    }

    public void registerRoutes(Javalin app) {
        app.get("/getSearchResults", this::getSearchResults);
        app.post("/excuteGeneralSearch", this::excuteGeneralSearch);
        app.post("/addCourse", this::addCourse);
       // app.post("/removeCourse", this::removeCourse);
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
        String searchTerm = ctx.queryParam("searchTerm");
        core.searchGeneral(searchTerm);
        core.searchAdvanced();
        ctx.json(core.getSearchResults());
    }


}
