/*
 * Copyright © 2014, Terramenta. All rights reserved.
 *
 * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or 
 * the Common Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this work except in compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 */
package com.terramenta.layermanager.layerselector;

import java.awt.Component;
import java.util.HashMap;
import java.util.NoSuchElementException;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;

public final class LayerSelectorWizardIterator implements WizardDescriptor.Iterator {

    // To invoke this wizard, copy-paste and run the following code, e.g. from
    // SomeAction.performAction():
    /*
     * WizardDescriptor.Iterator iterator = new LayerSelectorWizardIterator(); WizardDescriptor wizardDescriptor = new WizardDescriptor(iterator); //
     * {0} will be replaced by WizardDescriptor.Panel.getComponent().getName() // {1} will be replaced by WizardDescriptor.Iterator.name()
     * wizardDescriptor.setTitleFormat(new MessageFormat("{0} ({1})")); wizardDescriptor.setTitle("Your wizard dialog title here"); Dialog dialog =
     * DialogDisplayer.getDefault().createDialog(wizardDescriptor); dialog.setVisible(true); dialog.toFront(); boolean cancelled =
     * wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION; if (!cancelled) { // do something }
     */
    private int index;
    private WizardDescriptor wizardDesc;
    private HashMap<String, String[]> labels;
    private HashMap<String, Panel[]> sequences;
    private String layerType = "WMS";

    public void initialize(WizardDescriptor wizardDescriptor) {
        wizardDesc = wizardDescriptor;
    }

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (sequences == null) {
            WizardDescriptor.Panel[] allPanels = new WizardDescriptor.Panel[]{
                new LayerSelectorLayerType(),
                new LayerSelectorGeotiff(),
                new LayerSelectorKml(),
                new LayerSelectorShapefile(),
                new LayerSelectorWms()
            };
            String[] steps = new String[allPanels.length];
            for (int i = 0; i < allPanels.length; i++) {
                Component c = allPanels[i].getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }

            labels = new HashMap<String, String[]>();
            labels.put("GEOTIFF", new String[]{steps[0], steps[1]});
            labels.put("KML", new String[]{steps[0], steps[2]});
            labels.put("SHAPEFILE", new String[]{steps[0], steps[3]});
            labels.put("WMS", new String[]{steps[0], steps[4]});

            sequences = new HashMap<String, WizardDescriptor.Panel[]>();
            sequences.put("GEOTIFF", new WizardDescriptor.Panel[]{allPanels[0], allPanels[1]});
            sequences.put("KML", new WizardDescriptor.Panel[]{allPanels[0], allPanels[2]});
            sequences.put("SHAPEFILE", new WizardDescriptor.Panel[]{allPanels[0], allPanels[3]});
            sequences.put("WMS", new WizardDescriptor.Panel[]{allPanels[0], allPanels[4]});

            setLayerType(layerType);
        }
        return sequences.get(layerType);
    }

    private void setLayerType(String layerType) {
        this.layerType = layerType;
        wizardDesc.putProperty(WizardDescriptor.PROP_CONTENT_DATA, labels.get(layerType));
    }

    public WizardDescriptor.Panel current() {
        return getPanels()[index];
    }

    public String name() {
        return index + 1 + " of " + getPanels().length;
    }

    public boolean hasNext() {
        return index < getPanels().length - 1;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (index == 0) {
            Object lt = wizardDesc.getProperty("layerType");
            if (lt != null) {
                setLayerType((String) lt);
            }
        }
        index++;
        wizardDesc.putProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, index);
    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
        wizardDesc.putProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, index);
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    public void addChangeListener(ChangeListener l) {
    }

    public void removeChangeListener(ChangeListener l) {
    }
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then uncomment
    // the following and call when needed: fireChangeEvent();
    /*
     * private Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0 public final void
     * addChangeListener(ChangeListener l) { synchronized (listeners) { listeners.add(l); } } public final void removeChangeListener(ChangeListener l)
     * { synchronized (listeners) { listeners.remove(l); } } protected final void fireChangeEvent() { Iterator<ChangeListener> it; synchronized
     * (listeners) { it = new HashSet<ChangeListener>(listeners).iterator(); } ChangeEvent ev = new ChangeEvent(this); while (it.hasNext()) {
     * it.next().stateChanged(ev); } }
     */
}
