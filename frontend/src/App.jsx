import React, {Component} from 'react';
import './App.css';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Calendar from "./Calendar";
import AddCourseScreen from "./addCourse";
import { ScheduleProvider } from "./ScheduleContext";
import ReviewCourseScreen from "./review";
import CourseDirectory from "./courseDirectory";
import QuickShecScreen from "./quickShec";

const App = () => {
   return (
       <Router>
         <ScheduleProvider> {/* Wrap the entire app with the ScheduleProvider */}
          <Routes>
            <Route path="/" element={<Calendar />} />
            <Route path="/addCourse" element={<AddCourseScreen />} />
            <Route path="/review" element={<ReviewCourseScreen />} />
            <Route path="/course-directory" element={<CourseDirectory />} />
            <Route path="/quick-schedule" element={<QuickShecScreen />} />
          </Routes>
        </ScheduleProvider>
      </Router>
    );
};


export default App;
