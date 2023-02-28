package controller;

public class UpdateSchedule extends Thread {
  @Override
  public void run() {
    while(true) {
      try {
        sleep(10000);
      } catch (InterruptedException e) {}
      DBZugriff.processList();
    }
  }
}
