package com.shiftytplaylists;

import com.google.gson.*;
import java.awt.Desktop;
import java.io.IOException;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SpotifyService {
    private static final String CLIENT_ID = System.getenv("SPOTIFY_CLIENT_ID");
    private static final String CLIENT_SECRET = System.getenv("SPOTIFY_CLIENT_SECRET");
    private static final String REDIRECT_URI = "http://127.0.0.1:8888/callback";
    private static final String AUTH_URL = "https://accounts.spotify.com/authorize";
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String API_URL = "https://api.spotify.com/v1";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static String authenticateAndGetToken() throws Exception {
        String state = UUID.randomUUID().toString();
        String scope = URLEncoder.encode("playlist-modify-public playlist-modify-private", StandardCharsets.UTF_8);
        String authUrl = AUTH_URL + "?client_id=" + CLIENT_ID +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8) +
                "&scope=" + scope +
                "&state=" + state;
        Desktop.getDesktop().browse(new URI(authUrl));
        // Listen for redirect
        String code = listenForCode(state);
        return requestAccessToken(code);
    }

    private static String listenForCode(String expectedState) throws IOException {
        try (ServerSocket server = new ServerSocket(8888)) {
            while (true) {
                Socket socket = server.accept();
                String line;
                String code = null, state = null;
                Scanner in = new Scanner(socket.getInputStream());
                while (in.hasNextLine() && !(line = in.nextLine()).isEmpty()) {
                    if (line.startsWith("GET ")) {
                        String query = line.split(" ")[1];
                        if (query.contains("/callback?")) {
                            String[] params = query.substring(query.indexOf('?') + 1).split("&");
                            for (String param : params) {
                                if (param.startsWith("code=")) code = param.substring(5);
                                if (param.startsWith("state=")) state = param.substring(6);
                            }
                        }
                    }
                }
                String response = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\nYou may close this window.";
                socket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
                socket.close();
                if (code != null && expectedState.equals(state)) return code;
            }
        }
    }

    private static String requestAccessToken(String code) throws Exception {
        String body = "grant_type=authorization_code" +
                "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8) +
                "&client_id=" + CLIENT_ID +
                "&client_secret=" + CLIENT_SECRET;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        return json.get("access_token").getAsString();
    }

    public static String getCurrentUserId(String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/me"))
                .header("Authorization", "Bearer " + token)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        return json.get("id").getAsString();
    }

    public static String createPlaylist(String token, String userId, String name) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("name", name);
        body.addProperty("public", false);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/users/" + userId + "/playlists"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        return json.get("id").getAsString();
    }

    public static String searchTrack(String token, String query) throws Exception {
        String url = API_URL + "/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&type=track&limit=1";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray items = json.getAsJsonObject("tracks").getAsJsonArray("items");
        if (items.size() > 0) {
            return items.get(0).getAsJsonObject().get("uri").getAsString();
        }
        return null;
    }

    public static boolean addTracksToPlaylist(String token, String playlistId, List<String> uris) throws Exception {
        JsonObject body = new JsonObject();
        JsonArray arr = new JsonArray();
        for (String uri : uris) arr.add(uri);
        body.add("uris", arr);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/playlists/" + playlistId + "/tracks"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 201;
    }
} 