package event;

import query.Query;

/**
 * A class that reassembles a event that the simulator should deal with. It uses time to compare with one another.
 */
public class Event implements Comparable<Event> {
    private EventType type;//The type of the event.
    private double time;//The time when the event will take place.
    private Query query;//The query involved in this event.

    /**
     * Event constructor.
     * @param type: The type of the event.
     * @param time:The time when the event will take place.
     * @param query:The query involved in this event.
     */
    public Event(EventType type, double time, Query query){
        this.setTime(time);
        this.setType(type);
        this.setQuery(query);
    }

    /**
     * Event constructor.
     * @param type: The type of the event.
     * @param time:The time when the event will take place.
     */
    public Event(EventType type, double time){
        this.setTime(time);
        this.setType(type);
    }

    /**
     * Method overriden in the Comparable interface.
     * @param event: Another Event object to compare with.
     * @return a positive integer if this is greater than "event", 0 if equals, and a negative integer otherwise.
     */
    public int compareTo(Event event){
        double comparison = time - event.getTime();
        return (comparison >= 0) ? 1 : -1;
    }

    /**
     * Getter method for this events type.
     * @return the type of this event.
     */
    public EventType getType() {
        return type;
    }

    /**
     * Sets the type with the given type.
     * @param type: The new event type.
     */
    public void setType(EventType type) {
        this.type = type;
    }

    /**
     * Getter method for this events time.
     * @return the time when the event will occur.
     */
    public double getTime() {
        return time;
    }

    /**
     * Setter method for the events time.
     * @param time: the new time.
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * A method that compares to objects and identifies if they are equals or not.
     * @param obj: An object to compare with.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        boolean comparison;
        if (! (obj instanceof Event) ){
            comparison = false;
        }
        else {
            Event event = (Event) obj;
            comparison = type.equals(event.type) && time == event.time;
        }
        return comparison;
    }

    /**
     * Getter method for this events query.
     * @return the query involved in this event.
     */
    public Query getQuery() {
        return query;
    }

    /**
     * A setter method for this events query.
     * @param query: the new query.
     */
    public void setQuery(Query query) {
        this.query = query;
    }

    /**
     * A method that uses this events attributes to generate a String.
     * @return A String representation of this.
     */
    @Override
    public String toString() {
        String string = "";
        if (query != null) string += "Query "+ query.getID() + " ";
        return string + type.toString();
    }
}
