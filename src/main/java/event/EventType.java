package event;

/**
 * Created by Carlos on 04/02/2017.
 */
public enum EventType {
    NEW_QUERY, MODULE_END, QUERY_TIMEOUT, QUERY_RETURN;

    @Override
    public String toString() {
        return this.name();
    }
}
