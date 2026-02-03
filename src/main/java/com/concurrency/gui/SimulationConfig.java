package com.concurrency.gui;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 * Thread-safe timing controller for simulation speed.
 * 
 * CRITICAL DESIGN:
 * - Uses volatile long for thread-safe reads without synchronization
 * - Threads must call getDelay() IMMEDIATELY before each sleep
 * - Never cache the delay value - always read fresh
 * 
 * Slider range: 50ms (fast) to 2000ms (slow)
 * Default: 500ms for observable execution
 */
public class SimulationConfig {

	private static final SimulationConfig INSTANCE = new SimulationConfig();

	// Volatile ensures visibility across all threads immediately
	private volatile long delayMs = 500;

	// Observable property for JavaFX slider binding
	private final LongProperty delayProperty = new SimpleLongProperty(500);

	// Delay bounds
	public static final long MIN_DELAY_MS = 50;
	public static final long MAX_DELAY_MS = 2000;
	public static final long DEFAULT_DELAY_MS = 500;

	private SimulationConfig() {
		// Sync volatile field when property changes
		delayProperty.addListener((obs, oldVal, newVal) -> {
			long clamped = Math.max(MIN_DELAY_MS, Math.min(MAX_DELAY_MS, newVal.longValue()));
			delayMs = clamped;
			if (clamped != newVal.longValue()) {
				delayProperty.set(clamped);
			}
		});
	}

	public static SimulationConfig getInstance() {
		return INSTANCE;
	}

	/**
	 * Get the current delay in milliseconds.
	 * MUST be called fresh before EVERY Thread.sleep() call.
	 * DO NOT cache this value.
	 */
	public long getDelay() {
		return delayMs;
	}

	/**
	 * Set the delay in milliseconds (clamped to valid range).
	 */
	public void setDelay(long ms) {
		long clamped = Math.max(MIN_DELAY_MS, Math.min(MAX_DELAY_MS, ms));
		delayMs = clamped;
		delayProperty.set(clamped);
	}

	/**
	 * Observable property for JavaFX slider binding.
	 */
	public LongProperty delayProperty() {
		return delayProperty;
	}

	/**
	 * Sleep for the current configured delay.
	 * Reads delay fresh - changes apply immediately.
	 */
	public static void dynamicSleep() throws InterruptedException {
		Thread.sleep(INSTANCE.getDelay());
	}

	/**
	 * Sleep for a fraction of the current delay.
	 * 
	 * @param fraction multiplier (e.g., 0.5 for half delay)
	 */
	public static void dynamicSleep(double fraction) throws InterruptedException {
		Thread.sleep((long) (INSTANCE.getDelay() * fraction));
	}

	// Legacy compatibility methods (deprecated - use getDelay() instead)
	@Deprecated
	public double getSpeedMultiplier() {
		return DEFAULT_DELAY_MS / (double) delayMs;
	}

	@Deprecated
	public void setSpeedMultiplier(double multiplier) {
		setDelay((long) (DEFAULT_DELAY_MS / multiplier));
	}

	@Deprecated
	public javafx.beans.property.DoubleProperty speedMultiplierProperty() {
		// Return a derived property for backward compatibility
		javafx.beans.property.DoubleProperty prop = new javafx.beans.property.SimpleDoubleProperty(getSpeedMultiplier());
		delayProperty.addListener((obs, o, n) -> prop.set(DEFAULT_DELAY_MS / (double) n.longValue()));
		return prop;
	}
}
