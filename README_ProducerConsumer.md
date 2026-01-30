# Producer–Consumer Problem

## 1. Problem Overview
The **Producer–Consumer Problem** (or Bounded-Buffer Problem) challenges us to coordinate threads that generate data (Producers) and threads that use data (Consumers) via a shared, fixed-size buffer. We must ensure:
1. Producers do not add data when the buffer is full (Overflow).
2. Consumers do not remove data when the buffer is empty (Underflow).
3. Access to the buffer is mutually exclusive.

### Real-Life Analogy
Think of a bakery (Producer) and customers (Consumers). The display case (Buffer) has space for only 5 cakes.
*   If the case is full, the baker must wait.
*   If the case is empty, looking customers must wait.
*   Only one person (baker or customer) can touch the display case at a time to avoid accidents.

---

## 2. Thread & Resource Mapping

| Component | Java Implementation | Role |
|-----------|--------------------|------|
| **Producer** | `Producer` (Thread) | Generates integers and adds them to the queue. |
| **Consumer** | `Consumer` (Thread) | Removes integers from the queue. |
| **Buffer** | `Queue<Integer>` | The shared bounded storage. |
| **Mutex** | `ReentrantLock` | Ensures only one thread modifies the queue at a time. |
| **Signaling** | `Condition` (`notFull`, `notEmpty`) | Notifies threads when state changes. |

---

## 3. Synchronization Strategy: Monitor Pattern
This implementation uses explicit **Locks** and **Condition Variables** to create a robust monitor.

### The Algorithm
**Producer Logic:**
1. Acquire Lock.
2. `while (buffer == full)` -> await `notFull`.
3. Add item.
4. Signal `notEmpty`.
5. Release Lock.

**Consumer Logic:**
1. Acquire Lock.
2. `while (buffer == empty)` -> await `notEmpty`.
3. Remove item.
4. Signal `notFull`.
5. Release Lock.

**Code Reference:**
```java
lock.lock();
try {
    while (queue.size() == capacity) {
        notFull.await(); // Releases lock and sleeps
    }
    // Critical Section
    queue.add(val);
    notEmpty.signal();
} finally {
    lock.unlock();
}
```

---

## 4. Visualization & Output
The console provides real-time feedback on the buffer state:
*   `Producer-1 produced: 42 | Buffer size: 3`
*   `Consumer-2 consumed: 42 | Buffer size: 2`

This clearly demonstrates the preservation of the invariant: `0 <= size <= capacity`.

## 5. Performance Notes
*   **Wait Time:** Measured using `System.nanoTime()` around the `await()` calls.
*   **Efficiency:** Using `signal()` instead of `signalAll()` is efficient here because one producer only enables one consumer (and vice versa), reducing "thundering herd" wake-ups.
