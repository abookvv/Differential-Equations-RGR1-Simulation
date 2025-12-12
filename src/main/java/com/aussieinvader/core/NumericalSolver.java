// src/main/java/com/aussieinvader/core/NumericalSolver.java
package com.aussieinvader.core;

import java.util.ArrayList;
import java.util.List;

public class NumericalSolver {

    public static SimulationResult solveEuler(int steps) {
        return solve(steps, "Метод Эйлера", NumericalSolver::eulerStep);
    }

    public static SimulationResult solveRK4(int steps) {
        return solve(steps, "Рунге-Кутта 4", NumericalSolver::rk4Step);
    }

    private static SimulationResult solve(int steps, String methodName, StepFunction stepFunc) {
        double tBurn = PhysicsModel.getBurnTime();
        double dt = tBurn / steps;

        List<SimulationResult.DataPoint> data = new ArrayList<>();
        double t = 0.0, v = 0.0, m = PhysicsModel.M0;
        data.add(new SimulationResult.DataPoint(t, v, m));

        for (int i = 0; i < steps; i++) {
            double[] next = stepFunc.step(t, v, m, dt);
            t = next[0]; v = next[1]; m = next[2];
            data.add(new SimulationResult.DataPoint(t, v, m));
        }

        return new SimulationResult(data, methodName);
    }

    @FunctionalInterface
    private interface StepFunction {
        double[] step(double t, double v, double m, double dt);
    }

    private static double[] eulerStep(double t, double v, double m, double dt) {
        double dv = PhysicsModel.dvdt(v, m) * dt;
        v += dv;
        m -= PhysicsModel.Q * dt;
        t += dt;
        return new double[]{t, v, m};
    }

    private static double[] rk4Step(double t, double v, double m, double dt) {
        double k1_v = PhysicsModel.dvdt(v, m);
        double k1_m = -PhysicsModel.Q;

        double k2_v = PhysicsModel.dvdt(v + k1_v * dt/2, m + k1_m * dt/2);
        double k2_m = -PhysicsModel.Q;

        double k3_v = PhysicsModel.dvdt(v + k2_v * dt/2, m + k2_m * dt/2);
        double k3_m = -PhysicsModel.Q;

        double k4_v = PhysicsModel.dvdt(v + k3_v * dt, m + k3_m * dt);
        double k4_m = -PhysicsModel.Q;

        v += (dt/6) * (k1_v + 2*k2_v + 2*k3_v + k4_v);
        m += (dt/6) * (k1_m + 2*k2_m + 2*k3_m + k4_m);
        t += dt;
        return new double[]{t, v, m};
    }
}