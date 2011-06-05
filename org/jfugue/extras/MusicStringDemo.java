package org.jfugue.extras;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jfugue.Player;

public class MusicStringDemo extends JFrame 
{
    private Player player;
    private JTextArea textArea;
    private Map map;
    
    public MusicStringDemo()
    {
        setSize(300, 300);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
                exit();
            }
        });
        setTitle("JFugue MusicString Demo");
        
        player = new Player();
        
        this.map = buildMap();
        buildUI(map);
    }
    
    private Map buildMap()
    {
        Map map = new HashMap();
        map.put("Simple", "C5w");
        map.put("Complex", "C5w+D5q_E5q_F5q_E5q");
        return map;
    }
    
    private void buildUI(Map map)
    {
        JTextArea description = new JTextArea();
        description.setWrapStyleWord(true);
        description.setText("When I found this and realized how easy it was to integrate your API with the project we were making, it only took a couple of hours to put everything together and made me a very VERY happy person! - Simon M");
        description.setBackground(new Color(255, 255, 225));
        JScrollPane descScroll = new JScrollPane(description);
           
        JLabel preLabel = new JLabel("Demonstration Music Strings");
        preLabel.setAlignmentX(LEFT_ALIGNMENT);
        JComboBox combo = new JComboBox();
        combo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e)
            {
                comboItemSelected((String)e.getItem());
            }
        } );
        combo.setAlignmentX(LEFT_ALIGNMENT);
        preLabel.setDisplayedMnemonic('D');
        preLabel.setLabelFor(combo);
        Box topPanel = Box.createVerticalBox();
        topPanel.add(preLabel);
        topPanel.add(combo);
        JPanel topPanelWrapped = wrap(topPanel, BorderLayout.NORTH);
        topPanelWrapped.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel msLabel = new JLabel("Music String");
        textArea = new JTextArea();
        textArea.setRows(5);
        msLabel.setDisplayedMnemonic('M');
        msLabel.setLabelFor(textArea);
        JScrollPane textScroll = new JScrollPane(textArea);
        
        JPanel msPanel = new JPanel();
        msPanel.setLayout(new BorderLayout());
        msPanel.add(msLabel, BorderLayout.NORTH);
        msPanel.add(textScroll, BorderLayout.CENTER);
        msPanel.setAlignmentX(LEFT_ALIGNMENT);

        JButton playButton = new JButton("Play");
        playButton.setMnemonic('P');
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                play();
            }
        } );
        
        JButton exitButton = new JButton("Exit");
        exitButton.setMnemonic('x');
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                exit();
            }
        } );
        
        JPanel buttonGrid = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonGrid.add(playButton);
        buttonGrid.add(exitButton);
        JPanel wrappedButtons = wrap(buttonGrid, BorderLayout.EAST);
        wrappedButtons.setAlignmentX(LEFT_ALIGNMENT);
        
        Box framePanel = Box.createVerticalBox();
        framePanel.add(topPanelWrapped);
        framePanel.add(Box.createVerticalStrut(5));
        framePanel.add(msPanel);
        framePanel.add(Box.createVerticalStrut(5));
        framePanel.add(wrappedButtons);
        
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(framePanel, BorderLayout.CENTER);
        
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext())
        {
            String key = (String)keys.next();
            combo.addItem(key);
        }
    }
    
    private JPanel wrap(JComponent component, String direction)
    {
        JPanel panel = new JPanel();
        panel.add(component, direction);
        return panel;
    }
    
    private void comboItemSelected(String selectedItem)
    {
        textArea.setText((String)map.get(selectedItem));
    }

    private void play()
    {
        MusicStringDemo.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        player.play(MusicStringDemo.this.textArea.getText());
        MusicStringDemo.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    private void exit()
    {
        player.close();
        System.exit(0);
    }
    
    public static void main(String[] args)
    {
        MusicStringDemo demo = new MusicStringDemo();
        demo.show();
    }
}
