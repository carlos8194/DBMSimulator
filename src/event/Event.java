package event;

/**
 * Created by Carlos on 03/02/2017.
 */
public class Event implements Comparable<Event> {
    private EventType type;
    private double time;

    public Event(EventType type, double time){
        this.time = time;
        this.type = type;
    }

    public int compareTo(Event event){
        double comparison = time - event.time;
        return (comparison >= 0) ? 1 : -1;
    }


}
