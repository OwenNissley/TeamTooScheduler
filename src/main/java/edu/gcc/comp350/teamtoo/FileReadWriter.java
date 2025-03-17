package edu.gcc.comp350.teamtoo;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Class to read from and write to a file.
 *
 * @author Team Too
 */
public class FileReadWriter {
    CourseRegistry CR;
    ArrayList<Course> directory;

    public FileReadWriter(){
        CR = new CourseRegistry();
        CR.loadCoursesFromJson("src/main/java/edu/gcc/comp350/teamtoo/data_wolfe_1.json");
        directory = CR.getCourses();
    }

    /**
     * Reads the schedule from the specified file into a
     * ArrayList with objects of type 'Schedule'
     * Important Notes:
     * 1: Courses are indexed from the file by their Subject, then Number
     * then section. For example, the course "Software Engineering" section A
     * would be represented in the file as 'COMP350A'
     * 2: A series of ten *'s is used to separate schedules in the file
     * @param fileName the name of the file to read from
     * @return the ArrayList of schedules from the specified file
     */
    public ArrayList<Schedule> readScheduleFromFile(String fileName) {
        ArrayList<Schedule> schedules = new ArrayList<>();
        ArrayList<Course> courses = new ArrayList<>();
        int courseCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                if(line.equals("**********")){
                    System.out.println("Runs");
                    schedules.add(new Schedule(courses));
                    courses.clear();
                    courseCount = 0;
                }
                else{
                    System.out.println("Other runs");
                    Course course = getCourseByID(line);
                    System.out.println(course.getName());
                    if(course != null){
                        courses.add(course);
                        courseCount++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return schedules;
    }



    /**
     * Writes an array of objects with type Schedule to a specified file.
     *
     * @param fileName the name of the file to write to
     * @param schedules an ArrayList of schedules to write to the file
     */
    public void readScheduleIntoFile(String fileName, ArrayList<Schedule> schedules) {
        if (schedules.isEmpty()) {
            System.out.println("No schedules to save.");
            return;
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            for(Schedule schedule : schedules){
                for (Course course : schedule.getCourses()) {
                    writer.write(course.getSubject() + course.getNumber() + course.getSection() + "\n");
                }
                writer.write("**********\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Course getCourseByID(String ID){
        for(Course course : directory) {
            if (("" + course.getSubject() + course.getNumber() + course.getSection()).equals(ID)) {
                return course;
            }
        }
        return null;
    }
}
