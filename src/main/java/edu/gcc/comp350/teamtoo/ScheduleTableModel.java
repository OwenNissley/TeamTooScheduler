package edu.gcc.comp350.teamtoo;

import javax.swing.table.AbstractTableModel;
import java.util.List;

class ScheduleTableModel extends AbstractTableModel {
    private final String[] columnNames = {"Day", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM", "9:00 PM"};
    private final Object[][] data;

    public ScheduleTableModel(List<Course> courses) {
        data = new Object[5][15]; // 5 rows for each weekday, 15 columns for each hour from 8 AM to 9 PM
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (int i = 0; i < 5; i++) {
            data[i][0] = days[i]; // Day column
        }
        for (Course course : courses) {
            for (Course.TimeSlot timeSlot : course.getTimes()) {
                int startMinutes = parseTimeToMinutes(timeSlot.getStartTime());
                int endMinutes = parseTimeToMinutes(timeSlot.getEndTime());
                int dayIndex = getDayIndex(timeSlot.getDay());
                if (dayIndex != -1) {
                    int startIndex = (startMinutes - 480) / 60 + 1; // 480 minutes = 8 AM
                    int endIndex = (endMinutes - 480) / 60 + 1;
                    if (endMinutes % 60 != 0) {
                        endIndex++; // Include the last block if the end time is not exactly on the hour
                    }
                    //System.out.println("here?");
                    for (int timeIndex = startIndex; timeIndex < endIndex; timeIndex++) {
                        if (timeIndex >= 1 && timeIndex < 15) {
                            data[dayIndex][timeIndex] = course.getName();
                        }
                    }
                }
            }
        }
    }

    private int getDayIndex(String day) {
        switch (day) {
            case "M": return 0;
            case "T": return 1;
            case "W": return 2;
            case "R": return 3;
            case "F": return 4;
            default: return -1;
        }
    }

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

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
}