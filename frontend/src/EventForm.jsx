// components/EventForm.jsx
import React, { useState } from 'react';

function EventForm({ addEvent, selectedDate }) {
  const [title, setTitle] = useState('');
  const [time, setTime] = useState('');

  const handleSubmit = (e)={
    e.preventDefault();
    if (title && time) {
      const newEvent = {
        id: Date.now(),
        date: selectedDate.toISOString().slice(0, 10),
        title,
        time,
      };
      addEvent(newEvent);
      setTitle('');
      setTime('');
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <h2>Add Event</h2>
      <input
        type="text"
        placeholder="Event Title"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
      />
      <input
        type="time"
        placeholder="Time"
        value={time}
        onChange={(e) => setTime(e.target.value)}
      />
      <button type="submit">Add Event</button>
    </form>
  );
}

export default EventForm;