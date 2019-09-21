import gui.ChatWindow;
import gui.ClientChatWindow;
import gui.ServerChatWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Launchers extends JFrame {

    JButton mClientLaunchButton, mServerLaunchButton;

    ChatWindow mServerChatWindow, mClientChatWindow;

    public Launchers() {

        mClientLaunchButton = new JButton("Launch Client");
        mServerLaunchButton = new JButton("Launch Server");

        setSize(500, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(new FlowLayout());
        add(mClientLaunchButton);
        add(mServerLaunchButton);

        mServerLaunchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        Launchers.this.dispose();
                        mServerChatWindow = new ServerChatWindow();
                    }
                });

            }
        });

        mClientLaunchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        Launchers.this.dispose();
                        mClientChatWindow = new ClientChatWindow();
                    }
                });

            }
        });
    }

    public static void main(String [] args) {
        Launchers o1 = new Launchers();
    }

}
