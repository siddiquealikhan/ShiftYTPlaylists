package com.shiftytplaylists;

public class TitleCleaner {
    public static String clean(String title) {
        String t = title.toLowerCase();
        // Remove common clutter
        t = t.replaceAll("\\(.*official.*video.*\\)", "");
        t = t.replaceAll("\\[.*official.*video.*\\]", "");
        t = t.replaceAll("\\(.*lyrics.*\\)", "");
        t = t.replaceAll("\\[.*lyrics.*\\]", "");
        t = t.replaceAll("\\(.*audio.*\\)", "");
        t = t.replaceAll("\\[.*audio.*\\]", "");
        t = t.replaceAll("\\(.*hd.*\\)", "");
        t = t.replaceAll("\\[.*hd.*\\]", "");
        t = t.replaceAll("\\(.*remastered.*\\)", "");
        t = t.replaceAll("\\[.*remastered.*\\]", "");
        t = t.replaceAll("\\(.*live.*\\)", "");
        t = t.replaceAll("\\[.*live.*\\]", "");
        t = t.replaceAll("\\(.*video.*\\)", "");
        t = t.replaceAll("\\[.*video.*\\]", "");
        t = t.replaceAll("\\(.*explicit.*\\)", "");
        t = t.replaceAll("\\[.*explicit.*\\]", "");
        t = t.replaceAll("\\(.*performance.*\\)", "");
        t = t.replaceAll("\\[.*performance.*\\]", "");
        t = t.replaceAll("\\(.*cover.*\\)", "");
        t = t.replaceAll("\\[.*cover.*\\]", "");
        t = t.replaceAll("\\(.*color.*\\)", "");
        t = t.replaceAll("\\[.*color.*\\]", "");
        t = t.replaceAll("\\(.*color coded.*\\)", "");
        t = t.replaceAll("\\[.*color coded.*\\]", "");
        t = t.replaceAll("\\(.*mv.*\\)", "");
        t = t.replaceAll("\\[.*mv.*\\]", "");
        t = t.replaceAll("\\(.*full album.*\\)", "");
        t = t.replaceAll("\\[.*full album.*\\]", "");
        t = t.replaceAll("\\(.*album.*\\)", "");
        t = t.replaceAll("\\[.*album.*\\]", "");
        t = t.replaceAll("\\(.*single.*\\)", "");
        t = t.replaceAll("\\[.*single.*\\]", "");
        t = t.replaceAll("\\(.*track.*\\)", "");
        t = t.replaceAll("\\[.*track.*\\]", "");
        t = t.replaceAll("\\(.*audio.*\\)", "");
        t = t.replaceAll("\\[.*audio.*\\]", "");
        t = t.replaceAll("\\(.*visualizer.*\\)", "");
        t = t.replaceAll("\\[.*visualizer.*\\]", "");
        t = t.replaceAll("\\(.*teaser.*\\)", "");
        t = t.replaceAll("\\[.*teaser.*\\]", "");
        t = t.replaceAll("\\(.*trailer.*\\)", "");
        t = t.replaceAll("\\[.*trailer.*\\]", "");
        t = t.replaceAll("\\(.*shorts.*\\)", "");
        t = t.replaceAll("\\[.*shorts.*\\]", "");
        t = t.replaceAll("\\(.*short.*\\)", "");
        t = t.replaceAll("\\[.*short.*\\]", "");
        // Remove anything in brackets/parentheses
        t = t.replaceAll("\\(.*?\\)", "");
        t = t.replaceAll("\\[.*?\\]", "");
        // Remove extra whitespace and dashes
        t = t.replaceAll("-", " ");
        t = t.replaceAll("\\s+", " ").trim();
        return t;
    }
} 