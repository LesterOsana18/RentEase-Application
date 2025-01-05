// Parent Class
package UI;

import javax.swing.*;

public abstract class Screen {
    protected JPanel panel;
    
    public Screen() {
        initializePanel();
        customizePanel();
    }
    
    protected void initializePanel() {
        panel = new JPanel();
        panel.setLayout(null);
    }
    
    public JPanel getPanel() {
        return panel;
    }
    
    protected abstract void customizePanel();
}