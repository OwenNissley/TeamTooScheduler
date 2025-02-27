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

    // Getters and setters
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public List<String> getFaculty() { return faculty; }
    public void setFaculty(List<String> faculty) { this.faculty = faculty; }

    public boolean Lab() { return lab; }
    public void setLab(boolean is_lab) { this.lab = is_lab; }

    public boolean Open() { return open; }
    public void setOpen(boolean is_open) { this.open = is_open; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public int getOpenSeats() { return openSeats; }
    public void setOpenSeats(int open_seats) { this.openSeats = open_seats; }

    public char getSection() { return section; }
    public void setSection(char section) { this.section = section; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public List<TimeSlot> getTimes() { return times; }
    public void setTimes(List<TimeSlot> times) { this.times = times; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int total_seats) { this.totalSeats = total_seats; }

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

        // Getters and setters
        public String getDay() { return day; }
        public void setDay(String day) { this.day = day; }

        public String getStartTime() { return start_time; }
        public void setStartTime(String start_time) { this.start_time = start_time; }

        public String getEndTime() { return end_time; }
        public void setEndTime(String end_time) { this.end_time = end_time; }
    }
}

