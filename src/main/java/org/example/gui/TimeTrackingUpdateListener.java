package org.example.gui;

import org.example.api.ApiException;
import org.example.model.TimeTrackingManager;
import org.openstreetmap.josm.tools.HttpClient;

import java.net.MalformedURLException;

/**
 * This listener is used to notify other components about changes in {@link TimeTrackingManager}.
 * E.g. {@link RovasConnectorDialog} implements this interface.
 */
public interface TimeTrackingUpdateListener {
    /**
     * This is called each time an update occur
     * @param n the total number of seconds that have been tracked so far
     */
    void updateNumberOfTrackedSeconds(final long n) throws MalformedURLException, ApiException.ConnectionFailure ;
}
