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
package rsacommunicator.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.time.LocalDateTime;
import javafx.util.Pair;
import rsacommunicator.MessageReader;
import rsacommunicator.client.User;
import rsacommunicator.messages.Message;

/**
 * Communicator Client - server side.
 * 
 * <p>
 * This class manages all functionality attributed to users on the server side.
 * </p>
 * <h3>This class is responsible for:</h3>
 * <ul>
 * <li>Storing user's informations;</li>
 * <li>Initializing time controllers;</li>
 * <li>Initializing communication channels with the user;</li>
 * <li>Manipulating the channels and marking communication packages.</li>
 * </ul>
 * 
 * <h3>Those responsibilities are shared with:</h3>
 * <ul>
 * <li>{@link UserCollector};</li>
 * <li>{@link MessageReader}.</li>
 * </ul>
 * 
 * @author Victor de Lima Soares
 * @version 1.0
 */
public class Client implements Comparable<Client>, AutoCloseable {

    private String name;
    private Pair<BigInteger, BigInteger> publicKeyPair;

    private final Socket socket;
    private final ObjectOutputStream out;
    private final MessageReader receiver;

    private final RSAServer server;

    /**
     * Field to control time limits.
     *
     * @see UserCollector
     */
    private final UserCollector userColeCollector;

    /**
     * Flag to indicate if this user has logedin.
     *
     * @since 1.0
     */
    private boolean connected = false;

    /**
     * Creates a new user from a connection request.
     *
     * @param server
     * @param socket
     * @throws IOException
     */
    public Client(RSAServer server, Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        this.server = server;
        
        receiver = new MessageReader(this,socket.getInputStream());
        receiver.addPropertyChangeListener(server);
        receiver.startReader();

        userColeCollector = new UserCollector(this);
    }

    /**
     * Send a message for this user.
     *
     * @since 1.0
     * @param msg Message to be sent.
     * @throws java.io.IOException
     */
    public void sendMessage(Message msg) throws IOException {
        out.writeObject(msg);
        out.flush();
    }

    /**
     * Get user's name.
     *
     * @since 1.0
     * @return Client's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Update user's name.
     *
     * @since 1.0
     * @param name New name for this user.
     */
    public void setName(String name) {
        this.name = name.trim();
    }

    /**
     * Get RSA public key.
     *
     * @since 1.0
     * @return Client's public key.
     */
    public Pair<BigInteger, BigInteger> getPublicKeyPair() {
        return publicKeyPair;
    }

    /**
     * Update RSA public key.
     *
     * @since 1.0
     * @param publicKey New public key.
     */
    public void setPublicKeyPair(Pair<BigInteger, BigInteger> publicKeyPair) {
        this.publicKeyPair = publicKeyPair;
    }

    /**
     * Close resources.
     *
     * @since 1.0
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        synchronized (this) {
            if (this.isConnected()) {
                server.removeUser(this);
            }

            userColeCollector.cancelTasks();
            receiver.close();
            out.close();
            socket.close();
        }
    }

    /**
     * Verifies if the servers has connected to this user.
     *
     * <p>
     * A user is connected after login.
     * </p>
     *
     * @since 1.0
     * @return
     * <ul>
     * <li>true: if the user is connected;</li>
     * <li>false: otherwise.</li>
     * </ul>
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Connects the user to the server.
     *
     * @since 1.0
     * @param connected
     * @see #isConnected()
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * Access the timestamp for the last message sent by this user.
     *
     * @since 1.0
     * @return timestamp for the last message.
     */
    public LocalDateTime getLastMessage() {
        return receiver.getLastMessage();
    }

    /**
     * Get the time controller for this user.
     *
     * @since 1.0
     * @return Client collector.
     * @see UserCollector
     */
    public UserCollector getUserColeCollector() {
        return userColeCollector;
    }

    @Override
    public int compareTo(Client o) {
        if (o == this) {
            return 0;
        }
        return this.name.compareTo(o.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Client)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return this.name.equals(((Client) obj).name);
    }

    public User toClientUser(){
        User clientUser = new rsacommunicator.client.User(name);
        clientUser.setPublicKey(publicKeyPair);
        return clientUser;
    }
}
