import React, { useContext, useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, useLocation } from "react-router-dom";
import "./Calendar.css";
import ScheduleControls from "./ScheduleControls";
import { ScheduleContext } from "./ScheduleContext";

const AddCourseScreen = () => {
  const [selectedCourseIndex, setSelectedCourseIndex] = useState(null); // Track selected course index
  const [filteredCourses, setFilteredCourses] = useState([]); // Filtered courses based on search
  const [searchTerm, setSearchTerm] = useState(""); // Track search input
  const [selectedDayFormat, setSelectedDayFormat] = useState(null); // Default day is Monday
  const [startTime, setStartTime] = useState("7:00 AM");
  const [endTime, setEndTime] = useState("7:00 PM");
  const { selectedYear, selectedTerm, selectedSchedule, numOfSchedules } = useContext(ScheduleContext);
  const [conflictStatuses, setConflictStatuses] = useState([]);
  const [addedStatuses, setAddedStatuses] = useState([]);
  const [conflictingCourses, setConflictingCourses] = useState([]); // Cache for conflicting courses
  const [addedCourses, setAddedCourses] = useState([]); // Cache for added courses

  const navigate = useNavigate();
  const location = useLocation();

  // Fetch conflicting courses and added courses once
  useEffect(() => {
    const fetchConflictingCourses = async () => {
      try {
        const response = await axios.post("http://localhost:7000/getConflictingCoursesInSearchResults");
        setConflictingCourses(response.data);
      } catch (error) {
        console.error("Error fetching conflicting courses:", error);
      }
    };

    const fetchAddedCourses = async () => {
      try {
        const response = await axios.post("http://localhost:7000/getAllCourses");
        setAddedCourses(response.data);
      } catch (error) {
        console.error("Error fetching added courses:", error);
      }
    };

    fetchConflictingCourses();
    fetchAddedCourses();
  }, [selectedYear, selectedTerm, selectedSchedule]); // Refetch if these dependencies change

  useEffect(() => {
    const checkStatuses = async () => {
      const conflicts = filteredCourses.map((course) => isCourseConflicting(course));
      const added = filteredCourses.map((course) => isCourseAdded(course));
      setConflictStatuses(conflicts);
      setAddedStatuses(added);
    };

    if (filteredCourses.length > 0) {
      checkStatuses();
    }
  }, [filteredCourses]);

  const [navigationCount, setNavigationCount] = useState(0);

  useEffect(() => {
    setNavigationCount((count) => count + 1);
  }, [location.key]); // location.key changes every time you go to this route

  useEffect(() => {
    fetchSearchResults();
    setSearchTerm("");
    clearFilters();
  }, [navigationCount, selectedYear, selectedTerm, selectedSchedule]);

  const clearSearch = async () => {
    try {
      const response = await axios.post("http://localhost:7000/clearSearch");
      setFilteredCourses(response.data); // assuming response.data is an array of courses
      setSearchTerm("");
    } catch (error) {
      console.error("Error clearing search filters:", error);
    }
  };

  const clearDayFormat = async () => {
    try {
      const response = await axios.post("http://localhost:7000/clearDayFormat");
      setFilteredCourses(response.data); // assuming response.data is an array of courses
      setSelectedDayFormat(null);
    } catch (error) {
      console.error("Error clearing day format:", error);
    }
  };

  const clearTimeRange = async () => {
    try {
      const response = await axios.post("http://localhost:7000/clearTimeRange");
      setFilteredCourses(response.data); // assuming response.data is an array of courses
      setStartTime("7:00 AM");
      setEndTime("7:00 PM");
    } catch (error) {
      console.error("Error clearing time range:", error);
    }
  };

  const fetchSearchResults = async () => {
    try {
      const response = await axios.get("http://localhost:7000/getSearchResults");
      setFilteredCourses(response.data); // assuming response.data is an array of courses
    } catch (error) {
      console.error("Error fetching courses:", error);
    }
  };

  const clearFilters = async () => {
    try {
      const response = await axios.post("http://localhost:7000/clearFilters");
      setFilteredCourses(response.data); // assuming response.data is an array of courses
      setSearchTerm(""); // Track search input
      setSelectedDayFormat(null); // Default day is Monday
      setStartTime("7:00 AM");
      setEndTime("7:00 PM");
    } catch (error) {
      console.error("Error clearing filters:", error);
    }
  };

  const handleSearchChange = async (e) => {
    const searchValue = e.target.value;
    setSearchTerm(searchValue);
    if (searchValue === "") {
      fetchSearchResults();
    } else {
      const response = await axios.post("http://localhost:7000/excuteGeneralSearch", null, {
        params: { searchTerm: searchValue },
      });
      setFilteredCourses(response.data); // Update filteredCourses from response
    }
  };

  const handleCourseSelect = (index) => {
    setSelectedCourseIndex((prev) => {
      console.log("Previous selected index:", prev);
      console.log("New selected index:", index);
      return index;
    });
  };

  const addCourseHandler = async () => {
    if (selectedCourseIndex === null) {
      alert("Please select a course first.");
      return;
    }

    const selectedCourse = filteredCourses[selectedCourseIndex];

    try {
      const response = await axios.post("http://localhost:7000/addCourse", null, {
        params: { courseIndex: selectedCourseIndex },
      });

      setFilteredCourses(response.data); // Update filteredCourses from response
      setSelectedCourseIndex(null); // Reset selection after successful addition
      alert(`Course "${selectedCourse.name}" added successfully!`);
    } catch (error) {
      console.error("Error adding course:", error);
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

  const handleDayChange = async (e) => {
    const dayFormatChoice = e.target.value;
    setSelectedDayFormat(dayFormatChoice);

    const response = await axios.post("http://localhost:7000/excuteDayFilterSearch", null, {
      params: { dayFormatChoice: dayFormatChoice },
    });
    setFilteredCourses(response.data); // Update filteredCourses from response
  };

  const generateTimeOptions = () => {
    const times = [];
    for (let hour = 7; hour <= 22; hour++) {
      const ampm = hour < 12 ? "AM" : "PM";
      const hour12 = hour % 12 === 0 ? 12 : hour % 12;
      times.push(`${hour12}:00 ${ampm}`);
    }
    return times;
  };

  const handleStartTimeChange = async (e) => {
    const newValue = e.target.value;
    setStartTime(newValue);

    const response = await axios.post("http://localhost:7000/excuteTimeFilterSearch", null, {
      params: { startTime: newValue, endTime: endTime },
    });
    setFilteredCourses(response.data); // Update filteredCourses from response
  };

  const handleEndTimeChange = async (e) => {
    const newValue = e.target.value;
    setEndTime(newValue);

    const response = await axios.post("http://localhost:7000/excuteTimeFilterSearch", null, {
      params: { startTime: startTime, endTime: newValue },
    });
    setFilteredCourses(response.data); // Update filteredCourses from response
  };

  const undoAddCourseHandler = async () => {
    const response = await axios.post("http://localhost:7000/undoAdd");
    setFilteredCourses(response.data); // Update filteredCourses from response
  };

  const isTimeOverlapping = (courseTime, conflictingTime) => {
    const startCourse = convertToMinutes(courseTime.start_time);
    const endCourse = convertToMinutes(courseTime.end_time);
    const startConflict = convertToMinutes(conflictingTime.start_time);
    const endConflict = convertToMinutes(conflictingTime.end_time);

    return (
      (startCourse >= startConflict && startCourse <= endConflict) ||
      (endCourse >= startConflict && endCourse <= endConflict)
    );
  };

  const convertToMinutes = (time) => {
    const [hours, minutes] = time.split(":").map(Number);
    return hours * 60 + minutes;
  };

  const isCourseConflicting = (course) => {
    return conflictingCourses.some((conflictingCourse) => {
      if (conflictingCourse.name === course.name && conflictingCourse.number === course.number) {
        return true;
      }
      return conflictingCourse.times.some((conflictingTime) =>
        course.times.some((courseTime) =>
          courseTime.day === conflictingTime.day && isTimeOverlapping(courseTime, conflictingTime)
        )
      );
    });
  };

  const isCourseAdded = (course) => {
    return addedCourses.some((addedCourse) => {
      if (addedCourse.name === course.name && addedCourse.number === course.number) {
        return true;
      }
      return addedCourse.times.some((addedTime) =>
        course.times.some((courseTime) =>
          courseTime.day === addedTime.day && isTimeOverlapping(courseTime, addedTime)
        )
      );
    });
  };

  return (
    <div>
      <ScheduleControls />

      <div className="search-container">
        <input
          type="text"
          value={searchTerm}
          onChange={handleSearchChange}
          placeholder="General Search"
        />
        <button className="clear-button" onClick={clearSearch}>Clear</button>
      </div>

      <div className="day-selector">
        <label>
          <input
            type="radio"
            value="MWF"
            checked={selectedDayFormat === "MWF"}
            onChange={handleDayChange}
          />
          MWF (Monday, Wednesday, Friday)
        </label>
        <label>
          <input
            type="radio"
            value="TR"
            checked={selectedDayFormat === "TR"}
            onChange={handleDayChange}
          />
          TR (Tuesday, Thursday)
        </label>
        <button className="clear-button" onClick={clearDayFormat}>Clear</button>
      </div>

      <div className="time-range">
        <label>
          Start Time:
          <select value={startTime} onChange={handleStartTimeChange}>
            {generateTimeOptions().map((time) => (
              <option key={time} value={time}>{time}</option>
            ))}
          </select>
        </label>

        <label>
          End Time:
          <select value={endTime} onChange={handleEndTimeChange}>
            {generateTimeOptions().map((time) => (
              <option key={time} value={time}>{time}</option>
            ))}
          </select>
        </label>
        <button className="clear-button" onClick={clearTimeRange}>Clear</button>
      </div>

      <div className="courses-list">
        <h3>Courses</h3>
        <div className="course-box">
          {filteredCourses.length === 0 ? (
            <p>No courses found</p>
          ) : (
            filteredCourses.map((course, index) => (
              <div
                key={index}
                className={`course-item ${selectedCourseIndex === index ? "selected-course" : ""}`}
                onClick={() => setSelectedCourseIndex(index)}
              >
                <span>{course.name} ({course.credits} credits)</span>
                <span>
                  {" - "}
                  {course.times
                    .map((time) => `${time.day} ${convertTo12HourFormat(time.start_time)} - ${convertTo12HourFormat(time.end_time)}`)
                    .join(", ")}
                </span>
                <button
                  onClick={() => handleCourseSelect(index)}
                  disabled={selectedCourseIndex === index}
                  className={`course-button ${selectedCourseIndex === index ? "selected-button" : ""}`}
                >
                  {selectedCourseIndex === index ? "Selected" : "Select"}
                </button>
              </div>
            ))
          )}
        </div>
      </div>

      <div className="button-container">
        <button className="add-course-button" onClick={addCourseHandler}>
          Add Course
        </button>
        <button className="add-course-button" onClick={undoAddCourseHandler}>
          Undo Add
        </button>
      </div>
    </div>
  );
};

export default AddCourseScreen;