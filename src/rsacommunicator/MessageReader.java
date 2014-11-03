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
package rsacommunicator;

import rsacommunicator.messages.Message;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import rsacommunicator.server.User;

/**
 * Message reader.
 *
 * @author Victor de Lima Soares
 * @version 1.0
 */
public class MessageReader implements Runnable, AutoCloseable {

    private final BlockingQueue<Message> messages = new LinkedBlockingQueue();
    private final ObjectInputStream source;
    private final PropertyChangeSupport pcs;
    private final User responsable;

    /**
     * Flag to request the threads to stop and die.
     *
     * @since 1.0
     */
    private Boolean CLOSING = false;

    /**
     * Time stamp for the last message.
     */
    private LocalDateTime lastMessage;

    /**
     * Creates a new reader from a input source.
     *
     * @since 1.0
     * @param source
     * @throws java.io.IOException
     */
    public MessageReader(User responsable, InputStream source) throws IOException {
        this.source = new ObjectInputStream(new BufferedInputStream(source));
        this.responsable = responsable;
        pcs = new PropertyChangeSupport(responsable);
    }

    /**
     * Creates a new reader from a input source.
     *
     * @since 1.0
     * @param source
     * @throws java.io.IOException
     */
    public MessageReader(InputStream source) throws IOException {
        this.source = new ObjectInputStream(new BufferedInputStream(source));
        responsable = null;
        pcs = new PropertyChangeSupport(this);
    }

    /**
     * Starts the necessary threads.
     *
     * <p>
     * This class performs two main tasks: receive messages from a source and
     * notify subscribers. Each of those tasks are performed by different
     * threads, while one is receiving and processing new incoming messages, the
     * second is notifying its clients.
     * </p>
     *
     * <p>
     * This method will start both.
     * </p>
     *
     * @since 1.0
     * @see #startsEmissary()
     * @see #run()
     */
    public void startReader() {
        Thread reader = new Thread(this);
        reader.start();
    }

    /**
     * Starts listening for new messages and notifying subscribers.
     *
     * @since 1.0
     * @see #startReader()
     */
    @Override
    public void run() {
        startsEmissary();
        while (true) {
            if (CLOSING == true) {
                break;
            }
            try {
                readInput();
            } catch (IOException ex) {
                Logger.getLogger(MessageReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Read new messages arriving through the source.
     *
     * @since 1.0
     * @throws IOException
     * <ul>
     * <li>if any exception occur during transmission.</li>
     * </ul>
     */
    private void readInput() throws IOException {
        try {

            Message message = (Message) source.readObject();
            messages.put(message);
            setLastMessage(LocalDateTime.now());
        } catch (IOException | InterruptedException | ClassNotFoundException ex) {
            throw new IOException("Input error.", ex);
        }
    }

    /**
     * Method creates a new thread to manage notifications.
     * <p>
     * Notification might be heavy, especially, for multiple clients and
     * graphical interfaces. While one thread reads from the source and stores
     * the incoming messages on a queue, the other notifies the clients.
     * </p>
     *
     * @since 1.0
     */
    public void startsEmissary() {
        Runnable emissary = new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {

                        Message message = messages.take();
                        pcs.firePropertyChange(message.getType().name(), null, message);

                    } catch (InterruptedException ex) {
                        Logger.getLogger(MessageReader.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (CLOSING == true && messages.isEmpty()) {
                        break;
                    }
                }
            }
        };

        Thread emissaryThread = new Thread(emissary);
        emissaryThread.start();
    }

    /**
     * Access the timestamp for the last message received.
     *
     * @since 1.0
     * @return timestamp for the last message.
     */
    public LocalDateTime getLastMessage() {
        return lastMessage;
    }

    /**
     * Update the timestamp for the last message received.
     *
     * @since 1.0
     * @param lastMessage
     */
    private void setLastMessage(LocalDateTime lastMessage) {
        this.lastMessage = lastMessage;
        if(responsable != null) responsable.getUserColeCollector().resetInnativeTimer();
    }

    /**
     * Adds a listener for new messages.
     *
     * @since 1.0
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    /**
     * Removes a listener for new messages.
     *
     * @since 1.0
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    /**
     * Close resource.
     *
     * @since 1.0
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        source.close();
        CLOSING = true;
    }

}
