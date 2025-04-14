//NOTES FOR MICAH"S RETSARTED ASSS
//I need method to return numebr of schudules
// i need method to change the selected schedule
// i need method to return the selected schedule
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
    private final searchController searchController;

    public reactController() {
        this.core = new Core();
        calendarControllor = new calendarControllor(core);
        searchController = new searchController(core);
        core.addFilter(new FilterSemester("2023", "Fall"));
        core.searchAdvanced();
        //Test lines
       // core.searchGeneral("Comp");
       // core.searchAdvanced();
       // core.addCourse(1);
        //core.addCourse(5);
       // core.addCourse(6);

    }

    public void registerRoutes(Javalin app) {
       calendarControllor.registerRoutes(app);
       searchController.registerRoutes(app);
    }



    private void setFilter(Context ctx) {

      //  search
    }

}
