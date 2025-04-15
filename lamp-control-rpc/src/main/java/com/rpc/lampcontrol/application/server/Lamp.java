package com.rpc.lampcontrol.application.server;

/**
 * Interface for controllable lamp devices.
 * Any device implementing this interface can be toggled on and off remotely.
 */
public interface Lamp {
    
    /**
     * Toggles the lamp between on and off states.
     * 
     * @return String message indicating the new lamp state
     */
    String toggle();
    
    /**
     * Gets the current state of the lamp.
     * 
     * @return true if the lamp is on, false if off
     */
    boolean isOn();
}