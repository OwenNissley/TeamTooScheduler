import React, { useContext, useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, useLocation } from "react-router-dom";
import "./Calendar.css";
import ScheduleControls from "./ScheduleControls";
import { ScheduleContext } from "./ScheduleContext";
import "./quickShec.css"; // Import the CSS file for styling

const QuickShecScreen = () => {
       const [selectedDayFormat, setSelectedDayFormat] = useState(null); // Default day is Monday
       const [startTime, setStartTime] = useState("");
       const [endTime, setEndTime] = useState("");
       const {selectedYear, selectedTerm, selectedSchedule, numOfSchedules} = useContext(ScheduleContext);
       const [totalCredits, setTotalCredits] = useState("");
       const [fields, setFields] = useState(Array(7).fill(""));
       const navigate = useNavigate();

      const handleFieldChange = (index, value) => {
        const updatedFields = [...fields];
        updatedFields[index] = value;
        setFields(updatedFields);
        };

        const handleCreditsChange = (e) => {
          const value = e.target.value;
          if (value === "" || /^[0-9]+$/.test(value)) {
            setTotalCredits(value); // Only update if the value is an integer
          } else {
            alert("Input must be an integer!"); // Show alert if input is not an integer
          }
        };


     const handleDayChange = async (e) => {
         const dayFormatChoice = e.target.value;
         setSelectedDayFormat(dayFormatChoice);
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
      };



      const handlePrintAllValues = () => {
        console.log("Selected Day Format:", selectedDayFormat);
        console.log("Start Time:", startTime);
        console.log("End Time:", endTime);
        console.log("Total Credits:", totalCredits);
        console.log("Fields:", fields);
      };

        const createNewScheduleManual = async () => {
            try {
                const response = await axios.post("http://localhost:7000/Youcan creak your own backend call and implement it,- Micah", {
                selectedDayFormat,
                startTime,
                endTime,
                totalCredits,
                fields,
                });
                console.log("Schedule created:", response.data);
            } catch (error) {
                console.error("Error creating schedule:", error);
            }
        }


        const createNewScheduleStatusSheet = async () => {
            try {
                const response = await axios.post("http://localhost:7000/Youcan creak your own backend call and implement it,- Micah/Coldan", {
                selectedDayFormat,
                startTime,
                endTime,
                totalCredits,
                fields,
                });
                console.log("Schedule created:", response.data);
            } catch (error) {
                console.error("Error creating schedule:", error);
            }
        }

  return (
      <div className="center-container">
        <div>
          <ScheduleControls />

          <h2>Quick Search</h2>

          <div>
            <h1>Preferences</h1>
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
            </div>

            {/* Time Range Selectors */}
            <div className="time-range">
              <label>
                Start Time:
                <select value={startTime} onChange={handleStartTimeChange}>
                  {generateTimeOptions().map((time) => (
                    <option key={time} value={time}>
                      {time}
                    </option>
                  ))}
                </select>
              </label>

              <label>
                End Time:
                <select value={endTime} onChange={handleEndTimeChange}>
                  {generateTimeOptions().map((time) => (
                    <option key={time} value={time}>
                      {time}
                    </option>
                  ))}
                </select>
              </label>
            </div>

            <div>
              <label>
                Total Credits:
                <input
                  type="number"
                  value={totalCredits}
                  onChange={handleCreditsChange}
                  placeholder="Enter total credits"
                />
              </label>
            </div>
          </div>

          <div>
            <h3>Required Courses</h3>

            {/* Buttons */}
            <div style={{ display: "flex", gap: "10px", marginBottom: "20px" }}>
              <button onClick={createNewScheduleStatusSheet} >Create from Status Sheets</button>
              <button className="nav-button" onClick={() => navigate("/")}>Skip</button>
            </div>

            {/* Manual Header */}
            <h2>Manual</h2>

            {/* Text Fields */}
            <div>
              {fields.map((field, index) => (
                <div key={index} style={{ marginBottom: "10px" }}>
                  <label>
                    Field {index + 1}:
                    <input
                      type="text"
                      value={field}
                      onChange={(e) => handleFieldChange(index, e.target.value)}
                      placeholder={`Enter value for field ${index + 1}`}
                    />
                  </label>
                </div>
              ))}
            </div>
          </div>

          <div>
            <button onClick={handlePrintAllValues}>Print All Values</button>
          </div>

          {/* Create Schedule Button */}
          <div>
            <button onClick={createNewScheduleManual}>Create Schedule</button>
          </div>
        </div>
      </div>
    );

  };

     export default QuickShecScreen;
