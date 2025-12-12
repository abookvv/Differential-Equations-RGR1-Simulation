// src/main/java/com/aussieinvader/core/SimulationResult.java
package com.aussieinvader.core;

import java.util.List;

public class SimulationResult {
    private final List<DataPoint> data;
    private final double maxVelocity;
    private final double timeOfMaxVelocity;
    private final String methodName;

    public SimulationResult(List<DataPoint> data, String methodName) {
        this.data = data;
        this.methodName = methodName;
        // Находим максимум
        DataPoint max = data.stream()
                .max((p1, p2) -> Double.compare(p1.velocity, p2.velocity))
                .orElse(new DataPoint(0, 0, PhysicsModel.M0));
        this.maxVelocity = max.velocity;
        this.timeOfMaxVelocity = max.time;
    }

    // Геттеры
    public List<DataPoint> getData() { return data; }
    public double getMaxVelocity() { return maxVelocity; }
    public double getTimeOfMaxVelocity() { return timeOfMaxVelocity; }
    public String getMethodName() { return methodName; }

    public static class DataPoint {
        public final double time;
        public final double velocity;
        public final double mass;

        public DataPoint(double time, double velocity, double mass) {
            this.time = time;
            this.velocity = velocity;
            this.mass = mass;
        }
    }
}