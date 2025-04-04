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
  const [calendar, setCalendar] = useState(null);
  const [events, setEvents] = useState([]);
  const [courses, setCourses] = useState([]);
  const [hasConflict, setHasConflict] = useState(false);
  const [conflictingCourses, setConflictingCourses] = useState([]);
  const navigate = useNavigate();

  const { selectedYear, selectedTerm, selectedSchedule,setSelectedYear,
                                                           setSelectedTerm,
                                                           setSelectedSchedule,
                                                           handleNewSchedule,
                                                           handleDeleteSchedule, } = useContext(ScheduleContext);

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
    try {
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
          };
        })
      );

      setEvents(newEvents);
      if (calendar) {
        calendar.update({ events: newEvents });
      }
    } catch (error) {
      console.error("Error fetching courses:", error);
    }
  };

  useEffect(() => {
    fetchCourses();
  }, [selectedYear, selectedTerm, selectedSchedule]);

  const updateConflicts = async () => {
    try {
      const response = await axios.get("http://localhost:7000/checkConflicts");
      const { hasConflict, conflictingCourses } = response.data;

      setHasConflict(hasConflict); // Update state based on API response
      setConflictingCourses(conflictingCourses); // Store conflicting courses if needed
    } catch (error) {
      console.error("Error checking for conflicts:", error);
    }
  };

    useEffect(() => {
        updateConflicts();
      }, [selectedSchedule]);


  return (
    <div className="page-container">
      <div className="top-banner">
        <button onClick={() => navigate("/")}>Home</button>
        <button onClick={() => navigate("/addCourse")}>Add Course</button>
      </div>

      <ScheduleControls />

      <div className="calendar-container">
        <div className="calendar-wrapper">
          <DayPilotCalendar {...{ viewType: "Week", events, controlRef: (ref) => setCalendar(ref) }} />
        </div>
      </div>
    </div>
  );
};

export default Calendar;
