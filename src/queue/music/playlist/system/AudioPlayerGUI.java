package queue.music.playlist.system;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.*;

public class AudioPlayerGUI extends JFrame{
    // color configurations


    private JPanel leftPanel, bottomPanel;

    private JSlider playbackSlider;

    public AudioPlayerGUI() {
        // calls JFrame constructor to configure out gui and set the title heaader to "Music Player"
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

        /* change the frame color
        getContentPane().setBackground(FRAME_COLOR);
        */


        // add the gui components
        addGuiComponents();

        // ----- THIS
        // load record image
        JLabel backgroundImage = new JLabel(loadImage("src/images/mainbackground.png"));
        // Set the size of the label to cover the entire frame
        backgroundImage.setBounds(0, 0, 1280,720);
        add(backgroundImage);

    }

    //
    private void addGuiComponents() {


        // add the Bottom Part to this method
        addBottomButtons();

        // add the Left Part to this method
        addLeftButtons();



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
        songListBtn.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
        songListBtn.setBounds(1, 120, 125, 45); // x, y, width, height
        songListBtn.setBorderPainted(false);
        songListBtn.setBackground(null);
        songListBtn.setForeground(Color.decode("#b3b3b3"));

        // Check Queue Button
        JButton checkQueueBtn = new JButton("Check Queue");
        checkQueueBtn.setFont(new Font("Helvetica Neue", Font.BOLD, 14));
        checkQueueBtn.setBounds(9, 190, 135, 45); // x, y, width, height
        checkQueueBtn.setBorderPainted(false);
        checkQueueBtn.setBackground(null);
        checkQueueBtn.setForeground(Color.decode("#b3b3b3"));

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

        // Play Button
        RoundedButton playBtn = new RoundedButton("Play");
        playBtn.setFont(new Font("Helvetica Neue", Font.BOLD, 12));
        playBtn.setBounds(475, 10,85,35);
        playBtn.setBorderPainted(false);
        playBtn.setBackground(null);
        playBtn.setForeground(Color.WHITE);

        // Dequeue Button
        RoundedButton dequeueBtn = new RoundedButton("Dequeue");
        dequeueBtn.setFont(new Font("Helvetica Neue", Font.BOLD, 12));
        dequeueBtn.setBounds(575, 10,90,35);
        dequeueBtn.setBorderPainted(false);
        dequeueBtn.setBackground(null);
        dequeueBtn.setForeground(Color.WHITE);

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


// Custom JPanel with transparency
class TransparentPanel extends JPanel {
    private float opacity = 0.5f; // Set transparency level (0.0f to 1.0f)

    public TransparentPanel() {
        setOpaque(false); // Ensure JPanel is not opaque
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        // Set the transparency using AlphaComposite
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        // Set the background color
        g2d.setColor(Color.decode("#212121")); // You can change the color here
        g2d.fillRect(0, 0, getWidth(), getHeight()); // Fill the panel with the color
        g2d.dispose(); // Dispose of the graphics object
        super.paintComponent(g); // Ensure other components are painted properly
    }
}
