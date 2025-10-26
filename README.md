# Multi‑Processor Scheduling Simulation (Operating Systems Project)

This Java project simulates how an **Operating System kernel** can distribute tasks among multiple processors, considering **deadlines, task values, hardware requirements**, and **processor specialization**.

It’s designed as an educational model of scheduler behavior in multi‑core environments.

---

##  Overview

Each processor executes tasks whose hardware specs are compatible with its own (cache, memory, frequency).  
The simulation manages task arrivals, selection, and completion using Java threads — ensuring safe concurrent execution.

Core goals implemented:
- Task scheduling based on **deadline + task value** optimization  
- Compatibility checks between tasks and processors  
- Thread‑based simulation of concurrent execution  
- Handling idle processors (free‑time management)  
- Optional random selection mode for fair task allocation  
- Printing results: start & end time, assigned processor, and completion status.

## Files

`ProcessorSimulation.java` — Main Java source implementing scheduling, task struct, and simulation logic.  

## Run

Compile & execute the simulation using:
```bash
javac ProcessorSimulation.java
```

### Features
The simulation includes:

- Reading input list of processors and tasks
- Checking hardware compatibility for each task
- Managing concurrent execution via Java Threads (each processor behaves as one thread)
- Implementing deadline‑aware task selection
- Generating and printing detailed output tables with task–processor mapping.

### Core Concepts
The scheduling model follows principles of Operating Systems:

- Deadlines & Value‑based prioritization
- Specialization constraint → each processor runs only tasks matching its capabilities
- Thread safety mechanism (simulating multi‑core execution)
- Idleness optimization → avoiding unnecessary waiting during task assignment

 ## Educational Goal
This project demonstrates practical understanding of multi‑core scheduling in an OS context 

- how tasks are assigned based on hardware and timing constraints, and how the system maximizes throughput while preserving fairness.
