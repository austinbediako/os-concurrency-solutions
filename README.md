#  Concurrency Problem Solutions
**Operating Systems Course Project – Group 6**

##  Project Overview
This project is a **concurrency and synchronization simulator** developed as part of the Operating Systems course.  
It demonstrates how operating systems manage **multiple concurrent threads**, **shared resources**, and **synchronization** using classic concurrency problems.

The system provides **deadlock-free solutions** to well-known synchronization problems and visually demonstrates **process/thread states and resource usage** through both **Console Output** and a **JavaFX GUI**.

---

## Objectives
The main objectives of this project are to:

- Understand and apply **concurrency concepts**
- Implement **classic synchronization problems**
- Use proper **synchronization primitives**
- Prevent **race conditions and deadlocks**
- Compare **performance of different synchronization approaches**
- Gain practical experience with **multithreaded programming**

---

##  Key Concepts Covered
- Processes vs Threads
- Concurrency and Parallelism
- Critical Sections
- Race Conditions
- Deadlocks and Deadlock Prevention
- Mutex Locks
- Semaphores
- Condition Variables
- Monitors
- Performance Measurement

---

##  Problems Implemented

### 1️. Producer–Consumer Problem
- Synchronization between producer and consumer threads
- Shared bounded buffer
- Prevents buffer overflow and underflow
- Uses **mutex locks** and **condition variables**
- **GUI**: Visualizes buffer filling/emptying and producer/consumer states (Working/Waiting).

### 2️. Dining Philosophers Problem
- Philosophers competing for shared forks
- Demonstrates deadlock scenarios and deadlock-free solutions
- Uses **semaphores** and resource ordering (Resource Hierarchy)
- **GUI**: Visualizes philosophers sitting at a table, changing color based on state (Thinking, Hungry, Eating) and fork usage.

### 3️. Readers–Writers Problem
- Manages concurrent access to shared data
- Multiple readers or one writer at a time
- Demonstrates fairness and priority control
- Uses **Monitors (synchronized/wait/notify)**
- **GUI**: Visualizes readers and writers accessing a central shared resource.

---

##  System Architecture

```
OSConcurrency/
│
├── Main.java
│   └── Console application entry point
│
├── com.concurrency.gui.ConcurrencyVisualizerApp
│   └── JavaFX GUI application entry point
│
├── ProducerConsumer.java
│   └── Producer–Consumer problem implementation
│
├── DiningPhilosophers.java
│   └── Dining Philosophers problem implementation
│
├── ReadersWriters.java
│   └── Readers–Writers problem implementation
│
├── PerformanceMetrics.java
│   └── Execution time and performance measurement
│
└── README.md
```

---

##  Technologies Used
- **Language:** Java
- **Concurrency Tools:**
    - `Thread`
    - `Runnable`
    - `synchronized`
    - `Semaphore`
    - `Lock`
    - `Condition`
- **UI Framework:** JavaFX
- **Platform:** Console-based or Desktop GUI

---

##  How to Run

### Console Mode
To run the standard console-based simulation:
```bash
mvn compile
java -cp target/classes com.concurrency.Main
```

### GUI Mode
To run the JavaFX visualizer:
```bash
mvn compile
mvn javafx:run
# OR if using the shade plugin or direct class run:
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp target/classes com.concurrency.gui.ConcurrencyVisualizerApp
```
*Note: Ensure you have a compatible JavaFX runtime if running directly with java command on JDK 11+.*

---

##  Visualization & Output

### Console
Logs show thread actions, resource usage, and wait states.
```
Producer-1 produced: 45 | Buffer size: 3
Consumer-2 consumed: 45 | Buffer size: 2
```

### GUI
- **Color Semantics**:
    - **Green**: Working / Eating / Producing / Consuming / Reading
    - **Orange**: Waiting / Hungry
    - **Blue**: Idle / Thinking
    - **Red**: Writing / Fork Taken
    - **Purple**: Buffer Slot Full

---

##  Performance Measurement
- Execution time
- Waiting time
- Throughput (Operations per second)

Measured using `System.nanoTime()` and displayed in the GUI's Metrics Panel.

---

## Team Roles & Contributions

| Developer | Responsibility |
|--|---------------|
| Akelisiyine Desmond Nsoh Brew | Producer–Consumer |
| Austin Bediako Tsibuah | Dining Philosophers |
| Ebenezer Fuachie | Readers–Writers |
| Jessy Kankam Yeboah | Integration & Performance |

---

##  Testing Strategy
- Independent module testing
- Multiple-thread simulations
- Edge case validation
- Log-based verification
- **Unit Tests**: Run `mvn test` to verify logic correctness.

---

##  Conclusion
This project demonstrates practical understanding of **Operating Systems concurrency concepts** through clean, deadlock-free implementations and structured performance evaluation, enhanced by a real-time visualization tool.
