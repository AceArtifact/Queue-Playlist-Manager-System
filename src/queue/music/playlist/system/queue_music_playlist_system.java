
package queue.music.playlist.system;

import java.util.ArrayList;
import java.util.List;


public class queue_music_playlist_system<T> { 
    List<T> queue;

    
    public queue_music_playlist_system() {

        queue = new ArrayList<>(); 

    }


    public void enqueue(T element) {

        queue.add(element);
    }


    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty! Cannot dequeue.");
        }
        return queue.remove(0); // Removes and returns the first element
    }
    
    public T clearQueue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty! Cannot clear.");
        }
        T front = queue.get(0);
        queue.clear();                
        return front;           
    }

    
    public boolean isEmpty() {
        
        return queue.isEmpty();
    }

  
    public T peek() {
        
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty! Cannot peek.");
        }
        return queue.get(0); 
    }

    public int size() {
        return queue.size();
    }

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

