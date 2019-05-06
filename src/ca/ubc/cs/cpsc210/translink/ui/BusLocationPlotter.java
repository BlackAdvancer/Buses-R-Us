package ca.ubc.cs.cpsc210.translink.ui;

import android.content.Context;
import ca.ubc.cs.cpsc210.translink.R;
import ca.ubc.cs.cpsc210.translink.model.Bus;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.parsers.BusParser;
import ca.ubc.cs.cpsc210.translink.providers.HttpArrivalDataProvider;
import ca.ubc.cs.cpsc210.translink.providers.HttpBusLocationDataProvider;
import org.json.JSONException;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.IOException;
import java.util.ArrayList;

import static ca.ubc.cs.cpsc210.translink.util.Geometry.gpFromLL;

// A plotter for bus locations
public class BusLocationPlotter extends MapViewOverlay {
    /** overlay used to display bus locations */
    private ItemizedIconOverlay<OverlayItem> busLocationsOverlay;

    /**
     * Constructor
     * @param context  the application context
     * @param mapView  the map view
     */
    public BusLocationPlotter(Context context, MapView mapView) {
        super(context, mapView);
        busLocationsOverlay = createBusLocnOverlay();
    }

    public ItemizedIconOverlay<OverlayItem> getBusLocationsOverlay() {
        return busLocationsOverlay;
    }

    /**
     * Plot buses serving selected stop
     */
    public void plotBuses() {
        busLocationsOverlay.removeAllItems();
        Stop selectedStop = StopManager.getInstance().getSelected();
        if (selectedStop != null) {
            HttpBusLocationDataProvider httpBusLocationDataProvider = new HttpBusLocationDataProvider(selectedStop);
            try {
                BusParser.parseBuses(selectedStop, httpBusLocationDataProvider.dataSourceToString());
            } catch (Exception e) {

            }
            for (Bus b : selectedStop.getBuses()) {
                OverlayItem bus = new OverlayItem(b.getRoute().getNumber(), b.getDestination(), gpFromLL(b.getLatLon()));
                busLocationsOverlay.addItem(bus);
            }
        }
    }

    /**
     * Create the overlay for bus markers.
     */
    private ItemizedIconOverlay<OverlayItem> createBusLocnOverlay() {
        ResourceProxy rp = new DefaultResourceProxyImpl(context);

        return new ItemizedIconOverlay<OverlayItem>(
                new ArrayList<OverlayItem>(),
                context.getResources().getDrawable(R.drawable.bus),
                null, rp);
    }
}
