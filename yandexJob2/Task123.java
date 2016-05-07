// Задание 1
// Ответ: варианты d, e, f, g.
//
// Задание 2
// В строчках 2 и 4 нет отступа.
// В строчке 10 действие doDigest лучше выполнять между строчек 6 и 7, т.к. его можно не синхронизировать,
// и среднее время ожидания будет меньше.
//
// Задание 3
public class Task123 {
    class Node {
        int payload;
        Node next;
    }

    static Node reverse(Node head) {
        Node prevNode = null;
        Node nextNode;
        while (head != null) {
            nextNode = head.next;
            head.next = prevNode;
            prevNode = head;
            head = nextNode;
        }
        return prevNode;
    }
}