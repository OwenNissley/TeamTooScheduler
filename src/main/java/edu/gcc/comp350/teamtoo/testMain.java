package edu.gcc.comp350.teamtoo;

import io.javalin.Javalin;

public class testMain {
    public static void main(String[] args) {
        reactController Controller = new reactController();
        //Javalin app = Javalin.create().start(7000);
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.anyHost();
                });
            });
            // Update static files configuration to point to resources/public
            // config.staticFiles.add("public");  // No need to use "/src/main/resources", just "public"
        }).start(7000);
        Controller.registerRoutes(app);
    }

}
