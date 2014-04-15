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
package com.terramenta.time.options;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.prefs.Preferences;
import javax.swing.SwingWorker;
import org.openide.util.NbPreferences;

final class TimeOptionsPanel extends javax.swing.JPanel {

    private static final Preferences prefs = NbPreferences.forModule(TimeOptions.class);
    private final TimeOptionsPanelController controller;
    private final SortableComboBoxModel<TimeZoneProxy> timeZoneModel = new SortableComboBoxModel<>();
    private final SortableComboBoxModel<LocaleProxy> localeModel = new SortableComboBoxModel<>();
    private final SortableComboBoxModel<FormatProxy> formatModel = new SortableComboBoxModel<>();

    /**
     * TimeZoneProxy makes a TimeZone compatible with a ComboBoxModel.
     */
    private class TimeZoneProxy implements Comparable<TimeZoneProxy> {

        private final TimeZone tz;

        TimeZoneProxy(String timeZoneID) {
            tz = TimeZone.getTimeZone(timeZoneID);
        }

        public TimeZone getTimeZone() {
            return tz;
        }

        @Override
        public String toString() {
            // E.g.: (UTC-08:00) America/Los Angeles : Pacific Time Zone - PST
            int hours = tz.getRawOffset() / 3600000;
            int minutes = Math.abs(tz.getRawOffset() % 3600);
            return String.format("(UTC%1$+03d:%2$02d) %3$s : %4$s - %5$s",
                    hours, minutes, tz.getID(), tz.getDisplayName(false, TimeZone.LONG), tz.getDisplayName(false, TimeZone.SHORT));
        }

        @Override
        public int compareTo(TimeZoneProxy o) {
            return toString().compareTo(o.toString());
        }

        @Override
        public int hashCode() {
            return tz.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TimeZoneProxy other = (TimeZoneProxy) obj;
            return tz.getID().equals(other.tz.getID());
        }

    }

    /**
     * LocaleProxy makes a Locale compatible with a ComboBoxModel.
     */
    private class LocaleProxy implements Comparable<LocaleProxy> {

        private final Locale locale;

        LocaleProxy(Locale locale) {
            this.locale = locale;
        }

        public Locale getLocale() {
            return locale;
        }

        @Override
        public String toString() {
            return locale.getDisplayName();
        }

        @Override
        public int compareTo(LocaleProxy o) {
            return toString().compareTo(o.toString());
        }

        @Override
        public int hashCode() {
            return locale.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final LocaleProxy other = (LocaleProxy) obj;
            return locale.toLanguageTag().equals(other.locale.toLanguageTag());
        }

    }

    /**
     * LocaleProxy makes a Locale compatible with a ComboBoxModel.
     */
    private class FormatProxy implements Comparable<FormatProxy> {

        private final String pattern;

        FormatProxy(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }

        @Override
        public String toString() {
            Locale locale = getSelectedLocale();
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
            return dateFormat.format(Calendar.getInstance().getTime()) + " (" + dateFormat.toLocalizedPattern() + ")";
        }

        @Override
        public int compareTo(FormatProxy o) {
            return toString().compareTo(o.toString());
        }

        @Override
        public int hashCode() {
            return pattern.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final FormatProxy other = (FormatProxy) obj;
            return pattern.equals(other.pattern);
        }

    }

    private class TimeZoneLoader extends SwingWorker<SortableComboBoxModel, TimeZoneProxy> {

        private final SortableComboBoxModel model;

        public TimeZoneLoader(SortableComboBoxModel model) {
            this.model = model;
        }

        @Override
        protected SortableComboBoxModel doInBackground() throws Exception {
            for (String id : TimeZone.getAvailableIDs()) {
                TimeZoneProxy proxy = new TimeZoneProxy(id);
                publish(proxy);
            }
            return model;
        }

        @Override
        protected void process(List<TimeZoneProxy> proxies) {
            for (TimeZoneProxy proxy : proxies) {
                model.addElement(proxy);
            }
        }

    }

    private class LocaleLoader extends SwingWorker<SortableComboBoxModel, LocaleProxy> {

        private final SortableComboBoxModel model;

        public LocaleLoader(SortableComboBoxModel model) {
            this.model = model;
        }

        @Override
        protected SortableComboBoxModel doInBackground() throws Exception {
            //List<TimeZoneProxy> proxies = new ArrayList<>();
            for (Locale locale : DateFormat.getAvailableLocales()) {
                LocaleProxy proxy = new LocaleProxy(locale);
                //proxies.add(new TimeZoneProxy(id));
                publish(proxy);
            }

            //Collections.sort(proxies);
            return model;
        }

        @Override
        protected void process(List<LocaleProxy> proxies) {
            for (LocaleProxy proxy : proxies) {
                model.addElement(proxy);
            }
        }
    }

    private class FormatLoader extends SwingWorker<SortableComboBoxModel, FormatProxy> {

        private final SortableComboBoxModel model;

        public FormatLoader(SortableComboBoxModel model) {
            this.model = model;
        }

        @Override
        protected SortableComboBoxModel doInBackground() throws Exception {
            String[] patterns = {
                "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd HH:mm:ss z",
                "yyyy/MM/dd HH:mm:ss XXX",
                getDefaultPattern(getSelectedLocale())};

            for (String pattern : patterns) {
                FormatProxy proxy = new FormatProxy(pattern);
                publish(proxy);
            }

            return model;
        }

        @Override
        protected void process(List<FormatProxy> proxies) {
            for (FormatProxy proxy : proxies) {
                model.addElement(proxy);
            }
        }
    }

    TimeOptionsPanel(TimeOptionsPanelController controller) {
        this.controller = controller;

        new TimeZoneLoader(timeZoneModel).run();
        new LocaleLoader(localeModel).run();
        new FormatLoader(formatModel).run();

        initComponents();
    }

    private Locale getSelectedLocale() {
        return localeCombo != null
                ? ((LocaleProxy) (localeCombo.getSelectedItem())).getLocale()
                : Locale.forLanguageTag(prefs.get(TimeOptions.LOCALE, TimeOptions.DEFAULT_LOCALE));
    }

    private String getDefaultPattern(Locale locale) {
        // Get a format pattern in the selected locale by overriding the default locale
        Locale oldLocale = Locale.getDefault(Locale.Category.FORMAT);
        Locale.setDefault(Locale.Category.FORMAT, locale);
        String pattern = new SimpleDateFormat().toPattern();    // uses the 'new' default locale
        Locale.setDefault(Locale.Category.FORMAT, oldLocale);
        return pattern;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        timeZoneCombo = new javax.swing.JComboBox();
        localeCombo = new javax.swing.JComboBox();
        formatCombo = new javax.swing.JComboBox();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TimeOptionsPanel.class, "TimeOptionsPanel.jPanel1.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TimeOptionsPanel.class, "TimeOptionsPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(TimeOptionsPanel.class, "TimeOptionsPanel.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(TimeOptionsPanel.class, "TimeOptionsPanel.jLabel3.text")); // NOI18N

        timeZoneCombo.setModel(this.timeZoneModel);

        localeCombo.setModel(this.localeModel);
        localeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localeComboActionPerformed(evt);
            }
        });

        formatCombo.setModel(this.formatModel);
        formatCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formatComboActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(timeZoneCombo, 0, 228, Short.MAX_VALUE)
                    .addComponent(localeCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(formatCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(timeZoneCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(localeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(formatCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formatComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formatComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_formatComboActionPerformed

    private void localeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localeComboActionPerformed
        // Reinitialize formats to reflect locale specific format
        //initFormats();
    }//GEN-LAST:event_localeComboActionPerformed

    void load() {
        timeZoneCombo.setSelectedItem(
                new TimeZoneProxy(prefs.get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE)));
        localeCombo.setSelectedItem(
                new LocaleProxy(Locale.forLanguageTag(
                                prefs.get(TimeOptions.LOCALE, TimeOptions.DEFAULT_LOCALE))));
        formatCombo.setSelectedItem(
                new FormatProxy(prefs.get(TimeOptions.FORMAT, TimeOptions.DEFAULT_FORMAT)));
    }

    void store() {
        prefs.put(TimeOptions.TIMEZONE, ((TimeZoneProxy) (timeZoneCombo.getSelectedItem())).getTimeZone().getID());
        prefs.put(TimeOptions.LOCALE, ((LocaleProxy) (localeCombo.getSelectedItem())).getLocale().toLanguageTag());
        prefs.put(TimeOptions.FORMAT, ((FormatProxy) (formatCombo.getSelectedItem())).getPattern());
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox formatCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox localeCombo;
    private javax.swing.JComboBox timeZoneCombo;
    // End of variables declaration//GEN-END:variables
}
