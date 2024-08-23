package org.example.logging;

import org.openstreetmap.josm.tools.Logging;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Logger extends AbstractAction implements PropertyChangeListener {

//    @Override
//    public void actionPerformed(ActionEvent e) {
//        Relation relation = new Relation();
//        model.applyToPrimitive(relation);
//        new OsmTransferHandler().pasteTags(Collections.singleton(relation));
//        model.updateTags(new TagMap(relation.getKeys()).getTags());
//    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Logging.info("Way modified");
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        Logging.info("Way modified");
    }
}
