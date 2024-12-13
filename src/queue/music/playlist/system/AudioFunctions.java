package queue.music.playlist.system;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class AudioFunctions extends JPanel {

    private JLabel songLabel;
    private JFrame frame;
    private JButton playButton, pauseButton, dequeueButton, checkQueueButton, songListButton;
    private JProgressBar songProgressBar;
    private JTextArea songListArea;

    private Clip audioClip;
    private long clipPosition = 0;  // Keep track of clip position
    private ArrayList<File> songs;
    private int songNumber = 0;
    private Timer timer;

    queue_music_playlist_system<File> playlist = new queue_music_playlist_system<>();  // Changed to handle File objects

    public AudioFunctions() {
        frame = new JFrame();
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setTitle("Music Queue");

        // Set BorderLayout as the layout manager
        frame.setLayout(new BorderLayout());

        // Create a JPanel for the bottom (South)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.DARK_GRAY);
        bottomPanel.setPreferredSize(new Dimension(frame.getWidth(), 75)); // Width x Height
        bottomPanel.add(pauseButton = new RoundedButton("Pause"));
        bottomPanel.add(playButton = new RoundedButton("Play"));
        bottomPanel.add(dequeueButton = new RoundedButton("Dequeue"));
        dequeueButton.setFont(new Font("Helvetica", Font.BOLD, 15));
        pauseButton.setFont(new Font("Helvetica", Font.BOLD, 15));
        playButton.setFont(new Font("Helvetica", Font.BOLD, 15));

        // Create a JPanel for the left (West)
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.gray);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS)); // Vertical layout for buttons
        leftPanel.add(songListButton = new RoundedButton("Song List"));
        leftPanel.add(checkQueueButton = new RoundedButton("Check Queue"));
        leftPanel.setPreferredSize(new Dimension(150, frame.getHeight())); // Width x Height

        // Create a JPanel for the center (for displaying songs)
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Color.white);
        centerPanel.setPreferredSize(new Dimension(frame.getWidth() - leftPanel.getWidth(), frame.getHeight() - bottomPanel.getHeight()));
        songListArea = new JTextArea(15, 30);
        songListArea.setEditable(false);
        centerPanel.add(new JScrollPane(songListArea));

        // Add the panels to the BorderLayout
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(centerPanel, BorderLayout.CENTER);

        // Button Actions
        playButton.addActionListener(this::playMedia);
        pauseButton.addActionListener(this::pauseMedia);
        dequeueButton.addActionListener(this::dequeueSong);
        songListButton.addActionListener(this::showSongList);
        checkQueueButton.addActionListener(this::checkQueue);

        // Mouse Listener for song selection and enqueueing
        songListArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int lineHeight = songListArea.getFontMetrics(songListArea.getFont()).getHeight();
                int clickedLine = e.getY() / lineHeight;
                if (clickedLine < songs.size()) {
                    File selectedSong = songs.get(clickedLine);
                    enqueueSong(selectedSong);
                }
            }
        });

        frame.setVisible(true);

        songs = new ArrayList<>();
        loadSongs();  // Load songs on start
    }

    // Load songs from the "music" directory
    private void loadSongs() {
        File directory = new File("src/musics");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".wav"));

        if (files != null) {
            Arrays.sort(files);  // Sort the files alphabetically
            songs.addAll(Arrays.asList(files));
        }
    }

    // Display the song list in the center panel
    private void showSongList(ActionEvent e) {
        songListArea.setText("");  // Clear the previous list
        for (int i = 0; i < songs.size(); i++) {
            songListArea.append((i + 1) + ". " + songs.get(i).getName() + "\n");
        }
    }

    // Enqueue a song (called when a song is clicked in the list)
    private void enqueueSong(File song) {
        // Reset the clip position before enqueuing the song
        clipPosition = 0;  // Reset the position of the song
        playlist.enqueue(song);  // Add the song to the queue
        System.out.println("Enqueued: " + song.getName());
    }

    // Play the media (next song from the queue)
    private void playMedia(ActionEvent e) {
        if (audioClip != null && audioClip.isRunning()) {
            return;  // Already playing
        }
        try {
            File songFile = playlist.peek();  // Get the song at the front of the queue
            javax.sound.sampled.AudioInputStream audioStream = javax.sound.sampled.AudioSystem.getAudioInputStream(songFile);
            audioClip = javax.sound.sampled.AudioSystem.getClip();
            audioClip.open(audioStream);
            audioClip.setFramePosition((int) clipPosition); // Start from where it was paused
            audioClip.start();

            songLabel.setText(songFile.getName());
            beginTimer();
        } catch (Exception ex) {
            System.out.println("Error playing audio: " + ex.getMessage());
        }
    }

    // Pause the audio
    private void pauseMedia(ActionEvent e) {
        if (audioClip != null && audioClip.isRunning()) {
            clipPosition = audioClip.getFramePosition();  // Save the current position
            audioClip.stop();  // Pause the clip
            cancelTimer();
        }
    }

    // Dequeue the song in the front queue and play the next song
    private void dequeueSong(ActionEvent e) {
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
            playMedia(e);  // This will start playing the next song in the queue
        } else {
            System.out.println("Queue is empty! No more songs to play.");
        }
    }

    // Display the songs in the queue
    private void checkQueue(ActionEvent e) {
        StringBuilder queueDisplay = new StringBuilder();
        for (File song : playlist.queue) {
            queueDisplay.append(song.getName()).append("\n");
        }
        songListArea.setText(queueDisplay.toString());  // Show the queue in the center panel
    }

    private void beginTimer() {
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
                        dequeueSong(null); // Automatically dequeue and play the next song
                    }
                } else {
                    cancelTimer(); // Stop the timer if the audioClip is not open
                }
            }
        });
        timer.start();
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.stop();
        }
    }
}

class RoundedButton extends JButton {
    private int radius;

    public RoundedButton(String text) {
        super(text);
        this.radius = 15;  // You can change this value to adjust the roundness
        setOpaque(false);
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background color and rounded rectangle
        if (getModel().isArmed()) {
            g2.setColor(getBackground().darker());  // Darker color when pressed
        } else {
            g2.setColor(getBackground());
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // Draw the text
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getForeground());
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        setForeground(color);
    }
}
