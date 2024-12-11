
package queue.music.playlist.system;

import java.util.ArrayList;
import java.util.List;


public class queue_music_playlist_system<T> { // Generic class to handle any data type
    private List<T> queue;

    // Constructor
    public queue_music_playlist_system() {
        queue = new ArrayList<>(); // Initialize the list
    }

    // Method to add an element to the queue
    public void enqueue(T element) {
        // TODO: Add the element to the queue
    }

    // Method to remove and return the front element of the queue
    public T dequeue() {
        // TODO: Check if the queue is empty, then remove and return the first element
        return null; // Replace with the removed element
    }

    // Method to check if the queue is empty
    public boolean isEmpty() {
        // TODO: Return true if the queue is empty, false otherwise
        return false; // Replace with actual condition
    }

    // Method to view the front element without removing it
    public T peek() {
        // TODO: Check if the queue is empty, then return the first element
        return null; // Replace with the front element
    }

    // Method to get the size of the queue
    public int size() {
        // TODO: Return the size of the queue
        return 0; // Replace with the actual size
    }

}

