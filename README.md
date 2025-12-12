# Aussie Invader 5R — Mathematical Simulation 

This project implements a complete simulation of the **Aussie Invader 5R rocket car** motion, based on the physical model from the lab assignment. It includes:

- **Analytical solution** using Frobenius series (Bessel functions)
- **Numerical solutions**: Euler and Runge-Kutta 4th order methods
- **Interactive GUI** for parameter tuning and real-time comparison
- **Convergence analysis** and accuracy estimation

The goal is to determine whether the car can theoretically exceed the **1000 mph speed record**.

![Application Screenshot](screenshot.png)

---

## Key Results

| Method              | Max Speed (m/s) | Max Speed (mph) | Error vs Analytical |
|---------------------|------------------|------------------|----------------------|
| Analytical (20 terms) | 446.8           | **999.2**        | —                    |
| Runge-Kutta 4       | 446.7           | 998.9            | < 0.1 m/s            |
| Euler               | 442.5           | 989.8            | ~4.3 m/s             |

> **Conclusion**: With the given parameters, the car **approaches but does not exceed** the 1000 mph barrier (447.04 m/s).

---

## How to Run

### Prerequisites
- Java 11 or higher
- Maven

### Build and Run
```bash
git clone https://github.com/ваш-логин/aussie-invader-simulation.git
cd aussie-invader-simulation
mvn compile
mvn exec:java -Dexec.mainClass="com.aussieinvader.Main"
