package gui;

import java.math.BigInteger;

public interface ChatWindow {
    public void startConnection(String ip);
    public void sendMessage();
    public String getSenderName();
    public void setEncryptionKey(String n);
    public BigInteger getQ();
    public BigInteger getP();
}
