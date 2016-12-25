package se.brewingsystem.android.utilities;

import android.content.Context;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import general.HopAddition;
import general.IngredientAddition;
import general.MaltAddition;
import general.TemperatureLevel;
import messages.BrewingAbortedMessage;
import messages.BrewingCompleteMessage;
import messages.BrewingStartMessage;
import messages.ConfirmationMessage;
import messages.EndMessage;
import messages.HopAdditionMessage;
import messages.MaltAdditionMessage;
import messages.StartMessage;
import roboguice.util.Strings;
import se.brewingsystem.android.R;
import general.BrewingState;
import messages.ConfirmationRequestMessage;
import messages.IodineTestMessage;
import messages.ManualStepMessage;
import messages.MashingMessage;
import messages.Message;
import messages.PreNotificationMessage;
import messages.TemperatureLevelMessage;
import messages.TemperatureMessage;

/**
 * MessagesHelper which offers toString-Methods for messages and brewing states
 */
public class MessageHelper implements IMessageHelper {

    /**
     * BrewingStateMap for receiving messages
     */
    private Map<BrewingState, String> brewingStateStringMap;

    /**
     * Context for retrieving strings from resources
     */
    private final Context context;

    /**
     * Instantiates the MessageHelper with the help of the global context
     * @param context
     */
    @Inject
    public MessageHelper(Context context) {
        this.context = context;
        init();
    }

    /**
     * Instantiates the brewing state string map which is required for later message-tasks
     */
    private void init() {
        brewingStateStringMap = new HashMap<>();
        /**
         * 1XX
         */
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.NOT_STARTED, BrewingState.Position.START), context.getString(R.string.not_started_normal_start_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.NOT_STARTED, BrewingState.Position.ONGOING), context.getString(R.string.not_started_normal_on_going_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.NOT_STARTED, BrewingState.Position.END), context.getString(R.string.not_started_normal_end_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.NOT_STARTED, BrewingState.Position.START), context.getString(R.string.not_started_request_start_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.NOT_STARTED, BrewingState.Position.ONGOING), context.getString(R.string.not_started_request_on_going_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.NOT_STARTED, BrewingState.Position.END), context.getString(R.string.not_started_request_end_string));

        /**
         * 2XX
         */
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.MASHING, BrewingState.Position.START), context.getString(R.string.mashing_normal_start_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.MASHING, BrewingState.Position.ONGOING), context.getString(R.string.mashing_normal_on_going_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.MASHING, BrewingState.Position.END), context.getString(R.string.mashing_normal_end_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.MASHING, BrewingState.Position.START), context.getString(R.string.mashing_request_start_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.MASHING, BrewingState.Position.ONGOING), context.getString(R.string.mashing_request_on_going_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.MASHING, BrewingState.Position.END), context.getString(R.string.mashing_request_end_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.MASHING, BrewingState.Position.IODINE), context.getString(R.string.mashing_request_iodine_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.MASHING, BrewingState.Position.IODINE), context.getString(R.string.mashing_normal_iodine_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.MASHING, BrewingState.Position.ADDING), context.getString(R.string.mashing_request_adding_string));

        /**
         * 3XX
         */
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.LAUTERING, BrewingState.Position.START), context.getString(R.string.lautering_normal_start_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.LAUTERING, BrewingState.Position.ONGOING), context.getString(R.string.lautering_normal_on_going_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.LAUTERING, BrewingState.Position.END), context.getString(R.string.lautering_normal_end_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.LAUTERING, BrewingState.Position.START), context.getString(R.string.lautering_request_start_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.LAUTERING, BrewingState.Position.ONGOING), context.getString(R.string.lautering_request_on_going_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.LAUTERING, BrewingState.Position.END), "Läutern abgeschlossen. Bestätigen zum Fortfahren");

        /**
         * 4XX
         */
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.HOP_COOKING, BrewingState.Position.START), context.getString(R.string.hop_cooking_normal_start_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.HOP_COOKING, BrewingState.Position.ONGOING), context.getString(R.string.hop_cooking_normal_on_going_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.HOP_COOKING, BrewingState.Position.END), context.getString(R.string.hop_cooking_normal_end_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.HOP_COOKING, BrewingState.Position.START), context.getString(R.string.hop_cooking_request_start_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.HOP_COOKING, BrewingState.Position.ONGOING), context.getString(R.string.hop_cooking_request_on_going_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.HOP_COOKING, BrewingState.Position.END), context.getString(R.string.hop_cooking_request_end_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.HOP_COOKING, BrewingState.Position.ADDING), context.getString(R.string.hop_cooking_request_adding_string));

        /**
         * 5XX
         */
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.WHIRLPOOL, BrewingState.Position.START), context.getString(R.string.whirlpool_normal_start_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.WHIRLPOOL, BrewingState.Position.ONGOING), context.getString(R.string.whirlpool_normal_on_going_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.WHIRLPOOL, BrewingState.Position.END), context.getString(R.string.whirlpool_normal_end_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.WHIRLPOOL, BrewingState.Position.START), context.getString(R.string.whirlpool_request_start_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.WHIRLPOOL, BrewingState.Position.ONGOING), context.getString(R.string.whirlpool_request_on_going_string));
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.WHIRLPOOL, BrewingState.Position.END), context.getString(R.string.whirlpool_request_end_string));

        /**
         * 6XX
         */
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.FINISHED, BrewingState.Position.START), "Das Ende des Brauvorgangs wird vorbereitet");
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.FINISHED, BrewingState.Position.ONGOING), "Der Brauvorgang wird abgeschlossen");
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.NORMAL, BrewingState.State.FINISHED, BrewingState.Position.END), "Brauvorgang abgeschlossen");
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.FINISHED, BrewingState.Position.START), "Das Abschließen des Brauvorgangs muss bestätigt werden");
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.FINISHED, BrewingState.Position.ONGOING), "Das Beenden des Brauvorgangs muss bestätigt werden");
        brewingStateStringMap.put(new BrewingState(BrewingState.Type.REQUEST, BrewingState.State.FINISHED, BrewingState.Position.END), "Brauvorgang abgeschlossen. Bestätigen zum Fortfahren");
    }

    @Override
    public String getTextFromBrewingState(BrewingState state) {
        if (state == null || brewingStateStringMap == null) {
            return "";
        }
        String text = brewingStateStringMap.get(BrewingState.fromValue(state.toValue()));
        if (text == null) {
            return state.toValue() + "";
        }
        if (state.getPosition().equals(BrewingState.Position.ADDING)) {
            List<IngredientAddition> additions = new ArrayList<>();
            if (state.getData() instanceof IngredientAddition) {
                additions.add((IngredientAddition) state.getData());
            } else if (state.getData() instanceof List<?>) {
                if (state.getState().equals(BrewingState.State.HOP_COOKING)) {
                    List<HopAddition> hopAdditions = (List<HopAddition>) state.getData();
                    additions.addAll(hopAdditions);
                } else if (state.getState().equals(BrewingState.State.MASHING)) {
                    List<MaltAddition> maltAdditions = (List<MaltAddition>) state.getData();
                    additions.addAll(maltAdditions);
                }
            }
            StringBuilder ingredientsStringBuilder = new StringBuilder();
            for (IngredientAddition ingredient : additions) {
                ingredientsStringBuilder.append(String.format("%s (%.2f%s)", ingredient.getName(), ingredient.getAmount(), ingredient.getUnit().name()));
            }
            return text + ingredientsStringBuilder.toString();
        }
        return text;
    }

    @Override
    public Pair<String, String> getTextFromMessage(Message message) {
            if (message == null) {
                return null;
            }
            if (message instanceof BrewingStartMessage) {
                BrewingStartMessage brewingStartMessage = (BrewingStartMessage) message;
                return new Pair<>("Brauvorgang gestartet", String.format("Brauen von %s gestartet", brewingStartMessage.getRecipeName()));
            }
            if (message instanceof BrewingAbortedMessage) {
                BrewingAbortedMessage brewingAbortedMessage = (BrewingAbortedMessage) message;
                return new Pair<>("Brauvorgang abgebrochen", brewingAbortedMessage.getReason());
            }
            if (message instanceof BrewingCompleteMessage) {
                BrewingCompleteMessage brewingCompleteMessage = (BrewingCompleteMessage) message;
                return new Pair<>("Brauvorgang abgeschlossen", String.format("Brauen von %s beendet", brewingCompleteMessage
                        .getRecipeName()));
            }
            if (message instanceof HopAdditionMessage) {
                HopAdditionMessage hopAdditionMessage = (HopAdditionMessage) message;
                return new Pair<>(context.getString(R.string.hop_cooking_message_string), String.format("Hopfen hinzugefügt: %s", getListOfIngrediends(hopAdditionMessage.getHopAddition())));
            }
            if (message instanceof ConfirmationRequestMessage) {
                ConfirmationRequestMessage confirmationRequestMessage = (ConfirmationRequestMessage) message;
                return new Pair<>(context.getString(R.string.confirmation_request_message_string),
                        getTextFromBrewingState(confirmationRequestMessage.getBrewingStep()));
            }

            if (message instanceof MaltAdditionMessage) {
                MaltAdditionMessage maltAdditionMessage = (MaltAdditionMessage) message;
                return new Pair<>(context.getString(R.string.ingredient_addition_message_string), String.format("Malz hinzugefügt %s", getListOfIngrediends(maltAdditionMessage.getMaltAdditions())));
            }
            if (message instanceof IodineTestMessage) {
                IodineTestMessage iodineTestMessage = (IodineTestMessage) message;
                boolean iodineTestPositive = iodineTestMessage.getIodineTest().isPositive();
                if (iodineTestPositive) {
                    return new Pair<>(context.getString(R.string.iodine_test_message_string), "Iodintest erfolgreich abgeschlossen");
                }
                return new Pair<>(context.getString(R.string.iodine_test_message_string), String.format("Iodintest in %d Sekunden erneut ausführen", iodineTestMessage.getIodineTest().getWaitingPeriod()));
            }
            if (message instanceof ManualStepMessage) {
                ManualStepMessage manualStepMessage = (ManualStepMessage) message;
                return new Pair<>(context.getString(R.string.manual_step_message_string), manualStepMessage.getManualStep().getDescription());
            }
            if (message instanceof PreNotificationMessage) {
                PreNotificationMessage preNotificationMessage = (PreNotificationMessage) message;
                Message subMessage = preNotificationMessage.getContent();
                String text = "";
                if (subMessage instanceof HopAdditionMessage) {
                    text = getListOfIngrediends(((HopAdditionMessage) subMessage).getHopAddition());
                } else if (subMessage instanceof MaltAdditionMessage) {
                    text = getListOfIngrediends(((MaltAdditionMessage) subMessage).getMaltAdditions());
                }
                return new Pair<>(context.getString(R.string.pre_notification_message_string), String.format("Bitte %s in %d Minuten hinzufügen", text, preNotificationMessage.getMillisToNotification() / 1000 / 60));
            }
            if (message instanceof TemperatureLevelMessage) {
                TemperatureLevelMessage temperatureLevelMessage = (TemperatureLevelMessage) message;
                return new Pair<>(context.getString(R.string.temperature_level_message_string), String.format("Raste %.2f°C, %d Minuten", temperatureLevelMessage.getTemperatureLevel().getTemperature(), temperatureLevelMessage.getTemperatureLevel().getDuration() / 1000 / 60));
            }
            if (message instanceof TemperatureMessage) {
                TemperatureMessage temperatureMessage = (TemperatureMessage) message;
                return new Pair<>(context.getString(R.string.temperature_message_string), String.format("Temperatur erreicht: %.2f°C", temperatureMessage.getTemperature()));
            }
            if (message instanceof MashingMessage) {
                MashingMessage mashingMessage = (MashingMessage) message;
                return new Pair<>(context.getString(R.string.mashing_message_string), message.getMessage());
            }
            if (message instanceof ConfirmationMessage) {
                ConfirmationMessage conf = (ConfirmationMessage) message;
                return new Pair<>("Vorgang bestätigt", getTextFromBrewingState(conf.getConfirmedState()));
            }
            if (message instanceof EndMessage){
                EndMessage end = (EndMessage) message;
                String desc = String.format("%s wurde beendet", getTitleFromPosition(end.getPosition()));
                return new Pair<>("Ende des Brauschrittes", desc);
            }
            if (message instanceof StartMessage){
                StartMessage start = (StartMessage) message;
                String desc = String.format("%s wurde gestartet", getTitleFromPosition(start.getPosition()));
                return new Pair<>("Start des Brauschrittes", desc);
            }
            return new Pair<>(message.getClass().getSimpleName(), message.getMessage());
        }

    private String getTitleFromPosition(BrewingState.State position){
        switch (position){
            case HOP_COOKING:
                return "Hopfenkochen";
            case LAUTERING:
                return "Läutern";
            case MASHING:
                return "Maischen";
            case WHIRLPOOL:
                return "Whirlpool";
        }
        return "";
    }

    /**
     * Returns the list of ingredients with amount and uni seperated by comma
     * @param ingredients list of ingredients
     * @return
     */
    public static String getListOfIngrediends(Collection<? extends IngredientAddition> ingredients) {
        List<String> additions = new ArrayList<>();
        if (ingredients != null) {
            for (IngredientAddition ingredient : ingredients) {
                additions.add(String.format("%s (%.0f%s)", ingredient.getName(), ingredient.getAmount(), ingredient.getUnit().name()));
            }
        }
        return Strings.join( ", ", additions);
    }

    /**
     * Gets the temperature level review item.
     *
     * @param list the list
     * @return the temperature level review item
     */
    public static String getTemperatureLevelReviewItem(List<TemperatureLevel> list) {
        List<String> tempStrings = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (TemperatureLevel temperatureLevel : list) {
                tempStrings.add(temperatureLevel.getTemperature() + "°");
            }
        }
        return Strings.join( ",", tempStrings);
    }
}
