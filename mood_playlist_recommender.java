// Mood-Based Playlist Recommender
// Updated to include YouTube search links without using the API

import java.util.*;
import java.io.*;

class Playlist {
    private String mood;
    private ArrayList<String> songs;

    public Playlist(String mood) {
        this.mood = mood;
        this.songs = new ArrayList<>();
    }

    public String getMood() {
        return mood;
    }

    public ArrayList<String> getSongs() {
        return songs;
    }

    public void addSong(String title) {
        String query = title.replace(" ", "+");
        String youtubeLink = "https://www.youtube.com/results?search_query=" + query;
        String fullEntry = title + " | " + youtubeLink;
        songs.add(fullEntry);
    }

    public void removeSong(String title) {
        songs.removeIf(song -> song.startsWith(title + " |"));
    }

    public void showPlaylist() {
        if (songs.isEmpty()) {
            System.out.println("No songs in the playlist for mood: " + mood);
        } else {
            System.out.println("Playlist for mood: " + mood);
            for (String song : songs) {
                System.out.println("- " + song);
            }
        }
    }
}

class FileHandler {
    public static ArrayList<String> loadPlaylist(String mood) {
        ArrayList<String> songs = new ArrayList<>();
        File file = new File(mood + ".txt");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                songs.add(line.trim());
            }
        } catch (IOException e) {
            // File might not exist yet
        }
        return songs;
    }

    public static void savePlaylist(String mood, ArrayList<String> songs) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(mood + ".txt"))) {
            for (String song : songs) {
                bw.write(song);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving playlist for mood: " + mood);
        }
    }
}

class MoodManager {
    private HashMap<String, Playlist> moodPlaylists;

    public MoodManager() {
        moodPlaylists = new HashMap<>();
    }

    public Playlist getPlaylistForMood(String mood) {
        mood = mood.toLowerCase();
        if (!moodPlaylists.containsKey(mood)) {
            Playlist p = new Playlist(mood);
            p.getSongs().addAll(FileHandler.loadPlaylist(mood));
            moodPlaylists.put(mood, p);
        }
        return moodPlaylists.get(mood);
    }

    public void saveAllPlaylists() {
        for (String mood : moodPlaylists.keySet()) {
            FileHandler.savePlaylist(mood, moodPlaylists.get(mood).getSongs());
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoodManager manager = new MoodManager();

        System.out.println("\uD83C\uDFB5 Mood-Based Playlist Recommender \uD83C\uDFB5");
        System.out.print("How are you feeling today? ");
        String mood = scanner.nextLine().toLowerCase();

        Playlist playlist = manager.getPlaylistForMood(mood);

        while (true) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("1. View playlist");
            System.out.println("2. Add song");
            System.out.println("3. Remove song");
            System.out.println("4. Save & Exit");

            System.out.print("Enter your choice: ");
            String choiceStr = scanner.nextLine();
            int choice = 0;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1:
                    playlist.showPlaylist();
                    break;
                case 2:
                    System.out.print("Enter song title to add: ");
                    String title = scanner.nextLine();
                    playlist.addSong(title);
                    System.out.println("Song added.");
                    break;
                case 3:
                    System.out.print("Enter song title to remove: ");
                    String removeTitle = scanner.nextLine();
                    playlist.removeSong(removeTitle);
                    System.out.println("Song removed if it existed.");
                    break;
                case 4:
                    manager.saveAllPlaylists();
                    System.out.println("Playlists saved. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
