import React, { useState, useEffect, useRef } from "react";
import axios from "axios";
import { useNavigate, useLocation } from "react-router-dom";
import "./Calendar.css";

const CourseDirectory = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredCourses, setFilteredCourses] = useState([]);
  const [hoveredCourseDetails, setHoveredCourseDetails] = useState(null);
  const [popupPosition, setPopupPosition] = useState({ top: 0, left: 0 });
  const [isMouseOver, setIsMouseOver] = useState(false);
  const [selectedYear, setSelectedYear] = useState("");
  const [selectedSemester, setSelectedSemester] = useState("");
  const debounceTimer = useRef(null); // Ref for debounce timer
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const searchQuery = params.get("search");
    const semester = params.get("semester");
    const year = params.get("year");

    if (searchQuery) setSearchTerm(searchQuery);
    if (semester) setSelectedSemester(semester);
    if (year) setSelectedYear(year);

    performSearch(searchQuery, year, semester);
  }, [location]);

  const handleSearchChange = (e) => {
    const searchValue = e.target.value;
    setSearchTerm(searchValue);

    // Debounce the search
    if (debounceTimer.current) clearTimeout(debounceTimer.current);
    debounceTimer.current = setTimeout(() => {
      performSearch(searchValue, selectedYear, selectedSemester);
    }, 300); // 300ms delay
  };

  const handleYearChange = (e) => {
    const year = e.target.value;
    setSelectedYear(year);

    // Debounce the search
    if (debounceTimer.current) clearTimeout(debounceTimer.current);
    debounceTimer.current = setTimeout(() => {
      performSearch(searchTerm, year, selectedSemester);
    }, 300);
  };

  const handleSemesterChange = (e) => {
    const semester = e.target.value;
    setSelectedSemester(semester);

    // Debounce the search
    if (debounceTimer.current) clearTimeout(debounceTimer.current);
    debounceTimer.current = setTimeout(() => {
      performSearch(searchTerm, selectedYear, semester);
    }, 300);
  };

  const performSearch = async (query, year, semester) => {
    try {
      const response = await axios.post("http://localhost:7000/excuteGeneralSearch", null, {
        params: { searchTerm: query },
      });

      let courses = response.data;

      // Filter courses based on year and semester
      courses = courses.filter((course) => {
        const [courseYear, courseSemester] = course.semester.split("_");
        return (
          (!year || courseYear === year) &&
          (!semester || courseSemester === semester)
        );
      });

      setFilteredCourses(courses);
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
        setIsMouseOver("course");

        const fadeOutTimer = setTimeout(() => {
          if (!isMouseOver) {
            const popup = document.querySelector(".hovered-course-details-popup");
            if (popup) {
              popup.classList.add("fade-out");
              setTimeout(() => {
                setHoveredCourseDetails(null);
              }, 500);
            }
          }
        }, 4000);

        const popup = document.querySelector(".hovered-course-details-popup");
        if (popup) {
          popup.addEventListener("mouseenter", () => {
            clearTimeout(fadeOutTimer);
          });
        }
      } else {
        console.error("No course details returned from the server");
      }
    } catch (error) {
      console.error("Error fetching course details:", error);
    }
  };

  const handleMouseLeaveCourse = () => {
    if (isMouseOver === "course") {
      setIsMouseOver(null);
      const popup = document.querySelector(".hovered-course-details-popup");
      if (popup) {
        popup.classList.add("fade-out");
        setTimeout(() => {
          setHoveredCourseDetails(null);
        }, 500);
      }
    }
  };

  const handleMouseEnterArea = () => {
    setIsMouseOver("popup");
  };

  const handleMouseLeaveArea = () => {
    if (isMouseOver === "popup") {
      setIsMouseOver(null);
      setTimeout(() => {
        if (!isMouseOver) {
          const popup = document.querySelector(".hovered-course-details-popup");
          if (popup) {
            popup.classList.add("fade-out");
            setTimeout(() => {
              setHoveredCourseDetails(null);
            }, 500);
          }
        }
      }, 50);
    }
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

      <div className="semester-selector">
        <label>
          Year:
          <select value={selectedYear} onChange={handleYearChange}>
            <option value="">Any</option>
            <option value="2023">2023</option>
            <option value="2024">2024</option>
            <option value="2025">2025</option>
          </select>
        </label>
        <label>
          Semester:
          <select value={selectedSemester} onChange={handleSemesterChange}>
            <option value="">Any</option>
            <option value="Fall">Fall</option>
            <option value="Spring">Spring</option>
            <option value="Summer">Summer</option>
          </select>
        </label>
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
              <button
                className="close-popup-button"
                onClick={() => {
                  const popup = document.querySelector(".hovered-course-details-popup");
                  if (popup) {
                    popup.classList.add("fade-out");
                    setTimeout(() => {
                      setHoveredCourseDetails(null);
                    }, 500);
                  }

                  setIsMouseOver("blocked");
                  setTimeout(() => {
                    setIsMouseOver(null);
                  }, 1000);
                }}
              >
                &times;
              </button>
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