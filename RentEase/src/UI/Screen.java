// Screen.java
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
        panel.setLayout(null); // Or any layout you prefer
    }
    
    public JPanel getPanel() {
        return panel;
    }
    
    protected abstract void customizePanel();
}