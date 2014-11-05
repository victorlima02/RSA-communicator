/*
 * This code was written for an assignment for concept demonstration purposes:
 *  caution required
 *
 * The MIT License
 *
 * Copyright 2014 Victor de Lima Soares.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package rsacommunicator.client;

import crypto.ciphers.Cipher;
import crypto.ciphers.asy.rsa.RSA;
import crypto.ciphers.block.feistel.des.DES;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import rsacommunicator.MessageReader;
import rsacommunicator.messages.Destination;
import rsacommunicator.messages.Key;
import rsacommunicator.messages.Login;
import rsacommunicator.messages.Logout;
import rsacommunicator.messages.Message;
import rsacommunicator.messages.PlainMessage;
import rsacommunicator.messages.PublicKey;
import rsacommunicator.messages.RSAMessage;
import rsacommunicator.messages.SymmetricMessage;
import rsacommunicator.messages.UserList;
import rsacommunicator.server.RSAServer;

/**
 * The RSA communicator client.
 *
 * @author Victor de Lima Soares
 * @version 1.0
 */
public class RSAClient implements PropertyChangeListener, AutoCloseable {

    /**
     * User name.
     *
     * @since 1.0
     */
    private String name;

    /**
     * RSA field for encryption/decryption using RSA algorithm.
     *
     * @since 1.0
     * @see RSA#RSA(int)
     */
    private final RSA rsa = new RSA(512);

    /**
     * Symmetric Cipher to be used for encryption/decryption operations - with
     * DES.
     *
     * @since 1.0
     * @see DES#DES()
     */
    private static final Cipher des = new DES();

    /**
     * Communication channel.
     *
     * @since 1.0
     */
    private Socket socket;

    /**
     * Communication channel: output stream.
     *
     * @since 1.0
     */
    private ObjectOutputStream out;

    /**
     * Communication channel: message receiver.
     *
     * @since 1.0
     */
    private MessageReader receiver;

    /**
     * Network configuration: Server's IP
     *
     * @since 1.0
     */
    private final String IP = "127.0.0.1";

    /**
     * Network configuration: Server's TCP port
     *
     * @since 1.0
     */
    private final Integer PORT = 4931;

    /**
     * Map with all known users.
     *
     * @since 1.0
     */
    private final Map<String, User> users = new TreeMap<>();

    /**
     * User for broadcasts.
     *
     * @since 1.0
     */
    public final User BROADCAST = new User(Destination.BROADCAST.name());

    /**
     * User for server communication.
     *
     * @since 1.0
     */
    public final User SERVER = new User(Destination.SERVER.name());

    /**
     * Events notifier.
     *
     * <p>
     * This field will notify subscribers about events that might interest them
     * as clients, especially interfaces.
     * </p>
     *
     * @since 1.0
     */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * Creates a RSAClient that forwards messages to the specified interface.
     *
     * @since 1.0
     * @param face
     * @throws java.io.IOException
     */
    public RSAClient(PropertyChangeListener face) throws IOException {
        this.pcs.addPropertyChangeListener(face);
    }

    /**
     * Creates a network connection between the client and the server.
     *
     * @since 1.0
     * @throws IOException
     */
    public void connect() throws IOException {
        socket = new Socket(IP, PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        receiver = new MessageReader(socket.getInputStream());
        receiver.addPropertyChangeListener(this);
        receiver.startReader();
    }

    /**
     * Login into the server with the specified user name.
     *
     * @since 1.0
     * @param userName
     * @throws IOException
     */
    public void login(String userName) throws IOException {
        Message login = new Login(userName);
        sendMessage(login);
        this.name = userName;
        sendPublicKeyMessage();
    }

    /**
     * Logout from the server.
     *
     * @since 1.0
     * @param notifyServer If needs to send a LOGOUT message to the server. (if
     * the LOGOUT request came from the server this will not be necessary).
     *
     * @throws IOException
     * @throws Exception
     */
    public void logout(boolean notifyServer) throws IOException, Exception {
        Message logout = new Logout(name, name);

        if (notifyServer) {
            sendMessage(logout);
        }

        close();
        pcs.firePropertyChange(ClientEvents.LOGOUT.name(), null, logout);
    }

    /**
     * Sends the message.
     *
     * @since 1.0
     * @param msg Message to be sent.
     * @throws IOException
     */
    public void sendMessage(Message msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    /**
     * Get username used for connecting with the server.
     *
     * @since 1.0
     * @return username
     */
    public String getName() {
        return name;
    }

    /**
     * Message processor method.
     * <p>
     * Method called when a event is fired and the client is a subscriber.
     * </p>
     * <p>
     * The message receiver will communicate with the client through events.
     * </p>
     *
     * @since 1.0
     * @param evt Property change event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Message msg = (Message) evt.getNewValue();

        switch (msg.getType()) {
            case LOGIN:
                process((Login) msg);
                break;
            case LOGOUT: {
                try {
                    process((Logout) msg);
                } catch (Exception ex) {
                    Logger.getLogger(RSAClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
            case USER_LIST:
                process((UserList) msg);
                break;
            case KEY:
                process((Key) msg);
                break;
            case PLAIN_MSG:
                process((PlainMessage) msg);
                break;
            case PUB_KEY:
                process((PublicKey) msg);
                break;
            case RSA_MSG:
                process((RSAMessage) msg);
                break;
            case SYM_MSG: {
                try {
                    process((SymmetricMessage) msg);
                } catch (IOException ex) {
                    Logger.getLogger(RSAClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
        }

    }

    /**
     * Return the current list of users.
     *
     * @since 1.0
     * @return users connected to the server.
     */
    public Map<String, User> getUsers() {
        return users;
    }

    /**
     * Process a LOGIN message.
     *
     * @since 1.0
     * @param msg
     */
    public void process(Login msg) {
        User newUser = new User(msg.getMessage());
        users.put(newUser.getName(), newUser);
        pcs.firePropertyChange(ClientEvents.USER_UPDATE.name(), null, getUsers());
    }

    /**
     * Process a LOGOUT message.
     *
     * @since 1.0
     * @param msg
     */
    public void process(Logout msg) throws IOException, Exception {
        users.remove(msg.getMessage());
        if (!msg.getMessage().equals(name)) {
            pcs.firePropertyChange(ClientEvents.USER_UPDATE.name(), null, getUsers());
        } else {
            if (msg.getSource().equals(Destination.SERVER.name())) {
                logout(false);
            }
        }
    }

    /**
     * Process a USER_LIST message.
     *
     * @since 1.0
     * @param msg
     */
    public void process(UserList msg) {
        Map userList = msg.getMessage();
        users.clear();
        users.putAll(userList);
        pcs.firePropertyChange(ClientEvents.USER_UPDATE.name(), null, getUsers());
    }

    /**
     * Process a Key message.
     *
     * @since 1.0
     * @param msg
     */
    public void process(Key msg) {
        BigInteger msgEncrypted = msg.getMessage();

        byte[] key = new byte[DES.BLOCK_SIZE / Byte.SIZE];
        byte[] msgKey = rsa.decrypt(msgEncrypted).toByteArray();
        System.arraycopy(msgKey, 0, key, 0, msgKey.length);

        users.get(msg.getSource()).setKey(key);
        pcs.firePropertyChange(ClientEvents.USER_UPDATE.name(), null, getUsers());
    }

    /**
     * Process a PUB_KEY message.
     *
     * @since 1.0
     * @param msg
     */
    public void process(PublicKey msg) {

        users.get(msg.getSource()).setPublicKey(msg.getMessage());
        pcs.firePropertyChange(ClientEvents.USER_UPDATE.name(), null, getUsers());
    }

    /**
     * Process a PLAIN_MSG message.
     *
     * @since 1.0
     * @param msg
     */
    public void process(PlainMessage msg) {
        pcs.firePropertyChange(ClientEvents.NEW_MESSAGE.name(), null, msg);
    }

    /**
     * Process a RSA_MSG message.
     *
     * @since 1.0
     * @param msg
     */
    public void process(RSAMessage msg) {

        BigInteger msgEncrypted = msg.getMessage();

        String plainText = RSA.BigIntegerToString(rsa.decrypt(msgEncrypted));

        pcs.firePropertyChange(ClientEvents.NEW_MESSAGE.name(), null, new PlainMessage(msg.getSource(), msg.getDestination(), plainText));
    }

    /**
     * Process a SYM_MSG message.
     *
     * @since 1.0
     * @param msg
     */
    public void process(SymmetricMessage msg) throws IOException {

        User source = users.get(msg.getSource());
        String plainText;

        try (InputStream msgEncrypted = new ByteArrayInputStream(msg.getMessage());
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            des.decrypt(msgEncrypted, source.getKey(), output);
            plainText = output.toString(RSAServer.CHARSET.name());

        }

        pcs.firePropertyChange(ClientEvents.NEW_MESSAGE.name(), null, new PlainMessage(msg.getSource(), msg.getDestination(), plainText));
    }

    /**
     * Sends a RSA_MSG message.
     *
     * @since 1.0
     * @param destine
     * @param message
     * @throws java.io.IOException
     */
    public void sendRSAMessage(String destine, String message) throws IOException {
        PlainMessage msg = new PlainMessage(name, destine, message);
        sendRSAMessage(msg);
    }

    /**
     * Send a RSA_MSG message.
     *
     * @since 1.0
     * @param msg
     * @throws java.io.IOException
     */
    public void sendRSAMessage(PlainMessage msg) throws IOException {

        User destination = users.get(msg.getDestination());

        String plainText = msg.getMessage();

        BigInteger cipherText = RSA.encrypt(destination.getPublicKeyPair(), plainText);

        sendMessage(new RSAMessage(name, msg.getDestination(), cipherText));
        pcs.firePropertyChange(ClientEvents.NEW_MESSAGE.name(), null, new PlainMessage(msg.getSource(), msg.getDestination(), plainText));
    }

    /**
     * Send a PUB_KEY message.
     *
     * @since 1.0
     * @throws IOException
     */
    public void sendPublicKeyMessage() throws IOException {
        sendMessage(new PublicKey(name, Destination.SERVER.name(), rsa.getPublicKeyPair()));
    }

    /**
     * Sends a PLAIN_MSG message.
     *
     * @since 1.0
     * @param destine
     * @param message
     * @throws IOException
     */
    public void sendPlainMessage(String destine, String message) throws IOException {
        PlainMessage msg = new PlainMessage(name, destine, message);
        sendMessage(msg);
    }

    /**
     * Sends a SYM_MSG message.
     *
     * @since 1.0
     * @param destine
     * @param message
     * @throws IOException
     */
    public void sendSYMMessage(String destine, String message) throws IOException {
        PlainMessage msg = new PlainMessage(name, destine, message);
        sendSYMMessage(msg);
    }

    /**
     * Send a SYM_MSG message.
     *
     * @since 1.0
     * @param msg
     */
    public void sendSYMMessage(PlainMessage msg) throws IOException {

        User destination = users.get(msg.getDestination());

        if (destination.getKey() == null) {
            shareKey(destination);
        }

        byte[] cipherText;

        try (InputStream msgEncrypted = new ByteArrayInputStream(msg.getMessage().getBytes(RSAServer.CHARSET));
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            des.encrypt(msgEncrypted, destination.getKey(), output);
            cipherText = output.toByteArray();
            sendMessage(new SymmetricMessage(name, msg.getDestination(), cipherText));
            pcs.firePropertyChange(ClientEvents.NEW_MESSAGE.name(), null, msg);
        }

    }

    /**
     * Generates a session key and send the the destination user.
     *
     * @since 1.0
     * @param destination
     * @throws IOException
     */
    public void shareKey(User destination) throws IOException {
        try {
            byte[] newKey = DES.genkey(true);

            destination.setKey(newKey);

            BigInteger encryptedKey = RSA.encrypt(destination.getPublicKeyPair(), newKey);
            sendMessage(new Key(name, destination.getName(), encryptedKey));

            pcs.firePropertyChange(ClientEvents.USER_UPDATE.name(), null, getUsers());
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RSAClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Verify if the client is connected to the server.
     *
     * @since 1.0
     * @return
     * <ul>
     * <li>true: if the client is connected;</li>
     * <li>false: otherwise.</li>
     * </ul>
     */
    boolean isConnected() {
        return socket.isConnected();
    }

    /**
     * Close resources.
     *
     * @since 1.0
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        out.close();
        receiver.close();
        socket.close();
    }

}
