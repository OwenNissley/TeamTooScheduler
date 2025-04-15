import React, { useContext } from "react";
import { ScheduleContext } from "./ScheduleContext";
import { useNavigate } from "react-router-dom";

const ScheduleControls = () => {
    const navigate = useNavigate();
  const {
    selectedYear,
    selectedTerm,
    selectedSchedule,
    numOfSchedules,
    setSelectedYear,
    setSelectedTerm,
    setSelectedSchedule,
    setNumOfSchedules,
    handleNewSchedule,
    handleDeleteSchedule,
  } = useContext(ScheduleContext);

  return (
    <div className="controls-container">
      <div className="top-banner">
        <button className="nav-button" onClick={() => navigate("/")}>Home</button>
        <button className="nav-button" onClick={() => navigate("/quick-schedule")}>Quick Schedule</button>
        <button className="nav-button" onClick={() => navigate("/addCourse")}>Add Course</button>
        <button className="nav-button" onClick={() => navigate("/review")}>Review</button>
        <button className="nav-button" onClick={() => navigate("/course-directory")}>Course Directory</button>
        <button className="nav-button" onClick={() => navigate("/your-info")}>Your Info</button>
      </div>

      <div className="control-banner">
        <select value={selectedTerm} onChange={(e) => setSelectedTerm(e.target.value)}>
          <option value="Spring">Spring</option>
          <option value="Fall">Fall</option>
        </select>

        <select value={selectedYear} onChange={(e) => setSelectedYear(e.target.value)}>
          <option value="2023">2023</option>
          <option value="2024">2024</option>
          <option value="2025">2025</option>
        </select>

        <select value={selectedSchedule} onChange={(e) => setSelectedSchedule(Number(e.target.value))}>
          {Array.from({ length: numOfSchedules }, (_, index) => (
            <option key={index + 1} value={index + 1}>
              Schedule {index + 1}
            </option>
          ))}
        </select>

        <button onClick={handleNewSchedule}>New Schedule</button>
        <button onClick={handleDeleteSchedule}>Delete Schedule</button>
      </div>
    </div>
  );

};

export default ScheduleControls;
