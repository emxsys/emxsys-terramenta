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

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;
import javax.swing.SwingWorker;
import org.openide.util.NbPreferences;

final class TimeOptionsPanel extends javax.swing.JPanel {

    private static final Preferences prefs = NbPreferences.forModule(TimeOptions.class);
    private final TimeOptionsPanelController controller;
    private final SortableComboBoxModel<TimeZoneComparable> timeZoneModel = new SortableComboBoxModel<>();
    private final SortableComboBoxModel<LocaleComparable> localeModel = new SortableComboBoxModel<>();
    private final SortableComboBoxModel<FormatComparable> formatModel = new SortableComboBoxModel<>();

    /**
     * TimeZoneProxy makes a TimeZone compatible with a ComboBoxModel.
     */
    private class TimeZoneComparable implements Comparable<TimeZoneComparable> {

        private final ZoneId zoneId;

        TimeZoneComparable(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        public ZoneId getTimeZone() {
            return zoneId;
        }

        @Override
        public String toString() {
            return LocalDateTime.now().atZone(zoneId).getOffset() + " : \t" + zoneId;
        }

        @Override
        public int compareTo(TimeZoneComparable o) {
            return toString().compareTo(o.toString());
        }

        @Override
        public int hashCode() {
            return zoneId.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TimeZoneComparable other = (TimeZoneComparable) obj;
            return zoneId.getId().equals(other.zoneId.getId());
        }

    }

    /**
     * LocaleProxy makes a Locale compatible with a ComboBoxModel.
     */
    private class LocaleComparable implements Comparable<LocaleComparable> {

        private final Locale locale;

        LocaleComparable(Locale locale) {
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
        public int compareTo(LocaleComparable o) {
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
            final LocaleComparable other = (LocaleComparable) obj;
            return locale.toLanguageTag().equals(other.locale.toLanguageTag());
        }

    }

    /**
     * LocaleProxy makes a Locale compatible with a ComboBoxModel.
     */
    private class FormatComparable implements Comparable<FormatComparable> {

        private final String pattern;

        FormatComparable(String pattern) {
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }

        @Override
        public String toString() {
            return DateTimeFormatter.ofPattern(pattern, getSelectedLocale()).format(ZonedDateTime.now(getSelectedTimeZone()));
        }

        @Override
        public int compareTo(FormatComparable o) {
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
            final FormatComparable other = (FormatComparable) obj;
            return pattern.equals(other.pattern);
        }

    }

    private class TimeZoneLoader extends SwingWorker<SortableComboBoxModel, TimeZoneComparable> {

        private final SortableComboBoxModel model;

        public TimeZoneLoader(SortableComboBoxModel model) {

            this.model = model;
        }

        @Override
        protected SortableComboBoxModel doInBackground() throws Exception {
            ZoneId.getAvailableZoneIds().stream().map((id) -> new TimeZoneComparable(ZoneId.of(id))).forEach((proxy) -> {
                publish(proxy);
            });
            return model;
        }

        @Override
        protected void process(List<TimeZoneComparable> proxies) {
            proxies.stream().forEach((proxy) -> {
                model.addElement(proxy);
            });
        }

        @Override
        protected void done() {
            model.setSelectedItem(new TimeZoneComparable(ZoneId.of(prefs.get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE))));
        }

    }

    private class LocaleLoader extends SwingWorker<SortableComboBoxModel, LocaleComparable> {

        private final SortableComboBoxModel model;

        public LocaleLoader(SortableComboBoxModel model) {
            this.model = model;
        }

        @Override
        protected SortableComboBoxModel doInBackground() throws Exception {
            for (Locale locale : Locale.getAvailableLocales()) {
                LocaleComparable proxy = new LocaleComparable(locale);
                publish(proxy);
            }
            return model;
        }

        @Override
        protected void process(List<LocaleComparable> proxies) {
            proxies.stream().forEach((proxy) -> {
                model.addElement(proxy);
            });
        }

        @Override
        protected void done() {
            model.setSelectedItem(new LocaleComparable(Locale.forLanguageTag(prefs.get(TimeOptions.LOCALE, TimeOptions.DEFAULT_LOCALE))));
        }
    }

    private class FormatLoader extends SwingWorker<SortableComboBoxModel, FormatComparable> {

        private final SortableComboBoxModel model;

        public FormatLoader(SortableComboBoxModel model) {
            this.model = model;
        }

        @Override
        protected SortableComboBoxModel doInBackground() throws Exception {
            String[] patterns = {
                "yyyy-MM-dd HH:mm:ss z",
                "yyyy-MM-dd HH:mm:ss XXX",
                "dd MMM yy @ HH:mm:ss",
                "MMMM dd yyyy '@' HH:mm:ss",
                getDefaultPattern(getSelectedLocale())};

            for (String pattern : patterns) {
                FormatComparable proxy = new FormatComparable(pattern);
                publish(proxy);
            }

            return model;
        }

        @Override
        protected void process(List<FormatComparable> proxies) {
            proxies.stream().forEach((proxy) -> {
                model.addElement(proxy);
            });
        }

        @Override
        protected void done() {
            model.setSelectedItem(new FormatComparable(prefs.get(TimeOptions.FORMAT, TimeOptions.DEFAULT_FORMAT)));
        }

    }

    TimeOptionsPanel(TimeOptionsPanelController controller) {
        this.controller = controller;

        initComponents();

        new TimeZoneLoader(timeZoneModel).run();
        new LocaleLoader(localeModel).run();
        new FormatLoader(formatModel).run();
    }

    void load() {
        timeZoneCombo.setSelectedItem(new TimeZoneComparable(ZoneId.of(prefs.get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE))));
        localeCombo.setSelectedItem(new LocaleComparable(Locale.forLanguageTag(prefs.get(TimeOptions.LOCALE, TimeOptions.DEFAULT_LOCALE))));
        formatCombo.setSelectedItem(new FormatComparable(prefs.get(TimeOptions.FORMAT, TimeOptions.DEFAULT_FORMAT)));
    }

    void store() {
        prefs.put(TimeOptions.TIMEZONE, ((TimeZoneComparable) (timeZoneCombo.getSelectedItem())).getTimeZone().getId());
        prefs.put(TimeOptions.LOCALE, ((LocaleComparable) (localeCombo.getSelectedItem())).getLocale().toLanguageTag());
        prefs.put(TimeOptions.FORMAT, ((FormatComparable) (formatCombo.getSelectedItem())).getPattern());
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    private Locale getSelectedLocale() {
        if (localeCombo != null) {
            LocaleComparable selection = (LocaleComparable) localeCombo.getSelectedItem();
            if (selection != null) {
                return selection.getLocale();
            }
        }

        return Locale.forLanguageTag(prefs.get(TimeOptions.LOCALE, TimeOptions.DEFAULT_LOCALE));
    }

    private ZoneId getSelectedTimeZone() {
        if (timeZoneCombo != null) {
            TimeZoneComparable selection = (TimeZoneComparable) timeZoneCombo.getSelectedItem();
            if (selection != null) {
                return selection.getTimeZone();
            }
        }

        return ZoneId.of(prefs.get(TimeOptions.TIMEZONE, TimeOptions.DEFAULT_TIMEZONE));
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

        formatCombo.setModel(this.formatModel);

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
