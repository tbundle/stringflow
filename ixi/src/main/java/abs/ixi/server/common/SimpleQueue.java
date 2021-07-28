package abs.ixi.server.common;

import java.util.Collection;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;

import abs.ixi.util.CollectionUtils;

/**
 * This is a very simple implementation of a Queue. This queue is intented to be
 * used by for each session created witin server. It does not implement
 * {@link Queue} interface.
 * 
 * @author Yogi
 *
 */
public class SimpleQueue<T> implements Cloneable {
	private Node<T> head;
	private Node<T> tail;
	private int size;

	public SimpleQueue() {
		// Nothing to do here
	}

	public SimpleQueue(Collection<T> elements) {
		if (!CollectionUtils.isNullOrEmpty(elements)) {
			for (T elm : elements) {
				add(elm);
			}
		}
	}

	public synchronized T getHead() {
		return this.isEmpty() ? null : this.head.elm;
	}

	/**
	 * Add an element to the queue
	 * 
	 * @param elm
	 */
	public synchronized void add(T elm) {
		Node<T> node = new Node<>(elm);

		if (this.head == null) {
			this.head = node;
			this.tail = this.head;

		} else {
			this.tail.next = node;
			this.tail = this.tail.next;
		}

		size++;
	}

	/**
	 * Check Q is empty or not.
	 * 
	 * @return
	 */
	public synchronized boolean isEmpty() {
		return this.head == null;
	}

	/**
	 * Drop elements from the queue starting from head
	 * 
	 * @param count
	 */
	public synchronized void drop(int count) {
		while (this.head != null && count > 0) {
			this.head = this.head.next;
			count--;
			size--;
		}
	}

	/**
	 * Drop the head element from the queue
	 * 
	 * @return dropped element
	 */
	public synchronized T drop() {
		T element = null;

		if (this.head != null) {
			element = this.head.elm;
			this.head = this.head.next;
			size--;
		}

		return element;
	}

	public synchronized boolean drop(Predicate<T> c) {
		if (c.test(this.getHead())) {
			this.drop();
			return true;
		}

		return false;
	}

	/**
	 * Perform a given action on each of the elements in this queue
	 * 
	 * @param consumer
	 */
	public synchronized void forEach(Consumer<T> consumer) {
		Node<T> node = this.head;

		while (node != null) {
			T element = node.elm;
			consumer.accept(element);

			node = node.next;
		}
	}

	/**
	 * make this queue empty.
	 * 
	 * @return
	 */
	public boolean clear() {
		this.head = null;
		this.tail = null;
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		SimpleQueue<T> queue = new SimpleQueue<>();
		queue.head = this.head;
		queue.tail = this.tail;
		return queue;
	}

	public Object size() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This is basic building block of the {@link SimpleQueue}
	 */
	class Node<V> {
		private V elm;
		private Node<V> next;

		public Node() {
			// do-nothing constructor
		}

		public Node(V elm) {
			this.elm = elm;
			this.next = null;
		}
	}

}
