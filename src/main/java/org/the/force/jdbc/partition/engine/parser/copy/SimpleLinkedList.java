package org.the.force.jdbc.partition.engine.parser.copy;

/**
 * Created by xuji on 2017/5/27.
 */
public class SimpleLinkedList<E> {

    private int size = 0;

    private SimpleLinkedList.Node<E> first;
    private SimpleLinkedList.Node<E> last;

    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    public E remove(SimpleLinkedList.Node<E> x) {
        // assert x != null;
        final E element = x.item;
        final SimpleLinkedList.Node<E> next = x.next;
        final SimpleLinkedList.Node<E> prev = x.prev;

        if (prev == null) {//remove first
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {//remove last
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }
        x.item = null;
        size--;
        return element;
    }

    public int size() {
        return size;
    }

    public SimpleLinkedList.Node<E> first() {
        return first;
    }


    public static class Node<E> {
        private E item;
        private SimpleLinkedList.Node<E> next;
        private SimpleLinkedList.Node<E> prev;

        Node(SimpleLinkedList.Node<E> prev, E element, SimpleLinkedList.Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }

        public SimpleLinkedList.Node<E> next() {
            return next;
        }

        public E item() {
            return item;
        }

    }

    void linkFirst(E e) {
        final SimpleLinkedList.Node<E> f = first;
        final SimpleLinkedList.Node<E> newNode = new SimpleLinkedList.Node<>(null, e, f);
        first = newNode;
        if (f == null)
            last = newNode;
        else
            f.prev = newNode;
        size++;
    }


    /**
     * Links e as last element.
     */
    void linkLast(E e) {
        final SimpleLinkedList.Node<E> l = last;
        final SimpleLinkedList.Node<E> newNode = new SimpleLinkedList.Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
    }



}
