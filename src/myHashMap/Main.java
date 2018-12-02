package myHashMap;

public class Main {

    public static void main(String[] args) {
        MyHashMap<String, String> map = new MyHashMap<>();
        map.insert("key1", "value1");
        map.insert("key2", "value2");
        map.insert("key3", "value3");
        System.out.println(map.get("key1"));
        System.out.println("\nРазмер коллекции: " + map.size());
    }

}
