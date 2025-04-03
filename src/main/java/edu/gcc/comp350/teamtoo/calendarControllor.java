package edu.gcc.comp350.teamtoo;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class calendarControllor {
    private final Core core;

    public calendarControllor(Core core) {
        this.core = core;
    }

    public void registerRoutes(Javalin app) {
        //app.get("/courseReg", this::getCourseList);
        app.get("/updateSchedule", this::getCourseList);
        app.get("/getNumOfSchedules", this::getNumOfShecules);
        app.post("/updateYear", this::updateYear);
        app.post("/updateTerm", this::updateTerm);
        //app.get("/getSchedules", this::getNumOfShecules);
        // app.delete("/courseReg", this::removeItem);
    }

    private void getCourseList(Context ctx) {
        ctx.json(core.getSchedule());
    }

    private void getNumOfShecules(Context ctx) {
       // ctx.json(core.getNumOfSchedules());
    }

    private void updateYear(Context ctx) {
        String year = ctx.queryParam("yearTermString");
        if (year != null) {
            core.updateSemester(year);
            ctx.status(200).result("Semester updated to: " + year);
        } else {
            ctx.status(400).result("Year parameter is missing.");
        }
    }
    private void updateTerm(Context ctx) {
        String year = ctx.queryParam("yearTermString");
        if (year != null) {
            core.updateSemester(year);
            ctx.status(200).result("Semester updated to: " + year);
        } else {
            ctx.status(400).result("Year parameter is missing.");
        }
    }

}
