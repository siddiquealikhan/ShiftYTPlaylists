package com.shiftytplaylists;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ShiftYTPlaylistsApp {
    private JFrame frame;
    private JTextField ytUrlField;
    private JButton shiftButton;
    private JTextArea logArea;

    public ShiftYTPlaylistsApp() {
        frame = new JFrame("ShiftYTPlaylists");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(480, 260);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        frame.add(new JLabel("YouTube Playlist URL:"), gbc);

        ytUrlField = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        frame.add(ytUrlField, gbc);

        shiftButton = new JButton("Shift");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 0;
        frame.add(shiftButton, gbc);

        logArea = new JTextArea(8, 40);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(logArea);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(scrollPane, gbc);

        shiftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shiftButton.setEnabled(false);
                logArea.setText("");
                String ytUrl = ytUrlField.getText().trim();
                if (ytUrl.isEmpty()) {
                    log("Please enter a YouTube playlist URL.");
                    shiftButton.setEnabled(true);
                    return;
                }
                new ShiftWorker(ytUrl).execute();
            }
        });

        frame.setVisible(true);
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    private class ShiftWorker extends SwingWorker<Void, String> {
        private final String ytUrl;
        public ShiftWorker(String ytUrl) {
            this.ytUrl = ytUrl;
        }
        @Override
        protected Void doInBackground() {
            int total = 0, matched = 0;
            try {
                publish("Extracting playlist ID...");
                String playlistId = YouTubeService.extractPlaylistId(ytUrl);
                if (playlistId == null) {
                    publish("Could not extract playlist ID from URL.");
                    return null;
                }
                publish("Fetching YouTube playlist titles...");
                List<String> ytTitles = YouTubeService.fetchPlaylistTitles(playlistId);
                if (ytTitles.isEmpty()) {
                    publish("No videos found in playlist.");
                    return null;
                }
                total = ytTitles.size();
                publish("Fetched " + total + " videos. Authenticating with Spotify...");
                String token = SpotifyService.authenticateAndGetToken();
                String userId = SpotifyService.getCurrentUserId(token);
                publish("Creating new Spotify playlist...");
                String playlistName = "Shifted: " + (ytTitles.size() > 0 ? ytTitles.get(0) : "YouTube Playlist");
                String playlistIdSpotify = SpotifyService.createPlaylist(token, userId, playlistName);
                List<String> uris = new ArrayList<>();
                int idx = 1;
                for (String ytTitle : ytTitles) {
                    String cleaned = TitleCleaner.clean(ytTitle);
                    publish("[" + idx + "/" + total + "] Searching: " + cleaned);
                    String uri = null;
                    try {
                        uri = SpotifyService.searchTrack(token, cleaned);
                    } catch (Exception e) {
                        publish("  Error searching: " + e.getMessage());
                    }
                    if (uri != null) {
                        uris.add(uri);
                        matched++;
                        publish("  ✓ Found and will add.");
                    } else {
                        publish("  ✗ Not found on Spotify.");
                    }
                    idx++;
                    // Add in batches of 50 (Spotify API limit)
                    if (uris.size() == 50) {
                        SpotifyService.addTracksToPlaylist(token, playlistIdSpotify, uris);
                        uris.clear();
                    }
                }
                if (!uris.isEmpty()) {
                    SpotifyService.addTracksToPlaylist(token, playlistIdSpotify, uris);
                }
                publish("\n" + matched + " out of " + total + " songs transferred successfully.");
            } catch (Exception ex) {
                publish("Error: " + ex.getMessage());
            }
            return null;
        }
        @Override
        protected void process(java.util.List<String> chunks) {
            for (String msg : chunks) log(msg);
        }
        @Override
        protected void done() {
            shiftButton.setEnabled(true);
            log("Done.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ShiftYTPlaylistsApp::new);
    }
} 