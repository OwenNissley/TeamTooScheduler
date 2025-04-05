import React, { useContext, useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, useLocation } from "react-router-dom";
import "./Calendar.css";
import ScheduleControls from "./ScheduleControls";
import { ScheduleContext } from "./ScheduleContext";

const ReviewCourseScreen = () => {
    const { selectedYear, selectedTerm, selectedSchedule, numOfSchedules} = useContext(ScheduleContext);
    const navigate = useNavigate();
    const [courses, setCourses] = useState([]);
    const [hasConflict, setHasConflict] = useState(false);
    const [conflictingCourses, setConflictingCourses] = useState([]);
    const [selectedCourseIndex, setSelectedCourseIndex] = useState(null);

     const fetchCourses = async () => {
         const response = await axios.get("http://localhost:7000/updateSchedule");
         const courses = response.data;
         setCourses(courses);
         console.log("Fetched courses:", courses);
     };

  useEffect(() => {
     fetchCourses();
     updateConflicts();
   }, [selectedYear, selectedTerm, selectedSchedule,numOfSchedules ]);

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



 const handleCourseSelect = (index) => {
       setSelectedCourseIndex((prev) => {
           console.log("Previous selected index:", prev);
           console.log("New selected index:", index);
           return index;
       });
   };

const removeCourseHandler = async () => {
    /*
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

    */

 };


  return (
    <div className="review-course-screen">
      <h1>Review Courses</h1>
      {/* Add your review course logic here */}

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

   <div className="courses-list">
        <h3>Courses</h3>
        <div className="course-box">
          {courses.length === 0 ? (
            <p>No courses found</p>
          ) : (
            courses.map((course, index) => (
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
            <button className="remove-course-button" onClick={removeCourseHandler}>
              Remove Course
            </button>




    </div>
  );

}

export default ReviewCourseScreen;
