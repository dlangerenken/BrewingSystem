/*
 *
 */
package general;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import messages.BrewingAbortedMessage;
import messages.BrewingCompleteMessage;
import messages.Message;

/**
 * This class saves a possible protocol and messages which were received during
 * the brewing process.
 */
public class BrewingLog {

	/** the recipe that was brewed */
	private final Recipe recipe;

	/**format for time stamps*/
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"HH:mm:ss, dd.MM.yyyy");

	/** Protocol which can be created through this log-file. */
	private Protocol protocol;

	/** List of messages which were received during the brewing process. */
	private List<Message> messages;

	/** the unique id of this log */
	private int id;

	/**
	 * Initiates the log and creates a new list of messages.
	 */
	public BrewingLog(final Recipe recipe, final int id) {
		this.id = id;
		this.recipe = recipe;
		messages = new CopyOnWriteArrayList<Message>();
	}
	
	/**sets the id */
	public void setId(final int id) {
		this.id = id;
	}

	/**
	 * gets the recipe that was brewed
	 */
	public Recipe getRecipe() {
		return recipe;
	}

	/**
	 * Returns every message which was added within the brewing process.
	 *
	 * @return list of messages from the brewing process
	 */
	public List<Message> getMessages() {
		return messages;
	}

	/**
	 * Returns the time of the last entry in the log
	 *
	 * @return the time, or null if no messages have been written yet
	 */
	public Long getLatestTime() {
		if (messages == null || messages.isEmpty()) {
			return null;
		}
		return messages.get(messages.size() - 1).getTime();
	}

	/**
	 * Returns the time of the first entry in the log
	 *
	 * @return the time, or null if no messages have been written yet
	 */
	public Long getStartTime() {
		if (messages == null || messages.isEmpty()) {
			return null;
		}
		return messages.get(0).getTime();
	}

	/**
	 * creates a Protocol of this log
	 *
	 * @return protocol
	 */
	public Protocol getProtocol() {
		return new Protocol(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ ((messages == null) ? 0 : messages.hashCode());
		result = prime * result
				+ ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((recipe == null) ? 0 : recipe.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BrewingLog other = (BrewingLog) obj;
		if (id != other.id) {
			return false;
		}
		if (messages == null) {
			if (other.messages != null) {
				return false;
			}
		} else if (!messages.equals(other.messages)) {
			return false;
		}
		if (protocol == null) {
			if (other.protocol != null) {
				return false;
			}
		} else if (!protocol.equals(other.protocol)) {
			return false;
		}
		if (recipe == null) {
			if (other.recipe != null) {
				return false;
			}
		} else if (!recipe.equals(other.recipe)) {
			return false;
		}
		return true;
	}

	/**
	 * Sets messages for the brewing log - usually not required (only for
	 * mocks/json).
	 *
	 * @param messages
	 *            list of messages which should be added to the log
	 */
	public void setMessages(final List<Message> messages) {
		this.messages = messages;
	}

	/**
	 * Sets the protocol.
	 *
	 * @param protocol
	 */
	public void setProtocol(final Protocol protocol) {
		this.protocol = protocol;
	}

	/** Gets the id of this log */
	public int getId() {
		return id;
	}

	/**
	 * Gets the summary, containing recipe id and name, start time, and a
	 * description.
	 *
	 * @return the summary
	 */
	public LogSummary getSummary() {
		String recipeName = recipe.getName();
		boolean completed = false;
		Long endTime = -1L;
		if(messages != null) {
			for(Message m: messages) {
				if(m instanceof BrewingAbortedMessage || m instanceof BrewingCompleteMessage) {
					completed = (m instanceof BrewingCompleteMessage);
					endTime = m.getTime();
				}
			}
		}
		
		String description = "Rezept \"" + recipeName + "\", Brauprozess " + (getStartTime() != null ? "gestartet um " + dateFormat.format(new Date(
				getStartTime())) : "nicht gestartet") + ". ";
		description += (completed? "Fertiggestellt um " : "Abgebrochen um ") + dateFormat.format(endTime) + ".";
		
		String title = "Rezept \""
				+ recipe.getName() + "\", Prozess " + (completed? "fertiggestellt." : "abgebrochen.");
	
		return new LogSummary(title, description,
				(getLatestTime() == null ? 0 : getLatestTime()),
				id,
				(recipe == null ? null :recipe.getId()));
	}

	/** Adds a message to the log */
	public boolean log(final Message message) {
		if (messages == null) {
			messages = new CopyOnWriteArrayList<Message>();
		}
		return messages.add(message);
	}

}