package edu.gcc.comp350.teamtoo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CourseRegistry {
    private ArrayList<Course> courses;
    private Map<String, ArrayList<Course>> courseMap;

    @JsonProperty("date")
    private String date;

    @JsonProperty("time")
    private String time;

    public CourseRegistry() {
        this.courses = new ArrayList<>();
        this.courseMap = new HashMap<>();
    }

    //this should only be called once (that once is in loadCoursesFromJson)
    //and we will allow it to be called once for the FRW so i don't have to refactor that
    public ArrayList<Course> getCourses() {
        return courses;
    }

    public Map<String, ArrayList<Course>> getCourseMap() {
        return courseMap;
    }

    public ArrayList<Course> getCourses(String semester) {
        return courseMap.get(semester);
    }

    public void loadCoursesFromJson(String filePath) {
        try {
            // Create ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);  // This is already default, just to make sure


            // Read JSON from file and map to CourseRegistry class
            CourseRegistry registry = objectMapper.readValue(new File(filePath), CourseRegistry.class);

            // Store parsed courses
            this.courses = registry.getCourses();
          //  ArrayList<String> names = new ArrayList<>();
            // Print all course data
           /* for (Course course : courses) {
                System.out.println("\n--- Course ---");

                System.out.println("Credits: " + course.getCredits());
                System.out.println("Faculty: " + course.getFaculty());
                System.out.println("Is Lab: " + course.isLab());
                System.out.println("Is Open: " + course.isOpen());
                System.out.println("Location: " + course.getLocation());
                System.out.println("Name: " + course.getName());
                System.out.println("Number: " + course.getNumber());
                System.out.println("Open Seats: " + course.getOpenSeats());
                System.out.println("Section: " + course.getSection());
                System.out.println("Semester: " + course.getSemester());
                System.out.println("Subject: " + course.getSubject());
                System.out.println("Total Seats: " + course.getTotalSeats());



                // Print times
                System.out.println("Times:");
                int i =0;
                for (Course.TimeSlot timeSlot : course.getTimes()) {
                    System.out.println("  Day: " + timeSlot.getDay());
                    System.out.println("Start Time: " + timeSlot.getStartTime() + ", End Time: " + timeSlot.getEndTime());
                    i++;
                }
                System.out.println("Days: " + course.daysString());
            }


            */
        } catch (IOException e) {
            e.printStackTrace();
        }

        //break course list into map
        generateCourseMap();
    }

    public static void main(String[] args) {
        CourseRegistry registry = new CourseRegistry();
        registry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");  // Load and print courses
    }

    //generates a map of arraylists based on semester
    public void generateCourseMap() {
        // Iterate through the list of courses
        for (Course course : courses) {
            String semester = course.getSemester();

            // If the semester is not already in the map, add it with an empty list
            if (!courseMap.containsKey(semester)) {
                courseMap.put(semester, new ArrayList<>());
            }

            // Add the course to the list for that semester
            courseMap.get(semester).add(course);
        }

        // Print the map
        for (String semester : courseMap.keySet()) {
            System.out.println("Semester: " + semester);
            for (Course course : courseMap.get(semester)) {
                System.out.println("  Course: " + course.getName());
            }
        }
    }
}
