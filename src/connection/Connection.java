package connection;

/**
 * Created by Carlos on 03/02/2017.
 */
public class Connection {
    private Statement state;
    private double lifeTime;
    private boolean readOnly;
    private ConnectionStatistics statistics;

    public Connection(double t){
        lifeTime = t;
        double random = Math.random();
        if (random < 0.32){
            state = Statement.SELECT;
            readOnly = true;
        }
        else if (random < 0.6){
            state = Statement.UPDATE;
            readOnly = false;
        }
        else if (random < 0.93){
            state = Statement.JOIN;
            readOnly = true;
        }
        else {
            state = Statement.DDL;
            readOnly = false;
        }
    }

    public Statement getState(){
        return state;
    }

    public double getLifeTime(){
        return lifeTime;
    }

    public boolean readOnly(){
        return readOnly;
    }

    public void reduceLifeTime(double time){
        lifeTime -= time;
    }

    public ConnectionStatistics getStatistics(){
        return statistics;
    }



}
