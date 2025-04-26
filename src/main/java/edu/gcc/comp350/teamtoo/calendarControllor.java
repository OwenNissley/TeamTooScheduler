package edu.gcc.comp350.teamtoo;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class calendarControllor {
    private final Core core;

    public calendarControllor(Core core) {
        this.core = core;
    }

    public void registerRoutes(Javalin app) {
        //app.get("/courseReg", this::getCourseList);
        app.get("/updateSchedule", this::getNonConflictingCourses);
        app.get("/getNumberOfSchedulesAndCurrentSchedule", this::getNumberOfSchedulesAndCurrentSchedule);
        app.post("/updateYear", this::updateYear);
        app.post("/updateTerm", this::updateTerm);
        app.post("/selectSchedule", this::selectSchedule);
        app.post("/newSchedule", this::createNewSchedule);
        app.post("/deleteSchedule", this::deleteSchedule);
        app.get("/checkConflicts", this::hasConflicts);
        app.post("/saveSchedule", this::saveSchedule);
        app.get("/getCurrentData", this::getCurrentData);
    }


    private void getCurrentData(Context ctx) {
        // These would be retrieved from your server's current state

        String semester = core.getSemester();
        String[] parts = semester.split("_");

        String currentYear = parts[0]; // "2023"
        String currentTerm = parts[1]; // "Fall"

        int scheduleIndex = core.getSelectedSchedule();
        int numOfSchedules = core.getNumOfSchedules();

        String yearTermString = currentYear + "_" + currentTerm;

        Map<String, Object> response = new HashMap<>();
        response.put("yearTermString", yearTermString);
        response.put("scheduleIndex", scheduleIndex);
        response.put("numOfSchedules", numOfSchedules);

        ctx.json(response);
    }




    private void saveSchedule(Context ctx) {
       core.saveSchedulesIntoFile();
    }

    private void hasConflicts(Context ctx) {
        ArrayList<Course> conflictingCourses = core.getConflictingCourses();
        boolean hasConflicts = !conflictingCourses.isEmpty();
        Map<String, Object> response = new HashMap<>();
        response.put("hasConflict", hasConflicts);
        response.put("conflictingCourses", conflictingCourses);
        ctx.json(response);
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
    private void getNonConflictingCourses(Context ctx) {
        ctx.json(core.getNonConflictingCourses());
    }

    private void getNumberOfSchedulesAndCurrentSchedule(Context ctx) {
        int num  = core.getNumOfSchedules();
        int currentSchedule = core.getSelectedSchedule();
        Map<String, Integer> response = new HashMap<>();
        response.put("numOfSchedules", num);
        response.put("currentSchedule", currentSchedule);
        ctx.json(response);
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
        //split the year string into year and term
        String[] parts = year.split("_");
        String yearPart = parts[0];
        String termPart = parts[1];
        core.addFilter(new FilterSemester(yearPart, termPart));
        core.searchAdvanced();
    }
    private void updateTerm(Context ctx) {
        String year = ctx.queryParam("yearTermString");
        if (year != null) {
            core.updateSemester(year);
            ctx.status(200).result("Semester updated to: " + year);
        } else {
            ctx.status(400).result("Year parameter is missing.");
        }
        String[] parts = year.split("_");
        String yearPart = parts[0];
        String termPart = parts[1];
        core.addFilter(new FilterSemester(yearPart, termPart));
        core.searchAdvanced();
    }

}
