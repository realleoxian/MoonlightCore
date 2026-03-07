package de.leoxian.moonlightcore.impl.internal;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class ModLoggingMarkers {
    public static final Marker API_LOOKUP = MarkerFactory.getMarker("moonlightcore:api-lookup-api");
    public static final Marker NETWORK = MarkerFactory.getMarker("moonlightcore:network-api");
    public static final Marker ATTACHMENT = MarkerFactory.getMarker("moonlightcore:attachment-api");
    public static final Marker EVENT = MarkerFactory.getMarker("moonlightcore:event-api");

    private ModLoggingMarkers() {}
}
