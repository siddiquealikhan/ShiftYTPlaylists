package com.shiftytplaylists;

import com.google.gson.*;
import java.net.URI;
import java.net.http.*;
import java.util.*;

public class YouTubeService {
    private static final String API_KEY = System.getenv("YOUTUBE_API_KEY");
    private static final String API_URL = "https://www.googleapis.com/youtube/v3/playlistItems";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static List<String> fetchPlaylistTitles(String playlistId) throws Exception {
        List<String> titles = new ArrayList<>();
        String pageToken = null;
        do {
            StringBuilder url = new StringBuilder(API_URL + "?part=snippet&maxResults=50&playlistId=" + playlistId + "&key=" + API_KEY);
            if (pageToken != null) url.append("&pageToken=").append(pageToken);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url.toString())).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray items = root.getAsJsonArray("items");
            for (JsonElement item : items) {
                JsonObject snippet = item.getAsJsonObject().getAsJsonObject("snippet");
                if (snippet != null && snippet.has("title")) {
                    titles.add(snippet.get("title").getAsString());
                }
            }
            pageToken = root.has("nextPageToken") ? root.get("nextPageToken").getAsString() : null;
        } while (pageToken != null);
        return titles;
    }

    public static String extractPlaylistId(String url) {
        // Handles URLs like https://www.youtube.com/playlist?list=PLxxxx
        int idx = url.indexOf("list=");
        if (idx == -1) return null;
        String id = url.substring(idx + 5);
        int amp = id.indexOf('&');
        if (amp != -1) id = id.substring(0, amp);
        return id;
    }
} 