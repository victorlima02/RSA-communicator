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
package rsacommunicator.messages;

import java.io.Serializable;

/**
 * Message for the RSA communicator.
 *
 * @author Victor de Lima Soares
 * @version 1.0
 */
public abstract class Message<M extends Serializable> implements Serializable {

    /**
     * Message version in use.
     *
     * @since 1.0
     */
    public static final long serialVersionUID = 1L;

    /**
     * A string identifying the source of this message.
     *
     * @since 1.0
     */
    private final String source;

    /**
     * A string identifying the destination for this message.
     *
     * @since 1.0
     */
    private final String destination;

    /**
     * Message type.
     *
     * @since 1.0
     */
    private final Type type;

    /**
     * The content of the message.
     *
     * <p>
     * The contents type can change as the message types change.
     * </p>
     *
     * @since 1.0
     */
    private final M message;

    /**
     * Creates a new message.
     *
     * @param source
     * @param destination
     * @param type
     * @param message
     */
    public Message(String source, String destination, Type type, M message) {
        this.type = type;
        this.message = message;
        this.source = source;
        this.destination = destination.trim();
    }

    /**
     * Returns the message type.
     *
     * <p>
     * Used to define how to process the message.
     * </p>
     *
     * @since 1.0
     * @return Message type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the message contents.
     *
     * <p>
     * The contents can be of any type, how to serialize and deserialize depends
     * on the communication protocol and the type of message used.
     * </p>
     *
     * @since 1.0
     * @return Contents.
     */
    public M getMessage() {
        return message;
    }

    /**
     * To whom the message is for.
     *
     * @since 1.0
     * @return Destination.
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Who sent the message.
     *
     * @since 1.0
     * @return Source.
     */
    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return source + " -> " + destination + ": " + message;
    }

}
