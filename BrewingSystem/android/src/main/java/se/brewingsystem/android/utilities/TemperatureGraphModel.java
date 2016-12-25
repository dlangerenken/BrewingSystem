/*
 * 
 */
package se.brewingsystem.android.utilities;

import com.jjoe64.graphview.GraphViewDataInterface;

import messages.TemperatureMessage;


/**
 * Created by Daniel on 18.12.2014.
 */
public class TemperatureGraphModel  implements GraphViewDataInterface, Comparable<TemperatureGraphModel> {
    
    /** The m message. */
    private final TemperatureMessage mMessage;

    /**
     * Instantiates a new temperature graph model.
     *
     * @param message the message
     */
    public TemperatureGraphModel(TemperatureMessage message){
        mMessage = message;
    }
    
    /**
     * Gets the x.
     *
     * @return the x
     */
    @Override
    public double getX() {
        return mMessage.getTime();
    }

    /**
     * Gets the y.
     *
     * @return the y
     */
    @Override
    public double getY() {
        return mMessage.getTemperature();
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public TemperatureMessage getMessage(){
        return mMessage;
    }

    /**
     * Compare to.
     *
     * @param another the another
     * @return the int
     */
    @Override
    public int compareTo(TemperatureGraphModel another) {
        return Double.compare(getX(), another.getX());
    }
}
