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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import rsacommunicator.messages.Logout;
import rsacommunicator.messages.PlainMessage;
import rsacommunicator.server.RSAServer;

/**
 * GUI interface for the RSA client.
 *
 * <p>
 * This class do not contains any functionally related to RSA, all RSA logic is
 * provided by the {@link RSAClient}.
 * </p>
 * <p>
 * A list of users connected is provided along with a message area and a text
 * field to send messages.
 * </p>
 * <p>
 * To send a message, click on the user you want to communicate with over the list
 * on the right - loaded after logging in.
 * </p>
 *
 * @author Victor de Lima Soares
 * @version 1.0
 *
 * @see RSAClient
 * @see RSAServer
 * @see ClientEvents
 */
public class ClientGUI extends javax.swing.JFrame implements PropertyChangeListener {

    /**
     * RSA client.
     */
    private final RSAClient client;

    /**
     * Creates new form ClientGUI and initializes the RSA client.
     *
     * @since 1.0
     * @throws java.io.IOException
     */
    public ClientGUI() throws IOException {
        initComponents();
        client = new RSAClient(this);
    }

    /**
     * Destination for the next message.
     *
     * @since 1.0
     */
    private User destination;

    /**
     * Events processor method.
     * <p>
     * Method called when a event is fired from the RSA client.
     * </p>
     * <p>
     * The RSA client will communicate with the GUI through events.
     * </p>
     *
     * @since 1.0
     * @param evt Property change event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (rsacommunicator.client.ClientEvents.valueOf(evt.getPropertyName())) {
            case USER_UPDATE:
                updateUsers((Map<String, User>) evt.getNewValue());
                break;
            case NEW_MESSAGE:
                newMessage((PlainMessage) evt.getNewValue());
                break;
            case LOGOUT:
                process((Logout) evt.getNewValue());
        }
    }

    /**
     * Updates the user tree view.
     *
     * @since 1.0
     * @param msg
     */
    private void updateUsers(Map<String, User> usersMap) {

        DefaultTreeModel model = (DefaultTreeModel) users.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

        root.removeAllChildren(); //this removes all nodes
        model.reload(); //this notifies the listeners and changes the GUI

        usersMap.values().stream().forEach((User user) -> {
            DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(user);
            model.insertNodeInto(userNode, root, root.getChildCount());

            if (user.getKey() != null) {
                model.insertNodeInto(new DefaultMutableTreeNode("Sym Key"), userNode, 0);
            }
            if (user.getPublicKeyPair() != null) {
                model.insertNodeInto(new DefaultMutableTreeNode("Pub Key"), userNode, Math.min(1, userNode.getChildCount()));
            }
        });

        for (int i = 0; i < users.getRowCount(); i++) {
            users.expandRow(i);
        }
    }

    /**
     * Writes new messages on the received messages area.
     *
     * @since 1.0
     * @param message
     */
    public void newMessage(PlainMessage message) {
        incomingTextArea.append(message + "\n");
    }

    /**
     * Actions to be taken when a logout instruction comes form the server.
     *
     * @since 1.0
     * @param msg
     */
    private void process(Logout msg) {
        enableLogin(true);
    }

    /**
     * Enables or disables the message to send area.
     *
     * @since 1.0
     * @param enable
     */
    private void enableMessages(boolean enable) {
        outcomingTextArea.setEnabled(enable);
    }

    /**
     * Adjusts GUI interface after for login/logout.
     *
     * @since 1.0
     * @param enable Indicates if the GUI shout prepare for a login.
     */
    private void enableLogin(boolean enable) {
        enableMessages(!enable);
        sendButton.setEnabled(false);
        if (enable) {
            cleanTreeView();
            loginButton.setText("Login");
        } else {
            loginButton.setText("Logout");
        }
        userTextField.setEnabled(enable);
    }

    /**
     * Cleans the user tree view.
     *
     * @since 1.0
     */
    private void cleanTreeView() {
        DefaultTreeModel model = (DefaultTreeModel) users.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

        root.removeAllChildren(); //this removes all nodes
        model.reload(); //this notifies the listeners and changes the GUI
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        usersPane = new javax.swing.JScrollPane();
        users = new javax.swing.JTree();
        userTextField = new javax.swing.JTextField();
        loginButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        incomingPane = new javax.swing.JScrollPane();
        incomingTextArea = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();
        outcomingPane = new javax.swing.JScrollPane();
        outcomingTextArea = new javax.swing.JTextArea();
        sendButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RSA Communicator");
        setMinimumSize(new java.awt.Dimension(880, 480));
        setPreferredSize(new java.awt.Dimension(1053, 384));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(200, 100));
        java.awt.GridBagLayout jPanel1Layout = new java.awt.GridBagLayout();
        jPanel1Layout.columnWidths = new int[] {0, 0, 0};
        jPanel1Layout.rowHeights = new int[] {0, 0, 0};
        jPanel1.setLayout(jPanel1Layout);

        usersPane.setMinimumSize(new java.awt.Dimension(25, 360));
        usersPane.setPreferredSize(new java.awt.Dimension(480, 360));

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Users");
        users.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        users.setMaximumSize(new java.awt.Dimension(0, 0));
        users.setMinimumSize(new java.awt.Dimension(95, 95));
        users.setName(""); // NOI18N
        users.setPreferredSize(new java.awt.Dimension(180, 350));
        users.setRequestFocusEnabled(false);
        users.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                usersValueChanged(evt);
            }
        });
        usersPane.setViewportView(users);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(usersPane, gridBagConstraints);

        userTextField.setText("User"+((int)(Math.random()*9999)));
        userTextField.setMinimumSize(new java.awt.Dimension(0, 0));
        userTextField.setPreferredSize(new java.awt.Dimension(60, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(userTextField, gridBagConstraints);

        loginButton.setText("Login");
        loginButton.setMinimumSize(new java.awt.Dimension(0, 0));
        loginButton.setName(""); // NOI18N
        loginButton.setPreferredSize(new java.awt.Dimension(5, 5));
        loginButton.setRequestFocusEnabled(false);
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(loginButton, gridBagConstraints);

        jSplitPane1.setLeftComponent(jPanel1);

        java.awt.GridBagLayout mainPanelLayout = new java.awt.GridBagLayout();
        mainPanelLayout.columnWidths = new int[] {0, 0, 0};
        mainPanelLayout.rowHeights = new int[] {0, 13, 0, 13, 0};
        mainPanel.setLayout(mainPanelLayout);

        incomingPane.setPreferredSize(new java.awt.Dimension(250, 300));

        incomingTextArea.setEditable(false);
        incomingTextArea.setColumns(20);
        incomingTextArea.setRows(5);
        incomingTextArea.setText("To send a message, click on the user you want to communicate with over the list on the right - loaded after logging in. \nDocumentation about how the software works and the steps taken is available as Javadoc for the RSAClient (rsacommunicator.client.RSAClient).");
        incomingTextArea.setPreferredSize(new java.awt.Dimension(232, 300));
        incomingPane.setViewportView(incomingTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        mainPanel.add(incomingPane, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainPanel.add(jSeparator1, gridBagConstraints);

        outcomingPane.setPreferredSize(new java.awt.Dimension(300, 80));

        outcomingTextArea.setColumns(20);
        outcomingTextArea.setLineWrap(true);
        outcomingTextArea.setRows(3);
        outcomingTextArea.setEnabled(false);
        outcomingPane.setViewportView(outcomingTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        mainPanel.add(outcomingPane, gridBagConstraints);

        sendButton.setText("Send");
        sendButton.setEnabled(false);
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        mainPanel.add(sendButton, gridBagConstraints);

        jSplitPane1.setRightComponent(mainPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jSplitPane1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        try {
            if (destination == client.BROADCAST) {
                client.sendPlainMessage(destination.getName(), outcomingTextArea.getText());
            } else {
                client.sendSYMMessage(destination.getName(), outcomingTextArea.getText());
            }
            outcomingTextArea.setText("");
            outcomingPane.requestFocus();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_sendButtonActionPerformed

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        try {
            if (!client.isConnected()) {
                client.login(userTextField.getText());
                enableMessages(true);
                enableLogin(false);
            } else {
                client.logout(true);
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_loginButtonActionPerformed

    private void usersValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_usersValueChanged
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) users.getLastSelectedPathComponent();

        if (node == null || (node.isRoot() && node.getChildCount() == 0)) {
            //Nothing is selected. 
            sendButton.setEnabled(false);
            sendButton.setText("None");
            sendButton.setToolTipText("No user selected.");
            destination = null;
            return;
        }

        if (node.isLeaf()) {
            node = (DefaultMutableTreeNode) node.getParent();
        }

        if (node == (DefaultMutableTreeNode) users.getModel().getRoot()) {
            sendButton.setEnabled(true);
            sendButton.setText("Broadcast");
            sendButton.setToolTipText("Broadcast a plain text message.");
            destination = client.BROADCAST;

        } else {
            sendButton.setEnabled(true);
            destination = (User) node.getUserObject();
            sendButton.setText(destination.getName());
            sendButton.setToolTipText("Send message to: " + destination.getName());
        }
    }//GEN-LAST:event_usersValueChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            if (client.isConnected()) {
                client.logout(true);
            }
        } catch (Exception ex) {
            Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {

                    new ClientGUI().setVisible(true);

                } catch (IOException ex) {
                    Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane incomingPane;
    private javax.swing.JTextArea incomingTextArea;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton loginButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JScrollPane outcomingPane;
    private javax.swing.JTextArea outcomingTextArea;
    private javax.swing.JButton sendButton;
    private javax.swing.JTextField userTextField;
    private javax.swing.JTree users;
    private javax.swing.JScrollPane usersPane;
    // End of variables declaration//GEN-END:variables

}
