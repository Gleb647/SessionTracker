package org.example;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;
import org.example.api.ApiException;
import org.example.model.ModifiedUploadAction;
import org.openstreetmap.josm.data.osm.*;
import org.openstreetmap.josm.data.osm.event.*;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.tools.ImageProvider;
import org.example.gui.RovasConnectorDialog;
import org.example.model.AnyOsmDataChangeTracker;
import org.example.model.TimeTrackingManager;
import org.openstreetmap.josm.tools.Logging;

import static org.openstreetmap.josm.gui.MainApplication.getLayerManager;

public class ChangeTrackerPlugin extends Plugin implements DataSetListener, ActionListener {

    public static final ImageProvider LOGO = new ImageProvider("rovas_logo");
    private final TimeTrackingManager timeTrackingManager = new TimeTrackingManager();

    private final List<TodoListItem> todoList = new ArrayList<>();

    /**
     * Creates the plugin
     *
     * @param info the plugin information describing the plugin.
     */
    public ChangeTrackerPlugin(final PluginInformation info) throws MalformedURLException, ApiException.ConnectionFailure {
        super(info);
        AnyOsmDataChangeTracker osmDataChangeTracker = new AnyOsmDataChangeTracker(timeTrackingManager);
        getLayerManager().addAndFireLayerChangeListener(osmDataChangeTracker);
        ModifiedUploadAction modifiedUploadAction = new ModifiedUploadAction(timeTrackingManager);
        modifiedUploadAction.AddHook();
        DatasetEventManager.getInstance().addDatasetListener(this, DatasetEventManager.FireMode.IN_EDT_CONSOLIDATED);
        timeTrackingManager.trackChangeNow();
    }

    @Override
    public void mapFrameInitialized(final MapFrame oldFrame, final MapFrame newFrame) {
        super.mapFrameInitialized(oldFrame, newFrame);
        if (newFrame != null && newFrame.getToggleDialog(RovasConnectorDialog.class) == null) {
            try {
                newFrame.addToggleDialog(new RovasConnectorDialog(timeTrackingManager));
            } catch (MalformedURLException | ApiException.ConnectionFailure e) {
                throw new RuntimeException(e);
            }
        }
    }



    @Override
    public void primitivesAdded(PrimitivesAddedEvent event) {
    }

    Collection<TodoListItem> getItemsForPrimitives(Collection<? extends IPrimitive> primitives) {
        final ArrayList<TodoListItem> items = new ArrayList<>(todoList.size());
        final Map<PrimitiveId, IPrimitive> primitiveMap = new HashMap<>(primitives.size());
        primitives.forEach(primitive -> primitiveMap.put(primitive.getPrimitiveId(), primitive));
        for (var todoListItem : todoList) {
            final var pid = todoListItem.primitive().getPrimitiveId();
            if (primitiveMap.containsKey(pid)
                    && todoListItem.layer().getDataSet().equals(primitiveMap.get(pid).getDataSet())) {
                System.out.println(todoListItem.primitive().getPrimitiveId());
            }
        }
        items.trimToSize();
        return items;
    }

    @Override
    public void primitivesRemoved(PrimitivesRemovedEvent event) {
        System.out.println("Event type " + "primitivesRemoved");
        removeItems(getItemsForPrimitives(event.getPrimitives()));
    }

    @Override
    public void tagsChanged(TagsChangedEvent event) {
        for (OsmPrimitive p : event.getPrimitives()) {
            StringBuilder res = new StringBuilder();
            if (p instanceof Way) {
                res = p.getKeys().getTags().stream().collect(StringBuilder::new, (x, y) -> x.append(y).append("; "),
                                (a, b) -> a.append(",").append(b));
                Logging.info("Way with id=" + p.getKeys().get("id") + " changed: \n" + res);
            }
        }
    }

    @Override
    public void nodeMoved(NodeMovedEvent event) {
    }

    @Override
    public void wayNodesChanged(WayNodesChangedEvent event) {
    }

    @Override
    public void relationMembersChanged(RelationMembersChangedEvent event) {
    }

    @Override
    public void otherDatasetChange(AbstractDatasetChangedEvent event) {
    }

    @Override
    public void dataChanged(DataChangedEvent event) {
        final Collection<OsmPrimitive> changedPrimitives;
        final var changeEvents = event.getEvents();
        if (changeEvents != null) {
            changedPrimitives = new HashSet<>();
            for (var e : changeEvents) {
                System.out.println(e.getType());
                if (e instanceof TagsChangedEvent tagsChangedEvent) {
                    tagsChanged(tagsChangedEvent);
                }
                changedPrimitives.addAll(e.getPrimitives());
            }
        } else {
            changedPrimitives = event.getPrimitives();
        }
        getItemsForPrimitives(changedPrimitives);
    }

    @Override
    public void dataChangedIndividualEvents(DataChangedEvent event) {
        DataSetListener.super.dataChangedIndividualEvents(event);
    }

    void removeItems(Collection<TodoListItem> items) {
        if (items == null || items.isEmpty())
            return;
        todoList.removeAll(items);
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
    }
}
