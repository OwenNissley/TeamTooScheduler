import React, { createContext, useState, useEffect } from "react";
import axios from "axios";

export const ScheduleContext = createContext();

export const ScheduleProvider = ({ children }) => {
  const [selectedYear, setSelectedYear] = useState("2023");
  const [selectedTerm, setSelectedTerm] = useState("Fall");
  const [selectedSchedule, setSelectedSchedule] = useState(1);
  const [numOfSchedules, setNumOfSchedules] = useState(1);

  useEffect(() => {
    const getNumberOfSchedules = async () => {
      try {
        const response = await axios.get("http://localhost:7000/getNumOfSchedules");
        setNumOfSchedules(response.data);
      } catch (error) {
        console.error("Error fetching number of schedules:", error);
      }
    };

    getNumberOfSchedules();
  }, [selectedTerm, selectedYear]);

  const handleYearChange = async (newYear) => {
    setSelectedYear(newYear);
    try {
      await axios.post("http://localhost:7000/updateYear", null, {
        params: { yearTermString: `${newYear}_${selectedTerm}` },
      });
    } catch (error) {
      console.error("Error updating year:", error);
    }
  };

  const handleTermChange = async (newTerm) => {
    setSelectedTerm(newTerm);
    try {
      await axios.post("http://localhost:7000/updateTerm", null, {
        params: { yearTermString: `${selectedYear}_${newTerm}` },
      });
    } catch (error) {
      console.error("Error updating term:", error);
    }
  };

  const handleScheduleChange = async (newScheduleNum) => {
    setSelectedSchedule(newScheduleNum);
    try {
      await axios.post("http://localhost:7000/selectSchedule", null, {
        params: { scheduleIndex: newScheduleNum - 1 },
      });
    } catch (error) {
      console.error("Error updating schedule:", error);
    }
  };

  const handleNewSchedule = async () => {
    try {
      const response = await axios.post("http://localhost:7000/newSchedule");
      const newScheduleNumber = response.data;
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

  return (
    <ScheduleContext.Provider
      value={{
        selectedYear,
        selectedTerm,
        selectedSchedule,
        numOfSchedules,
        setSelectedYear: handleYearChange,
        setSelectedTerm: handleTermChange,
        setSelectedSchedule: handleScheduleChange,
        handleNewSchedule,
        handleDeleteSchedule,
      }}
    >
      {children}
    </ScheduleContext.Provider>
  );
};
