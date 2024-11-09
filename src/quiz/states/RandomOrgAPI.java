package quiz.states;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class RandomOrgAPI {

    private static final String API_URL = "https://api.random.org/json-rpc/4/invoke";
    private static final String API_KEY = "980ad555-942f-44ac-8577-4242b61c9151";
    private Queue<Integer> numbers;

    public RandomOrgAPI() {
        sendRequest();
    }

    private void sendRequest() {
        try {
            String requestBody = "{"
                    + "\"jsonrpc\": \"2.0\","
                    + "\"method\": \"generateIntegers\","
                    + "\"params\": {"
                    + "\"apiKey\": \"" + API_KEY + "\","
                    + "\"n\": 10,"                                          // Number of random integers to generate
                    + "\"min\": 0,"                                         // Minimum value of the range
                    + "\"max\": " + (Quiz.getInstance().getQuestions().size()-1) + ","    // Maximum value of the range
                    + "\"replacement\": false"                               // Allow replacement
                    + "},"
                    + "\"id\": 42"                                          // Request ID
                    + "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray jsonArray = jsonResponse.get("result").getAsJsonObject().get("random").getAsJsonObject().get("data").getAsJsonArray();

            if (numbers == null) numbers = new LinkedList<>();
            for (int i = 0; i < jsonArray.size(); i++)
                numbers.add(jsonArray.get(i).getAsInt());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            e.fillInStackTrace();
        }
    }

    public int getNext() {
        if (numbers.size() <= 2) sendRequest();
        return numbers.peek() == null ? new Random().nextInt() % (Quiz.getInstance().getQuestions().size()-1) : numbers.poll();
    }
}
