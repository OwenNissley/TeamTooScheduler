import { useState } from "react";

function HomeScreen({ onSearchClick }) {
    // Example list of current courses
    const currentCourses = [
        { name: "CS101", time: "9:00 AM - 9:50 AM", days: ["Monday", "Wednesday", "Friday"] },
        { name: "MATH202", time: "11:00 AM - 11:50 AM", days: ["Tuesday", "Thursday"] },
        { name: "HIST305", time: "2:00 PM - 3:15 PM", days: ["Tuesday", "Thursday"] },
    ];

    // Generate time slots (8:00 AM - 8:00 PM in 30-minute increments)
    const timeSlots = [];
    for (let hour = 8; hour <= 20; hour++) {
        timeSlots.push(`${hour % 12 || 12}:00 ${hour < 12 ? "AM" : "PM"}`);
        timeSlots.push(`${hour % 12 || 12}:30 ${hour < 12 ? "AM" : "PM"}`);
    }

    const days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"];

    return (
        <div className="p-6 w-full max-w-5xl">
            {/* Course List */}
            <h2 className="text-2xl font-bold mb-4">Your Courses</h2>
            <ul className="mb-6 bg-white shadow-md rounded-lg p-4">
                {currentCourses.map((course, index) => (
                    <li key={index} className="p-2 border-b">
                        <span className="font-semibold">{course.name}</span> – {course.time} ({course.days.join(", ")})
                    </li>
                ))}
            </ul>

            {/* Weekly Calendar Grid */}
            <div className="overflow-x-auto border border-gray-300 rounded-lg shadow-md">
                <div className="grid grid-cols-6 divide-x divide-gray-300">
                    {/* Header Row */}
                    <div className="bg-gray-200 font-semibold p-3 border border-gray-300"></div>
                    {days.map((day) => (
                        <div key={day} className="bg-gray-200 font-semibold p-3 border border-gray-300 text-center">
                            {day}
                        </div>
                    ))}

                    {/* Time Slots */}
                    {timeSlots.map((time, rowIndex) => (
                        <div key={`row-${rowIndex}`} className="grid grid-cols-6 border-t border-gray-300">
                            {/* Time Column */}
                            <div className="p-3 text-sm font-semibold border-r border-gray-300">{time}</div>

                            {/* Empty slots for each day */}
                            {days.map((day, colIndex) => (
                                <div
                                    key={`slot-${rowIndex}-${colIndex}`}
                                    className="h-12 border-r border-gray-300 hover:bg-gray-100"
                                ></div>
                            ))}
                        </div>
                    ))}
                </div>
            </div>

            {/* Search Courses Button */}
            <button
                onClick={onSearchClick}
                className="mt-6 bg-blue-500 text-white px-4 py-2 rounded-lg w-full"
            >
                Search Courses
            </button>
        </div>
    );
}

export default HomeScreen;
