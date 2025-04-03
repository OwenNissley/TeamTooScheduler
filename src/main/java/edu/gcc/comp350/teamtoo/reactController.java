//NOTES FOR MICAH"S RETSARTED ASSS
//I need method to return numebr of schudules
//
//
//
//
//
//
//
//
// -------------------------------------------------------------------------------------------------------------


package edu.gcc.comp350.teamtoo;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;

public class reactController {
    private final Core core;
    private final calendarControllor calendarControllor;

    public reactController() {
        this.core = new Core();
        calendarControllor = new calendarControllor(core);
        //Test lines
        core.searchGeneral("Comp");
        core.searchAdvanced();
        core.addCourse(0);
        core.addCourse(1);
       // core.addCourse(2);
    }

    public void registerRoutes(Javalin app) {
       calendarControllor.registerRoutes(app);
        // app.get("/courseReg", this::getCourseList);
        // app.get("/updateSchedule", this::getCourseListSchedule);
        // app.get("/getSchedules", this::getNumOfShecules);
        // app.delete("/courseReg", this::removeItem);
        // app.get("/search", this::setFilter);
       // app.delete("/courseReg", this::removeItem);
    }



    private void setFilter(Context ctx) {

      //  search
    }

}
