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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import rsacommunicator.messages.Login;
import rsacommunicator.messages.Message;
import rsacommunicator.messages.Type;

/**
 *
 * @author Victor de Lima Soares
 * @version 1.0
 */
public class Server implements Runnable, PropertyChangeListener {

    private final String IP = "127.0.0.45";
    private final Integer PORT = 4931;

    private final Set<User> usersConnected = new TreeSet<>();

    private final ServerSocket serverSock;

    public Server() throws IOException {
        this.serverSock = new ServerSocket(PORT);
    }

    @Override
    public void run() {
        while (true) {
            try {

                Socket clientSocket = serverSock.accept();
                User user = new User(this, clientSocket);

            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void bradcast(Message message) throws IOException {

        for (User user : usersConnected) {
            user.sendMessage(message);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {

            switch (Type.valueOf(evt.getPropertyName())) {

                case LOGIN: {

                    login((User) evt.getSource(), (Login) evt.getNewValue());

                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Login a user to the server.
     * 
     * @since 1.0
     * @param user
     * @param msg
     * @throws IOException 
     */
    public void login(User user, Login msg) throws IOException {
        synchronized (user) {
            String userName = msg.getMessage();
            user.setName(userName);
            if (!usersConnected.contains(user)) {
                user.setConnected(true);
                usersConnected.add(user);
                bradcast(new Login(userName));
            }
        }
        for (User userConnected : usersConnected) {
            user.sendMessage(new Login(userConnected.getName()));
        }
    }

    /**
     * Remove a user from the server.
     *
     * @since 1.0
     * @param user
     */
    public void removeUser(User user) {
        usersConnected.remove(user);
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.run();
    }
}
