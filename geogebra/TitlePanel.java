/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra;

import geogebra.kernel.Construction;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panel with title, author and date of construction. Handles all updates
 * to kernel and notifies attached ActionListeners about kernel changes.
 * 
 * @author Markus Hohenwarter
 */
public class TitlePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField tfTitle, tfAuthor, tfDate;
	private Vector listeners = new Vector();
	//private Application app;
	private Construction cons;

	public TitlePanel(Application app) {
		//this.app = app;
		cons = app.getKernel().getConstruction();
		
		setLayout(new BorderLayout(5, 5));
		tfTitle = new JTextField();
		tfAuthor = new JTextField();
		tfDate = new JTextField();
		tfDate.setColumns(12);		
		
		tfTitle.setText(cons.getTitle());
		tfAuthor.setText(cons.getAuthor());
		tfDate.setText(cons.getDate());
		
	

		JPanel p = new JPanel(new BorderLayout(5, 5));
		p.add(
			new JLabel(app.getPlain("Title") + ": "),
			BorderLayout.WEST);
		p.add(tfTitle, BorderLayout.CENTER);
		add(p, BorderLayout.NORTH);
		p = new JPanel(new BorderLayout(5, 5));
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(
			new JLabel(app.getPlain("Author") + ": "),
			BorderLayout.WEST);
		p1.add(tfAuthor, BorderLayout.CENTER);
		p.add(p1, BorderLayout.CENTER);
		p1 = new JPanel(new BorderLayout());
		p1.add(
			new JLabel(app.getPlain("Date") + ": "),
			BorderLayout.WEST);
		p1.add(tfDate, BorderLayout.CENTER);
		
		p.add(p1, BorderLayout.EAST);
		add(p, BorderLayout.CENTER);
		setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		ActionListener lst = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireTextFieldUpdate((JTextField) e.getSource());
			}
		};
		tfTitle.addActionListener(lst);
		tfAuthor.addActionListener(lst);
		tfDate.addActionListener(lst);

		FocusListener flst = new FocusListener() {
			public void focusLost(FocusEvent e) {
				fireTextFieldUpdate((JTextField) e.getSource());
			}
			public void focusGained(FocusEvent e) {
			}
		};
		tfTitle.addFocusListener(flst);
		tfAuthor.addFocusListener(flst);
		tfDate.addFocusListener(flst);
	}

	private void fireTextFieldUpdate(JTextField tf) {
		String text = tf.getText();
		boolean kernelChanged = false;
		if (tf == tfTitle) {
			if (text.equals(cons.getTitle()))
				return;
			cons.setTitle(text);
			kernelChanged = true;
		} else if (tf == tfAuthor) {
			if (text.equals(cons.getAuthor()))
				return;
			cons.setAuthor(text);
			kernelChanged = true;
		} else if (tf == tfDate) {
			if (text.equals(cons.getDate()))
				return;
			cons.setDate(text);
			kernelChanged = true;
		}

		if (kernelChanged) {
			notifyListeners();
		}
	}

	public void addActionListener(ActionListener lst) {
		listeners.add(lst);
	}

	private void notifyListeners() {
		int size = listeners.size();
		for (int i = 0; i < size; i++) {
			((ActionListener) listeners.get(i)).actionPerformed(
				new ActionEvent(
					this,
					ActionEvent.ACTION_PERFORMED,
					"TitleChanged"));
		}
	}

}
