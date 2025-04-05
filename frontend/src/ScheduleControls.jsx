import React, { useContext } from "react";
import { ScheduleContext } from "./ScheduleContext";

const ScheduleControls = () => {
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
  );
};

export default ScheduleControls;
