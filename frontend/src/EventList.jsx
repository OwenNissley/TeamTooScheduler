// components/EventList.jsx
import React from 'react';

function EventList({ events, selectedDate, onDeleteEvent }) {
  const formattedDate = selectedDate.toISOString().slice(0, 10);
  const eventsForSelectedDate = events.filter(event => event.date === formattedDate);

  return (
    <div className="event-list">
      <h2>Events for {formattedDate}</h2>
      {eventsForSelectedDate.length === 0 ? (
        <p>No events for this day.</p>
      ) : (
        <ul>
          {eventsForSelectedDate.map((event) => (
            <li key={event.id}>
              {event.title} - {event.time}
              <button onClick={() => onDeleteEvent(event.id)}>Delete</button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default EventList;