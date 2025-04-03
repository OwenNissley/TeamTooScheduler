import React, { useEffect, useState } from "react";
import axios from "axios";
import { DayPilot, DayPilotCalendar } from "@daypilot/daypilot-lite-react";
import { useNavigate } from "react-router-dom";
import "./Calendar.css";

const Calendar = () => {
  const [calendar, setCalendar] = useState(null);
  const [events, setEvents] = useState([]);
  const [courses, setCourses] = useState([]);
  const [numOfSchedules, setNumOfSchedules] = useState(1);
  const [selectedYear, setSelectedYear] = useState("2024");
  const [selectedTerm, setSelectedTerm] = useState("Fall");
  const navigate = useNavigate();
/*
const config = {
  viewType: "Week",
  durationBarVisible: false,
  timeRangeSelectedHandling: "Disabled",
  weekStarts: 1, // Start on Monday
  businessBeginsHour: 8,
  businessEndsHour: 19,
  heightSpec: "BusinessHours",
  days: 5, // Show only Monday to Friday
  headerDateFormat: "dddd", // Show full weekday names
  scrollingEnabled: false,
  visibleRange: { start: "Monday", days: 5 }, // Ensure only Mon-Fri are visible
};
*/

  const config = {
    viewType: "Week",
    durationBarVisible: false,
    timeRangeSelectedHandling: "Disabled",
     weekStarts: 1,
    businessBeginsHour: 8,
    businessEndsHour: 19,
    heightSpec: "BusinessHours",
    days: 5, // Monday–Friday
    headerDateFormat: "dddd", // Show full weekday names
    scrollingEnabled: false, // Remove scrollbars
        eventMoveHandling: "Disabled",
        eventResizeHandling: "Disabled",
    visibleRange: { start: "Monday", days: 5 },
     //days: 7,  // Keep 7 days but manually hide Sat/Sun
      onBeforeCellRender: (args) => {
        const day = args.cell.start.getDay(); // 0 = Sunday, 6 = Saturday
        if (day === 0 || day === 4) {
          args.cell.hidden = true;  // Hide Sat & Sun
        }
      },// Ensure only Mon-Fri are visible
    onEventClick: async (args) => {
      await editEvent(args.e);
    },
    contextMenu: new DayPilot.Menu({
      items: [
        {
          text: "Delete",
          onClick: async (args) => {
            calendar.events.remove(args.source);
          },
        },
        {
          text: "Edit...",
          onClick: async (args) => {
            await editEvent(args.source);
          },
        },
      ],
    }),
  };

  const editEvent = async (e) => {
    const modal = await DayPilot.Modal.prompt("Update event text:", e.text());
    if (!modal.result) return;
    e.data.text = modal.result;
    calendar.events.update(e);
  };

const courseColors = {};
const usedColors = new Set(); // Track assigned colors

const getUniqueColor = () => {
  const colors = ["#3c78d8", "#6aa84f", "#f1c232", "#cc4125", "#ff5733", "#33ff57", "#3357ff", "#ff33a8"];
  let color;
  do {
    color = colors[Math.floor(Math.random() * colors.length)];
  } while (usedColors.has(color)); // Keep picking until unique

  usedColors.add(color); // Mark this color as used
  return color;
};

 useEffect(() => {
    const getNumberOfSchudules = async () => {
        const response = await axios.get("http://localhost:7000/getNumOfSchedules");
        const numOfSchedules = response.data;
        setNumOfSchedules(numOfSchedules);
     }
      getNumberOfSchudules();
}, [calendar]);


  useEffect(() => {
    const fetchCourses = async () => {
      try {
        const response = await axios.get("http://localhost:7000/updateSchedule");
        const courses = response.data;
         setCourses(courses);

        console.log("Fetched courses:", courses);
        // Convert courses into events
        const newEvents = courses.flatMap((course) =>
          course.times.map((timeSlot) => {
            const dayOffset = dayToNumber(timeSlot.day); // Convert to weekday index
            const startDate = DayPilot.Date.today().firstDayOfWeek().addDays(dayOffset); // Get correct day

             if (!courseColors[course.number]) {
               courseColors[course.number] = getUniqueColor(); // Assign unique color
             }
             const courseColor = courseColors[course.number];
            const event = {
              id: `${course.number}-${timeSlot.day}-${timeSlot.start_time}`,
              text: course.name, // Course name
              start: startDate.addHours(parseTime(timeSlot.start_time)), // Adjust start time
              end: startDate.addHours(parseTime(timeSlot.end_time)), // Adjust end time
              resource: dayOffset.toString(), // Assign to correct weekday
              backColor: courseColor,
            };
            console.log("Mapped event:", event);
            return event;
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

    fetchCourses();
  }, [calendar]);

  // Convert day letter to weekday offset (Monday = 0, Friday = 4)
  const dayToNumber = (day) => {
    const dayMap = { M: 1, T: 2, W: 3, R: 4, F: 5 }; // Shift everything by +1
    return dayMap[day] ?? 1; // Default to Monday
  };

  // Convert "HH:MM:SS" to hours as a float (e.g., "15:30:00" -> 15.5)
  const parseTime = (timeStr) => {
    const [hours, minutes] = timeStr.split(":").map(Number);
    return hours + minutes / 60;
  };

  // Generate random colors for events
  const getRandomColor = () => {
    const colors = ["#3c78d8", "#6aa84f", "#f1c232", "#cc4125"];
    return colors[Math.floor(Math.random() * colors.length)];
  };

  const handleYearChange = async (e) => {
      const newYear = e.target.value; // Get selected year
      setSelectedYear(newYear); // Update state

      const yearTermString = `${newYear}_${selectedTerm}`; // Format as "2023_Fall"

      try {
          await axios.post("http://localhost:7000/updateYear", null, {
              params: { yearTermString: yearTermString } // Send as a query parameter
          });

          console.log("Updated year successfully:", yearTermString);
      } catch (error) {
          console.error("Error updating year:", error);
      }
  };


 const handleTermChange = async (e) =>   {
         const newTerm = e.target.value; // Get selected year
         setSelectedTerm(newTerm); // Update state

         const yearTermString = `${selectedYear}_${newTerm}`; // Format as "2023_Fall"

              try {
                  await axios.post("http://localhost:7000/updateTerm", null, {
                      params: { yearTermString: yearTermString } // Send as a query parameter
                  });

                  console.log("Updated year successfully:", yearTermString);
              } catch (error) {
                  console.error("Error updating year:", error);
              }
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

          <select>
            {Array.from({ length: numOfSchedules }, (_, index) => (
              <option key={index + 1} value={index + 1}>
                {index + 1}
              </option>
            ))}
          </select>

          <button>New Schedule</button>
          <button>Delete Schedule</button>
        </div>

        <div className="warnings-section">
          <p className="warning-text">⚠️ Course conflict warnings will appear here.</p>
        </div>

        <div className="calendar-container">
          <div className="calendar-wrapper">
            <DayPilotCalendar {...config} controlRef={(ref) => setCalendar(ref)} events={events} />
          </div>
        </div>
      </div>
    );
};

export default Calendar;
