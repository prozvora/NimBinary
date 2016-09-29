//******************************************************************************
//
// File:    NimUI.java
// Package: ---
// Unit:    Class NimUI.java
//
// This Java source file is copyright (C) 2015 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by the Free
// Software Foundation; either version 3 of the License, or (at your option) any
// later version.
//
// This Java source file is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// You may obtain a copy of the GNU General Public License on the World Wide Web
// at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.sql.Ref;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Class NimUI provides the user interface for the Nim network game.
 *
 * @author  Alan Kaminsky
 * @version 07-Oct-2015
 * 
 * @author  Pavel Rozvora (pxr8306) *Contributor
 * @version 2015-11-08
 * 
 * Contributor comments begin with pxr
 * 
 */
public class NimUI implements ModelListener
{
    // pxr - fields added for use by the UI
    private ViewListener viewListener;
    private int id;
    private String name, theirName;

    // Interface for a listener for HeapPanel events.

    private static interface HeapListener
    {
        // Report that markers are to be removed from a heap.
        public void removeObjects
        (int id,          // Heap panel ID
                int numRemoved); // Number of markers to be removed
    }

    // Class for a Swing widget displaying a heap of markers.

    private static class HeapPanel
    extends JPanel
    {
        private static final int W = 50;
        private static final int H = 30;
        private static final Color FC = Color.RED;
        private static final Color OC = Color.BLACK;

        private int id;
        private int maxCount;
        private int count;
        private boolean isEnabled;
        private HeapListener listener;

        // Construct a new heap panel.
        public HeapPanel
        (int id,       // Heap panel ID
                int maxCount) // Maximum number of markers
        {
            this.id = id;
            this.maxCount = maxCount;
            this.count = maxCount;
            this.isEnabled = true;
            Dimension dim = new Dimension (W, maxCount*H);
            setMinimumSize (dim);
            setMaximumSize (dim);
            setPreferredSize (dim);
            addMouseListener (new MouseAdapter()
            {
                public void mouseClicked (MouseEvent e)
                {
                    if (isEnabled && listener != null)
                    {
                        int objClicked = maxCount - 1 - e.getY()/H;
                        int numRemoved = count - objClicked;
                        if (numRemoved > 0)
                            listener.removeObjects (id, numRemoved);
                    }
                }
            });
        }

        // Set this heap panel's listener.
        public void setListener
        (HeapListener listener)
        {
            this.listener = listener;
        }

        // Set the number of markers in this heap panel.
        public void setCount
        (int count) // Number of markers
        {
            count = Math.max (0, Math.min (count, maxCount));
            if (this.count != count)
            {
                this.count = count;
                repaint();
            }
        }

        // Enable or disable this heap panel.
        public void setEnabled
        (boolean enabled) // True to enable, false to disable
        {
            if (this.isEnabled != enabled)
            {
                this.isEnabled = enabled;
                repaint();
            }
        }

        // Paint this heap panel.
        protected void paintComponent
        (Graphics g) // Graphics context
        {
            super.paintComponent (g);

            // Clone graphics context.
            Graphics2D g2d = (Graphics2D) g.create();

            // Turn on antialiasing.
            g2d.setRenderingHint
            (RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // For drawing markers.
            Ellipse2D.Double ellipse = new Ellipse2D.Double();
            ellipse.width = W - 2;
            ellipse.height = H - 2;
            ellipse.x = 1;

            // If enabled, draw filled markers.
            if (isEnabled)
            {
                g2d.setColor (FC);
                for (int i = 0; i < count; ++ i)
                {
                    ellipse.y = (maxCount - 1 - i)*H + 1;
                    g2d.fill (ellipse);
                }
            }

            // If disabled, draw outlined markers.
            else
            {
                g2d.setColor (OC);
                for (int i = 0; i < count; ++ i)
                {
                    ellipse.y = (maxCount - 1 - i)*H + 1;
                    g2d.draw (ellipse);
                }
            }
        }
    }

    // Hidden data members.

    private static final int NUMHEAPS = 3;
    private static final int NUMOBJECTS = 5;
    private static final int GAP = 10;
    private static final int COL = 10;

    private JFrame frame;
    private HeapPanel[] heapPanel;
    private JTextField myNameField;
    private JTextField theirNameField;
    private JTextField whoWonField;
    private JButton newGameButton;

    // Hidden constructors.

    /**
     * Construct a new Nim UI.
     */
    private NimUI
    (String name)
    {
        frame = new JFrame ("Nim -- " + name);
        JPanel panel = new JPanel();
        panel.setLayout (new BoxLayout (panel, BoxLayout.X_AXIS));
        frame.add (panel);
        panel.setBorder (BorderFactory.createEmptyBorder (GAP, GAP, GAP, GAP));

        heapPanel = new HeapPanel [NUMHEAPS];
        for (int h = 0; h < NUMHEAPS; ++ h)
        {
            panel.add (heapPanel[h] = new HeapPanel (h, NUMOBJECTS));
            panel.add (Box.createHorizontalStrut (GAP));
        }

        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout (new BoxLayout (fieldPanel, BoxLayout.Y_AXIS));
        panel.add (fieldPanel);

        myNameField = new JTextField (COL);
        myNameField.setEditable (false);
        myNameField.setHorizontalAlignment (JTextField.CENTER);
        myNameField.setAlignmentX (0.5f);
        fieldPanel.add (myNameField);
        fieldPanel.add (Box.createVerticalStrut (GAP));

        theirNameField = new JTextField (COL);
        theirNameField.setEditable (false);
        theirNameField.setHorizontalAlignment (JTextField.CENTER);
        theirNameField.setAlignmentX (0.5f);
        fieldPanel.add (theirNameField);
        fieldPanel.add (Box.createVerticalStrut (GAP));

        whoWonField = new JTextField (COL);
        whoWonField.setEditable (false);
        whoWonField.setHorizontalAlignment (JTextField.CENTER);
        whoWonField.setAlignmentX (0.5f);
        fieldPanel.add (whoWonField);
        fieldPanel.add (Box.createVerticalStrut (GAP));

        newGameButton = new JButton ("New Game");
        newGameButton.setAlignmentX (0.5f);
        newGameButton.setFocusable (false);
        fieldPanel.add (newGameButton);

        frame.pack();
        frame.setVisible (true);

        // pxr - listener added to the new game button to detect a press
        //       and send the message to the server
        newGameButton.addActionListener(new ActionListener() {
            /**
             * This listener tells the view listener to begin a new game.
             */
            public void actionPerformed (ActionEvent e) {
                try {
                    viewListener.newGame();
                } catch (IOException err) {
                    // pxr - catch taken from in-class example
                    //       created by ark
                    //       Password Crack Application Version 2 — 
                    //       Class PasswordCrackUI
                    JOptionPane.showMessageDialog
                    (/*parentComponent*/ frame,
                            /*message        */ "I/O error when sending to server",
                            /*title          */ "I/O error",
                            /*messageType    */ JOptionPane.ERROR_MESSAGE);
                    System.exit (0);
                }
            }
        });
        // pxr - HeapListener interface implemented here and set for each heap
        for (int i = 0; i < 3; i++) {
            heapPanel[i].setListener(new HeapListener() {
                /**
                 * This method tells the view listener to update the heaps.
                 */
                public void removeObjects(int id, int numRemoved) {
                    try {
                        viewListener.take(id, numRemoved);
                    } catch (IOException e) {
                        // pxr - catch taken from in-class example
                        //       created by ark
                        //       Password Crack Application Version 2 — 
                        //       Class PasswordCrackUI
                        JOptionPane.showMessageDialog
                        (/*parentComponent*/ frame,
                                /*message        */ "I/O error when sending to server",
                                /*title          */ "I/O error",
                                /*messageType    */ JOptionPane.ERROR_MESSAGE);
                        System.exit (0);
                    }

                }
            });
        }
        // pxr - WindowListener added to allow the client to send a quit message
        //       before closing
        frame.addWindowListener(new WindowListener() {
            /**
             * This method overrides the windowClosing method to send a quit msg
             * to the server before closing the window.
             */
            public void windowClosing(WindowEvent e) {
                try {
                    viewListener.quit();
                    System.exit(0);
                } catch (IOException e1) {
                    // pxr - catch taken from in-class example
                    //       created by ark
                    //       Password Crack Application Version 2 — 
                    //       Class PasswordCrackUI
                    JOptionPane.showMessageDialog
                    (/*parentComponent*/ frame,
                            /*message        */ "I/O error when sending to server",
                            /*title          */ "I/O error",
                            /*messageType    */ JOptionPane.ERROR_MESSAGE);
                    System.exit (0);
                }
            }
            // pxr - unaffected methods
            public void windowActivated(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowOpened(WindowEvent e) {}
        });
    }

    // Exported operations.

    /**
     * An object holding a reference to a Nim UI.
     */
    private static class UIRef
    {
        public NimUI ui;
    }

    /**
     * Construct a new Nim UI.
     */
    public static NimUI create
    (String name)
    {
        final UIRef ref = new UIRef();
        onSwingThreadDo (new Runnable()
        {
            public void run()
            {
                ref.ui = new NimUI (name);
            }
        });
        return ref.ui;
    }

    // Hidden operations.

    /**
     * Execute the given runnable object on the Swing thread.
     */
    private static void onSwingThreadDo
    (Runnable task)
    {
        try
        {
            SwingUtilities.invokeAndWait (task);
        }
        catch (Throwable exc)
        {
            exc.printStackTrace (System.err);
            System.exit (1);
        }
    }


    /**
     * This method sets the UI's view listener.
     * @param viewListener  The view listener that the messages will go through
     */
    public synchronized void setViewListener(ViewListener viewListener) {
        this.viewListener = viewListener;
    }

    // pxr - implement ModelListener interface

    /**
     * This method sets the player's id and initializes the window.
     * @param id    the player's id
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public synchronized void id(int id) throws IOException {
        this.id = id;
        onSwingThreadDo(new Runnable() {
            public void run() {
                for (int i = 0; i < 3; i++) {
                    heapPanel[i].setCount(i+3);
                    heapPanel[i].setEnabled(false);
                }
                if (id == 0) {
                    newGameButton.setEnabled(false);
                }
            }
        });
    }
    /**
     * This method sets the player names. Scores initialized to 0.
     * @param id    the player to whom this name belongs
     * @param name  the player's name
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public synchronized void name(int id, String name) throws IOException {
        if (id == this.id) {
            this.name = name;
            onSwingThreadDo(new Runnable() {
                public void run() {
                    myNameField.setText(name + " = 0");
                }
            });
        } else {
            theirName = name;
            onSwingThreadDo(new Runnable() {
                public void run() {
                    theirNameField.setText(name + " = 0");
                }
            });
        }
    }
    /**
     * This method sets the player scores.
     * @param id    the player to whom this score belongs
     * @param score the player's score
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public synchronized void score(int id, int score) throws IOException {
        if (id == this.id) {
            onSwingThreadDo(new Runnable() {
                public void run() {
                    myNameField.setText(name + " = " + score);
                }
            });
        } else {
            onSwingThreadDo(new Runnable() {
                public void run() {
                    theirNameField.setText(theirName + " = " + score);
                }
            });
        }
    }
    /**
     * This method updates the state of a heap.
     * @param heapId    the id of the heap
     * @param markers   the amount of markers in the heap
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public synchronized void heap(int heapId, int markers) throws IOException {
        onSwingThreadDo(new Runnable() {
            public void run() {
                heapPanel[heapId].setCount(markers);
                heapPanel[heapId].repaint();
            }
        });
    }
    /**
     * This method tells the players whose turn it is. The heap is disabled
     * if the other player is taking their turn.
     * @param id    id of the player who is taking their turn
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public synchronized void turn(int id) throws IOException {
        if (id == this.id) {
            onSwingThreadDo(new Runnable() {
                public void run() {
                    whoWonField.setText("");
                    newGameButton.setEnabled(true);
                    for (int i = 0; i < 3; i++) {
                        heapPanel[i].setEnabled(true);
                    }
                }
            });
        } else {
            onSwingThreadDo(new Runnable() {
                public void run() {
                    whoWonField.setText("");
                    newGameButton.setEnabled(true);
                    for (int i = 0; i < 3; i++) {
                        heapPanel[i].setEnabled(false);
                    }
                }
            });
        }
    }
    /**
     * This method informs the players of the game's winner.
     * @param id    id belonging to the winner
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public synchronized void win(int id) throws IOException {
        if (id == this.id) {
            onSwingThreadDo(new Runnable() {
                public void run() {
                    whoWonField.setText(name + " wins!");
                }
            });
        } else {
            onSwingThreadDo(new Runnable() {
                public void run() {
                    whoWonField.setText(theirName + " wins!");
                }
            });
        }
    }
    /**
     * This method informs the player that the other player quit by closing
     * the window.
     * @throws IOException  Thrown when I/O fails or is interrupted
     */
    public synchronized void quit() throws IOException {
        //inform mailbox manager to dispose this view
        viewListener.quit();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        System.exit(0);
    }

}