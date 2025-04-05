import React, {Component} from 'react';
import './App.css';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Calendar from "./Calendar";
import AddCourseScreen from "./addCourse";
import { ScheduleProvider } from "./ScheduleContext";
import ReviewCourseScreen from "./review";

const App = () => {
   return (
      <ScheduleProvider> {/* Wrap the entire app with the ScheduleProvider */}
        <Router>
          <Routes>
            <Route path="/" element={<Calendar />} />
            <Route path="/addCourse" element={<AddCourseScreen />} />
            <Route path="/review" element={<ReviewCourseScreen />} />
          </Routes>
        </Router>
      </ScheduleProvider>
    );
};


export default App;
