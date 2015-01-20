package playn.flurry.core;

public interface Flurry {

  /** Logs a named event.
   * @param args zero or more (string) key / (string or int) value pairs. */
  void logEvent (String eventName, Object... args);
}
