
package queue.music.playlist.system;

import java.util.ArrayList;
import java.util.List;


public class queue_music_playlist_system<T> { // Generic class to handle any data type
    List<T> queue;

    // Constructor
    public queue_music_playlist_system() {

        queue = new ArrayList<>(); // Initialize the list

    }

    // Method to add an element to the queue
    public void enqueue(T element) {

        queue.add(element);
    }

    // Method to remove and return the front element of the queue
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty! Cannot dequeue.");
        }
        return queue.remove(0); // Removes and returns the first element
    }

    // Method to check if the queue is empty
    public boolean isEmpty() {
        // TODO: Return true if the queue is empty, false otherwise
        return queue.isEmpty();
    }

    // Method to view the front element without removing it
    public T peek() {
        // TODO: Check if the queue is empty, then return the first element
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty! Cannot peek.");
        }
        return queue.get(0); // Return the first element
    }

    // Method to get the size of the queue
    public int size() {
        // TODO: Return the size of the queue
        return queue.size();
    }

    // Method to display all elements in the queue
    public void display() {
        if (isEmpty()) {
            System.out.println("Queue is empty!");
        } else {
            for (int i = 0; i < queue.size(); i++) {
                System.out.println(queue.get(i));
            }
        }
    }
    

}

