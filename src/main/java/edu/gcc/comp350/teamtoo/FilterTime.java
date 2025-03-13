package edu.gcc.comp350.teamtoo;

import java.util.ArrayList;
import java.util.Arrays;

// Subclass extending the abstract class "Filter"
public class FilterTime extends Filter {

    private String startTime;
    private String endTime;

    public FilterTime( String startTime, String endTime) {
        super(FilterType.TIME);
        if (startTime.matches("\\d{1,2}:\\d{2} (AM|PM)")) {
            System.out.println("Valid format");
        } else {
           throw new RuntimeException("Invalid format, need hour:minute AM/PM");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

 //   @Override
    public boolean filtersCourse(Course course) {
        //boolean didFilterFit = false;
        ArrayList<Course.TimeSlot> times = course.getTimes();
        for(Course.TimeSlot timeSlot : times){
            if (timeSlot.getStartTime().equals(startTime) && timeSlot.getEndTime().equals(endTime)){
                return true;//didFilterFit =true;
            }
        }
        return false;//return didFilterFit;
    }

    public static void main(String[] args) {
        ArrayList<Course> filteredCourses = new ArrayList<>();
        CourseRegistry registry = new CourseRegistry();
        registry.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");  // Load and print courses
        Filter timeFilter = new FilterTime("9:30 AM","10:45 AM");
        for(Course course: registry.getCourses()){
            if (timeFilter.filtersCourse(course)){
                filteredCourses.add(course);
            }
        }
      /*  for(Course course: filteredCourses){
            for(Course.TimeSlot timeSlot: course.getTimes()){
                System.out.println(course.getName() + "--" +timeSlot.getDay() + ":" + timeSlot.getStartTime() + "-" + timeSlot.getEndTime());
            }
        }

       */
       // for (Course course : filteredCourses){
         //   System.out.println(course.getName());
        //}

    }



    //takes a time string and returns the minute value
    //useful for Time Filter

   /* private static int parseTimeToMinutes(String time) {
        // Example format: "1:30 PM"
        String[] parts = time.split(" ");
        String[] timeParts = parts[0].split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Adjust for AM/PM
        if (parts[1].equals("PM") && hour != 12) {
            hour += 12;
        } else if (parts[1].equals("AM") && hour == 12) {
            hour = 0;
        }

        return hour * 60 + minute; // Convert to total minutes
    }

    */
}

