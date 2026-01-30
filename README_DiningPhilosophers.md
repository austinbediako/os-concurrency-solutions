# Dining Philosophers Problem

## 1. Problem Overview
The **Dining Philosophers Problem** is a classic synchronization problem that illustrates the challenges of allocating limited shared resources (forks) among multiple concurrent processes (philosophers) without causing deadlock or starvation.

### Real-Life Analogy
Imagine 5 philosophers sitting around a circular table. Between each pair of philosophers is a single fork. To eat, a philosopher must acquire **both** the fork to their left and the fork to their right. If all philosophers pick up their left fork simultaneously, no one can pick up their right fork, leading to a standstill (deadlock).

---

## 2. Thread & Resource Mapping

| Component | Java Implementation | Role |
|-----------|--------------------|------|
| **Philosopher** | `Philosopher` (Thread) | The active agent that thinks and eats. |
| **Fork** | `Semaphore(1)` | The shared resource. A permit of 1 represents "available". |
| **Table** | `DiningPhilosophers` (Class) | Initializes resources and manages the simulation. |

---

## 3. Synchronization Strategy: Resource Hierarchy
This implementation uses the **Resource Hierarchy** solution to prevent deadlocks.

### The Algorithm
Instead of blindly picking up left then right forks, philosophers are required to pick up forks in a specific order based on the fork's index (ID):
1. Identify `leftFork` and `rightFork` indices.
2. **ALWAYS** acquire the lower-indexed fork first.
3. Then acquire the higher-indexed fork.

**Code Reference:**
```java
int firstFork = Math.min(leftFork, rightFork);
int secondFork = Math.max(leftFork, rightFork);

forks[firstFork].acquire();
forks[secondFork].acquire();
```

### Deadlock Handling
This strategy explicitly breaks the **Circular Wait** condition, one of the four necessary conditions for deadlock (Coffman conditions).
*   **Mutual Exclusion:** Holds (Forks are semaphores).
*   **Hold and Wait:** Holds (Philosopher holds one fork while waiting for the other).
*   **No Preemption:** Holds (Forks cannot be forcibly taken).
*   **Circular Wait:** **BROKEN**. Because the strict ordering prevents a cycle. The philosopher with the highest numbered fork will try to pick up the lowest numbered fork (which is already being contended for by the lowest numbered philosopher), preventing the circle from closing.

---

## 4. Visualization & Output
The console logs illustrate the state transitions of each philosopher:
*   `Philosopher X is thinking.` (Resource Release / Idle)
*   `Philosopher X is eating.` (Resource Acquisition / Critical Section)
*   `Philosopher X stopped.` (Termination)

Run the simulation to observe that the system never freezes, proving the deadlock-free property.

## 5. Performance Notes
*   **Throughput:** Depends on `Thread.sleep` durations for eating/thinking.
*   **Fairness:** Java's `Semaphore` is initialized as non-fair by default. While deadlock is impossible, starvation is theoretically possible (if a philosopher is constantly preempted), though unlikely with random sleep intervals.
