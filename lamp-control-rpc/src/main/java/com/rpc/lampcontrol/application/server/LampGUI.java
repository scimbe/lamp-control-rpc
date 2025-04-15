package com.rpc.lampcontrol.application.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * GUI representation of a lamp using a light bulb image.
 * This class provides a visual indication of the lamp's current state.
 */
public class LampGUI extends JFrame {
    private final Lamp lamp;
    private final JPanel bulbPanel;
    private boolean isLampOn = false;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Creates a new lamp GUI for the given lamp.
     *
     * @param lamp The lamp to represent
     */
    public LampGUI(Lamp lamp) {
        this.lamp = lamp;
        
        // Set up the JFrame
        setTitle("Lamp Control Server");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        
        // Add a window listener to handle close events
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                scheduler.shutdown();
                dispose();
            }
        });
        
        // Create the bulb panel
        bulbPanel = new BulbPanel();
        getContentPane().add(bulbPanel, BorderLayout.CENTER);
        
        // Create status label
        JLabel statusLabel = new JLabel("Lamp is OFF", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        getContentPane().add(statusLabel, BorderLayout.SOUTH);
        
        // Schedule a task to check lamp status and update GUI
        scheduler.scheduleAtFixedRate(() -> {
            boolean currentState = lamp.isOn();
            if (isLampOn != currentState) {
                isLampOn = currentState;
                SwingUtilities.invokeLater(() -> {
                    bulbPanel.repaint();
                    statusLabel.setText("Lamp is " + (isLampOn ? "ON" : "OFF"));
                });
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        
        // Center the frame on the screen
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * Panel that draws the bulb.
     */
    private class BulbPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Draw bulb base
            g2d.setColor(Color.DARK_GRAY);
            int baseWidth = width / 3;
            int baseHeight = height / 10;
            g2d.fillRect((width - baseWidth) / 2, height - baseHeight, baseWidth, baseHeight);
            
            // Draw screw base
            g2d.setColor(Color.GRAY);
            int screwWidth = baseWidth - 10;
            int screwHeight = baseHeight + 20;
            g2d.fillRect((width - screwWidth) / 2, height - baseHeight - screwHeight, screwWidth, screwHeight);
            
            // Draw bulb glass
            if (isLampOn) {
                g2d.setColor(Color.YELLOW);
                // Add a glow effect when on
                for (int i = 10; i > 0; i--) {
                    float alpha = i / 10.0f;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2d.fillOval((width - 150 - i * 5) / 2, 50 - i * 2, 150 + i * 10, 200 + i * 5);
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            } else {
                g2d.setColor(new Color(220, 220, 220)); // Light gray when off
            }
            
            // Draw the main bulb shape
            g2d.fillOval((width - 150) / 2, 50, 150, 200);
            
            // Draw bulb outline
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval((width - 150) / 2, 50, 150, 200);
            
            // Draw filament when the lamp is off
            if (!isLampOn) {
                g2d.setColor(Color.DARK_GRAY);
                g2d.setStroke(new BasicStroke(3));
                int filamentStartX = width / 2 - 30;
                int filamentEndX = width / 2 + 30;
                int filamentY = 150;
                g2d.drawLine(filamentStartX, filamentY, width / 2, filamentY - 20);
                g2d.drawLine(width / 2, filamentY - 20, filamentEndX, filamentY);
            }
        }
    }
}