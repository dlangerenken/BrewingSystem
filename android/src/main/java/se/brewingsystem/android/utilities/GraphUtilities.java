/*
 * 
 */
package se.brewingsystem.android.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import messages.Message;
import messages.TemperatureMessage;


/**
 * Created by Daniel on 18.12.2014.
 */
public class GraphUtilities {

    /**
     * Gets the graph model.
     *
     * @param messages the messages
     * @return the graph model
     */
    public static TemperatureGraphModel[] getGraphModel(List<Message> messages){
        List<TemperatureGraphModel> model = new ArrayList<>();
        for (Message message : messages) {
            if (message instanceof TemperatureMessage) {
                TemperatureMessage tempMessage = (TemperatureMessage) message;
                model.add(new TemperatureGraphModel(tempMessage));
            }
        }
        Collections.sort(model); // newest at the end
        return model.toArray(new TemperatureGraphModel[model.size()]);
    }
}
