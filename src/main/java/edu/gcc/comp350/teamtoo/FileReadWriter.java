package edu.gcc.comp350.teamtoo;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to read from and write to a file.
 *
 * @author Team Too
 */
public class FileReadWriter {
    ArrayList<Course> directory;

    public FileReadWriter(ArrayList<Course> courses) {
        directory = courses;
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
    public Map<String, ArrayList<Schedule>> readScheduleFromFile(String fileName) {
        Map<String, ArrayList<Schedule>> hashedSchedules = new HashMap<>();
        ArrayList<Schedule> schedules = new ArrayList<>();
        Schedule schedule = new Schedule();
        int courseCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            String semester = "";
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                if(line.contains("SEMESTER: ")) {
                    //System.out.println("Semester: " + line);
                    if(!semester.equals("")){
                        hashedSchedules.put(semester, schedules);
                        schedules = new ArrayList<>();
                    }
                    semester = line.substring(10);
                }
                else if(line.equals("**********")){
                    //System.out.println("Runs");
                    schedules.add(schedule);
                    schedule = new Schedule();
                    courseCount = 0;
                }
                else{
                    //System.out.println("Other runs");
                    Course course = getCourseByID(line);
                    //System.out.println(course.getName());
                    if(course != null){
                        schedule.addCourse(course);
                        courseCount++;
                    }
                }
            }
            if(!semester.equals("")){
                hashedSchedules.put(semester, schedules);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashedSchedules;
    }



    /**
     * Writes an array of objects with type Schedule to a specified file.
     *
     * @param fileName the name of the file to write to
     * @param hashedSchedules an ArrayList of schedules to write to the file
     */
    public void readScheduleIntoFile(String fileName, Map<String, ArrayList<Schedule>> hashedSchedules) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for(String key : hashedSchedules.keySet()) {
                ArrayList<Schedule> schedules = hashedSchedules.get(key);
                if (schedules.isEmpty()) {
                    System.out.println("No schedules to save.");
                    continue;
                }
                writer.write("SEMESTER: " + key + "\n");
                for (Schedule schedule : schedules) {
                    for (Course course : schedule.getCourses()) {
                        writer.write(course.getSubject() + course.getNumber() + course.getSection() + course.getSemester() + "\n");
                    }
                    writer.write("**********\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //not really by id too much anymore
    public Course getCourseByID(String ID){
        for(Course course : directory) {
            if (("" + course.getSubject() + course.getNumber() + course.getSection() + course.getSemester()).equals(ID)) {
                return course;
            }
        }
        return null;
    }

}
