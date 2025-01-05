
package UI;

import java.awt.*;
import javax.swing.*;

public class LoadingScreen extends Screen {
    @Override
    protected void customizePanel() {
        panel.setBackground(Color.decode("#FE724C"));
        
        ImageIcon icon = new ImageIcon("../resources/icon.png");
        JLabel iconLabel = new JLabel(icon);

        JLabel appName = new JLabel("RENTEASE");
        appName.setFont(new Font("PhosphateInline", Font.PLAIN, 40));
        appName.setForeground(Color.WHITE);
        
        int x = (375 - icon.getIconWidth()) / 2;
        int y = (812 - icon.getIconHeight()) / 2;
        
        iconLabel.setBounds(x, y - 100, icon.getIconWidth(), icon.getIconHeight());
        appName.setBounds(x - 43, y, 200, 50);
        panel.add(iconLabel);
        panel.add(appName);
    }
}