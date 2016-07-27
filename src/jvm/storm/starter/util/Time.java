package storm.starter.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Time {
  public static final Logger LOG = LogManager.getLogger(Time.class);

  private static AtomicBoolean simulating = new AtomicBoolean(false);
  //TODO: should probably use weak references here or something
  private static volatile Map<Thread, AtomicLong> threadSleepTimes;
  private static final Object sleepTimesLock = new Object();

  private static AtomicLong simulatedCurrTimeMs; //should this be a thread local that's allowed to keep advancing?

  public static void startSimulating() {
    synchronized(sleepTimesLock) {
      simulating.set(true);
      simulatedCurrTimeMs = new AtomicLong(0);
      threadSleepTimes = new ConcurrentHashMap<Thread, AtomicLong>();
    }
  }

  public static void stopSimulating() {
    synchronized(sleepTimesLock) {
      simulating.set(false);
      threadSleepTimes = null;
    }
  }

  public static boolean isSimulating() {
    return simulating.get();
  }

  public static void sleepUntil(long targetTimeMs) throws InterruptedException {
    if(simulating.get()) {
      try {
        synchronized(sleepTimesLock) {
          if (threadSleepTimes == null) {
            LOG.debug("{} is still sleeping after simulated time disabled.", Thread.currentThread(), new RuntimeException("STACK TRACE"));
            throw new InterruptedException();
          }
          threadSleepTimes.put(Thread.currentThread(), new AtomicLong(targetTimeMs));
        }
        while(simulatedCurrTimeMs.get() < targetTimeMs) {
          synchronized(sleepTimesLock) {
            if (threadSleepTimes == null) {
              LOG.debug("{} is still sleeping after simulated time disabled.", Thread.currentThread(), new RuntimeException("STACK TRACE"));
              throw new InterruptedException();
            }
          }
          Thread.sleep(10);
        }
      } finally {
        synchronized(sleepTimesLock) {
          if (simulating.get() && threadSleepTimes != null) {
            threadSleepTimes.remove(Thread.currentThread());
          }
        }
      }
    } else {
      long sleepTime = targetTimeMs-currentTimeMillis();
      if(sleepTime>0)
        Thread.sleep(sleepTime);
    }
  }

  public static void sleep(long ms) throws InterruptedException {
    sleepUntil(currentTimeMillis()+ms);
  }

  public static void sleepSecs (long secs) throws InterruptedException {
    if (secs > 0) {
      sleep(secs * 1000);
    }
  }

  public static long currentTimeMillis() {
    if(simulating.get()) {
      return simulatedCurrTimeMs.get();
    } else {
      return System.currentTimeMillis();
    }
  }

  public static long secsToMillis (int secs) {
    return 1000*(long) secs;
  }

  public static long secsToMillisLong(double secs) {
    return (long) (1000 * secs);
  }

  public static int currentTimeSecs() {
    return (int) (currentTimeMillis() / 1000);
  }

  public static int deltaSecs(int timeInSeconds) {
    return Time.currentTimeSecs() - timeInSeconds;
  }

  public static long deltaMs(long timeInMilliseconds) {
    return Time.currentTimeMillis() - timeInMilliseconds;
  }

  public static void advanceTime(long ms) {
    if(!simulating.get()) throw new IllegalStateException("Cannot simulate time unless in simulation mode");
    if(ms < 0) throw new IllegalArgumentException("advanceTime only accepts positive time as an argument");
    simulatedCurrTimeMs.set(simulatedCurrTimeMs.get() + ms);
  }

  public static boolean isThreadWaiting(Thread t) {
    if(!simulating.get()) throw new IllegalStateException("Must be in simulation mode");
    AtomicLong time;
    synchronized(sleepTimesLock) {
      time = threadSleepTimes.get(t);
    }
    return !t.isAlive() || time!=null && currentTimeMillis() < time.longValue();
  }
}