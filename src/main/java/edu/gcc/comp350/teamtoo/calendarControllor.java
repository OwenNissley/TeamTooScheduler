package edu.gcc.comp350.teamtoo;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class calendarControllor {
    private final coreTest core;

    public calendarControllor(coreTest core) {
        this.core = core;
    }

    public void registerRoutes(Javalin app) {
        //app.get("/courseReg", this::getCourseList);
        app.get("/updateSchedule", this::getCourseList);
        app.get("/getNumOfSchedules", this::getNumOfShecules);
        app.post("/updateYear", this::updateYear);
        app.post("/updateTerm", this::updateTerm);
        app.post("/selectSchedule", this::selectSchedule);
        app.post("/newSchedule", this::createNewSchedule);
        app.post("/deleteSchedule", this::deleteSchedule);
    }


    private void deleteSchedule(Context ctx) {
        int scheduleIndex = core.deleteSchedule();
        ctx.json(scheduleIndex);
    }
    private void createNewSchedule(Context ctx) {
        core.newSchedule();
        //ctx.status(200).result("New schedule created.");
        int index = core.getSelectedSchedule();
        ctx.json(index);
    }
    private void getCourseList(Context ctx) {
        ctx.json(core.getSchedule());
    }

    private void getNumOfShecules(Context ctx) {
        int num  = core.getNumOfSchedules();
        ctx.json(num);
    }

    private void selectSchedule(Context ctx) {
        String scheduleIndex = ctx.queryParam("scheduleIndex");
        if (scheduleIndex != null) {
            int index = Integer.parseInt(scheduleIndex);
            System.out.println("Selected schedule index: " + index);
            core.setSelectedSchedule(index);
            ctx.status(200).result("Selected schedule: " + index);
        } else {
            ctx.status(400).result("Schedule index parameter is missing.");
        }
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
