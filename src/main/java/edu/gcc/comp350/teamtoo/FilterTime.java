package edu.gcc.comp350.teamtoo;

// Subclass extending the abstract class "Filter"
public class FilterTime extends Filter {

    int startTime;
    int endTime;

    public FilterTime(FilterType filterType, String startTime, String endTime) {
        super(filterType);
        this.startTime = parseTimeToMinutes(startTime);
        this.endTime = parseTimeToMinutes(endTime);
    }

    @Override
    public boolean filtersCourse(Course course) {
        if (startTime <= parseTimeToMinutes(course.startTime) && endTime >= parseTimeToMinutes(course.endTime))
        {
            return true;
        }
        return false;
    }

    //takes a time string and returns the minute value
    //useful for Time Filter
    private static int parseTimeToMinutes(String time) {
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
}