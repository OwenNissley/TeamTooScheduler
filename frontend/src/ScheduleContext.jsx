import React, { createContext, useState, useEffect } from "react";
import axios from "axios";
import { useLocation } from "react-router-dom";

export const ScheduleContext = createContext();

export const ScheduleProvider = ({ children }) => {
  const [selectedYear, setSelectedYear] = useState("2023");
  const [selectedTerm, setSelectedTerm] = useState("Fall");
  const [selectedSchedule, setSelectedSchedule] = useState(1);
  const [numOfSchedules, setNumOfSchedules] = useState(1);
  const [deleteRunFlag, setDeleteRunFlag] = useState(false);
  const location = useLocation();



  useEffect(() => {
    const getNumberOfSchedulesAndCurrentSchedule = async () => {
        await new Promise(resolve => setTimeout(resolve, 500)); // wait .5 second
      try {
            console.log("Fetching number of schedules...");
        const response = await axios.get("http://localhost:7000/getNumberOfSchedulesAndCurrentSchedule");
        console.log("Number of schedules:", response.data);
        setNumOfSchedules(response.data.numOfSchedules);
        setSelectedSchedule(response.data.currentSchedule + 1); // +1 to match the UI
      } catch (error) {
        console.error("Error fetching number of schedules:", error);
      }
    };

    getNumberOfSchedulesAndCurrentSchedule();

  }, [selectedTerm, selectedYear,location,deleteRunFlag]);

 useEffect(() => {


    fetchCurrentSemester();

  }, [location, numOfSchedules, selectedSchedule,deleteRunFlag]);


  const handleYearChange = async (newYear) => {
    setSelectedYear(newYear);
    try {
      await axios.post("http://localhost:7000/updateYear", null, {
        params: { yearTermString: `${newYear}_${selectedTerm}` },
      });
        setSelectedSchedule(1); // Reset schedule to 1 when year changes
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
    setSelectedSchedule(1); // Reset schedule to 1 when year changes
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

  // Fetch current data
     const fetchCurrentSemester = async () => {
       try {
           await new Promise(resolve => setTimeout(resolve, 500)); // wait .5 second
         const response = await axios.get("http://localhost:7000/getCurrentData");
         const { yearTermString, scheduleIndex, numOfSchedules: scheduleNum } = response.data;
         const [year, term] = yearTermString.split("_");

         console.log("Fetched current data:", response.data);
         setSelectedYear(year);
         setSelectedTerm(term);
       } catch (error) {
         console.error("Error fetching current data:", error);
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
      const oldFlag = deleteRunFlag;
      setDeleteRunFlag(!oldFlag);
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
        handleYearChange,
        handleTermChange,
        handleScheduleChange,
        fetchCurrentSemester,
        deleteRunFlag,
      }}
    >
      {children}
    </ScheduleContext.Provider>
  );
};
