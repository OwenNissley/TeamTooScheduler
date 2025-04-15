import { useState, useEffect } from "react";
import axios from "axios";

const CourseList = () => {
    const [courses, setCourses] = useState([]); // ✅ Always initialize as an array

    useEffect(() => {
        axios.get("http://localhost:7000/courseReg")
            .then(response => {
                console.log("Course Data:", response.data);
                setCourses(Array.isArray(response.data) ? response.data : []); // ✅ Ensure it's an array
            })
            .catch(error => {
                console.error("Error fetching course list:", error);
                setCourses([]); // ✅ Set to empty array if request fails
            });
    }, []);

    return (
        <div className="max-w-2xl mx-auto p-6 bg-white shadow-lg rounded-xl">
            <h2 className="text-4xl font-semibold text-center mb-6 text-gray-800">Course List</h2>

            {courses.length === 0 ? (
                <p className="text-center text-gray-500">No courses available.</p>
            ) : (
                <ul className="space-y-4">
                    {courses.map((course, index) => (
                        <li key={index} className="bg-gray-100 p-3 rounded-lg shadow-md">
                            <h3 className="text-xl font-semibold text-gray-800">{course.name}</h3>
                            <p className="text-gray-600">{course.description}</p>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default CourseList;
