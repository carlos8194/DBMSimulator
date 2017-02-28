package query;

/**
 * This class implements an enumerable class to distinguish between query types.
 * Queries may be require only read operations or both read and write operations, this
 * class differentiates them by a boolean readOnly field.
 */
public enum QueryType {
    DDL(true),
    UPDATE(false),
    JOIN(true),
    SELECT(false);

    public boolean readOnly;

    /**
     * Constructor for the Query Type class, creates class and set readOnly to true for DDL and Join queryTypes.
     * @param readOnly
     */
    QueryType(boolean readOnly){
        this.readOnly = readOnly;
    }

}
