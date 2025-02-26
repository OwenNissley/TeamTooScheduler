package edu.gcc.comp350.teamtoo;

public class Course
{
    private boolean isFavorite;
    //private variables concerining all course information
    private int credits;
    private String faculty;
    private boolean isLab;
    private boolean isOpen;
    private String location;
    private String name;
    private int number;
    private int openSeats;
    private char section;
    private String semester;
    private String subject;
    //private Time times;
    private int totalSeats;

    // Constructor
    public Course(int credits, String faculty, boolean isLab, boolean isOpen, String location,
                  String name, int number, int openSeats, char section, String semester,
                  String subject, int totalSeats) {
        this.credits = credits;
        this.faculty = faculty;
        this.isLab = isLab;
        this.isOpen = isOpen;
        this.location = location;
        this.name = name;
        this.number = number;
        this.openSeats = openSeats;
        this.section = section;
        this.semester = semester;
        this.subject = subject;
      // this.times = times;
        this.totalSeats = totalSeats;
    }
    //time variables
    public String startTime;
    public String endTime;

    public boolean hasConflict(Course courseToCompare) { return false; }
}
