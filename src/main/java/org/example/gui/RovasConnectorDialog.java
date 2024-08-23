package org.example.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.net.*;
import java.util.Collections;
import javax.swing.*;

import org.example.api.ApiException;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.gui.SideButton;
import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.tagging.presets.TaggingPresetListener;
import org.openstreetmap.josm.gui.util.GuiHelper;
import org.openstreetmap.josm.gui.util.MultiLineFlowLayout;
import org.openstreetmap.josm.gui.widgets.JMultilineLabel;
import org.openstreetmap.josm.gui.widgets.VerticallyScrollablePanel;
import org.openstreetmap.josm.tools.*;

import org.example.ChangeTrackerPlugin;
import org.example.action.ResetTimerAction;
import org.example.model.RovasProperties;
import org.example.model.TimeTrackingManager;
import org.example.util.GBCUtil;
import org.example.util.GuiComponentFactory;
import org.example.util.I18nStrings;
import org.example.util.TimeConverterUtil;


import static org.openstreetmap.josm.tools.I18n.tr;

/**
 * The main dialog that displays a live timer of the time that the plugin has tracked so far.
 */
public class RovasConnectorDialog extends ToggleDialog implements TimeTrackingUpdateListener, TaggingPresetListener {
    private static final GBC GBC_LEFT_COLUMN = GBCUtil.fixedToColumn(0, GBC.std().insets(5).span(1)).anchor(GBC.LINE_END);
    private static final GBC GBC_RIGHT_COLUMN = GBCUtil.fixedToColumn(1, GBC.eol().insets(5).span(1).fill(GBC.HORIZONTAL));
    private static final GBC GBC_BOTH_COLUMNS = GBCUtil.fixedToColumn(0, GBC.eol().insets(5).span(2).fill(GBC.HORIZONTAL));

    private final JLabel timerValue = new JLabel(); // populated later with appropriate text

    private static final JMultilineLabel previousTimeLabel = new JMultilineLabel(""); // populated later with appropriate text
    private static final JPanel previousTimePanel = new JPanel(new BorderLayout());
    private static TimeTrackingManager timeTrackingManager;

    /**
     * Creates a new dialog and registers it with the {@link TimeTrackingManager}
     */
    public RovasConnectorDialog(final TimeTrackingManager timeTrackingManager) throws MalformedURLException, ApiException.ConnectionFailure {
        super(
                tr("Rovas"),
                "rovas_logo",
                tr("Time tracking with Rovas"),
                null,
                150,
                true,
                null,
                false
        );
        this.timeTrackingManager = timeTrackingManager;

        final VerticallyScrollablePanel panel = new VerticallyScrollablePanel(new GridBagLayout());

        panel.add(new JLabel(tr("Active time")), GBC_LEFT_COLUMN);
        panel.add(timerValue, GBC_RIGHT_COLUMN);

        updatePreviousTime();
        previousTimePanel.add(previousTimeLabel, BorderLayout.CENTER);
        previousTimePanel.setBackground(new Color(0xffcc33));
        previousTimePanel.add(
                GuiComponentFactory.createWrapperPanel(
                        new MultiLineFlowLayout(),
                        new JButton(new HandlePreviouslyTrackedTimeAction(true, tr("Add"))),
                        new JButton(new HandlePreviouslyTrackedTimeAction(false, tr("Discard")))
                ),
                BorderLayout.SOUTH
        );

        panel.add(previousTimePanel, GBC_BOTH_COLUMNS);

        createLayout(
                panel.getVerticalScrollPane(),
                false,
                Collections.singletonList(
                        new SideButton(new ResetTimerAction(timeTrackingManager))
                )
        );

        timeTrackingManager.addAndFireTimeTrackingUpdateListener(this);
    }

    public static void updatePreviousTime(){
        final int previousMinutes = TimeConverterUtil.secondsToMinutes(timeTrackingManager.getPreviouslyTrackedSeconds());
        final String commonMessage = tr(
                // i18n: {0} is the amount of previously tracked time as something like: "0 h 42 m"
                "You have tracked {0} of active time in a previous JOSM session, which was not reported to the server.",
                String.format(
                        "<strong>%d %s %d %s</strong>",
                        previousMinutes / 60,
                        I18nStrings.trShorthandForHours(),
                        previousMinutes % 60,
                        I18nStrings.trShorthandForMinutes()
                )

        );
        Logging.info("Way modified");

        if (previousMinutes > 0) {
            new Notification(
                    commonMessage + "<br>" +
                            tr("If you want to add that time to your current timer, you can do that in the Rovas dialog.")
            )
                    .setDuration(Notification.TIME_LONG)
                    .setIcon(ChangeTrackerPlugin.LOGO.setSize(ImageProvider.ImageSizes.DEFAULT).get())
                    .show();
        }
        previousTimePanel.setVisible(previousMinutes > 0);
        previousTimeLabel.setText(
                commonMessage + "<br>" +
                        tr("Should this time be added to the currently tracked active time, or should the previous time be discarded?")
        );

    }

    @Override
    public void destroy() {
        super.destroy();
        timeTrackingManager.removeTimeTrackingUpdateListener(this);
        try {
            RovasProperties.ALREADY_TRACKED_TIME.put(timeTrackingManager.commit());
        } catch (MalformedURLException | ApiException.ConnectionFailure e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateNumberOfTrackedSeconds(final long n){
        if (n < 0) {
            throw new IllegalArgumentException("Number of tracked seconds must always be non-negative (was" + n + ")!");
        }
        final long minutes = TimeConverterUtil.secondsToMinutes(n);
        GuiHelper.runInEDT(() ->
                timerValue.setText(String.format(
                        "<html><strong style='font-size:1.8em'>%d</strong>&#8239;" +
                                I18nStrings.trShorthandForHours() +
                                "&nbsp;<strong style='font-size:1.8em'>%02d</strong>&#8239;" +
                                I18nStrings.trShorthandForMinutes() +
                                "</html>",
                        minutes / 60,
                        minutes % 60
                ))
        );
    }

    @Override
    public void taggingPresetsModified() {

    }

    /**
     * Action for either adding or discarding time that has previously been tracked, but for which
     * no work report has been submitted yet.
     */
    private final class HandlePreviouslyTrackedTimeAction extends AbstractAction {
        private final boolean shouldBeAdded;

        private HandlePreviouslyTrackedTimeAction(final boolean shouldBeAdded, final String name) {
            super(name);
            this.shouldBeAdded = shouldBeAdded;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            try {
                timeTrackingManager.handlePreviouslyTrackedSeconds(shouldBeAdded);
            } catch (MalformedURLException | ApiException.ConnectionFailure ex) {
                throw new RuntimeException(ex);
            }
            previousTimePanel.setVisible(false);
        }
    }
}
