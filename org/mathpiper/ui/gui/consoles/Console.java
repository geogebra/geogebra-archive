/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}
// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:
package org.mathpiper.ui.gui.consoles;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import org.mathpiper.interpreters.EvaluationResponse;
import org.mathpiper.interpreters.Interpreter;
import org.mathpiper.interpreters.Interpreters;
import org.mathpiper.interpreters.ResponseListener;
import org.mathpiper.io.MathPiperOutputStream;
import org.mathpiper.lisp.Environment;


public class Console extends javax.swing.JPanel implements ActionListener, KeyListener, ResponseListener, ItemListener, MathPiperOutputStream {

    private Interpreter interpreter = Interpreters.getAsynchronousInterpreter();
    private StringBuilder input = new StringBuilder();
    private JButton haltButton, clearConsoleButton, clearRawButton, helpButton, button2, button3;
    private JCheckBox rawOutputCheckBox;
    private JCheckBox showRawOutputCheckBox;
    private JTextArea rawOutputTextArea;
    private JTextArea textArea;
    private MathPiperOutputStream currentOutput;
    private JScrollPane typePane;
    private char[] typedKey = new char[1];
    private JPanel buttons;
    private boolean deleteFlag = false;
    private float fontSize = 12;
    private Font bitstreamVera;
    private StringBuilder inputLines;
    private int responseInsertionOffset = -1;
    private boolean encounteredIn = false;
    private JSplitPane splitPane;
    private int splitPaneDividerLocation = 400;
    private JScrollPane rawOutputCheckBoxScrollPane;
    private Stack history = new java.util.Stack();
    private boolean controlKeyDown = false;
    private int historyIndex = -1;
    private String helpMessage =
            "Press <enter> after an expression to create an additional input line and to append a hidden ;.\n\n" +
            "Press <shift><enter> after any input line in a group of input lines to execute them all.\n\n" +
            "Type In> on the left edge of any line to create your own input prompt.\n\n" +
            "Press <enter> after an empty In> to erase the In>.\n" +
            "Any line in a group that ends with a \\ will not have a ; appended to it.\n\n" +
            "Pressing <ctrl><enter> at the end of a line automatically appends a \\ to the line.\n\n" +
            "Use <ctrl><up arrow> and <ctrl><down arrow> to navigate through the command line history.\n\n" +
            "The console window is an editable text area, so you can add text to it and remove text from \n" +
            "it as needed.\n\n" +
            "The Raw Output checkbox sends all side effects output to the raw output text area.";


    public Console() {

        inputLines = new StringBuilder();


        this.setLayout(new BorderLayout());

        //keySendQueue = new java.util.concurrent.ArrayBlockingQueue(30);

        buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

        Box guiBox = new Box(BoxLayout.Y_AXIS);

        textArea = new JTextArea(30, 20);
        textArea.append("MathPiper version " + org.mathpiper.Version.version + ".\n");
        textArea.append("Enter an expression after any In> prompt and press <shift><enter> to evaluate it.\n");


        textArea.append("\nIn> ");
        textArea.setCaretPosition(textArea.getDocument().getLength());

        //java.io.InputStream inputStream = org.gjt.sp.jedit.jEdit.getPlugin("org.mathrider.u6502plugin.U6502Plugin").getPluginJAR().getClassLoader().getResourceAsStream( "resources/ttf-bitstream-vera-1.10/VeraMono.ttf" );

        //bitstreamVera = Font.createFont (Font.TRUETYPE_FONT, inputStream);
        //bitstreamVera = bitstreamVera.deriveFont(fontSize);
        //typeArea.setFont(bitstreamVera);


        textArea.addKeyListener(this);
        typePane = new JScrollPane(textArea);
        //guiBox.add(typePane);

        Box ioBox = new Box(BoxLayout.Y_AXIS);

        haltButton = new JButton("Halt Calculation");
        haltButton.setEnabled(false);
        haltButton.setForeground(Color.RED);
        haltButton.addActionListener(this);
        buttons.add(haltButton);

        /*button2 = new JButton("Font--");
        button2.addActionListener(this);
        buttons.add(button2);
        button3 = new JButton("Font++");
        button3.addActionListener(this);
        buttons.add(button3);*/

        rawOutputCheckBox = new JCheckBox("Raw Output");
        rawOutputCheckBox.addItemListener(this);
        buttons.add(rawOutputCheckBox);
        this.rawOutputTextArea = new JTextArea();
        rawOutputTextArea.setEditable(false);
        rawOutputTextArea.setText("Raw output text area.\n\n");


        showRawOutputCheckBox = new JCheckBox("Show Raw Output");
        showRawOutputCheckBox.addItemListener(this);
        buttons.add(showRawOutputCheckBox);

        buttons.add(Box.createGlue());


        clearConsoleButton = new JButton("Clear");
        clearConsoleButton.addActionListener(this);
        buttons.add(clearConsoleButton);


        clearRawButton = new JButton("Clear Raw");
        clearRawButton.addActionListener(this);
        buttons.add(clearRawButton);


        helpButton = new JButton("Help");
        helpButton.addActionListener(this);
        buttons.add(helpButton);


        ioBox.add(buttons);


        this.add(ioBox, BorderLayout.NORTH);

        //this.add(guiBox, BorderLayout.CENTER);


        rawOutputCheckBoxScrollPane = new JScrollPane(rawOutputTextArea);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, typePane, null);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(splitPaneDividerLocation);

        this.add(splitPane);


    }//Constructor.


    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        //bitstreamVera = bitstreamVera.deriveFont(fontSize);
        //typeArea.setFont(bitstreamVera);
    }//end method.


    public void actionPerformed(ActionEvent event) {
        Object src = event.getSource();

        if (src == haltButton) {
            interpreter.haltEvaluation();
        } else if (src == button2) {
            this.fontSize -= 2;
            //bitstreamVera = bitstreamVera.deriveFont(fontSize);
            //typeArea.setFont(bitstreamVera);
        } else if (src == button3) {
            this.fontSize += 2;
            //bitstreamVera = bitstreamVera.deriveFont(fontSize);
            //typeArea.setFont(bitstreamVera);
        } else if (src == helpButton) {
            JOptionPane.showMessageDialog(this, this.helpMessage);
        } else if (src == clearConsoleButton) {
            this.textArea.setText("");
        } else if (src == clearRawButton) {
            this.rawOutputTextArea.setText("");
        }

    }//end method.


    public void itemStateChanged(ItemEvent ie) {
        Object source = ie.getSource();

        if (source == rawOutputCheckBox) {
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                Environment environment = interpreter.getEnvironment();
                this.currentOutput = environment.iCurrentOutput;
                environment.iCurrentOutput = this;
            } else {
                Environment environment = interpreter.getEnvironment();
                environment.iCurrentOutput = this.currentOutput;
            }//end if/else.
        } else if (source == showRawOutputCheckBox) {
            if (ie.getStateChange() == ItemEvent.SELECTED) {
                splitPane.add(rawOutputCheckBoxScrollPane);
                splitPane.setDividerLocation(splitPaneDividerLocation);
                splitPane.revalidate();
            } else {
                splitPane.remove(2);
                splitPane.revalidate();
            }//end if/else.
        }
    }//end method.


    public void putChar(char aChar) throws Exception {
        if (rawOutputTextArea != null && currentOutput != null) {
            this.rawOutputTextArea.append("" + aChar);
            this.rawOutputTextArea.setCaretPosition(this.rawOutputTextArea.getDocument().getLength());
            this.currentOutput.putChar(aChar);
        }//end if.
    }//end method.


    public void write(String aString) throws Exception {
        int i;
        for (i = 0; i < aString.length(); i++) {
            putChar(aString.charAt(i));
        }
    }//end method.


    public void keyPressed(KeyEvent e) {
        int keyCode = (int) e.getKeyCode();

        if (keyCode == KeyEvent.VK_CONTROL) {
            this.controlKeyDown = true;
        }//end if.


        if (keyCode == KeyEvent.VK_UP && this.controlKeyDown) {
            //System.out.println("up");

            if (!history.empty() && historyIndex != history.size() - 1) {


                historyIndex++;
                //System.out.println(history.get((history.size()-1) - historyIndex));

                try {
                    int lineNumber = textArea.getLineOfOffset(textArea.getCaretPosition());
                    int lineStartOffset = textArea.getLineStartOffset(lineNumber);
                    int lineEndOffset = textArea.getLineEndOffset(lineNumber);

                    textArea.replaceRange("In>" + (String) history.get((history.size() - 1) - historyIndex), lineStartOffset, lineEndOffset);

                } catch (BadLocationException ble) {
                    //Eat exception.
                }


            }//end if.

        }//end if.


    }//end method.


    public void keyReleased(KeyEvent e) {
        int keyCode = (int) e.getKeyCode();

        if (keyCode == KeyEvent.VK_CONTROL) {
            this.controlKeyDown = false;
        }//end if.


        if (keyCode == KeyEvent.VK_DOWN && this.controlKeyDown) {
            //System.out.println("down");

            if (!history.empty() && (!(historyIndex < 1))) {


                historyIndex--;
                //System.out.println(history.get((history.size()-1) - historyIndex));

                try {
                    int lineNumber = textArea.getLineOfOffset(textArea.getCaretPosition());
                    int lineStartOffset = textArea.getLineStartOffset(lineNumber);
                    int lineEndOffset = textArea.getLineEndOffset(lineNumber);

                    textArea.replaceRange("In>" + (String) history.get((history.size() - 1) - historyIndex), lineStartOffset, lineEndOffset);

                } catch (BadLocationException ble) {
                    //Eat exception.
                }

            } else if (!history.empty() && historyIndex == 0) {
                try {
                    int lineNumber = textArea.getLineOfOffset(textArea.getCaretPosition());
                    int lineStartOffset = textArea.getLineStartOffset(lineNumber);
                    int lineEndOffset = textArea.getLineEndOffset(lineNumber);

                    textArea.replaceRange("In>", lineStartOffset, lineEndOffset);

                    this.historyIndex = -1;

                } catch (BadLocationException ble) {
                    //Eat exception.;
                }

            }//end else.
        }//end if.

    }//end method.


    public void keyTyped(KeyEvent e) {

        char key = e.getKeyChar();

        //System.out.println((int)key);

        if ((int) key == 10) {
            try {
                int lineNumber = textArea.getLineOfOffset(textArea.getCaretPosition());
                String line = "";
                //System.out.println("key pressed"); //TODO remove.

                //System.out.println("LN: " + lineNumber + "  LSO: " + lineStartOffset + "  LEO: " + lineEndOffset  );
                if (e.isShiftDown()) {

                    captureInputLines(lineNumber);

                    clearPreviousResponse();


                    String code = inputLines.toString().replaceAll(";;", ";").trim();

                    code = code.replaceAll("\\\\", "");

                    //System.out.println(code);

                    history.push(code.substring(0, code.length() - 1));
                    this.historyIndex = -1;

                    if (code.length() > 0) {
                        interpreter.addResponseListener(this);
                        interpreter.evaluate("[" + code + "];", true);
                        haltButton.setEnabled(true);

                    }//end if.
                } else {
                    int relativeLineOffset = -1;
                    int cursorInsert = 0;
                    String eol = "";
                    if (e.isControlDown()) {
                        relativeLineOffset = 0;
                        int textAreaLineCount = textArea.getLineCount();
                        if (lineNumber + 1 == textAreaLineCount) {
                            eol = " \\\n";
                            cursorInsert = 3;
                        }

                    }
                    int lineStartOffset = textArea.getLineStartOffset(lineNumber + relativeLineOffset);
                    int lineEndOffset = textArea.getLineEndOffset(lineNumber + relativeLineOffset);
                    line = textArea.getText(lineStartOffset, lineEndOffset - lineStartOffset);
                    if (line.startsWith("In> \n") || line.startsWith("In>\n")) {
                        textArea.replaceRange("", lineStartOffset, lineEndOffset);
                    } else if (line.startsWith("In>")) {
                        textArea.insert(eol + "In> ", lineEndOffset);
                        textArea.setCaretPosition(lineEndOffset + 4 + cursorInsert);
                    }

                }



                //input.delete(0, input.length());
                // typeArea.append(response.getResult());

            } catch (BadLocationException ex) {
                System.out.println(ex.getMessage() + " , " + ex.offsetRequested());
            }

            //typeArea.append(new String(typedKey));
            //typeArea.setCaretPosition( typeArea.getDocument().getLength() );
        } else if ((int) key == 22) {
            try {
                String clipBoard = (String) java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().getData(java.awt.datatransfer.DataFlavor.stringFlavor);

                if (clipBoard.length() != 0) {
                    char[] chars = clipBoard.toCharArray();
                    for (int x = 0; x < chars.length; x++) {
                        //buffer.put((int) chars[x]);
                    }//end for.
                    //setReceiveDataRegisterFull(true);
                }//end if.

            } catch (NullPointerException ev) {
                ev.printStackTrace();
            } catch (IllegalStateException ev) {
                ev.printStackTrace();
            } catch (java.awt.datatransfer.UnsupportedFlavorException ev) {
                ev.printStackTrace();
            } catch (java.io.IOException ev) {
                ev.printStackTrace();
            }
        } else {
            //System.out.println(key);
            //registers[0] = (int) key;
            if ((int) key == 8) {
                deleteFlag = true;
            }

            input.append(key);
            //typeArea.append(Character.toString(key));
            //buffer.put((int) key);
            //setReceiveDataRegisterFull(true);
        }
    }//end method.


    public void response(EvaluationResponse response) {

        String output = "Result: " + response.getResult().trim();

        if (!response.getSideEffects().equalsIgnoreCase("")) {
            output += "\nSide Effects:\n" + response.getSideEffects();
        }

        if (response.isExceptionThrown()) {
            output += "\nException: " + response.getExceptionMessage();
        }

        if (!encounteredIn) {
            output = "\n" + output + "\n\nIn> ";
        }

        final String finalOutput = output;
        try {
            if (textArea.getLineOfOffset(responseInsertionOffset) == textArea.getLineCount()) {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        haltButton.setEnabled(false);
                        textArea.append(finalOutput);
                    }


                });

                //textArea.setCaretPosition( textArea.getDocument().getLength() );
            } else {
                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        haltButton.setEnabled(false);
                        textArea.insert(finalOutput, responseInsertionOffset);
                    }


                });

            }//end if/else.
        } catch (BadLocationException ex) {
            System.out.println(ex);
        }
    }//end method.


    public boolean remove() {
        return true;
    }


    private void clearPreviousResponse() {

        try {
            int lineNumber = textArea.getLineOfOffset(responseInsertionOffset);

            if (responseInsertionOffset == -1 || lineNumber == textArea.getLineCount()) {
                encounteredIn = false;
                return;
            }

            String line = "";
            int lineStartOffset = 0;

            do {

                lineNumber++;
                lineStartOffset = textArea.getLineStartOffset(lineNumber);
                int lineEndOffset = textArea.getLineEndOffset(lineNumber);
                line = textArea.getText(lineStartOffset, lineEndOffset - lineStartOffset);

            } while (!line.startsWith("In>") && lineNumber < textArea.getLineCount());

            textArea.replaceRange("\n\n", responseInsertionOffset, lineStartOffset);
            encounteredIn = line.startsWith("In>");
            return;

        } catch (BadLocationException ex) {
            encounteredIn = false;
            return;
        }
    }//end method.


    private void captureInputLines(int lineNumber) {

        inputLines.delete(0, inputLines.length());

        try {
            int lineStartOffset = textArea.getLineStartOffset(lineNumber);
            int lineEndOffset = textArea.getLineEndOffset(lineNumber);
            String line = textArea.getText(lineStartOffset, lineEndOffset - lineStartOffset);

            if (line.startsWith("In>")) {
                //Scan backwards to first line that does not start with In>.
                do {
                    lineStartOffset = textArea.getLineStartOffset(lineNumber);
                    lineEndOffset = textArea.getLineEndOffset(lineNumber);
                    line = textArea.getText(lineStartOffset, lineEndOffset - lineStartOffset);
                    lineNumber--;

                } while (line.startsWith("In>") && lineNumber != -1);//end do/while.

                if (lineNumber != -1) {
                    lineNumber++;
                }


                //Scan forwards to first line that does not start with In>.
                boolean pastInputLines = false;
                do {
                    lineNumber++;
                    lineStartOffset = textArea.getLineStartOffset(lineNumber);
                    lineEndOffset = textArea.getLineEndOffset(lineNumber);
                    line = textArea.getText(lineStartOffset, lineEndOffset - lineStartOffset);
                    if (line.startsWith("In>")) {
                        String eol = new String(line);
                        inputLines.append(line.substring(3, line.length()).trim());
                        responseInsertionOffset = lineEndOffset;
                        if (!eol.endsWith(";") && !eol.endsWith("\\\n")) {
                            inputLines.append(";");
                        }//end if.
                    } else {
                        pastInputLines = true;
                    }


                } while (!pastInputLines && lineNumber < textArea.getLineCount());//end while.

            }//end if.

        } catch (BadLocationException ex) {
        }


    }//end method.


    public void setHaltButtonEnabledState(boolean state) {
        this.haltButton.setEnabled(state);



    }//end method.


    public static void main(String[] args) {
        Console console = new Console();

        JFrame frame = new javax.swing.JFrame();
        Container contentPane = frame.getContentPane();
        contentPane.add(console, BorderLayout.CENTER);
        //frame.setAlwaysOnTop(true);
        frame.setSize(new Dimension(700, 600));
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        //frame.setResizable(false);
        frame.setPreferredSize(new Dimension(700, 600));
        frame.setLocationRelativeTo(null); // added
        frame.pack();
        frame.setVisible(true);
    }//end main.
}//end class.

