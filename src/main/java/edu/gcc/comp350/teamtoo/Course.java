package edu.gcc.comp350.teamtoo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Course {
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
    private List<TimeSlot> times;

    @JsonProperty("total_seats")// Changed to List<TimeSlot> for multiple TimeSlots
    private int totalSeats;


    public Course() {}

    // **Constructor**
    public Course(int credits, List<String> faculty, boolean lab, boolean open,
                  String location, String name, int number, int open_seats,
                  char section, String semester, String subject, List<TimeSlot> times,
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
    public List<TimeSlot> getTimes() { return times; }
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



}

