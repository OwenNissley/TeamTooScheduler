import React, {Component} from 'react';
import './App.css';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Calendar from "./Calendar";
import AddCourseScreen from "./addCourse";

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Calendar />} />
        <Route path="/addCourse" element={<AddCourseScreen />} />
      </Routes>
    </Router>
  );
};


export default App;
