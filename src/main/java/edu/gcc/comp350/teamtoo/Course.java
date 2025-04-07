package edu.gcc.comp350.teamtoo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Course {
    // private boolean isFavorite
    private int credits;
    private List<String> faculty;

    @JsonProperty("is_lab") // Map "is_lab" in JSON to "lab" in the class
    private boolean lab;

    @JsonProperty("is_open")
    private boolean open;


    private String location;
    private String name;
    private int number;

    @JsonProperty("open_seats")
    private int openSeats;


    private char section;
    private String semester;
    private String subject;
    private ArrayList<TimeSlot> times;

    @JsonProperty("total_seats")// Changed to List<TimeSlot> for multiple TimeSlots
    private int totalSeats;


    public Course() {}

    // **Constructor**
    public Course(int credits, List<String> faculty, boolean lab, boolean open,
                  String location, String name, int number, int open_seats,
                  char section, String semester, String subject, ArrayList<TimeSlot> times,
                  int totalSeats) {
        this.credits = credits;
        this.faculty = faculty;
        this.lab = lab;
        this.open = open;
        this.location = location;
        this.name = name;
        this.number = number;
        this.openSeats = open_seats;
        this.section = section;
        this.semester = semester;
        this.subject = subject;
        this.times = times;
        this.totalSeats = totalSeats;
    }

    //to string method - returns string with name of course, number of credits, the times and days of week
    @Override
    public String toString() {
        if (times.isEmpty())
            return name + " (" + credits + " credits) -- " + daysString() + " -- No time specified";
        else
        {
            StringBuilder timesString = new StringBuilder();
            for (TimeSlot time : times) {
                timesString.append(time.toString()).append("; ");
            }
            return name + " (" + credits + " credits) -- " + daysString() + " -- " + timesString.toString().trim();
        }
            //return name + " (" + credits + " credits) -- " + daysString() + ") -- " + times.get(0).toString();
    }

    // Getters, May note be needed
    public int getCredits() { return credits; }
    public String getFaculty() {
        String result = String.join(" ", faculty);
        return result;
    }
    public boolean isLab() { return lab; }
    public boolean isOpen() { return open; }
    public String getLocation() { return location; }
    public String getName() { return name; }
    public int getNumber() { return number; }
    public int getOpenSeats() { return openSeats; }
    public char getSection() { return section; }
    public String getSemester() { return semester; }
    public String getSubject() { return subject; }
    public ArrayList<TimeSlot> getTimes() { return times; }
    public int getTotalSeats() { return totalSeats; }



    public static class TimeSlot {

        @JsonProperty("day")
        private String day;

       @JsonProperty("start_time")
        private String start_time;


        @JsonProperty("end_time")
        private String end_time;

        public TimeSlot() {}
        // **Constructor**
        public TimeSlot(String day, String start_time, String end_time) {
            this.day = day;
            this.start_time = start_time;
            this.end_time = end_time;
        }

        // Getters for TimeSlot
        public String getDay() { return day; }

        public String getStartTime() { return convertTo12HourFormat(start_time);}

        public String getEndTime() { return convertTo12HourFormat(end_time); }

        @Override
        public String toString() {
            return /*day + " " + */convertTo12HourFormat(start_time) + " - " + convertTo12HourFormat(end_time);
        }

    }

    public String daysString() {
        int howManyDays = times.size();
        char [] days = new char[howManyDays];
        int i = 0;
        for (TimeSlot time : times) {
           days[i] = time.getDay().charAt(0);
            i++;
        }
        return new String(days);
    }

    public static String convertTo12HourFormat(String militaryTime) {
        String[] parts = militaryTime.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        String period = (hours < 12) ? "AM" : "PM";
        hours = (hours == 0) ? 12 : (hours > 12 ? hours - 12 : hours);

        return String.format("%d:%02d %s", hours, minutes, period);
    }

    //Not quite done yet, still needs some work, and probably some better logic
    // Check if the course has a conflict with another course
    //should be working now
    public boolean hasConflict(Course course) {
        // bug fix course name and number
        //if course names and number are the same, return true
        if (course.getNumber() == number && course.getName().equals(name)) {
            return true;
        }
      /*  if (name.equals(course.getName())) {
            return true;
        }


       */
        //if times overlapping, return true
        for (TimeSlot time : times) {
            for (TimeSlot otherTime : course.getTimes()) {
                if (time.getDay().equals(otherTime.getDay()))
                {
                    //compare parsed times and return true if there is an overlap in times
                    if (parseTimeToMinutes(time.getStartTime()) >= parseTimeToMinutes(otherTime.getStartTime()) &&
                            parseTimeToMinutes(time.getStartTime()) <= parseTimeToMinutes(otherTime.getEndTime()) ||
                            parseTimeToMinutes(time.getEndTime()) >= parseTimeToMinutes(otherTime.getStartTime()) &&
                            parseTimeToMinutes(time.getEndTime()) <= parseTimeToMinutes(otherTime.getEndTime()))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Parse time to minutes
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

    /*
    // I think I can get rid of this (Problem solved elsewhere [from multiple CourseRegistry objects])
    // Check if the course is equal to another course
    // check whether the courses have the same name, times, and days
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Course)) {
            return false;
        }

        Course course = (Course) obj;
        if (!name.equals(course.getName())){ return false; }

        if (times.size() != course.getTimes().size()) {
            return false;
        }


        for (TimeSlot time : times) {
            if (!course.getTimes().contains(time)) {
                return false;
            }
        }


        return true;
    }
    */
}

/*
if (time.getDay().equals(otherTime.getDay())) {
                    if (time.getStartTime().equals(otherTime.getStartTime()) ||
                            time.getEndTime().equals(otherTime.getEndTime())) {
                        return true;
                    }
                }
 */