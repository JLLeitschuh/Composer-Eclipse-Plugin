package com.dubture.composer.ui.parts;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.dubture.composer.ui.editor.FormLayoutFactory;
import com.dubture.composer.ui.utils.WidgetFactory;

public class BooleanFormEntry {

	private Label label;
	private Button checkbox;
	private boolean ignoreNotify = false;
	
	private ArrayList<IBooleanFormEntryListener> listeners = new ArrayList<IBooleanFormEntryListener>();
	
	public BooleanFormEntry(Composite parent, FormToolkit toolkit, String labelText) {
		this(parent, toolkit, labelText, SWT.FLAT);
	}
	
	public BooleanFormEntry(Composite parent, FormToolkit toolkit, String labelText, int style) {
		createControl(parent, toolkit, labelText, style);
	}
	

	public Label getLabel() {
		return label;
	}
	
	public Button getCheckbox() {
		return checkbox;
	}

	public boolean getValue() {
		return checkbox.getSelection();
	}
	
	public void setValue(boolean value) {
		checkbox.setSelection(value);
	}
	
	public void setValue(boolean value, boolean ignoreNotify) {
		this.ignoreNotify = ignoreNotify;
		setValue(value);
		this.ignoreNotify = false;
	}
	
	/**
	 * Attaches the listener for the entry.
	 * 
	 * @param listener
	 */
	public void addBooleanFormEntryListener(IBooleanFormEntryListener listener) {
		listeners.add(listener);
	}
	

	/**
	 * Detaches the listener for the entry.
	 * 
	 * @param listener
	 */
	public void removeBooleanFormEntryListener(IBooleanFormEntryListener listener) {
		listeners.remove(listener);
	}

	private void createControl(Composite parent, FormToolkit toolkit, String labelText, int style) {
		if (labelText != null) {
			label = toolkit.createLabel(parent, labelText);
			label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		}
		
		checkbox = WidgetFactory.createButton(toolkit, parent, style | SWT.CHECK);
		
		addListener();
		fillIntoGrid(parent);
	}
	
	private void addListener() {
		checkbox.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if (ignoreNotify) 
					return;
				
				for (IBooleanFormEntryListener listener : listeners) {
					listener.selectionChanged(BooleanFormEntry.this);
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}
	
	private void fillIntoGrid(Composite parent) {
		Layout layout = parent.getLayout();
		if (layout instanceof GridLayout) {
			int span = ((GridLayout) layout).numColumns;
			
			GridData gd;
			if (label != null) {
				gd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
				label.setLayoutData(gd);
				span--;
			}
			gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			gd.horizontalSpan = span;
			if (label != null) {
				gd.horizontalIndent = FormLayoutFactory.CONTROL_HORIZONTAL_INDENT;
			}
			gd.grabExcessHorizontalSpace = (span == 1);
			gd.widthHint = 10;
			checkbox.setLayoutData(gd);
		} else if (layout instanceof TableWrapLayout) {
			int span = ((TableWrapLayout) layout).numColumns;

			TableWrapData td;
			if (label != null) {
				td = new TableWrapData();
				td.valign = TableWrapData.MIDDLE;
				label.setLayoutData(td);
				span--;
			}
			td = new TableWrapData(TableWrapData.FILL);
			td.colspan = span;
			if (label != null) {
				td.indent = FormLayoutFactory.CONTROL_HORIZONTAL_INDENT;
			}
			td.grabHorizontal = (span == 1);
			td.valign = TableWrapData.MIDDLE;
			checkbox.setLayoutData(td);
		}
	}
}
