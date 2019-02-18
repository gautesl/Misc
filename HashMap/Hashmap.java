import java.util.NoSuchElementException;

public class Hashmap<K, V> {
    Node[] nodes = null;

    @SuppressWarnings("unchecked")
    public Hashmap() {
        this.nodes = new Hashmap.Node[200];
    }

    @SuppressWarnings("unchecked")
    public Hashmap(int capacity) {
        nodes = new Hashmap.Node[capacity];
    }

    public void put(K key, V value) {
        int index = index(key);
        Node node = new Node(key, value);

        if (nodes[index] == null) nodes[index] = node;
        else nodes[index].place(node);
    }

    public V get(K key) {
        int index = index(key);
        Node temp = nodes[index];

        while (temp != null) {
            if (temp.key.equals(key))
                return temp.value;
            temp = key.hashCode() < temp.key.hashCode() ? temp.left : temp.right;
        }
        return null;
    }

    private int index(K key) {
        return Math.abs(key.hashCode() * 7) % nodes.length;
    }

    public void writeStatus() {
        for (int i = 0; i < nodes.length; i++) {
            System.out.printf("i:%d, depth:%d\n", i, depth(nodes[i]));
        }
    }

    private int depth(Node n) {
        return n == null ? 0 : Math.max(depth(n.left), depth(n.right)) + 1;
    }

    private class Node {
        Node left;
        Node right;
        K key;
        V value;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        void place(Node node) {
            if (node.key.equals(key)) {
                value = node.value;
                return;
            }

            if (node.key.hashCode() < key.hashCode()) {
                if (left == null) left = node;
                else left.place(node);
            } else {
                if (right == null) right = node;
                else right.place(node);
            }
        }
    }
}
