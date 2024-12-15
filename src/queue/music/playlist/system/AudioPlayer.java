package queue.music.playlist.system;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class AudioPlayer {

    private JLabel songLabel, backgroundLabel;
    private JProgressBar songProgressBar;
    private JTextArea songListArea;

    private Clip audioClip;
    private long clipPosition = 0;  // Keep track of clip position
    private ArrayList<File> songs;
    private int songNumber = 0;
    private Timer timer;

    queue_music_playlist_system<File> playlist = new queue_music_playlist_system<>();  // Changed to handle File objects


    public AudioPlayer() {

        songs = new ArrayList<>();
        loadSongs();  // Load songs on start
    }


    // Display the song list in the center panel
    public void showSongList(JTextArea textArea) {
        textArea.setText("");  // Clear the previous list
        for (int i = 0; i < songs.size(); i++) {
            textArea.append((i + 1) + ". " + songs.get(i).getName() + "\n");
        }

    }

    /// ///////////////////////////////////////////////////
    //                                                   //
    //    METHODS FOR QUEUEING AND DEQUEUEING            //
    //                                                   //

    /// ///////////////////////////////////////////////////

    public void enqueueSong(File song) {

        if (playlist.contains(song)) {  // Check if the song is already in the queue
            System.out.println("Song already in queue: " + song.getName());

        } else {
            clipPosition = 0;  // Reset the position of the song
            playlist.enqueue(song);  // Add the song to the queue
            System.out.println("Enqueued: " + song.getName());
        }

    }

    // Dequeue the song in the front queue and play the next song
    public void dequeueSong() {

        // Check if the queue is not empty
        if (!playlist.isEmpty()) {
            // Stop the current audio clip if it's playing
            if (audioClip != null && audioClip.isRunning()) {
                audioClip.stop();  // Stop the currently playing song
            }

            // Remove the current song from the queue
            File currentSong = playlist.dequeue();  // Dequeue the first song in the queue
            System.out.println("Dequeued and stopped: " + currentSong.getName());

            // Automatically play the next song in the queue
            playAudio();  // This will start playing the next song in the queue
        } else {
            System.out.println("Queue is empty! No more songs to play.");
        }

    }

    /// ///////////////////////////////////
    //                                   //
    //    METHODS FOR PLAYING AUDIO      //
    //                                   //
    /// ///////////////////////////////////
    public void playAudio() {

        try {
            if (audioClip != null) {
                audioClip.stop();
                audioClip.close();  // Release resources
            }

            File songFile = playlist.peek();
            javax.sound.sampled.AudioInputStream audioStream = javax.sound.sampled.AudioSystem.getAudioInputStream(songFile);
            audioClip = javax.sound.sampled.AudioSystem.getClip();
            audioClip.open(audioStream);
            audioClip.setFramePosition((int) clipPosition);
            audioClip.start();

            songLabel.setText(songFile.getName());
            beginTimer();
        } catch (Exception ex) {
            System.out.println("Error playing audio: " + ex.getMessage());
        }

    }

    // Pause the audio
    public void pauseAudio() {

        if (audioClip != null && audioClip.isRunning()) {
            clipPosition = audioClip.getFramePosition();  // Save the current position
            audioClip.stop();  // Pause the clip
            cancelTimer();
        }

    }

    // Display the songs in the queue
    public void checkQueue(JTextArea t) {

        StringBuilder queueDisplay = new StringBuilder();
        for (File song : playlist.queue) {
            queueDisplay.append(song.getName()).append("\n");
        }

        t.setEditable(false);  // Make the text area non-editable (non-clickable)
        t.setText(queueDisplay.toString());  // Show the queue in the center panel

    }

    public void beginTimer() {

        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (audioClip != null && audioClip.isOpen()) {
                    // Update the progress bar
                    int progress = (int) ((audioClip.getFramePosition() / (float) audioClip.getFrameLength()) * 100);
                    songProgressBar.setValue(progress);

                    // Check if the song has reached its end
                    if (audioClip.getFramePosition() >= audioClip.getFrameLength() - 1) {
                        cancelTimer(); // Stop the timer
                        dequeueSong(); // Automatically dequeue and play the next song
                    }
                } else {
                    cancelTimer(); // Stop the timer if the audioClip is not open
                }
            }
        });
        timer.start();

    }

    public void cancelTimer() {

        if (timer != null) {
            timer.stop();
        }

    }

    // Load songs from the "music" directory
    public void loadSongs() {
        File directory = new File("src/musics");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".wav"));

        if (files != null) {
            Arrays.sort(files);  // Sort the files alphabetically
            songs.addAll(Arrays.asList(files));
            System.out.println("Loaded " + songs.size() + " songs.");
        } else {
            System.out.println("No .wav files found in directory.");
        }
    }
}