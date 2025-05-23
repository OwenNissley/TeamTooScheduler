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
    const [coursesToDisplay, setCoursesToDisplay] = useState([]);
    const [selectedCourseIndex, setSelectedCourseIndex] = useState(null);


    const updateCoursesToDisplay = async () => {
        try {
               const response = await axios.get("http://localhost:7000/getCoursesToDisplay");
               setCoursesToDisplay(response.data);
                console.log("Courses to display:", response.data);
             } catch (error) {
               console.error("Error updateing courses to display for conflicts:", error);
             }
    };


     const fetchCourses = async () => {
         const response = await axios.get("http://localhost:7000/updateSchedule");
         const courses = response.data;
         setCourses(courses);
         console.log("Fetched courses:", courses);
     };

  useEffect(() => {
     fetchCourses();
     updateConflicts();
     updateCoursesToDisplay();
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

const removeAllCoursesHandler = async () => {

   //const selectedCourse = courses[selectedCourseIndex];
   try {
     const response = await axios.post("http://localhost:7000/removeAllCourses");
     setCourses(response.data); // Update filteredCourses from response
     setSelectedCourseIndex(null); // Reset selection after successful addition
     updateConflicts();
     updateCoursesToDisplay();
    // alert(`Course "${selectedCourse.name}" removed successfully!`);
   } catch (error) {
     console.error("Error adding course:", error);
   }

 };


const removeCourseHandler = async () => {
   if (selectedCourseIndex === null) {
     alert("Please select a course first.");
     return;
   }

   const selectedCourse = coursesToDisplay[selectedCourseIndex];
   console.log(selectedCourse);
   const courseData = {
        name: selectedCourse.name,
        number: selectedCourse.number,
        credits: selectedCourse.credits,
       }



   try {
    const response = await axios.post( "http://localhost:7000/removeCourse",courseData);

     setCourses(response.data); // Update filteredCourses from response
     setSelectedCourseIndex(null); // Reset selection after successful addition
     updateConflicts();
     updateCoursesToDisplay();
    // alert(`Course "${selectedCourse.name}" removed successfully!`);
   } catch (error) {
     console.error("Error removing course:", error);
   }

 };

//This has to be in front end, and no contains for a list, so making it myself
const isTimeOverlapping = (courseTime, conflictingTime) => {
  // Convert times to minutes to simplify the comparison.
  const startCourseTimeInMinutes = convertToMinutes(courseTime.start_time);
  const endCourseTimeInMinutes = convertToMinutes(courseTime.end_time);
  const startConflictingTimeInMinutes = convertToMinutes(conflictingTime.start_time);
  const endConflictingTimeInMinutes = convertToMinutes(conflictingTime.end_time);

  // Check if there's any overlap.
  return (
    (startCourseTimeInMinutes < endConflictingTimeInMinutes && endCourseTimeInMinutes > startConflictingTimeInMinutes)
  );
};

const convertToMinutes = (time) => {
  const [hours, minutes] = time.split(":").map(Number);
  return hours * 60 + minutes;
};

// This needs redone to back end
const isCourseConflicting = (course) => {
  return conflictingCourses.some((conflictingCourse) => {
    // Case 1: Check if the name and number are the same
    if (conflictingCourse.name === course.name && conflictingCourse.number === course.number) {
      return true;
    }
    // Case 2: Check if the times overlap on the same day
    return conflictingCourse.times.some((conflictingTime) =>
      course.times.some((courseTime) => {
        // Check if the days are the same
        if (courseTime.day === conflictingTime.day) {
          // Use helper method to check if times overlap
          return isTimeOverlapping(courseTime, conflictingTime);
        }
        return false;
      })
    );
  });
};

    const undoRemoveCourseHandler = async () => {
        try {
            const response = await axios.post("http://localhost:7000/undoRemoveCourse");
            const courses = response.data;
            setCourses(courses);
            updateCoursesToDisplay();
            updateConflicts();
            console.log("Fetched courses:", courses);
        } catch (error) {
            console.error("Error fetching courses:", error);
        }

    }


 return (
   <div className="review-course-screen">
     <h1 className="title">Review Courses</h1>

       <ScheduleControls />

     {hasConflict && (
       <div className="courses-list conflict-section">
         <h3 className="section-title">⚠️ Conflict Detected!</h3>
         <div className="course-box">
           {conflictingCourses.length === 0 ? (
             <p>No conflicting courses</p>
           ) : (
             conflictingCourses.map((course, index) => (
               <div key={index} className="course-item">
                 <span className="course-name">
                   {course.name} ({course.credits} credits)
                 </span>
                 <span className="course-time">
                   {" - "}
                   {course.times
                     .map((time) =>
                       `${time.day} ${convertTo12HourFormat(time.start_time)} - ${convertTo12HourFormat(time.end_time)}`
                     )
                     .join(", ")}
                 </span>
               </div>
             ))
           )}
         </div>
       </div>
     )}

     <div className="courses-list">
       <h3 className="section-title">Courses</h3>
       <div className="course-box">
         {coursesToDisplay.length === 0 ? (
           <p>No courses found</p>
         ) : (
           coursesToDisplay.map((course, index) => (
                <div
                       key={index}
                       className={`course-item
                         ${selectedCourseIndex === index ? "selected-course" : ""}
                         ${isCourseConflicting(course) ? "conflicting-course" : ""}`}
                       onClick={() => setSelectedCourseIndex(index)}
                     >

               <span className="course-name">
                 {course.name} ({course.credits} credits)
               </span>
               <span className="course-time">
                 {" - "}
                 {course.times
                   .map((time) =>
                     `${time.day} ${convertTo12HourFormat(time.start_time)} - ${convertTo12HourFormat(time.end_time)}`
                   )
                   .join(", ")}
               </span>
               <button
                 onClick={(e) => {
                   e.stopPropagation();
                   handleCourseSelect(index);
                 }}
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

    <div className="button-wrapper">
      <button className="remove-course-button" onClick={removeCourseHandler}>
        Remove Course
      </button>
      <button
        className="remove-all-courses-button"
        onClick={removeAllCoursesHandler}
        disabled={coursesToDisplay.length === 0}
      >
        Remove All Courses
      </button>
       <button
              className="remove-course-button"
              onClick={undoRemoveCourseHandler}
            >
              Undo Remove
            </button>
    </div>

  </div>
 );


}

export default ReviewCourseScreen;
