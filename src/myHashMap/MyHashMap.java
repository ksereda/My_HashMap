package myHashMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.hash;

public class MyHashMap<K, V> implements MyHashMapInterface<K, V> {

    private Node<K, V> firstNode;
    private Node<K, V> lastNode;
    private int size = 0;

    private Node<K, V>[] hashTable;
    private float threshold;


    public MyHashMap() {
        lastNode = new Node<K, V>();
        firstNode = new Node<K, V>();
        hashTable = new Node[16];  // инициализируем хеш таблицу с размером 16
        threshold = hashTable.length + 0.75f;  // степень загруженности хеш-таблицы (длинна массива * 0.75)
    }

    @Override
    public boolean insert(K key, V value) {
        if (threshold <= size + 1) { // если хеш-таблица перегружена, то
            threshold = threshold * 2; // умножаем ее размер на 2 и заново производим перераспределение всех ее элементов
            reSize();
        }

        Node<K, V> newNode = new Node<>();
        int index = hash(key);  // с помощью хеш функции по ключу получаем индекс

        // 1) либо Добавление
        if (hashTable[index] == null) {  // если hashTable под этим индексом = null, то там пусто и мы добавляем туда
            return addElement(index, value);
        }

//        List<Node<K,V>> nodeList = hashTable[index].getNodes(); // вернем из нашей таблицы Node с тем индексом, под которым уже есть значение
        List<Node<K,V>> nodeList = new LinkedList<>();
        nodeList.add(hashTable[index].getValue());   // вернем из нашей таблицы Node с тем индексом, под которым уже есть значение

        // 2) либо Коллизия
        // пробежимся по этому листу nodeList
        for (Node<K, V> node : nodeList) {  // если ключ уже существует но значение новое (другое), то мы перезаписываем ИЛИ если коллизия
            if (keyExist(node, newNode, value) || collision(node, newNode, nodeList)) {
                return true;
            }
        }

        return false;
    }

    // Перераспределение элементов в хеш-таблице
    private void reSize() {
        Node<K, V>[] oldHashTable = hashTable;
        hashTable = new Node[oldHashTable.length * 2];  // присваиваем новый путсой массив с длинной в 2 раза больше
        size = 0;
        // в этот hashTable добавляем все пары (ключ, значение)
        for (Node<K, V> node : oldHashTable) {  // пробегаемся по всей таблице
            if (node != null) {  // если node != null
//                for (Node<K, V> n : node.getNodes()) {  // берем лист нодов (getNodes) по индексу
                for (Node<K, V> n : node.getValue()) {  // берем значение по индексу
                    insert(n.key, n.value);  // и добавлем все пары (ключ, значение)
                }
            }
        }
    }

    private boolean addElement(int index, V value) {
//        hashTable[index] = new Node<>(null, null);  // создаем Node с ключем и значением = null (исходя из конструктора)
//        hashTable[index].getNodes().add(newNode);  // взяли лист Node и добавили туда эту Node
//        size++;
//        return true;

        hashTable[index] = new Node<>();  // создаем Node с ключем и значением = null (исходя из конструктора)
        hashTable[index] = addLastElement(value);
        size++;
        return true;
    }

    public Node<K, V> addLastElement(V value) {
//        Node<K, V> prev = lastNode;
//        prev.setValue(newNode);
//        lastNode = new Node<K, V>(null, prev, null);
//        prev.setNextElement(lastNode);
//        size++;
        Node<K, V> currentElement = lastNode;
        currentElement.setValue(value);
        lastNode = new Node<>();
        lastNode.setPrevElement(currentElement);
        currentElement.setNextElement(lastNode);
        size++;

        return currentElement;
    }

    private boolean keyExist(final Node<K, V> nodeFromList, final Node<K,V> newNode, final V value) {  // если ключи равны а значения не равны, то
        if (newNode.getKey().equals(nodeFromList.getKey()) && !newNode.getValue().equals(nodeFromList.getValue())) {
            nodeFromList.setValue(value);  // то добавляем новое значение
            return true;
        }
        return false;
    }

    // Коллизия !
    private boolean collision(final Node<K,V> node, final Node<K,V> newNode, final List<Node<K,V>> nodes) {
        // (очередная нода из листа под каким-то индексом, новая нода, весь лист nodeList)
        if (newNode.hashCode() == node.hashCode() && !Objects.equals(newNode.key, node.key) &&  // если хэшкод newNode = хэшкоду ноды из листа
                // и ключи и значения НЕ РАВНЫ
                !Objects.equals(newNode.value, node.value)) {

            nodes.add(newNode);  // в лист добавляем newNode
            size++;
            return true;

        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    // Удаление по ключу
    @Override
    public boolean delete(final K key) {
        int index = hash(key); // с помощью хеш функции по ключу получаем индекс

        if (hashTable[index] == null) { // если по этому индексу ничего нет, то вернем false
            return false;
        }

        // Если не подходят оба вариант, то у нас коллизия
//        List<Node<K, V>> nodeList = hashTable[index].getNodes();  // пробегаемся по всем элементам нашего листа
        List<Node<K, V>> nodeList = new LinkedList<>();
        nodeList.add(hashTable[index].getValue());
        for (Node<K, V> node : nodeList) {
            if (key.equals(node.getKey())) {  // если ключ = ключу в конкретной node
                nodeList.remove(node);  // то удаляем его
                return true;
            }
        }

        return false;
    }

    // Получение
    @Override
    public V get(final K key) {
        int index = hash(key);

        if (index < hashTable.length && hashTable[index] != null) {
//            List<Node<K, V>> list = hashTable[index].getNodes();
//            List<Node<K, V>> list = hashTable[index].getCurrentElem();
            List<Node<K, V>> list = new LinkedList<>();
            list.add( hashTable[index].getValue());

            for (Node<K, V> node : list) {  // пробегаемся по всему листу
                if (key.equals(node.getKey())) {  // и если ключи равны
                    return node.getValue();  // то возвращаем значение
                }
            }

        }

        return null;
    }


    private class Node<K, V> {

        private int hash;
        private K key;
        private V value;
        private Node<K, V> nextElement;
        private Node<K, V> prevElement;

        private Node() {
        }

        private int hash() {
            return hashCode() % hashTable.length;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Node<K, V> getNextElement() {
            return nextElement;
        }

        public void setNextElement(Node<K, V> nextElement) {
            this.nextElement = nextElement;
        }

        public Node<K, V> getPrevElement() {
            return prevElement;
        }

        public void setPrevElement(Node<K, V> prevElement) {
            this.prevElement = prevElement;
        }
    }

}
