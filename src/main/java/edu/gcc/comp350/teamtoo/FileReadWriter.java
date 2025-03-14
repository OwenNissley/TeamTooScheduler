package edu.gcc.comp350.teamtoo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Class to read from and write to a file.
 *
 * @author Team Too
 */
public class FileReadWriter {
    CourseRegistry CR = new CourseRegistry();
    ArrayList<Course> directory = CR.getCourses();

    /**
     * Reads the schedule from the file and returns the number of courses.
     *
     * @param fileName the name of the file to read from
     * @return the number of courses in the schedule
     */
    public ArrayList<Schedule> readScheduleFromFile(String fileName) {
        ArrayList<Schedule> schedules = new ArrayList<>();
        int courseCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            ArrayList<Course> courses = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if(line.equals("**********")){
                    schedules.add(new Schedule(courses));
                    courses.clear();
                    courseCount = 0;
                }
                else{
                    Course course = getCourseByID(line);
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
     * Writes the schedule to the file.
     *
     * @param fileName the name of the file to write to
     * @param schedules  the list of schedules to write to the file
     */
    public void readScheduleIntoFile(String fileName, ArrayList<Schedule> schedules) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for(Schedule schedule : schedules){
                for (Course course : schedule.getCourses()) {
                    writer.write(course.getSubject() + course.getNumber() + "\n");
                }
                writer.write("**********\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Course getCourseByID(String ID){
        for(Course course : directory){
            if(("" + course.getSubject() + course.getNumber()).equals(ID)){
                return course;
            }
        }
        return null;
    }
}
