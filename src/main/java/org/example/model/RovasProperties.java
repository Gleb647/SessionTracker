package org.example.model;

import com.drew.lang.annotations.NotNull;

import org.openstreetmap.josm.data.preferences.BooleanProperty;
import org.openstreetmap.josm.data.preferences.IntegerProperty;
import org.openstreetmap.josm.data.preferences.LongProperty;
import org.openstreetmap.josm.data.preferences.StringProperty;

import org.example.gui.ApiCredentialsPanel;
import org.example.util.NullableProperty;

/**
 * The available (user-configurable) properties
 */
public final class RovasProperties {
    /**
     * When a value smaller than {@link ApiCredentials#MIN_PROJECT_ID} is set for {@link #ACTIVE_PROJECT_ID},
     * then the value is replaced by this value, which is equivalent to no value being set.
     */
    public static final int ACTIVE_PROJECT_ID_NO_VALUE = -1;
    /**
     * The Rovas project ID for which the JOSM user is submitting their work report.
     */
    public static final IntegerProperty ACTIVE_PROJECT_ID =
            new IntegerProperty("rovas.active-project-id", StaticConfig.ROVAS_OSM_PROJECT_ID);

    /**
     * The minimum value allowed for {@link #INACTIVITY_TOLERANCE}, smaller values are treated as if this value was set.
     */
    public static final int INACTIVITY_TOLERANCE_MIN_VALUE = 1;
    /**
     * The default value for {@link #INACTIVITY_TOLERANCE}.
     */
    public static final int INACTIVITY_TOLERANCE = 300;
    /**
     * The maximum value allowed for {@link #INACTIVITY_TOLERANCE}, larger values are treated as if this value was set.
     */
    public static final int INACTIVITY_TOLERANCE_MAX_VALUE = 6000;

    /**
     * The number of seconds of inactivity after each edit, which should still be counted.
     *
     * For a value of 30 this means: If you make only one edit, automatically 30 seconds are counted.
     * If you make one edit and after 15 seconds another one, then 45 seconds are counted, because the clock is
     * stopped 30 seconds after the second edit.
     * If you make one edit and another one after 60 seconds, then 60 seconds are counted, because 30 seconds
     * after the first edit the clock is stopped and also 30 seconds after the second edit.
     */
    //public static final IntegerProperty INACTIVITY_TOLERANCE =
            //new IntegerProperty("rovas.inactivity-tolerance-seconds", INACTIVITY_TOLERANCE_DEFAULT_VALUE);

    /**
     * This property persists the time that was already tracked across restarts
     */
    public static final LongProperty ALREADY_TRACKED_TIME = new LongProperty("rovas.already-tracked-time", 0);

    public static final BooleanProperty DEVELOPER = new BooleanProperty("rovas.developer", false);

    private RovasProperties() {
        // private constructor to avoid instantiation
    }
}
