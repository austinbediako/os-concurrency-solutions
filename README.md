#  Concurrency Problem Solutions
**Operating Systems Course Project – Group 6**

##  Project Overview
This project is a **concurrency and synchronization simulator** developed as part of the Operating Systems course.  
It demonstrates how operating systems manage **multiple concurrent threads**, **shared resources**, and **synchronization** using classic concurrency problems.

The system provides **deadlock-free solutions** to well-known synchronization problems and visually demonstrates **process/thread states and resource usage** through console output.

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

### 2️. Dining Philosophers Problem
- Philosophers competing for shared forks
- Demonstrates deadlock scenarios and deadlock-free solutions
- Uses **semaphores** and resource ordering

### 3️. Readers–Writers Problem
- Manages concurrent access to shared data
- Multiple readers or one writer at a time
- Demonstrates fairness and priority control
- Uses **read-write locks / semaphores**

---

##  System Architecture

```
OSConcurrency/
│
├── Main.java
│   └── Application entry point and menu controller
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
- **Platform:** Console-based
---

##  Program Flow
```
1. Run Producer–Consumer Simulation
2. Run Dining Philosophers Simulation
3. Run Readers–Writers Simulation
4. Exit
```

---

##  Visualization & Output
Console-based logs show:
- Thread actions
- Resource usage
- Waiting and signaling
- Execution states

Example:
```
Producer-1 produced item
Consumer-2 consumed item
Buffer size: 3
```

---

##  Performance Measurement
- Execution time
- Waiting time
- Throughput

Measured using:
```java
System.nanoTime()
```

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

---

##  Documentation
- Well-commented source code
- This README
- 10-page technical report

---




##  Conclusion
This project demonstrates practical understanding of **Operating Systems concurrency concepts** through clean, deadlock-free implementations and structured performance evaluation.
