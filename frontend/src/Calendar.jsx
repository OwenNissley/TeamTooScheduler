import React, { useContext, useEffect, useState } from "react";
import axios from "axios";
import { DayPilot, DayPilotCalendar } from "@daypilot/daypilot-lite-react";
import { useNavigate } from "react-router-dom";
import ScheduleControls from "./ScheduleControls";
import { ScheduleContext } from "./ScheduleContext";
import "./Calendar.css";

const dayToNumber = (day) => {
  const dayMap = { M: 1, T: 2, W: 3, R: 4, F: 5 };
  return dayMap[day] ?? 1; // Default to Monday
};

const parseTime = (timeStr) => {
  const [hours, minutes] = timeStr.split(":").map(Number);
  return hours + minutes / 60;
};

const Calendar = () => {
  const [events, setEvents] = useState([]);
  const [courses, setCourses] = useState([]);
  const [hasConflict, setHasConflict] = useState(false);
  const [conflictingCourses, setConflictingCourses] = useState([]);
  const navigate = useNavigate();

  const { selectedYear, selectedTerm, selectedSchedule, numOfSchedules } = useContext(ScheduleContext);
 const {
    handleYearChange,
    handleTermChange,
    handleScheduleChange,
    deleteRunFlag,
  } = useContext(ScheduleContext);

  const courseColors = {};
  const usedColors = new Set();
  const getUniqueColor = () => {
    const colors = ["#3c78d8", "#6aa84f", "#f1c232", "#cc4125", "#ff5733", "#33ff57", "#3357ff", "#ff33a8"];
    let color;
    do {
      color = colors[Math.floor(Math.random() * colors.length)];
    } while (usedColors.has(color));
    usedColors.add(color);
    return color;
  };

  const fetchCourses = async () => {
    const response = await axios.get("http://localhost:7000/updateSchedule");
    const courses = response.data;
    setCourses(courses);
    console.log("Fetched courses:", courses);

    const newEvents = courses.flatMap((course) =>
      course.times.map((timeSlot) => {
        const dayOffset = dayToNumber(timeSlot.day);
        const startDate = DayPilot.Date.today().firstDayOfWeek().addDays(dayOffset);

        if (!courseColors[course.number]) {
          courseColors[course.number] = getUniqueColor();
        }

        return {
          id: `${course.number}-${timeSlot.day}-${timeSlot.start_time}`,
          text: course.name,
          start: startDate.addHours(parseTime(timeSlot.start_time)),
          end: startDate.addHours(parseTime(timeSlot.end_time)),
          resource: dayOffset.toString(),
          backColor: courseColors[course.number],
          semester: course.semester.split("_")[1], // Extract "Fall", "Spring", etc.
          year: course.semester.split("_")[0], // Extract the year (e.g., "2024")
        };
      })
    );
    setEvents(newEvents);
    console.log("Events for calendar:", newEvents);
  };

useEffect(() => {
  fetchCourses();
  updateConflicts();
}, [numOfSchedules, selectedYear, selectedTerm, selectedSchedule,deleteRunFlag]);


  const updateConflicts = async () => {
    try {
      const response = await axios.get("http://localhost:7000/checkConflicts");
      const { hasConflict, conflictingCourses } = response.data;
      console.log("Conflict check response:", response.data);

      setHasConflict(hasConflict); // Update state based on API response
      setConflictingCourses(conflictingCourses); // Store conflicting courses if needed
    } catch (error) {
      console.error("Error checking for conflicts:", error);
    }
  };

  const convertTo12HourFormat = (militaryTime) => {
    const [hours, minutes] = militaryTime.split(":").map(Number);

    let period = hours < 12 ? "AM" : "PM";
    let convertedHour = hours % 12;
    if (convertedHour === 0) convertedHour = 12; // 0 hour should be 12 for AM/PM format

    const formattedMinutes = minutes < 10 ? `0${minutes}` : minutes;

    return `${convertedHour}:${formattedMinutes} ${period}`;
  };

  return (
    <>
      <ScheduleControls />

      <div className="calendar-container">
        <div className="calendar-wrapper">
          <DayPilotCalendar
            viewType="Week"
            events={events} // Pass events to the calendar
            headerDateFormat="dddd"
            onEventClick={(args) => {
              const { text: courseName, semester, year } = args.e.data; // Get course details from the clicked event
              navigate(
                `/course-directory?search=${encodeURIComponent(courseName)}&semester=${encodeURIComponent(
                  semester
                )}&year=${encodeURIComponent(year)}`
              ); // Redirect to courseDirectory with query parameters
            }}
          />
        </div>
      </div>

      {hasConflict && (
        <div className="courses-list">
          <h3>Conflict Detected!</h3>
          <div className="course-box">
            {conflictingCourses.length === 0 ? (
              <p>No conflicting courses</p>
            ) : (
              conflictingCourses.map((course, index) => (
                <div key={index} className="course-item">
                  <span>{course.name} ({course.credits} credits)</span>
                  <span>
                    {" - "}
                    {course.times
                      .map((time) => `${time.day} ${convertTo12HourFormat(time.start_time)} - ${convertTo12HourFormat(time.end_time)}`)
                      .join(", ")}
                  </span>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </>
  );
};

export default Calendar;