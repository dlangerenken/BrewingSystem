/*
 * 
 */
package se.brewingsystem.android.utilities;

import android.content.Context;
import android.content.Intent;

import com.jjoe64.graphview.CustomLabelFormatter;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Style;
import se.brewingsystem.android.R;


/**
 * Created by Daniel on 30.11.2014.
 */
public class CommonUtilities {

    /**
     * Converts seconds to string-output
     * @param seconds
     * @return
     */
    public static String convertSecondsToHMmSs(long seconds) {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h,m,s);
    }

    /**
     * Tag used on log messages.
     */
    public static final String TAG = "brewing";

    /** Expression for validating the url or ip-adress (ip v4/v6, dez/hex possible). */
    private static final String URL_AND_IP_REG_EX = "^((([hH][tT][tT][pP][sS]?|[fF][tT][pP])\\:\\/\\/)?([\\w\\.\\-]+(\\:[\\w\\.\\&%\\$\\-]+)*@)?"
            + "((([^\\s\\(\\)\\<\\>\\\\\\\"\\.\\[\\]\\,@;:]+)(\\.[^\\s\\(\\)\\<\\>\\\\\\\"\\.\\[\\]\\,@;:]+)*"
            + "(\\.[a-zA-Z]{2,4}))|((([01]?\\d{1,2}|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d{1,2}|2[0-4]\\d|25[0-5])))"
            + "(\\b\\:(6553[0-5]|655[0-2]\\d|65[0-4]\\d{2}|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)\\b)?((\\/[^\\/]"
            + "[\\w\\.\\,\\?\\'\\\\\\/\\+&%\\$#\\=~_\\-@]*)*[^\\.\\,\\?\\\"\\'\\(\\)\\[\\]!;<>{}\\s\\x7F-\\xFF])?)$";

    /** Base URL of the Server. */
    public static final String SERVER_URL = "http://192.168.0.13:1337";

    /**
     * Google API project id registered to use GCM.
     * previously lyrical-caster-355
     */
    public static final String SENDER_ID = "939174281019";


    /**
     * Intent used to display a message in the screen.
     */
    private static final String DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    private static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p/>
     * This method is defined in the common helper because it's used both by the
     * UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    /**
     * Validates if the server-adress is correct (ipv4 || ipv6 || url).
     *
     * @param serverUrl the server url
     * @return true, if is valid url
     */
    public static boolean isValidUrl(String serverUrl) {
        return serverUrl.matches(URL_AND_IP_REG_EX);
    }

    /**
     * Gets the success style.
     *
     * @param context the context
     * @return the success style
     */
    public static Style getSuccessStyle(Context context) {
        return new Style.Builder()
                .setBackgroundColorValue(context.getResources().getColor(R.color.app_color_second_alt))
                .setTextColorValue(context.getResources().getColor(R.color.app_color_text))
                .build();
    }

    /**
     * Returns the temperature-graph by a graphview-series and the context with default colors
     * @param context context for receiving colours
     * @param series series to show
     * @return line-graph-view to display
     */
    public static LineGraphView getTemperatureGraph(final Context context, final GraphViewSeries series){
        series.getStyle().color = context.getResources().getColor(R.color.app_color_second_alt);
        LineGraphView graphView = new LineGraphView(
                context,
                context.getResources().getString(R.string.temperatur_verlauf_graph_header_text));
        graphView.setManualYAxisBounds(100, 0);
        graphView.addSeries(series);
        graphView.setScalable(true);
        graphView.setCustomLabelFormatter(new CustomLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return new SimpleDateFormat(context.getString(R.string.hh_mm_format)).format(new Date((long) value));
                }
                return String.format(context.getString(R.string.degree_format),
                        new DecimalFormat(context.getString(R.string.degree_decimal_format)).format(value));
            }
        });
        return graphView;
    }

}
