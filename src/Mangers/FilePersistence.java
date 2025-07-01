package Mangers;

import java.util.Collection;

public interface FilePersistence<T> {
    void saveToFile(String filename, Collection<T> items);
    Collection<T> loadFromFile(String filename);
}
