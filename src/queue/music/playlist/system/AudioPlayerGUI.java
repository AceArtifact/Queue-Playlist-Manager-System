package queue.music.playlist.system;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class AudioPlayerGUI extends JFrame{

    public JPanel leftPanel, bottomPanel, contentPanel;

    // Declare different panels for Songs List and Check Queue
    public JPanel songsListPanel, checkQueuePanel;
    public JTextArea songListArea, checkQueueArea;

    private ArrayList<String> songs;

    private CardLayout cardLayout;
    //private JSlider playbackSlider;

    private final AudioPlayer audioPlayer;

    // Playlist queue
    queue_music_playlist_system<Songs> playlist = new queue_music_playlist_system<>();


    public AudioPlayerGUI() {
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

        audioPlayer = new AudioPlayer();
        songs = new ArrayList<>();

        // add the gui components
        addGuiComponents();

        audioPlayer.enqueueSong(new File("src/musics/Denzel Curry, Gizzle, Bren Joy - Dynasties & Dystopia ｜ Arcane League of Legends ｜ Riot Games Music.wav"));

    }

    //
    private void addGuiComponents() {


        // add the Bottom Part to this method
        addBottomButtons();

        // add the Left Part to this method
        addLeftButtons();

        // add the songs list and check queue panel to this method
        addContentPanel();

    }

    // ------ LEFT PART --------
    private void addLeftButtons () {
        leftPanel = new JPanel();
        leftPanel.setBounds(0, 0, 225, 720);
        leftPanel.setBackground(Color.decode("#121212"));

        // Set layout to null for free positioning
        leftPanel.setLayout(null);

        JLabel threeDots = new JLabel(loadImage("src/images/3Dots.png"));
        threeDots.setBounds(1, 20, 150, 50);

        // Song List Button
        JButton songListBtn = new JButton("Songs List");
        songListBtn.setFont(new Font("Helvetica Nee", Font.BOLD, 14));
        songListBtn.setBounds(1, 120, 125, 45); // x, y, width, height
        songListBtn.setBorderPainted(false);
        songListBtn.setBackground(null);
        songListBtn.setForeground(Color.decode("#b3b3b3"));

        songListBtn.addActionListener(this::songlistButtonActionListener);

        // Check Queue Button
        JButton checkQueueBtn = new JButton("Check Queue");
        checkQueueBtn.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
        checkQueueBtn.setBounds(9, 190, 135, 45); // x, y, width, height
        checkQueueBtn.setBorderPainted(false);
        checkQueueBtn.setBackground(null);
        checkQueueBtn.setForeground(Color.decode("#b3b3b3"));

        checkQueueBtn.addActionListener(this::checkQButtonActionListener);

        // Add buttons to the Left Panel
        leftPanel.add(threeDots);
        leftPanel.add(songListBtn);
        leftPanel.add(checkQueueBtn);

        // Add Left Panel to the frame
        add(leftPanel);
    }

    // ---- BOTTOM PART -------
    private void addBottomButtons () {
        bottomPanel = new JPanel();
        bottomPanel.setBounds(0,580,1080, 400);
        bottomPanel.setBackground(Color.decode("#212121"));

        bottomPanel.setLayout(null);

        // Pause Button
        RoundedButton pauseBtn = new RoundedButton("Pause");
        pauseBtn.setFont(new Font("Helvetica Neue", Font.BOLD, 12));
        pauseBtn.setBounds(375, 10,85,35);
        pauseBtn.setBorderPainted(false);
        pauseBtn.setBackground(null);
        pauseBtn.setForeground(Color.WHITE);

        pauseBtn.addActionListener(e -> {
            // go to the previous song
            audioPlayer.pauseAudio();
        });

        // Play Button
        RoundedButton playBtn = new RoundedButton("Play");
        playBtn.setFont(new Font("Helvetica Neue", Font.BOLD, 12));
        playBtn.setBounds(475, 10,85,35);
        playBtn.setBorderPainted(false);
        playBtn.setBackground(null);
        playBtn.setForeground(Color.WHITE);

        playBtn.addActionListener(e -> {
            // go to the previous song
            audioPlayer.playAudio();
        });

        // Dequeue Button
        RoundedButton dequeueBtn = new RoundedButton("Dequeue");
        dequeueBtn.setFont(new Font("Helvetica Neue", Font.BOLD, 12));
        dequeueBtn.setBounds(575, 10,90,35);
        dequeueBtn.setBorderPainted(false);
        dequeueBtn.setBackground(null);
        dequeueBtn.setForeground(Color.WHITE);

        dequeueBtn.addActionListener(e -> {
            // go to the previous song
            audioPlayer.dequeueSong();
        });

        // playback slider
        JSlider playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(368,50 , 307, 25);
        playbackSlider.setBackground(null);




        // add Pause and Play button to the Bottom Panel
        bottomPanel.add(pauseBtn);
        bottomPanel.add(playBtn);
        bottomPanel.add(dequeueBtn);
        bottomPanel.add(playbackSlider);

        add(bottomPanel);

    }

    private JList<String> songJList; // Declare JList at class level

    private void addContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBounds(225, 0, 855, 580);

        // ===== Songs List Panel =====
        songsListPanel = new JPanel();
        songsListPanel.setLayout(null);

        // Create a JList for songs
        DefaultListModel<String> listModel = new DefaultListModel<>();
        songJList = new JList<>(listModel);  // Use DefaultListModel to manage song list

        // Load songs into the list model
        listModel.addElement("Denzel Curry - Dynasties & Dystopia");
        listModel.addElement("Stray Kids - Come Play");
        listModel.addElement("Paint the Town Blue");

        // Add a MouseListener to detect clicks on list items
        songJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {  // Single-click detection
                    int index = songJList.locationToIndex(e.getPoint());  // Get clicked index
                    if (index != -1) {
                        String selectedSong = listModel.getElementAt(index);
                        enqueueSelectedSong(selectedSong);
                    }
                }
            }
        });

        // JScrollPane for JList
        JScrollPane scrollPane = new JScrollPane(songJList);
        scrollPane.setBounds(25, 120, 785, 435);

        // Label for Songs List
        JLabel songListLabel = new JLabel("Available Songs");
        songListLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 30));
        songListLabel.setBounds(25, 60, 300, 45);

        // Add components to panel
        songsListPanel.add(songListLabel);
        songsListPanel.add(scrollPane);

        // ===== Check Queue Panel =====
        checkQueuePanel = new JPanel();
        checkQueuePanel.setLayout(null);

        checkQueueArea = new JTextArea();
        checkQueueArea.setEditable(false);
        checkQueueArea.setBounds(25, 120, 785, 435);

        JScrollPane checkQScrollPane = new JScrollPane(checkQueueArea);
        checkQScrollPane.setBounds(25, 120, 785, 435);

        JLabel checkQueueLabel = new JLabel("Queue");
        checkQueueLabel.setFont(new Font("Helvetica Neue", Font.BOLD, 30));
        checkQueueLabel.setBounds(25, 60, 150, 45);

        checkQueuePanel.add(checkQueueLabel);
        checkQueuePanel.add(checkQScrollPane);

        // Add panels to content panel
        contentPanel.add(songsListPanel, "songsList");
        contentPanel.add(checkQueuePanel, "checkQueue");

        add(contentPanel);
    }


    public void songlistButtonActionListener(ActionEvent e) {
            cardLayout.show(contentPanel, "songsList");
    }

    public void checkQButtonActionListener(ActionEvent e) {
            // 1. Change the CardLayout to show the panel where the song list is displayed
            cardLayout.show(contentPanel, "checkQueue");
    }
    
    private void enqueueSelectedSong(String songName) {
        Songs song = null;

        // Map song names to their file paths
        if (songName.contains("Dynasties")) {
            song = new Songs("Dynasties & Dystopia", "src/musics/Denzel Curry, Gizzle, Bren Joy - Dynasties & Dystopia ｜ Arcane League of Legends ｜ Riot Games Music.wav");
        } else if (songName.contains("Come Play")) {
            song = new Songs("Come Play", "src/musics/Stray Kids, Young Miko, Tom Morello - ＂Come Play＂ (from Arcane Season 2) [Official Visualizer].wav");
        } else if (songName.contains("Paint the Town Blue")) {
            song = new Songs("Paint the Town Blue", "src/musics/Paint The Town Blue (from the series Arcane League of Legends).wav");
        }

        if (song != null) {
            playlist.enqueue(song);  // Add to playlist
            JOptionPane.showMessageDialog(this, song.getName() + " has been added to the queue!");
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

// just a class for inheriting the properties of it to make the button round
class RoundedButton extends JButton {
    private final int radius;

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

