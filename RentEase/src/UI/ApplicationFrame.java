//Contains the main frame of the application and other components
package UI;

import javax.swing.*;

public class ApplicationFrame {
    private JFrame mainFrame;
    private LoadingScreen loadingScreen;
    private WelcomeScreen welcomeScreen;
    
    public ApplicationFrame() {
        initializeFrame();
        loadingScreen = new LoadingScreen();
        welcomeScreen = new WelcomeScreen();
    }
    
    private void initializeFrame() {
        mainFrame = new JFrame("RentEase");
        mainFrame.setSize(375, 812);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(true);
        
        ImageIcon icon = new ImageIcon("../resources/icon.png");
        mainFrame.setIconImage(icon.getImage());
    }
    
    public void start() {
        showLoadingScreen();
        scheduleWelcomeScreen();
        mainFrame.setVisible(true);
    }

    private void showLoadingScreen() {
        mainFrame.setContentPane(loadingScreen.getPanel());
        mainFrame.revalidate();
        mainFrame.repaint();
    }
    
    private void showWelcomeScreen() {
        mainFrame.setContentPane(welcomeScreen.getPanel());
        mainFrame.revalidate();
        mainFrame.repaint();
    }
    
    private void scheduleWelcomeScreen() {
        Timer timer = new Timer(3000, _ -> showWelcomeScreen());
        timer.setRepeats(false);
        timer.start();
    }
}