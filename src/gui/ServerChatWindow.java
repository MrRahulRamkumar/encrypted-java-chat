package gui;

import chat.ReadMessages;
import crypto.RabinPKC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;

public class ServerChatWindow extends JFrame implements ChatWindow{

    private JPanel mPanel;
    private JTextField mInputTextField;
    private JTextArea mTextArea;
    private JScrollPane mScrollPane;
    private JButton mSendButton;
    private JButton mConnectButton;

    private Socket mSock;
    private OutputStream mOutputStream;
    private PrintWriter mPrintWriter;

    private BigInteger myN, p, q, senderN;

    public static volatile boolean finished = false;
    private String mName = "Client";

    public ServerChatWindow() {
        super("Chat Application (Server)");

        mPanel = new JPanel();
        mPanel.setLayout(new BorderLayout());

        mInputTextField = new JTextField(30);
        mInputTextField.setEditable(true);
        mSendButton = new JButton();
        mSendButton.setText("Send");

        mConnectButton = new JButton("Connect");

        mTextArea = new JTextArea();
        mTextArea.setLineWrap(true);
        mTextArea.setEditable(false);

        mScrollPane = new JScrollPane(mTextArea);
        mScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JPanel settingsPanel = new JPanel(new FlowLayout());

        mPanel.add(mScrollPane);

        inputPanel.add(mInputTextField);
        inputPanel.add(mSendButton);

        settingsPanel.add(mConnectButton);



        setVisible(true);
        setSize(500, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(mPanel);
        add(inputPanel, BorderLayout.SOUTH);
        add(settingsPanel, BorderLayout.NORTH);

        mConnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startConnection(null);
            }
        });
    }

    @Override
    public void startConnection(String ip) {


        mConnectButton.setText("Connecting...");

        BigInteger[] key = RabinPKC.genKey(512, new SecureRandom());
        myN = key[0];
        p = key[1];
        q = key[2];

        try {
            ServerSocket serverSocket = new ServerSocket(3000);
            mSock = serverSocket.accept();

            mConnectButton.setEnabled(false);
            mConnectButton.setText("Connected, Server ready");

            mOutputStream = mSock.getOutputStream();
            mPrintWriter = new PrintWriter(mOutputStream, true);

            String publicKey = myN.toString();
            mPrintWriter.println(RabinPKC.addPadding(publicKey));       // sending to client
            mPrintWriter.flush();

            Thread readMessageThread = new Thread(new ReadMessages(this, mSock, mTextArea, mConnectButton));

            readMessageThread.start();
            sendMessage();

        } catch (IOException e) {
            mConnectButton.setEnabled(true);
            mConnectButton.setText("Connect");
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage() {
        // sending to client (pwrite object)
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

        mSendButton.addActionListener(action);
        mInputTextField.addActionListener(action);
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
