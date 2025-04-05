package edu.gcc.comp350.teamtoo;

import io.javalin.Javalin;
import java.io.IOException;

public class testMain {
    private static Process reactProcess;

    public static void main(String[] args) {
        reactController Controller = new reactController();

        // Start Javalin server
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> it.anyHost());
            });
        }).start(7000);

        // Start React frontend
       //startReactFrontend();

        // Ensure React stops when the backend stops
       // Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      //      System.out.println("Shutting down React frontend...");
     //       stopReactFrontend();
     //   }));

        Controller.registerRoutes(app);
    }

    private static void startReactFrontend() {
        try {
            System.out.println("Starting React frontend...");
            reactProcess = new ProcessBuilder("C:\\Program Files\\nodejs\\npm.cmd", "run", "dev")
                    .directory(new java.io.File("C:\\Users\\NISSLEYOJ22\\OneDrive - Grove City College\\SWE\\code\\teamToo\\TeamTooScheduler\\frontend"))
                    .start();
        } catch (IOException e) {
            System.err.println("Failed to start React frontend: " + e.getMessage());
        }
    }

    private static void stopReactFrontend() {
        if (reactProcess != null && reactProcess.isAlive()) {
            reactProcess.destroy();
            System.out.println("React frontend stopped.");
        }
    }
}
