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
  const navigate = useNavigate();
  //Boshi not working ??
  const [navigationCount, setNavigationCount] = useState(0);
  const location = useLocation();

 //Boshi not working ??
useEffect(() => {
  setNavigationCount((count) => count + 1);
}, [location.key]); // location.key changes every time you go to this route

 //Boshi not working ??
useEffect(() => {
  fetchSearchResults();
   setSearchTerm("");
}, [navigationCount]);

 const fetchSearchResults = async () => {
    try {
      const response = await axios.get("http://localhost:7000/getSearchResults");
      setFilteredCourses(response.data); // assuming response.data is an array of courses
    } catch (error) {
      console.error("Error fetching courses:", error);
    }
  };

 const handleSearchChange = async (e) => {
    const searchValue = e.target.value;
    setSearchTerm(searchValue);
    if (searchValue === "") {
      // Trigger re-fetch of courses when search term is empty
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

     <ScheduleControls />

      <div className="search-container">
        <input
          type="text"
          value={searchTerm}
          onChange={handleSearchChange}
          placeholder="General Search"
        />
      </div>

    <div className="courses-list">
      <h3>Courses</h3>
      <div className="course-box">
        {filteredCourses.length === 0 ? (
          <p>No courses found</p>
        ) : (
          filteredCourses.map((course, index) => (
            <div key={index} className="course-item" onClick={() => setSelectedCourseIndex(index)}>
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



      {/* Green Add Course Button */}
            <button className="add-course-button" onClick={addCourseHandler}>
              Add Course
            </button>


    </div>
  );
};

export default AddCourseScreen;
