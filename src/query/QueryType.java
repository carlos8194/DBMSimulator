package query;

/**
 * Created by Carlos on 03/02/2017.
 */
public enum QueryType {
    DDL(true),
    UPDATE(false),
    JOIN(true),
    SELECT(false);

    public boolean readOnly;

    private QueryType(boolean readOnly){
        this.readOnly = readOnly;
    }

}
