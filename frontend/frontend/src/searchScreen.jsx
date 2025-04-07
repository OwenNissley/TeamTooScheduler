import { useState } from "react";
import axios from "axios";

function SearchScreen() {
  const [courseSearch, setCourseSearch] = useState("");
  const [courseCode, setCourseCode] = useState("");
  const [schedule, setSchedule] = useState("MWF");
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");

  // 30-minute increments time options
  const timeOptions = [
    "8:00 AM", "8:30 AM", "9:00 AM", "9:30 AM", "10:00 AM", "10:30 AM",
    "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "1:00 PM", "1:30 PM",
    "2:00 PM", "2:30 PM", "3:00 PM", "3:30 PM", "4:00 PM", "4:30 PM",
    "5:00 PM", "5:30 PM", "6:00 PM", "6:30 PM", "7:00 PM", "7:30 PM",
  ];

  // Handles search logic and queries backend
  const handleSearch = async () => {
    if (!courseSearch.match(/^[A-Za-z]*$/) || !courseCode.match(/^\d*$/)) {
      alert("Invalid input: Course search should be letters only, and Course code should be numbers only.");
      return;
    }

    try {
      const response = await axios.get("http://localhost:8080/search", {
        params: {
          courseSearch,
          courseCode,
          schedule,
          startTime,
          endTime,
        },
      });

      console.log("Search Results:", response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  return (
    <div className="p-4 bg-gray-100 rounded-lg shadow-md">
      <h2 className="text-lg font-semibold mb-2">Search Courses</h2>

      {/* Course Search Input (Letters Only) */}
      <input
        type="text"
        value={courseSearch}
        onChange={(e) => setCourseSearch(e.target.value)}
        placeholder="Enter Course (e.g., CS, MATH)"
        className="border p-2 w-full rounded mb-2"
      />

      {/* Course Code Input (Numbers Only) */}
      <input
        type="text"
        value={courseCode}
        onChange={(e) => setCourseCode(e.target.value)}
        placeholder="Enter Course Code (e.g., 101, 202)"
        className="border p-2 w-full rounded mb-2"
      />

      {/* Schedule Selection */}
      <div className="mb-2">
        <label className="mr-2">
          <input
            type="radio"
            value="MWF"
            checked={schedule === "MWF"}
            onChange={() => setSchedule("MWF")}
          />
          <span className="ml-1">MWF</span>
        </label>
        <label className="ml-4">
          <input
            type="radio"
            value="TR"
            checked={schedule === "TR"}
            onChange={() => setSchedule("TR")}
          />
          <span className="ml-1">TR</span>
        </label>
      </div>

      {/* Time Dropdowns */}
      <div className="flex space-x-2 mb-2">
        <select
          value={startTime}
          onChange={(e) => setStartTime(e.target.value)}
          className="border p-2 rounded w-1/2"
        >
          <option value="">Start Time</option>
          {timeOptions.map((time) => (
            <option key={time} value={time}>{time}</option>
          ))}
        </select>

        <select
          value={endTime}
          onChange={(e) => setEndTime(e.target.value)}
          className="border p-2 rounded w-1/2"
        >
          <option value="">End Time</option>
          {timeOptions.map((time) => (
            <option key={time} value={time}>{time}</option>
          ))}
        </select>
      </div>

      {/* Search Button */}
      <button
        onClick={handleSearch}
        className="bg-blue-500 text-white px-4 py-2 rounded w-full"
      >
        Search
      </button>
    </div>
  );
}

export default SearchScreen;
