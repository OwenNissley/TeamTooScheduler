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
  const { selectedYear, selectedTerm, selectedSchedule, numOfSchedules} = useContext(ScheduleContext);
  const [conflictStatuses, setConflictStatuses] = useState([]);
  const [addedStatuses, setAddedStatuses] = useState([]);

useEffect(() => {
  const checkStatuses = async () => {
    const conflicts = await Promise.all(
      filteredCourses.map((course) => isCourseConflicting(course))
    );
    const added = await Promise.all(
      filteredCourses.map((course) => isCourseAdded(course))
    );
    setConflictStatuses(conflicts);
    setAddedStatuses(added);
  };

  if (filteredCourses.length > 0) {
    checkStatuses();
  }
}, [filteredCourses]);


  const navigate = useNavigate();
  //Boshi not working ??
  const [navigationCount, setNavigationCount] = useState(0);
  const location = useLocation();

 //Boshi not working ??
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
           setSearchTerm ("");
           } catch(error){
                  console.error("Error clearing search filters:", error);
           }
       }

const clearDayFormat = async () => {
        try {
                const response = await axios.post("http://localhost:7000/clearDayFormat");
                setFilteredCourses(response.data); // assuming response.data is an array of courses
                setSelectedDayFormat(null);
                } catch(error){
                          console.error("Error clearing day format:", error);
                }
        }
const clearTimeRange = async () => {
        try {
                const response = await axios.post("http://localhost:7000/clearTimeRange");
                setFilteredCourses(response.data); // assuming response.data is an array of courses
                setStartTime("");
                setEndTime("");
                } catch(error){
                            console.error("Error clearing time range:", error);
                }
        }


 const fetchSearchResults = async () => {
    try {
      const response = await axios.get("http://localhost:7000/getSearchResults");
      setFilteredCourses(response.data); // assuming response.data is an array of courses
    } catch (error) {
      console.error("Error fetching courses:", error);
    }
  };

    //reset Search
   const clearFilters = async () => {
       try {
       const response = await axios.post("http://localhost:7000/clearFilters");
       setFilteredCourses(response.data);// assuming response.data is an array of courses
       setSearchTerm (""); // Track search input
       setSelectedDayFormat(null); // Default day is Monday
       setStartTime("");
       setEndTime("");
       } catch(error){
              console.error("Error clearing filters:", error);
       }
    }


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

const handleDayChange = async (e) => {
    const dayFormatChoice = e.target.value;
    setSelectedDayFormat(dayFormatChoice);
    // setSearchTerm(searchValue);

   const response = await axios.post("http://localhost:7000/excuteDayFilterSearch", null, {
        params: { dayFormatChoice: dayFormatChoice },
      });
      setFilteredCourses(response.data); // Update filteredCourses from response
  };

const generateTimeOptions = () => {
  const times = [];
  for (let hour = 7; hour <= 22; hour++) { // 7AM to 10PM
    const ampm = hour < 12 ? "AM" : "PM";
    const hour12 = hour % 12 === 0 ? 12 : hour % 12;
    times.push(`${hour12}:00 ${ampm}`);
  }
  return times;
};

 const handleStartTimeChange = async (e) => {
   const newValue = e.target.value;
   setStartTime((prev) => {
     console.log("Previous start time:", prev);
     console.log("New start time:", newValue);
     return newValue;
   });
   if (!newValue || !endTime) return; // don't send until both are selected

   console.log("Start time changed to:", newValue);
   console.log("End time:", endTime);
    const response = await axios.post("http://localhost:7000/excuteTimeFilterSearch", null, {
           params: { startTime: e.target.value, endTime: endTime },
         });
     setFilteredCourses(response.data); // Update filteredCourses from response
 };

 const handleEndTimeChange = async (e) => {
   const newValue = e.target.value;
   setEndTime((prev) => {
     console.log("Previous end time:", prev);
     console.log("New end time:", newValue);
     return newValue;
   });
   if (!newValue || !startTime) return; // don't send until both are selected

    console.log("End time changed to:", newValue);
    console.log("Start time:", startTime);
      const response = await axios.post("http://localhost:7000/excuteTimeFilterSearch", null, {
               params: { startTime: startTime, endTime: e.target.value },
             });
         setFilteredCourses(response.data); // Update filteredCourses from response
 };

 const undoAddCourseHandler = async () => {
        const response = await axios.post("http://localhost:7000/undoAdd");
        setFilteredCourses(response.data); // Update filteredCourses from response
     }

//This has to be in front end, and no contains for a list, so making it myself
const isTimeOverlapping = (courseTime, conflictingTime) => {
  const startCourse = convertToMinutes(courseTime.start_time);
  const endCourse = convertToMinutes(courseTime.end_time);
  const startConflict = convertToMinutes(conflictingTime.start_time);
  const endConflict = convertToMinutes(conflictingTime.end_time);

  // Check if the start of the course falls within the conflict time
  const startInConflict = startCourse >= startConflict && startCourse <= endConflict;
  // Check if the end of the course falls within the conflict time
  const endInConflict = endCourse >= startConflict && endCourse <= endConflict;

  // Return true if there's an overlap
  return startInConflict || endInConflict;
};

const convertToMinutes = (time) => {
  const [hours, minutes] = time.split(":").map(Number);
  return hours * 60 + minutes;
};

// This needs redone to back end
const isCourseConflicting = async (course) => {
    // get conflictingCoursesInSearchResults
  const response = await axios.post("http://localhost:7000/getConflictingCoursesInSearchResults");
  const conflictingCoursesInSearchResults = response.data;
  console.log("Conflicting courses in search results:", conflictingCoursesInSearchResults);
  if (conflictingCoursesInSearchResults.length === 0) {
    return false; // No conflicts if the list is empty
  }
// Check if the course is in the conflicting courses
return conflictingCoursesInSearchResults.some((conflictingCourse) => {
  // Check if the name and number are the same
  if (conflictingCourse.name === course.name && conflictingCourse.number === course.number) {
    return true;
  }

  // Check if the times overlap on the same day
  return conflictingCourse.times.some((conflictingTime) =>
    course.times.some((courseTime) =>
      courseTime.day === conflictingTime.day && isTimeOverlapping(courseTime, conflictingTime)
    )
  );
});
}

const isCourseAdded = async (course) => {
    // get allCourses
    const response = await axios.post("http://localhost:7000/getAllCourses");
    const addedCourses = response.data;
    console.log("Added courses:", addedCourses);
      return addedCourses.some((conflictingCourse) => {
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

     {/* Radio Buttons for MWF or TR */}
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

     {/* Time Range Selectors */}
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
                className={`course-item
                  ${selectedCourseIndex === index ? "selected-course" : ""}`}
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

     {/* Green Add Course Button */}
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
