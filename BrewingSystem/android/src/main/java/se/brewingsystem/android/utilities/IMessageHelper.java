package se.brewingsystem.android.utilities;

import android.util.Pair;

import general.BrewingState;
import messages.Message;

/**
 * Created by Daniel on 24.12.2014.
 */
public interface IMessageHelper {
    String getTextFromBrewingState(BrewingState state);
    Pair<String, String> getTextFromMessage(Message message);
}
