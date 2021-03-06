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
import java.io.IOException;
import rsacommunicator.client.ClientGUI;
import rsacommunicator.client.RSAClient;
import rsacommunicator.server.RSAServer;

/**
 * RSA communicator demonstration program.
 *
 * <p>
 * This is just a demonstration class for details about implementation follow
 * {@link RSAClient}.
 * </p>
 *
 * <p>
 * This demonstration will open two clients and one server(port PORT = 4931)
 * defined on {@link RSAClient} and {@link RSAServer}.
 * </p>
 *
 * @author Victor de Lima Soares
 * @version 1.0
 *
 * @see RSAClient
 * @see ClientGUI
 * @see RSAServer
 */
public class Demo {

    public static void main(String[] args) throws IOException {
        Thread server = new Thread(new RSAServer());
        server.start();

        ClientGUI.main(args);
        ClientGUI.main(args);
    }
}
