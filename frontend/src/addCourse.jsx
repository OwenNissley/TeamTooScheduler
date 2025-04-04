import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./Calendar.css";

const AddCourseScreen = () => {
  const [numOfSchedules, setNumOfSchedules] = useState(1);
  const [selectedSchedule, setSelectedSchedule] = useState(1);
  const [selectedYear, setSelectedYear] = useState("2023");
  const [selectedTerm, setSelectedTerm] = useState("Fall");
  const [selectedCourseIndex, setSelectedCourseIndex] = useState(null); // Track selected course index
  const [filteredCourses, setFilteredCourses] = useState([]); // Filtered courses based on search
  const [searchTerm, setSearchTerm] = useState(""); // Track search input
  const navigate = useNavigate();


 const fetchSearchResults = async () => {
    try {
      const response = await axios.get("http://localhost:7000/getSearchResults");
      setFilteredCourses(response.data); // assuming response.data is an array of courses
    } catch (error) {
      console.error("Error fetching courses:", error);
    }
  };

  useEffect(() => {
      if (searchTerm === "") {
        // Fetch initial courses when the search term is empty
        fetchSearchResults();
      }
    }, [searchTerm]); // Runs every time the searchTerm changes


useEffect(() => {
    fetchSearchResults();
    fetchNumberOfSchedules();
  }, []);

  const fetchNumberOfSchedules = async () => {
    try {
      const response = await axios.get("http://localhost:7000/getNumOfSchedules");
      setNumOfSchedules(response.data);
    } catch (error) {
      console.error("Error fetching number of schedules:", error);
    }
  };

  const handleScheduleChange = async (e) => {
    const newScheduleNum = Number(e.target.value);
    console.log("Selected schedule:", newScheduleNum);
    try {
      await axios.post("http://localhost:7000/selectSchedule", null, {
        params: { scheduleIndex: newScheduleNum - 1 },
      });
    } catch (error) {
      console.error("Error updating schedule:", error);
    }
    setSelectedSchedule(newScheduleNum);
  };

  const handleNewSchedule = async () => {
    try {
      const response = await axios.post("http://localhost:7000/newSchedule");
      const newScheduleNumber = response.data;
      console.log("New schedule created:", newScheduleNumber);
      setNumOfSchedules((prev) => prev + 1);
      setSelectedSchedule(newScheduleNumber + 1);
    } catch (error) {
      console.error("Error creating new schedule:", error);
    }
  };

  const handleDeleteSchedule = async () => {
    try {
      const response = await axios.post("http://localhost:7000/deleteSchedule");
      const newScheduleIndex = response.data;
      setNumOfSchedules((prev) => (prev > 1 ? prev - 1 : 1));
      setSelectedSchedule(newScheduleIndex + 1);
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
    fetchNumberOfSchedules();
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
    fetchNumberOfSchedules();
    setSelectedSchedule(1);
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
              Schedule {index + 1}
            </option>
          ))}
        </select>

        <button onClick={handleNewSchedule}>New Schedule</button>
        <button onClick={handleDeleteSchedule}>Delete Schedule</button>
      </div>

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
