package Mangers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractService<T> {
    protected Map<String, T> items = new HashMap<>();

    public T getById(String id) {
        return items.get(id);
    }

    public Collection<T> getAll() {
        return items.values();
    }

    public boolean exists(String id) {
        return items.containsKey(id);
    }

}
