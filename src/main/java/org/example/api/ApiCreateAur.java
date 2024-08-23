package org.example.api;

import java.net.URLConnection;
import java.util.Locale;
import java.util.Optional;
import javax.json.Json;

import org.openstreetmap.josm.tools.I18n;

import org.example.model.StaticConfig;
import org.example.model.ApiCredentials;
import org.example.util.TimeConverterUtil;
import org.example.util.UrlProvider;

/**
 * The API query that creates the Asset Usage Record (AUR) for the work report that has been created.
 */
public final class ApiCreateAur extends ApiQuery<ApiQuery.ErrorCode> {
    private final int workReportId;
    private final int reportedMinutes;

    /**
     * Create the query
     * @param urlProvider the {@link UrlProvider} from which we can obtain the API URL
     * @param workReportId the ID of the work report for which we create the AUR
     * @param reportedMinutes the number of minutes that were reported for the work report (for this amount we'll calculate the appropriate fee)
     */
    public ApiCreateAur(final UrlProvider urlProvider, final int workReportId, final int reportedMinutes) {
        super(urlProvider, urlProvider.rulesCreateAUR());
        this.workReportId = workReportId;
        this.reportedMinutes = reportedMinutes;
    }

    @Override
    protected ErrorCode[] getKnownErrorCodes() {
        return new ErrorCode[0];
    }

    @Override
    protected ErrorCode createAdditionalErrorCode(final Optional<Integer> code, final String translatableMessage) {
        return new ErrorCode(code, translatableMessage);
    }

}
