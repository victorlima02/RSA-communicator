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

import java.io.Serializable;
import java.math.BigInteger;
import javafx.util.Pair;

/**
 * Communicator User - client side.
 *
 * @author Victor de Lima Soares
 * @version 1.0
 */
public class User implements Serializable{

    private final String name;
    private Pair<BigInteger, BigInteger> publicKeyPair;
    private byte[] key;

    public User(String name) {
        this.name = name;
    }

    /**
     * Gets user name.
     *
     * <p>
     * The user name is also a unique identification for users and it is used
     * for routing.
     * </p>
     *
     * @since 1.0
     * @return User name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the synchronized key (DES).
     *
     * @since 1.0
     * @param key Synchronized algorithm key - session key.
     */
    public void setKey(byte[] key) {
        this.key = key;
    }

    /**
     * Gets the synchronized key (DES).
     *
     * @since 1.0
     * @return key Synchronized algorithm key - session key.
     */
    public byte[] getKey() {
        return key;
    }

    /**
     * Return the (n,public key) or (n,e) public key pair.
     *
     * @since 1.0
     * @return (n,e)
     */
    public Pair<BigInteger, BigInteger> getPublicKeyPair() {
        return publicKeyPair;
    }

    /**
     * Attributes a new (n,public key) or (n,e) public key pair to this user.
     *
     * @since 1.0
     * @param publicKeyPair RSA pair (n,e)
     */
    public void setPublicKey(Pair<BigInteger, BigInteger> publicKeyPair) {
        this.publicKeyPair = publicKeyPair;
    }

    @Override
    public String toString() {
        return getName();
    }
}
