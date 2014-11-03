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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.TreeSet;
import rsacommunicator.MessageReader;
import rsacommunicator.messages.Login;
import rsacommunicator.messages.Message;

/**
 * The RSA communicator client.
 *
 * @author Victor de Lima Soares
 * @version 1.0
 */
public class RSAClient implements PropertyChangeListener {

    private String name;

    private Socket socket;
    private ObjectOutputStream out;
    private MessageReader receiver;

    private final String IP = "127.0.0.1";
    private final Integer PORT = 4931;

    private final PropertyChangeListener messageProcessor;

    private final Set<User> users = new TreeSet<>();

    /**
     * Creates a RSAClient that forwards messages to the specified message
     * processor.
     *
     * @since 1.0
     * @param messageProcessor
     * @throws java.io.IOException
     */
    public RSAClient(PropertyChangeListener messageProcessor) throws IOException {
        this.messageProcessor = messageProcessor;
        setUpNetwork();
    }

    /**
     * Creates a network connection between the client and the server.
     *
     * @since 1.0
     * @throws IOException
     */
    private void setUpNetwork() throws IOException {
        socket = new Socket(IP, PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        receiver = new MessageReader(socket.getInputStream());
        receiver.addPropertyChangeListener(messageProcessor);
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (rsacommunicator.messages.Type.valueOf(evt.getPropertyName())) {
            case LOGIN:
                processLoginMessage((Login) evt.getNewValue());
        }
    }
    
    /**
     * Process a LOGIN message.
     * 
     * @since 1.0
     * @param msg 
     */
    public void processLoginMessage(Login msg){
        users.add(new User(msg.getMessage()));
    }

    /**
     * Return the current list of users.
     * 
     * @since 1.0
     * @return users connected to the server.
     */
    public Set<User> getUsers() {
        return users;
    }
}
