import React, { useContext, useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, useLocation } from "react-router-dom";
import "./Calendar.css";
import ScheduleControls from "./ScheduleControls";
import { ScheduleContext } from "./ScheduleContext";

const CourseDirectory = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredCourses, setFilteredCourses] = useState([]);
  const [selectedCourseIndex, setSelectedCourseIndex] = useState(null);
  const [selectedDayFormat, setSelectedDayFormat] = useState("");
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");
  const navigate = useNavigate();

  const handleSearchChange = async (e) => {
    const searchValue = e.target.value;
    setSearchTerm(searchValue);

    if (!searchValue) {
      setFilteredCourses([]);
      return;
    }

    const response = await axios.post("http://localhost:7000/excuteGeneralSearch", null, {
      params: { searchTerm: searchValue },
    });
    setFilteredCourses(response.data);
  };

  const handleCourseSelect = (index) => {
    setSelectedCourseIndex(index);
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

      setFilteredCourses(response.data);
      setSelectedCourseIndex(null);
      alert(`Course "${selectedCourse.name}" added successfully!`);
    } catch (error) {
      console.error("Error adding course:", error);
    }
  };

  const convertTo12HourFormat = (militaryTime) => {
    const [hours, minutes] = militaryTime.split(":").map(Number);
    const period = hours < 12 ? "AM" : "PM";
    const convertedHour = hours % 12 || 12;
    const formattedMinutes = minutes < 10 ? `0${minutes}` : minutes;
    return `${convertedHour}:${formattedMinutes} ${period}`;
  };

  const handleDayChange = async (e) => {
    const dayFormatChoice = e.target.value;
    setSelectedDayFormat(dayFormatChoice);

    const response = await axios.post("http://localhost:7000/excuteDayFilterSearch", null, {
      params: { dayFormatChoice },
    });
    setFilteredCourses(response.data);
  };

  const generateTimeOptions = () => {
    const times = [];
    for (let hour = 7; hour <= 22; hour++) {
      const ampm = hour < 12 ? "AM" : "PM";
      const hour12 = hour % 12 || 12;
      times.push(`${hour12}:00 ${ampm}`);
    }
    return times;
  };

  const handleStartTimeChange = async (e) => {
    const newValue = e.target.value;
    setStartTime(newValue);

    if (!newValue || !endTime) return;

    const response = await axios.post("http://localhost:7000/excuteTimeFilterSearch", null, {
      params: { startTime: newValue, endTime },
    });
    setFilteredCourses(response.data);
  };

  const handleEndTimeChange = async (e) => {
    const newValue = e.target.value;
    setEndTime(newValue);

    if (!newValue || !startTime) return;

    const response = await axios.post("http://localhost:7000/excuteTimeFilterSearch", null, {
      params: { startTime, endTime: newValue },
    });
    setFilteredCourses(response.data);
  };

  return (
    <div className="controls-container">
      <div className="top-banner">
        <button onClick={() => navigate("/")}>Home</button>
        <button onClick={() => navigate("/quick-schedule")}>Quick Schedule</button>
        <button onClick={() => navigate("/addCourse")}>Add Course</button>
        <button onClick={() => navigate("/review")}>Review</button>
        <button onClick={() => navigate("/course-directory")}>Course Directory</button>
        <button onClick={() => navigate("/your-info")}>Your Info</button>
      </div>

      <div className="search-container">
        <input
          type="text"
          value={searchTerm}
          onChange={handleSearchChange}
          placeholder="General Search"
        />
        <button className="clear-button" onClick={() => setSearchTerm("")}>
          Clear
        </button>
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
        <button className="clear-button" onClick={() => setSelectedDayFormat("")}>
          Clear
        </button>
      </div>

      <div className="time-range">
        <label>
          Start Time:
          <select value={startTime} onChange={handleStartTimeChange}>
            {generateTimeOptions().map((time) => (
              <option key={time} value={time}>
                {time}
              </option>
            ))}
          </select>
        </label>

        <label>
          End Time:
          <select value={endTime} onChange={handleEndTimeChange}>
            {generateTimeOptions().map((time) => (
              <option key={time} value={time}>
                {time}
              </option>
            ))}
          </select>
        </label>
        <button className="clear-button" onClick={() => {
          setStartTime("");
          setEndTime("");
        }}>
          Clear
        </button>
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
                className="course-item"
                onClick={() => handleCourseSelect(index)}
              >
                <span>
                  {course.name} ({course.credits} credits)
                </span>
                <span>
                  {" - "}
                  {course.times
                    .map(
                      (time) =>
                        `${time.day} ${convertTo12HourFormat(
                          time.start_time
                        )} - ${convertTo12HourFormat(time.end_time)}`
                    )
                    .join(", ")}
                </span>
                <button
                  onClick={() => handleCourseSelect(index)}
                  disabled={selectedCourseIndex === index}
                  className={`course-button ${
                    selectedCourseIndex === index ? "selected-button" : ""
                  }`}
                >
                  {selectedCourseIndex === index ? "Selected" : "Select"}
                </button>
              </div>
            ))
          )}
        </div>
      </div>

      <button className="add-course-button" onClick={addCourseHandler}>
        Add Course
      </button>
    </div>
  );
};

export default CourseDirectory;