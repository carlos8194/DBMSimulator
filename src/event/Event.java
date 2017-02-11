package event;

import query.Query;

/**
 * Created by Carlos on 03/02/2017.
 */
public class Event implements Comparable<Event> {
    private EventType type;
    private double time;
    private Query query;

    public Event(EventType type, double time, Query query){
        this.setTime(time);
        this.setType(type);
        this.setQuery(query);
    }

    public Event(EventType type, double time){
        this.setTime(time);
        this.setType(type);
    }

    public int compareTo(Event event){
        double comparison = getTime() - event.getTime();
        return (comparison >= 0) ? 1 : -1;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

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

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }
}
