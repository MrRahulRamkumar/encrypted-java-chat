package gui;

import chat.ReadMessages;
import crypto.RabinPKC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Random;

public class ClientChatWindow extends JFrame implements ChatWindow {

    private JPanel mPanel;
    private JTextField mInputTextField;
    private JTextField mIpAddressTextField;
    private JButton mConnectButton;
    private JTextArea mTextArea;
    private JScrollPane mScrollPane;
    private JButton mSendButton;

    private Socket mSock;
    private OutputStream mOutputStream;
    private PrintWriter mPrintWriter;

    private BigInteger myN, p, q, senderN;

    public static volatile boolean finished = false;
    private String mName = "Server";

    public ClientChatWindow() {
        super("Chat Application (Client)");

        mPanel = new JPanel();
        mPanel.setLayout(new BorderLayout());

        mInputTextField = new JTextField(30);
        mInputTextField.setEditable(true);
        mSendButton = new JButton();
        mSendButton.setText("Send");

        mIpAddressTextField = new JTextField(10);
        mIpAddressTextField.setToolTipText("Enter IP address of server");
        mIpAddressTextField.setEditable(true);
        mConnectButton = new JButton("Connect");

        mTextArea = new JTextArea();
        mTextArea.setLineWrap(true);
        mTextArea.setEditable(false);

        mScrollPane = new JScrollPane(mTextArea);
        mScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JPanel settingsPanel = new JPanel(new FlowLayout());

        mPanel.add(mScrollPane);

        settingsPanel.add(mIpAddressTextField);
        settingsPanel.add(mConnectButton);

        inputPanel.add(mInputTextField);
        inputPanel.add(mSendButton);

        add(mPanel);
        add(inputPanel, BorderLayout.SOUTH);
        add(settingsPanel, BorderLayout.NORTH);

        setSize(500, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(new BorderLayout());

        mConnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip =  mIpAddressTextField.getText();
                if(ip != null && ip.length() > 0) {
                    startConnection(ip);
                }
            }
        });
    }

    @Override
    public void startConnection(String ip) {

        BigInteger[] key = RabinPKC.genKey(512, new SecureRandom());
        myN = key[0];
        p = key[1];
        q = key[2];

        try {

            mConnectButton.setText("Connecting...");
            mConnectButton.setEnabled(false);

            mSock = new Socket(ip, 3000);

            mConnectButton.setText("Connected");

            // sending to client (pwrite object)
            mOutputStream = mSock.getOutputStream();
            mPrintWriter = new PrintWriter(mOutputStream, true);

            String publicKey = myN.toString();
            mPrintWriter.println(RabinPKC.addPadding(publicKey));       // sending to server
            mPrintWriter.flush();



            Thread readMessageThread = new Thread(new ReadMessages(this, mSock, mTextArea, mConnectButton));

            readMessageThread.start();
            sendMessage();


        } catch (IOException e) {
            mConnectButton.setText("Connect");
            mConnectButton.setEnabled(true);
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage() {


        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = mInputTextField.getText();  // reading from text input field

                String encryptedMessage = RabinPKC.encrypt(message, senderN);
                mInputTextField.setText("");
                mPrintWriter.println(encryptedMessage);       // sending to server
                mTextArea.append("\n" + "You: " + message);
                mPrintWriter.flush();
            }
        };
        mInputTextField.addActionListener(action);
        mSendButton.addActionListener(action);
    }


    @Override
    public String getSenderName() {
        return mName;
    }

    @Override
    public void setEncryptionKey(String n) {
        senderN = new BigInteger(n);
        System.out.println("P: " + p + "   Q: " + q + "   myN: " + myN + "  senderN: " + senderN);
    }

    @Override
    public BigInteger getQ() {
        return q;
    }

    @Override
    public BigInteger getP() {
        return p;
    }
}
