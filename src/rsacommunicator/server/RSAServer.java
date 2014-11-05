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
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import rsacommunicator.client.RSAClient;
import rsacommunicator.client.User;
import rsacommunicator.messages.Destination;
import rsacommunicator.messages.Key;
import rsacommunicator.messages.Login;
import rsacommunicator.messages.Logout;
import rsacommunicator.messages.Message;
import rsacommunicator.messages.PlainMessage;
import rsacommunicator.messages.PublicKey;
import rsacommunicator.messages.RSAMessage;
import rsacommunicator.messages.SymmetricMessage;
import rsacommunicator.messages.Type;
import rsacommunicator.messages.UserList;

/**
 * RSA communicator server.
 *
 * @author Victor de Lima Soares
 * @version 1.0
 */
public class RSAServer implements Runnable, PropertyChangeListener {

    private final Integer PORT = 4931;

    private final Map<String, Client> usersConnected = Collections.synchronizedSortedMap(new TreeMap<String, Client>());

    private final ServerSocket serverSock;

    public RSAServer() throws IOException {
        this.serverSock = new ServerSocket(PORT);
    }

    /**
     * Default Charset to be used for encoding strings.
     *
     * @since 1.0
     */
    public static final Charset CHARSET = Charset.forName("utf-8");

    /**
     * Starts listening for new clients.
     *
     * @since 1.0
     */
    @Override
    public void run() {
        while (true) {
            try {

                Socket clientSocket = serverSock.accept();
                Client user = new Client(this, clientSocket);

            } catch (IOException ex) {
                Logger.getLogger(RSAServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Broadcasts a message.
     *
     * @since 1.0
     * @param message
     * @throws IOException
     */
    public void bradcast(Message message) throws IOException {

        for (Client user : usersConnected.values()) {
            user.sendMessage(message);
        }
    }

    /**
     * Method called when a event is fired and the server is a subscriber.
     *
     * <p>
     * The message receiver will communicate with the server through events.
     * </p>
     * <p>
     * This method verifies if user in the channel has not sent a message as
     * another user.
     * </p>
     *
     * @since 1.0
     * @param evt Property change event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        try {
            //Verifies if user in the channel has not send a message as other user.
            Message msg = (Message) evt.getNewValue();
            String sourceField = msg.getSource();
            String channelOwner = ((Client) evt.getSource()).getName();

            if ((channelOwner == null && msg.getType() == Type.LOGIN) || (channelOwner != null && channelOwner.equals(sourceField))) {
                switch (msg.getType()) {
                    case LOGIN:
                        process((Client) evt.getSource(), (Login) msg);
                        break;
                    case LOGOUT:
                        process((Logout) msg);
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
        } catch (IOException ex) {
            Logger.getLogger(RSAServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RSAServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Remove a user from the server.
     *
     * @since 1.0
     * @param user
     */
    public void removeUser(Client user) {
        usersConnected.remove(user.getName());
    }

    /**
     * Run server as an independent program.
     *
     * @param args the command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        RSAServer server = new RSAServer();
        server.run();
    }

    /**
     * Process a LOGIN message.
     *
     * Login a user to the server.
     *
     * @since 1.0
     * @param user
     * @param msg
     * @throws IOException
     */
    public void process(Client user, Login msg) throws IOException, Exception {

        synchronized (user) {
            String userName = msg.getMessage();

            if (!usersConnected.containsKey(userName)) {
                user.setName(userName);
                user.setConnected(true);
                usersConnected.put(userName, user);
                bradcast(new Login(userName));
            } else {
                user.sendMessage(new Logout(Destination.SERVER.name(), userName));
                user.close();
            }
        }

        if (user.isConnected()) {
            user.sendMessage(new UserList(Destination.SERVER.name(), user.getName(), Type.USER_LIST, getClientUsersMap()));
        }
    }

    /**
     * Process a LOGOUT message.
     *
     * @since 1.0
     * @param msg
     * @throws IOException
     * @throws Exception
     */
    public void process(Logout msg) throws IOException, Exception {
        usersConnected.get(msg.getSource()).close();
        bradcast(new Logout(Destination.SERVER.name(), msg.getSource()));
    }

    /**
     * Process a Key message.
     *
     * @since 1.0
     * @param msg
     * @throws IOException
     */
    public void process(Key msg) throws IOException {
        relay(msg);
    }

    /**
     * Process a PUB_KEY message.
     *
     * @since 1.0
     * @param msg
     * @throws IOException
     */
    public void process(PublicKey msg) throws IOException {
        usersConnected.get(msg.getSource()).setPublicKeyPair(msg.getMessage());
        bradcast(msg);
    }

    /**
     * Process a PLAIN_MSG message.
     *
     * @since 1.0
     * @param msg
     * @throws IOException
     */
    public void process(PlainMessage msg) throws IOException {
        relay(msg);
    }

    /**
     * Process a RSA_MSG message.
     *
     * @since 1.0
     * @param msg
     * @throws java.io.IOException
     */
    public void process(RSAMessage msg) throws IOException {
        relay(msg);
    }

    /**
     * Process a SYM_MSG message.
     *
     * @since 1.0
     * @param msg
     * @throws java.io.IOException
     */
    public void process(SymmetricMessage msg) throws IOException {
        relay(msg);
    }

    /**
     * Relay a message to its destination.
     *
     * @since 1.0
     * @param msg
     * @throws IOException
     */
    public void relay(Message msg) throws IOException {
        if (Destination.BROADCAST.name().equals(msg.getDestination())) {
            bradcast(msg);
            return;
        }
        usersConnected.get(msg.getDestination()).sendMessage(msg);
    }

    /**
     * Create a map with users information to forward to clients.
     *
     * @since 1.0
     * @return User map.
     */
    public TreeMap<String, User> getClientUsersMap() {
        TreeMap<String, User> clients = new TreeMap<>();
        usersConnected.values().stream().forEach((user) -> {
            clients.put(user.getName(), user.toClientUser());
        });
        return clients;
    }
}
