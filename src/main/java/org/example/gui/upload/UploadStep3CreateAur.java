//package org.example.gui.upload;
//
//import java.awt.Window;
//import java.net.MalformedURLException;
//import java.util.Optional;
//import javax.swing.JOptionPane;
//
//import com.drew.lang.annotations.NotNull;
//
//import org.example.api.ApiException;
//import org.openstreetmap.josm.gui.Notification;
//import org.openstreetmap.josm.gui.widgets.HtmlPanel;
//import org.openstreetmap.josm.tools.I18n;
//import org.openstreetmap.josm.tools.ImageProvider;
//
//import org.example.api.ApiCreateAur;
//import org.example.model.ApiCredentials;
//import org.example.model.TimeTrackingManager;
//import org.example.util.UrlProvider;
//
///**
// * The third and last upload step, which creates the Asset Usage record (AUR) that accompanies the work report.
// */
//public class UploadStep3CreateAur implements UploadStep {
//
//    private final ApiCredentials credentials;
//    private final int workReportId;
//    private final int reportedMinutes;
//
//    /**
//     * Creates the third upload step for creating the AUR
//     * @param credentials the credentials used to authenticate with the API server
//     * @param workReportId the ID of the work report for which an AUR should be created
//     * @param reportedMinutes the number of minutes that were reported with the work report
//     */
//    public UploadStep3CreateAur(final ApiCredentials credentials, final int workReportId, final int reportedMinutes) {
//        this.credentials = credentials;
//        this.workReportId = workReportId;
//        this.reportedMinutes = reportedMinutes;
//    }
//
//    @Override
//    public void showStep(
//            @NotNull final Optional<Window> parent,
//            @NotNull final UrlProvider urlProvider,
//            @NotNull final TimeTrackingManager timeTrackingManager
//    ) throws ApiException {
//        timeTrackingManager.setCurrentlyTrackedSeconds(0);
//        parent.ifPresent(Window::dispose);
//        new ApiCreateAur(urlProvider, workReportId, reportedMinutes).query(
//                credentials,
//                result -> {
//                    final HtmlPanel panel = new HtmlPanel(I18n.tr("Your {0} was created successfully!", UrlProvider.toHtmlHyperlink(UrlProvider.getInstance().node(workReportId), I18n.tr("work report"))));
//                    panel.setOpaque(false);
//                    panel.enableClickableHyperlinks();
//                    new Notification()
//                            .setContent(panel)
//                            .setIcon(ImageProvider.get("misc", "check_large"))
//                            .setDuration(Notification.TIME_LONG)
//                            .show();
//                },
//                errorCode -> JOptionPane.showMessageDialog(parent.orElse(null), I18n.tr("Failed to create AUR!"))
//        );
//    }
//}
//
