package myHashMap;

public interface MyHashMapInterface<K, V> {

    boolean insert(K key, V value);   // вставить

    boolean delete(K key);

    V get(K key);   // вернуть

    int size();

}
