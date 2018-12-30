package com.keepingrack.tagmusicplayer;

import com.keepingrack.tagmusicplayer.bean.MusicItem;
import com.keepingrack.tagmusicplayer.bean.RelateTag;
import com.keepingrack.tagmusicplayer.layout.musicField.MusicRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Variable {

    private static List<String> musicKeys = new ArrayList<>();
    private static Map<String, MusicItem> musicItems = new ConcurrentHashMap<>();
    private static Set<String> tagKinds = new HashSet<>();
    private static List<String> displayMusicNames = new ArrayList<>();
    private static List<RelateTag> relateTags = new ArrayList<>();

    public static List<String> getMusicKeys() { return musicKeys; }
    public static Set<String> getTagKinds() { return tagKinds; }
    public static List<String> getDisplayMusicNames() { return displayMusicNames; }
    public static List<RelateTag> getRelateTags() { return relateTags; }

    public static void initialize() {
        musicKeys = new ArrayList<>();
        musicItems = new ConcurrentHashMap<>();
        tagKinds = new HashSet<>();
        displayMusicNames = new ArrayList<>();
        relateTags = new ArrayList<>();
    }


    public static void addMusicKeys(String key) {
        synchronized (musicKeys) {
            musicKeys.add(key);
        }
    }
    public static void clearMusicKeys() {
        synchronized (musicKeys) {
            musicKeys.clear();
        }
    }
    public static void putMusicItems(String key, MusicItem musicItem) {
        synchronized (musicItems) {
            musicItems.put(key, musicItem);
        }
    }
    public static void clearMusicItems() {
        synchronized (musicItems) {
            musicItems.clear();
        }
    }
    public static void setMusicRow(String key, MusicRow row) {
        synchronized (musicItems) {
            if (musicItems.get(key) != null) {
                musicItems.get(key).setRow(row);
            }
        }
    }
    public static void setMusicTags(String key, List<String> tags) {
        synchronized (musicItems) {
            if (musicItems.get(key) != null) {
                musicItems.get(key).setTags(tags);
            }
        }
    }
    public static void clearMusicRow() {
        synchronized (musicItems) {
            for (Map.Entry<String, MusicItem> musicItemMap : musicItems.entrySet()) {
                MusicItem musicItem = musicItemMap.getValue();
                musicItem.setRow(null); // この処理いる？
            }
        }
    }
    public static void addTagKinds(String tag) {
        synchronized (tagKinds) {
            tagKinds.add(tag);
        }
    }
    public static void addTagKinds(List<String> tags) {
        synchronized (tagKinds) {
            tagKinds.addAll(tags);
        }
    }
    public static void clearTagKinds() {
        synchronized (tagKinds) {
            tagKinds.clear();
        }
    }
    public static void setDisplayMusicNames(List<String> list) {
        synchronized (displayMusicNames) {
            displayMusicNames = list;
        }
    }
    public static void addDisplayMusicNames(String key) {
        synchronized (displayMusicNames) {
            if (!displayMusicNames.contains(key)) { displayMusicNames.add(key); }
        }
    }
    public static void addDisplayMusicNames(int order, String key) {
        synchronized (displayMusicNames) {
            if (!displayMusicNames.contains(key)) { displayMusicNames.add(order, key); }
        }
    }
    public static void removeDisplayMusicNames(String key) {
        synchronized (displayMusicNames) {
            displayMusicNames.remove(key);
        }
    }
    public static void clearDisplayMusicNames() {
        synchronized (displayMusicNames) {
            displayMusicNames.clear();
        }
    }
    public static void shuffleDisplayMusicNames() {
        synchronized (displayMusicNames) {
            Collections.shuffle(displayMusicNames);
        }
    }
    public static void setRelateTags(List<RelateTag> list) {
        synchronized (relateTags) {
            relateTags = list;
        }
    }
    public static void addRelateTags(RelateTag relateTag) {
        synchronized (relateTags) {
            relateTags.add(relateTag);
        }
    }
    public static void addRelateTags(List<RelateTag> list) {
        synchronized (relateTags) {
            relateTags.addAll(list);
        }
    }
    public static void clearRelateTags() {
        synchronized (relateTags) {
            relateTags.clear();
        }
    }

    public static MusicItem getMusicItem(String key) {
        synchronized (musicItems) {
            return musicItems.get(key);
        }
    }
    public static String getMusicTitle(String key) {
        String title = "";
        synchronized (musicItems) {
            title = musicItems.get(key).getTitle();
        }
        return title;
    }
    public static MusicRow getMusicRow(String key) {
        MusicRow row = null;
        synchronized (musicItems) {
            if (musicItems.get(key) != null) {
                row = musicItems.get(key).getRow();
            }
        }
        return row;
    }
    public static List<String> getMusicTags(String key) {
        List<String> tags = null;
        synchronized (musicItems) {
            if (musicItems.get(key) != null) {
                tags = musicItems.get(key).getTags();
            }
        }
        return tags;
    }
    public static String getMusicAbsolutePath(String key) {
        String absolutePath = "";
        synchronized (musicItems) {
            if (musicItems.get(key) != null) {
                absolutePath = musicItems.get(key).getAbsolutePath();
            }
        }
        return absolutePath;
    }
}
