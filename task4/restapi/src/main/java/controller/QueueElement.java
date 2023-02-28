package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class QueueElement {
  private String sql;
  private HashMap<Integer, Pair<String, Object>> params;
  private Condition condition;
  private Lock lock;
  private boolean waiting;
  private boolean ret;
  
  public QueueElement(String sql, HashMap<Integer, Pair<String, Object>> params){
    this.sql = sql;
    this.params = params;
    lock = new ReentrantLock();
    waiting = false;
    condition = lock.newCondition();
  }
  
  public void setRet(boolean ret){
    this.ret = ret;
  }
  
  public boolean getRet(){
    return ret;
  }
  
  public void waitForExecution(){
    try {
      lock.lock();
      waiting = true;
      condition.await();
    } catch (InterruptedException e) {
      ret = false;
    } finally {
      waiting = false;
      lock.unlock();
    }
  }
  
  public boolean isSomebodyWaiting(){
    boolean ret = lock.tryLock();
    if(ret) {
      ret = waiting;
      lock.unlock();
    }
    return ret;
  }
  
  public void wake(){
    lock.lock();
    condition.signal();
    lock.unlock();
  }
  
  public PreparedStatement prepareStatement(Connection connection) throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement(sql);
    for(HashMap.Entry<Integer, Pair<String, Object>> entry : params.entrySet()){
      if(entry.getValue().first.equals("String")){
        preparedStatement.setString(entry.getKey(), (String)entry.getValue().second);
      } else if(entry.getValue().first.equals("Int")){
        preparedStatement.setInt(entry.getKey(), (Integer)entry.getValue().second);
      } else if(entry.getValue().first.equals("Double")){
        preparedStatement.setDouble(entry.getKey(), (Double)entry.getValue().second);
      } else if(entry.getValue().first.equals("Long")){
        preparedStatement.setLong(entry.getKey(), (Long)entry.getValue().second);
      } else {
        System.out.println("Unsupported Datatype: " + entry.getValue().first);
      }
    }
    return preparedStatement;
  }
}
