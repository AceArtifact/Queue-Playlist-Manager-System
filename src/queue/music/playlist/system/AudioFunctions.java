package queue.music.playlist.system;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class AudioFunctions extends JFrame{

    private JLabel songLabel;
    private JButton playButton, pauseButton, dequeueButton, checkQueueButton, songListButton;
    private JTextArea songListArea;
    private JSlider volumeSlider;
    private float volume = 1.0f;
    private boolean isPaused = false;

    private Clip audioClip;
    private long clipPosition = 0;  // Keep track of clip position
    private ArrayList<File> songs;
    private int songNumber = 0;
    private Timer timer;

    private JPanel leftPanel, bottomPanel, centerPanel;

    private boolean isRunning;

    queue_music_playlist_system<File> playlist = new queue_music_playlist_system<>();  // Changed to handle File objects

    public AudioFunctions() {

        // calls JFrame constructor to configure out gui and set the title header to "Music Player"
        super("Music Player");

        // set the width and height
        setSize(1080, 720);

        // end process when app is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // launch the app at the center of the screen
        setLocationRelativeTo(null);

        // prevent the app from being resized
        setResizable(false);

        // set layout to null which allows us to control the (x, y) coordinates of our components
        // and also set the height and width
        setLayout(null);



        songs = new ArrayList<>();
        loadSongs();  // Load songs on start

        addGuiComponent();


        // Button Actions
        playButton.addActionListener(this::playMedia);
        pauseButton.addActionListener(this::pauseMedia);
        dequeueButton.addActionListener(this::dequeueSong);
        songListButton.addActionListener(this::showSongList);
        checkQueueButton.addActionListener(this::checkQueue);
        volumeSlider.addChangeListener(this::changeVolume);

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

    }

    private void addGuiComponent(){

        addBottomPanel();

        addLeftPanel();

        addCenterPanel();

    }

    private void addLeftPanel(){
        leftPanel = new JPanel();
        leftPanel.setBackground(Color.decode("#121212"));
        leftPanel.setBounds(0, 0, 225, 720);

        // Set layout to null for free positioning
        leftPanel.setLayout(null);

        JLabel threeDots = new JLabel(loadImage("src/images/3Dots.png"));
        threeDots.setBounds(1, 20, 150, 50);

        songListButton = new JButton("Song List");
        songListButton.setFont(new Font("Helvetica Nee", Font.BOLD, 14));
        songListButton.setBounds(1, 120, 125, 45); // x, y, width, height
        songListButton.setBorderPainted(false);
        songListButton.setBackground(null);
        songListButton.setForeground(Color.decode("#b3b3b3"));


        checkQueueButton = new JButton("Check Queue");
        checkQueueButton.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
        checkQueueButton.setBounds(9, 190, 135, 45); // x, y, width, height
        checkQueueButton.setBorderPainted(false);
        checkQueueButton.setBackground(null);
        checkQueueButton.setForeground(Color.decode("#b3b3b3"));

        leftPanel.add(threeDots);
        leftPanel.add(songListButton);
        leftPanel.add(checkQueueButton);

        add(leftPanel);

    }

    private void addBottomPanel(){

        bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.decode("#212121"));
        bottomPanel.setBounds(0,580,1080, 400);


        bottomPanel.add(pauseButton = new RoundedButton("Pause"));
        pauseButton.setFont(new Font("Helvetica Neue", Font.BOLD, 12));
        pauseButton.setBounds(375, 10,85,35);
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setForeground(Color.WHITE);

        bottomPanel.add(playButton = new RoundedButton("Play"));
        playButton.setFont(new Font("Helvetica Neue", Font.BOLD, 12));
        playButton.setBounds(475, 10,85,35);
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.setForeground(Color.WHITE);

        bottomPanel.add(dequeueButton = new RoundedButton("Dequeue"));
        dequeueButton.setFont(new Font("Helvetica Neue", Font.BOLD, 12));
        dequeueButton.setBounds(575, 10,90,35);
        dequeueButton.setBorderPainted(false);
        dequeueButton.setBackground(null);
        dequeueButton.setForeground(Color.WHITE);

        bottomPanel.add(volumeSlider = new JSlider(0, 100, 50));

        volumeSlider.setBackground(null);

        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);

        bottomPanel.add(volumeSlider);

        add(bottomPanel);

    }

    private void addCenterPanel() {

        // Create a JPanel for the center (for displaying songs)
        centerPanel = new JPanel();
        centerPanel.setBackground(Color.white);
        centerPanel.setBounds(225, 0, 855, 580);

        centerPanel.setBackground(Color.DARK_GRAY);

        songListArea = new JTextArea(15, 30);
        songListArea.setEditable(false);
        songListArea.setBounds(25, 120, 785, 435);
        centerPanel.add(new JScrollPane(songListArea));



        add(centerPanel);
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
        playlist.enqueue(song);  // Add the song to the queue
        System.out.println("Enqueued: " + song.getName());
    }

    private void playMedia(ActionEvent e) {
        try {
            if (audioClip != null && isPaused) {
                // If the song is paused, resume from the saved position
                audioClip.setFramePosition((int) clipPosition); // Set the position to where it was paused
                audioClip.start(); // Start the audio again from that position
                beginTimer(); // Continue the timer from the same position
                isPaused = false;  // Song is no longer paused
            } else {
                // Stop any currently running audio clip
                if (audioClip != null && audioClip.isRunning()) {
                    audioClip.stop();
                }

                // Get the next song from the queue
                File songFile = playlist.peek();
                if (songFile == null) {
                    System.out.println("No song in the queue to play!");
                    return;
                }

                // Load the new song
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(songFile);
                audioClip = AudioSystem.getClip();
                audioClip.open(audioStream);

                // Apply the volume setting after loading the clip
                FloatControl volumeControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(20f * (float) Math.log10(volume));  // Apply volume based on slider

                // Reset clip position and progress bar
                clipPosition = 0;
                audioClip.setFramePosition((int) clipPosition);

                // Start the clip and the timer
                audioClip.start();
                beginTimer();

                // Update song label
                if (songLabel != null) {
                    songLabel.setText(songFile.getName());
                }

                isPaused = false;  // Ensure the song is not paused at the start
            }
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
            isPaused = true;  // Set the state to paused
        }
    }

    // Dequeue the song in the front queue and play the next song
    private void dequeueSong(ActionEvent e) {
        if (!playlist.isEmpty()) {
            // Stop the current audio if playing
            if (audioClip != null && audioClip.isRunning()) {
                audioClip.stop();
            }

            // Remove the current song from the queue
            File currentSong = playlist.dequeue();
            System.out.println("Dequeued and stopped: " + currentSong.getName());

            // Reset the clip position and progress bar
            clipPosition = 0;

            // Automatically play the next song
            playMedia(e);
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
                if (audioClip != null) {
                    int progress = (int) ((audioClip.getFramePosition() / (float) audioClip.getFrameLength()) * 100);
                    if (audioClip.getFramePosition() == audioClip.getFrameLength()) {
                        dequeueSong(null);  // Automatically move to the next track when the current song ends
                    }
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

    private void changeVolume(ChangeEvent e) {
        volume = volumeSlider.getValue() / 100.0f;  // Volume range from 0.0 to 1.0
        if (audioClip != null) {
            FloatControl volumeControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(20f * (float) Math.log10(volume));  // Set the volume based on the slider value
        }
    }
    // method for loading image
    private ImageIcon loadImage(String imagePath){
        try{
            // read the image file from the given path
            BufferedImage image = ImageIO.read(new File(imagePath));

            // returns an image icon so that our component can render the image
            return new ImageIcon(image);
        }catch(Exception e){
            e.printStackTrace();
        }

        // could not find resource
        return null;
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
