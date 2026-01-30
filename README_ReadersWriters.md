# Readers–Writers Problem

## 1. Problem Overview
The **Readers–Writers Problem** involves a shared dataset accessed by multiple threads. It distinguishes between:
*   **Readers:** Only read data; multiple readers can access concurrently.
*   **Writers:** Read and modify data; require exclusive access (no other writers, no readers).

### Real-Life Analogy
Consider a Wikipedia page.
*   **Readers:** Millions of people can read the page simultaneously without issues.
*   **Writers:** If an editor wants to update the page, no one else should be reading the old version or writing over it at the exact same moment.

---

## 2. Thread & Resource Mapping

| Component | Java Implementation | Role |
|-----------|--------------------|------|
| **Reader** | `Reader` (Thread) | Reads the shared variable `data`. |
| **Writer** | `Writer` (Thread) | Updates `data` with a random value. |
| **Database** | `int data` | The shared resource. |
| **Controller** | `ReadersWritersMonitor` | Manages entry/exit logic. |

---

## 3. Synchronization Strategy: Reader-Preference Monitor
This implementation uses Java's built-in `synchronized` keyword (intrinsic locks) to manage access state variables (`readers` count and `writerActive` flag).

### The Algorithm (Reader Preference)
**Readers:**
*   Wait only if a `writerActive` is true.
*   Increment `readers` count.
*   Proceed to read.
*   **Result:** Readers can "gang up" on a writer. If a new reader arrives while a reader is already reading, it enters immediately, effectively bypassing the waiting writer.

**Writers:**
*   Wait if `writerActive` is true OR `readers > 0`.
*   Set `writerActive = true`.
*   Write.
*   **Result:** Writers must wait for *all* current readers to finish.

**Code Reference:**
```java
// Reader Entry
public synchronized void startRead() {
    while (writerActive) wait();
    readers++;
}

// Writer Entry
public synchronized void startWrite() {
    while (writerActive || readers > 0) wait();
    writerActive = true;
}
```

---

## 4. Starvation Analysis (Critical)
This implementation follows the **First Readers-Writers Problem** (Reader Preference).
*   **Risk:** Writer Starvation.
*   **Scenario:** If readers arrive fast enough such that `readers` never drops to 0, the writer will wait indefinitely.
*   **Mitigation:** In this simulation, `Thread.sleep` ensures gaps in arrival, allowing writers to eventually proceed. However, in a high-frequency system, this logic would require a fairness policy (e.g., stopping new readers if a writer is waiting).

---

## 5. Visualization & Output
*   `Reader-1 started reading. Readers = 2` -> Shows concurrency (multiple readers).
*   `Writer-1 started writing.` -> Shows exclusivity (always appears alone).
