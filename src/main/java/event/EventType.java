package event;

/**
 * An enum class, that represent the four only possible events of the simulator.
 */
public enum EventType {
    NEW_QUERY, MODULE_END, QUERY_TIMEOUT, QUERY_RETURN;

    /**
     * A String from this enums name.
     * @return A String representation of this object.
     */
    @Override
    public String toString() {
        return this.name();
    }
}
