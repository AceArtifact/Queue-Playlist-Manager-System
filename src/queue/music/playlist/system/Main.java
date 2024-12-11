
package queue.music.playlist.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main  {
    
    public static void main(String[] args) {
        // List of available songs
        List<Songs> availableSongs = new ArrayList<>();
        availableSongs.add(new Songs("Song A", "src/musics/Stray Kids, Young Miko, Tom Morello - ＂Come Play＂ (from Arcane Season 2) [Official Visualizer].wav"));
        availableSongs.add(new Songs("Song B", "src/musics/Denzel Curry, Gizzle, Bren Joy - Dynasties & Dystopia ｜ Arcane League of Legends ｜ Riot Games Music.wav"));
        availableSongs.add(new Songs("Song C", "src/musics/Paint The Town Blue (from the series Arcane League of Legends).wav"));

        // Playlist queue
        queue_music_playlist_system<Songs> playlist = new queue_music_playlist_system<>();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. View Available Songs");
            System.out.println("2. Add Songs to Playlist");
            System.out.println("3. View Playlist");
            System.out.println("4. Play Playlist");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> {
                    System.out.println("Available Songs:");
                    for (int i = 0; i < availableSongs.size(); i++) {
                        String fileName = new java.io.File(availableSongs.get(i).getFilePath()).getName();
                        System.out.println((i + 1) + ". " + availableSongs.get(i).getName() + " - " + fileName);
                    }
                }
                case 2 -> {
                    System.out.println("Enter the number of the song to add to the playlist:");
                    int songIndex = scanner.nextInt() - 1;
                    if (songIndex >= 0 && songIndex < availableSongs.size()) {
                        playlist.enqueue(availableSongs.get(songIndex));
                        System.out.println("Added: " + availableSongs.get(songIndex).getName());
                    } else {
                        System.out.println("Invalid choice!");
                    }
                }
                case 3 -> playlist.display();

                case 4 -> {
                    while (!playlist.isEmpty()) {
                        Songs song = playlist.dequeue();
                        System.out.println("Now playing: " + song.getName());
                        playAudio(song.getFilePath());
                    }
                }
                case 5 -> {
                    System.out.println("Exiting program. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice, please try again.");
            }
        }
    }

    // Method to play audio
    public static void playAudio(String filePath) {
        try {
            // Open an audio input stream
            java.io.File audioFile = new java.io.File(filePath);
            javax.sound.sampled.AudioInputStream audioStream = javax.sound.sampled.AudioSystem.getAudioInputStream(audioFile);

            // Get a sound clip resource
            javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
            clip.open(audioStream);

            // Play the audio clip
            clip.start();

            // Wait until the audio clip finishes playing
            clip.drain();  // This ensures the clip finishes playing before proceeding

            // Wait until the audio clip finishes playing
            while (clip.isRunning()) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            System.out.println("Error playing audio: " + e.getMessage());
        }
    }
}
