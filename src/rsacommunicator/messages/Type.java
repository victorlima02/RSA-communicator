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

/**
 * Type of messages that can be used on the RSA communicator.
 *
 * <p>
 * This enumeration dictates the communication protocol by defining which types
 * of message are available.
 * </p>
 *
 * @author Victor de Lima Soares
 * @since 1.0
 */
public enum Type {

    /**
     * Request to login.
     *
     * @since 1.0
     */
    LOGIN,
    /**
     * Request to logout.
     *
     * @since 1.0
     */
    LOGOUT,
    /**
     * Update Public key (for RSA).
     *
     * @since 1.0
     */
    PUB_KEY,
    /**
     * Update symmetric key (session key with other user, for DES).
     *
     * @since 1.0
     */
    KEY,
    /**
     * Message encrypted with DES - using session keys.
     *
     * @since 1.0
     */
    SYM_MSG,
    /**
     * Message encrypted with RSA - using public keys.
     *
     * @since 1.0
     */
    RSA_MSG
};
