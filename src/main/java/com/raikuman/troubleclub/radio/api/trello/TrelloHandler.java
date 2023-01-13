package com.raikuman.troubleclub.radio.api.trello;

import com.raikuman.botutilities.configs.EnvLoader;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

/**
 * Uses the Trello api to interact with user workspaces
 *
 * @version 1.0 2023-13-01
 * @since 1.2
 */
public class TrelloHandler {

	/**
	 * Creates a card under the specified list with a label
	 * @param listId The list id to create the card under
	 * @param labelId The label id to apply to the card
	 * @param cardName The title of the card
	 * @param cardDesc The description of the card
	 */
	public static void createCard(String listId, String labelId, String cardName, String cardDesc) {
		// Create card
		HttpResponse<JsonNode> responseCreateCard = Unirest.post("https://api.trello.com/1/cards")
			.header("Accept", "application/json")
			.queryString("idList", listId)
			.queryString("key", EnvLoader.get("trellokey"))
			.queryString("token", EnvLoader.get("trellotoken"))
			.queryString("name", cardName)
			.queryString("desc", cardDesc)
			.asJson();

		// Get card id
		JSONObject createCardJSON = responseCreateCard.getBody().getObject();
		String cardId = createCardJSON.getString("id");

		// Add label to card
		Unirest.post("https://api.trello.com/1/cards/" + cardId +
				"/idLabels")
			.queryString("key", EnvLoader.get("trellokey"))
			.queryString("token", EnvLoader.get("trellotoken"))
			.queryString("value", labelId)
			.asString();
	}
}
