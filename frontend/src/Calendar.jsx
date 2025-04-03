import React, { useEffect, useState } from "react";
import axios from "axios";
import { DayPilot, DayPilotCalendar } from "@daypilot/daypilot-lite-react";
import { useNavigate } from "react-router-dom";
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
  const [numOfSchedules, setNumOfSchedules] = useState(1);
  const [selectedSchedule, setSelectedSchedule] = useState(1);
  const [selectedYear, setSelectedYear] = useState("2023");
  const [selectedTerm, setSelectedTerm] = useState("Fall");
  const navigate = useNavigate();

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

  const getNumberOfSchedules = async () => {
    try {
      const response = await axios.get("http://localhost:7000/getNumOfSchedules");
      setNumOfSchedules(response.data);
    } catch (error) {
      console.error("Error fetching number of schedules:", error);
    }
  };

  useEffect(() => {
    getNumberOfSchedules();
  }, []);

  const handleScheduleChange = async (e) => {
    const newScheduleNum = Number(e.target.value);
    console.log("Selected schedule:", newScheduleNum);
    try {
      await axios.post("http://localhost:7000/selectSchedule", null, {
        params: { scheduleIndex: newScheduleNum-1 },
      });
    } catch (error) {
      console.error("Error updating schedule:", error);
    }
   setSelectedSchedule(newScheduleNum);
   //getNumOfSchedules();

  };

  const handleNewSchedule = async () => {
    try {
      const response = await axios.post("http://localhost:7000/newSchedule");
      const newScheduleNumber = response.data;
      console.log("New schedule created:", newScheduleNumber);
      setNumOfSchedules((prev) => prev + 1);
      setSelectedSchedule(newScheduleNumber+1);
    } catch (error) {
      console.error("Error creating new schedule:", error);
    }
  };

  const handleDeleteSchedule = async () => {
    try {
      await axios.post("http://localhost:7000/deleteSchedule", null, {
        params: { scheduleIndex: selectedSchedule },
      });
      setNumOfSchedules((prev) => (prev > 1 ? prev - 1 : 1));
      setSelectedSchedule(1);
      fetchCourses();
    } catch (error) {
      console.error("Error deleting schedule:", error);
    }
  };

  const handleYearChange = async (e) => {
    const newYear = e.target.value;
    setSelectedYear(newYear);
    try {
      await axios.post("http://localhost:7000/updateYear", null, {
        params: { yearTermString: `${newYear}_${selectedTerm}` },
      });
    } catch (error) {
      console.error("Error updating year:", error);
    }
    getNumberOfSchedules();
    setSelectedSchedule(1);

  };

  const handleTermChange = async (e) => {
    const newTerm = e.target.value;
    setSelectedTerm(newTerm);
    try {
      await axios.post("http://localhost:7000/updateTerm", null, {
        params: { yearTermString: `${selectedYear}_${newTerm}` },
      });
    } catch (error) {
      console.error("Error updating term:", error);
    }
   getNumberOfSchedules();
   console.log("numOfSchedules:", numOfSchedules);
   setSelectedSchedule(1);

  };

  return (
    <div className="page-container">
      <div className="top-banner">
        <button onClick={() => navigate("/")}>Home</button>
        <button onClick={() => navigate("/quick-schedule")}>Quick Schedule</button>
        <button onClick={() => navigate("/add-course")}>Add Course</button>
        <button onClick={() => navigate("/review")}>Review</button>
        <button onClick={() => navigate("/course-directory")}>Course Directory</button>
        <button onClick={() => navigate("/your-info")}>Your Info</button>
      </div>

      <div className="control-banner">
        <select value={selectedTerm} onChange={handleTermChange}>
          <option value="Spring">Spring</option>
          <option value="Fall">Fall</option>
        </select>

        <select value={selectedYear} onChange={handleYearChange}>
          <option value="2023">2023</option>
          <option value="2024">2024</option>
          <option value="2025">2025</option>
        </select>

        <select value={selectedSchedule} onChange={handleScheduleChange}>
          {Array.from({ length: numOfSchedules }, (_, index) => (
            <option key={index + 1} value={index + 1}>
              {index + 1}
            </option>
          ))}
        </select>

        <button onClick={handleNewSchedule}>New Schedule</button>
        <button onClick={handleDeleteSchedule}>Delete Schedule</button>
      </div>

      <div className="warnings-section">
        <p className="warning-text">⚠️ Course conflict warnings will appear here.</p>
      </div>

      <div className="calendar-container">
        <div className="calendar-wrapper">
          <DayPilotCalendar {...{ viewType: "Week", events, controlRef: (ref) => setCalendar(ref) }} />
        </div>
      </div>
    </div>
  );
};

export default Calendar;
