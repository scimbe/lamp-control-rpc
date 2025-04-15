package com.rpc.lampcontrol.application.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a controllable lamp.
 * This class simulates a physical lamp with toggle functionality.
 */
public class LampImpl implements Lamp {
    private static final Logger logger = LoggerFactory.getLogger(LampImpl.class);
    
    private volatile boolean on = false;
    private final String lampName;
    private LampGUI gui;
    
    /**
     * Creates a new lamp with a specific name.
     *
     * @param lampName The name of this lamp instance
     */
    public LampImpl(String lampName) {
        this.lampName = lampName;
        logger.info("Lamp '{}' created, initial state: OFF", lampName);
    }
    
    /**
     * Initialize the GUI for this lamp.
     * This should be called after the lamp is fully constructed.
     */
    public void initializeGUI() {
        if (gui == null) {
            // Create GUI on the Event Dispatch Thread
            javax.swing.SwingUtilities.invokeLater(() -> {
                gui = new LampGUI(this);
                logger.info("Lamp GUI initialized for '{}'", lampName);
            });
        }
    }
    
    @Override
    public synchronized String toggle() {
        on = !on;
        String state = on ? "ON" : "OFF";
        logger.info("Lamp '{}' toggled to: {}", lampName, state);
        return "Lamp '" + lampName + "' is now " + state;
    }
    
    @Override
    public boolean isOn() {
        return on;
    }
}