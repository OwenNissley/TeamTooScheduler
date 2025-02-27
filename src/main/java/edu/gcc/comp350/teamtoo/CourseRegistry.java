package edu.gcc.comp350.teamtoo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CourseRegistry {
    private List<Course> courses;

    @JsonProperty("date")
    private String date;

    @JsonProperty("time")
    private String time;

    public CourseRegistry() {
        this.courses = new ArrayList<>();
    }

    public List<Course> getCourses() {
        return courses;
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
            ArrayList<String> names = new ArrayList<>();
            // Print all course data
            for (Course course : courses) {
                //System.out.println("\n--- Course ---");
               /*
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


                */
                // Print times
                //System.out.println("Times:");
                int i =0;
                for (Course.TimeSlot timeSlot : course.getTimes()) {
                   // System.out.println("  Day: " + timeSlot.getDay());
                    i++;//+ //", Start Time: " + timeSlot.getStartTime() + ", End Time: " + timeSlot.getEndTime());
                }
                if (i ==4 ){
                    names.add(course.getName());
                    course.formatDays();
                    System.out.println("Days: " + course.getDays());
                    //throw new IOException("Too many classes");

                }
            }
            System.out.println(names.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CourseRegistry registry = new CourseRegistry();
        registry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");  // Load and print courses
    }
}
