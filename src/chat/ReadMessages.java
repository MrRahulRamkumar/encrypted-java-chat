package chat;

import crypto.RabinPKC;
import gui.ChatWindow;
import gui.ClientChatWindow;

import javax.swing.*;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;

public class ReadMessages implements Runnable{

    private JButton mConnectButton;
    private Socket mSock;
    private JTextArea mTextArea;
    private ChatWindow mChatWindow;

    public ReadMessages(ChatWindow chatWindow, Socket sock, JTextArea textArea, JButton connectButton) {
        this.mSock = sock;
        this.mTextArea = textArea;
        this.mChatWindow = chatWindow;
        this.mConnectButton = connectButton;
    }

    @Override
    public void run() {
        try {
            // receiving from server ( receiveRead  object)
            InputStream istream = mSock.getInputStream();
            BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));

            String message;
            while(!ClientChatWindow.finished)
            {
                if((message = receiveRead.readLine()) != null) //receive from server
                {
                    if(RabinPKC.isPadding(message)) {

                        String publicKey = message.substring(1, message.length()-1);
                        mChatWindow.setEncryptionKey(publicKey);

                    } else {
                        System.out.println(message); // displaying at DOS prompt
                        System.out.println("message: " + message);
                        String decryptedMessage = RabinPKC.decrypt(message, mChatWindow.getP(), mChatWindow.getQ());
                        System.out.println("decryptedMessage: " + decryptedMessage);
                        mTextArea.append("\n" + mChatWindow.getSenderName() + ": " + decryptedMessage);
                    }
                }
            }
        } catch (IOException e) {
            if(mConnectButton != null) {
                mConnectButton.setEnabled(true);
                mConnectButton.setText("Connect");
            }
            e.printStackTrace();
        }
    }
}
