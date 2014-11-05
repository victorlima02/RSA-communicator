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

/**
 * Events that can be fired for a RSA client.
 *
 * <p>
 * Those events make it possible to notify interfaces and, other interested
 * subscribers, about updates and actions that are being taken on the RSA
 * client; e.i., new message or new user.
 * </p>
 *
 * @author Victor de Lima Soares
 * @since 1.0
 */
public enum ClientEvents {
    /**
     * This event indicates that some user information has changed.
     * @since 1.0
     */
    USER_UPDATE,
    /**
     * This event indicates that a new message have arrived.
     * @since 1.0
     */
    NEW_MESSAGE,
    /**
     * The RSA client has disconnected from the server.
     * @since 1.0
     */
    LOGOUT
}
