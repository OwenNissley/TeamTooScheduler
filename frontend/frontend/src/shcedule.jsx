import React from 'react';
import './Schedule.css'; // We'll define custom styles

// Sample course data
const courses = [
  { name: 'Math 101', day: 'Monday', startTime: '09:00', endTime: '10:30' },
  { name: 'History 202', day: 'Monday', startTime: '11:00', endTime: '12:30' },
  { name: 'Science 303', day: 'Wednesday', startTime: '14:00', endTime: '16:00' },
  { name: 'Programming 404', day: 'Friday', startTime: '08:30', endTime: '10:00' },
];

// Days of the week
const daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];

// Convert time to minutes from 00:00 (helps with sorting and placement)
const timeToMinutes = (time) => {
  const [hours, minutes] = time.split(':').map(Number);
  return hours * 60 + minutes;
};

const Schedule = () => {
  // Generate time slots for the calendar, every 30 minutes starting from 8:00 AM
  const timeSlots = Array.from({ length: 12 }, (_, index) => {
    const hour = 8 + index;
    return `${hour}:00`;
  });

  // Helper to get row span (how many time slots a course occupies)
  const getRowSpan = (course) => {
    const start = timeToMinutes(course.startTime);
    const end = timeToMinutes(course.endTime);
    return (end - start) / 30;
  };

  // Helper to get row position for the course
  const getRowStart = (course) => {
    return (timeToMinutes(course.startTime) - timeToMinutes('08:00')) / 30;
  };

  return (
    <div className="schedule-container">
      <div className="header">
        <div className="time-header"></div>
        {daysOfWeek.map((day) => (
          <div key={day} className="day-header">{day}</div>
        ))}
      </div>
      <div className="body">
        {timeSlots.map((timeSlot, index) => (
          <div key={index} className="time-slot">
            <div className="time">{timeSlot}</div>
            {daysOfWeek.map((day) => {
              const dayCourses = courses.filter(
                (course) => course.day === day &&
                  timeToMinutes(course.startTime) <= timeToMinutes(timeSlot) &&
                  timeToMinutes(course.endTime) > timeToMinutes(timeSlot)
              );

              return (
                <div key={day} className="course-cell">
                  {dayCourses.map((course, idx) => (
                    <div
                      key={idx}
                      className="course"
                      style={{
                        gridRowStart: getRowStart(course) + 1,
                        gridRowEnd: getRowStart(course) + getRowSpan(course) + 1,
                      }}
                    >
                      {course.name}
                    </div>
                  ))}
                </div>
              );
            })}
          </div>
        ))}
      </div>
    </div>
  );
};

export default Schedule;
