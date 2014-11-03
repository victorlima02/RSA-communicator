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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User collector: to clean unused users after timeout.
 *
 * <p>
 * This class control the time limits for every user. Including, how long the
 * server waits for the user to login after initial connection and how long they
 * can stay inactive.
 * </p>
 *
 * @author Victor de Lima Soares
 * @version 1.0
 */
public class UserCollector {

    /**
     * Controlled user.
     *
     * @since 1.0
     */
    private final User user;

    /**
     * Task scheduler.
     *
     * @since 1.0
     */
    private final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);

    /**
     * Next scheduled deletion task.
     *
     * @since 1.0
     */
    private ScheduledFuture futureDelete;

    /**
     * Time limit to login in seconds.
     */
    private static final int TIME_TO_LOGIN = 30;

    /**
     * Time limit for inactive users in minutes.
     */
    private static final int TIME_INACTIVE = 30;

    /**
     * Creates a user collector.
     *
     * @since 1.0
     * @param user
     */
    public UserCollector(User user) {
        this.user = user;
        futureDelete = schedule.schedule(DeleteNotConnected, TIME_TO_LOGIN, TimeUnit.SECONDS);
    }

    /**
     * Deletes the user if not connected on the time limit.
     *
     * @since 1.0
     */
    private final Runnable DeleteNotConnected = new Runnable() {

        @Override
        public void run() {
            synchronized (user) {
                if (!user.isConnected()) {
                    try {
                        user.close();
                    } catch (Exception ex) {
                        Logger.getLogger(UserCollector.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    };

    /**
     * Deletes the user if not active during the time limit.
     *
     * @since 1.0
     */
    private final Runnable Delete = new Runnable() {

        @Override
        public void run() {
            synchronized (user) {
                try {
                    user.close();
                } catch (Exception ex) {
                    Logger.getLogger(UserCollector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    };

    /**
     * Reset inactive the timer.
     *
     * @since 1.0
     */
    public void resetInnativeTimer() {
        synchronized (user) {
            if (user.isConnected()) {
                futureDelete.cancel(false);
                futureDelete = schedule.schedule(Delete, TIME_INACTIVE, TimeUnit.MINUTES);
            }
        }
    }
    
    /**
     * Cancel all tasks.
     * 
     * @since 1.0
     */
    public void cancelTasks(){
        futureDelete.cancel(true);
    }
}
