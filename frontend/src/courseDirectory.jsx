import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./Calendar.css";

const CourseDirectory = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredCourses, setFilteredCourses] = useState([]);
  const [hoveredCourseDetails, setHoveredCourseDetails] = useState(null);
  const [popupPosition, setPopupPosition] = useState({ top: 0, left: 0 });
  const [isMouseOver, setIsMouseOver] = useState(false);
  const navigate = useNavigate();

  const handleSearchChange = async (e) => {
    const searchValue = e.target.value;
    setSearchTerm(searchValue);

    if (!searchValue) {
      setFilteredCourses([]);
      return;
    }

    try {
      const response = await axios.post("http://localhost:7000/excuteGeneralSearch", null, {
        params: { searchTerm: searchValue },
      });
      setFilteredCourses(response.data);
    } catch (error) {
      console.error("Error fetching courses:", error);
    }
  };

  const convertTo12HourFormat = (militaryTime) => {
    const [hours, minutes] = militaryTime.split(":").map(Number);
    const period = hours < 12 ? "AM" : "PM";
    const convertedHour = hours % 12 || 12;
    const formattedMinutes = minutes < 10 ? `0${minutes}` : minutes;
    return `${convertedHour}:${formattedMinutes} ${period}`;
  };

  const handleMouseEnterCourse = async (e, course) => {
    const rect = e.target.getBoundingClientRect();
    let top = rect.bottom + window.scrollY;
    let left = rect.left + window.scrollX;

    // Adjust popup position to fit within the screen
    const popupHeight = 200;
    const popupWidth = 300;
    if (top + popupHeight > window.innerHeight + window.scrollY) {
      top = rect.top + window.scrollY - popupHeight;
    }
    if (left + popupWidth > window.innerWidth + window.scrollX) {
      left = window.innerWidth + window.scrollX - popupWidth;
    }

    setPopupPosition({ top, left });

    try {
      const response = await axios.post(
        "http://localhost:7000/parseCourseInformation",
        null,
        { params: { courseId: course.name } }
      );
      if (response.data) {
        setHoveredCourseDetails(response.data);
        setIsMouseOver(true); // Keep the popup visible
      } else {
        console.error("No course details returned from the server");
      }
    } catch (error) {
      console.error("Error fetching course details:", error);
    }
  };

  const handleMouseLeaveCourse = () => {
    setTimeout(() => {
      if (!isMouseOver) {
        setHoveredCourseDetails(null); // Hide the popup
      }
    }, 50); // Small delay to allow mouse transition to the popup
  };

  const handleMouseEnterArea = () => {
    setIsMouseOver(true); // Keep the popup visible
  };

  const handleMouseLeaveArea = () => {
    setIsMouseOver(false);
    setTimeout(() => {
      if (!isMouseOver) {
        setHoveredCourseDetails(null); // Hide the popup
      }
    }, 50); // Small delay to ensure proper state update
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

      <div
        className="courses-container"
        onMouseEnter={handleMouseEnterArea}
        onMouseLeave={handleMouseLeaveArea}
      >
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
                  onMouseEnter={(e) => handleMouseEnterCourse(e, course)}
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
                </div>
              ))
            )}
          </div>
        </div>

        {hoveredCourseDetails && (
          <div
            className="hovered-course-details-popup"
            style={{ top: popupPosition.top, left: popupPosition.left }}
            onMouseEnter={handleMouseEnterArea}
            onMouseLeave={handleMouseLeaveArea}
          >
            <div className="popup-content">
              <h4>Course Details</h4>
              <p>Name: {hoveredCourseDetails.name}</p>
              <p>Credits: {hoveredCourseDetails.credits}</p>
              <p>Faculty: {hoveredCourseDetails.faculty}</p>
              <p>Location: {hoveredCourseDetails.location}</p>
              <p>Open Seats: {hoveredCourseDetails.openSeats}</p>
              <p>Total Seats: {hoveredCourseDetails.totalSeats}</p>
              <p>Times: {hoveredCourseDetails.times.join(", ")}</p>
              <p>Subject: {hoveredCourseDetails.subject}</p>
              <p>Number: {hoveredCourseDetails.number}</p>
              <p>Section: {hoveredCourseDetails.section}</p>
              <p>Semester: {hoveredCourseDetails.semester}</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default CourseDirectory;