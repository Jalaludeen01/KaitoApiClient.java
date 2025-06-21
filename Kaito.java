import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KaitoApiClient {
    // Actual Kaito API endpoints (from official docs)
    private static final String API_URL = "https://api.kaito.ai/api/v1/search";
    private static final String API_KEY = "ka-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"; // Replace with your actual key

    public static void main(String[] args) {
        String query = "What is the latest research on LLMs?";
        String response = queryKaitoApi(query);
        System.out.println("API Response:\n" + response);
    }

    public static String queryKaitoApi(String query) {
        HttpClient client = HttpClient.newHttpClient();

        // JSON payload structure per Kaito's API spec
        String jsonBody = String.format("""
            {
                "query": "%s",
                "size": 5,
                "search_mode": "hybrid"
            }""", query);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );
            
            // Handle rate limits (Kaito API returns 429)
            if (response.statusCode() == 429) {
                return "Error: Rate limit exceeded - try again later";
            }
            
            return response.body();
        } catch (IOException | InterruptedException e) {
            return "Error: " + e.getMessage();
        }
    }
}
