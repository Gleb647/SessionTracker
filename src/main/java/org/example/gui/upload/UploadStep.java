package org.example.gui.upload;

import java.awt.Window;
import java.util.Optional;

import com.drew.lang.annotations.NotNull;

import org.example.model.TimeTrackingManager;
import org.example.util.UrlProvider;

/**
 * Contains the common parts of the three upload steps that make the API queries.
 */
public interface UploadStep {

    /**
     * Starts the upload step. The message will block until the step is done.
     */
    void showStep(
            @NotNull final Optional<Window> parent,
            @NotNull final UrlProvider urlProvider,
            @NotNull final TimeTrackingManager timeTrackingManager
    );
}
