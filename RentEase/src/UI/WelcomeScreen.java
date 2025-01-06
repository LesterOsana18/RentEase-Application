//Child class of Screen
package UI;

import java.awt.*;

import javax.swing.JLabel;

//To be changed to the actual welcome screen
public class WelcomeScreen extends Screen {
    @Override
    protected void customizePanel() {
        panel.setBackground(Color.decode("#FFFFFF"));
        JLabel appName = new JLabel("Welcome Screen");
        appName.setFont(new Font("Arial", Font.PLAIN, 25));
        appName.setForeground(Color.BLACK);
        appName.setBounds(100, 100, 200, 50);
        panel.add(appName);
    }
}