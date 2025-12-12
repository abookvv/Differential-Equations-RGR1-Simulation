// src/main/java/com/aussieinvader/core/AnalyticalSolver.java
package com.aussieinvader.core;

import java.util.ArrayList;
import java.util.List;

public class AnalyticalSolver {

    private static final double NU = 2 * Math.sqrt(PhysicsModel.BETA * PhysicsModel.U / PhysicsModel.Q);
    private static final double C = calculateConstantC();

    // Вычисление константы C из начального условия v(0) = 0
    private static double calculateConstantC() {
        double tau0 = (2.0 / PhysicsModel.Q) * Math.sqrt(PhysicsModel.BETA * PhysicsModel.MU * PhysicsModel.M0 * PhysicsModel.G);
        double y1Prime = computeY1Prime(tau0, 50);
        double y2Prime = computeY2Prime(tau0, 50);
        return -y2Prime / y1Prime;
    }

    public static SimulationResult solve(int terms, int plotPoints) {
        double tBurn = PhysicsModel.getBurnTime();
        double dt = tBurn / plotPoints;
        List<SimulationResult.DataPoint> data = new ArrayList<>();

        for (int i = 0; i <= plotPoints; i++) {
            double t = i * dt;
            double m = PhysicsModel.M0 - PhysicsModel.Q * t;
            double v = computeVelocity(t, terms);
            data.add(new SimulationResult.DataPoint(t, v, m));
        }

        return new SimulationResult(data, "Аналитическое (" + terms + " членов)");
    }

    private static double computeVelocity(double t, int maxTerms) {
        if (t > PhysicsModel.getBurnTime()) return 0;
        double tau = (2.0 / PhysicsModel.Q) * Math.sqrt(PhysicsModel.BETA * PhysicsModel.MU * (PhysicsModel.M0 - PhysicsModel.Q * t) * PhysicsModel.G);
        double y1 = computeY1(tau, maxTerms);
        double y2 = computeY2(tau, maxTerms);
        double y1p = computeY1Prime(tau, maxTerms);
        double y2p = computeY2Prime(tau, maxTerms);
        double numerator = C * y1p + y2p;
        double denominator = C * y1 + y2;
        return - (PhysicsModel.Q * tau / (2 * PhysicsModel.BETA)) * (numerator / denominator);
    }

    // Реализация функций Бесселя через ряды (см. фото 3)
    private static double computeY1(double x, int maxTerms) {
        return Math.pow(x, NU) * seriesSum(x, maxTerms, j -> j + NU);
    }
    private static double computeY2(double x, int maxTerms) {
        return Math.pow(x, -NU) * seriesSum(x, maxTerms, j -> j - NU);
    }
    private static double computeY1Prime(double x, int maxTerms) {
        return Math.pow(x, NU - 1) * seriesSum(x, maxTerms, j -> j + NU, k -> 2*k + NU);
    }
    private static double computeY2Prime(double x, int maxTerms) {
        return Math.pow(x, -NU - 1) * seriesSum(x, maxTerms, j -> j - NU, k -> 2*k - NU);
    }

    private static double seriesSum(double x, int maxTerms, java.util.function.IntToDoubleFunction prodFunc) {
        return seriesSum(x, maxTerms, prodFunc, k -> 1.0);
    }
    private static double seriesSum(double x, int maxTerms, java.util.function.IntToDoubleFunction prodFunc, java.util.function.IntToDoubleFunction coeffFunc) {
        double sum = 0.0;
        for (int k = 0; k <= maxTerms; k++) {
            double term = Math.pow(-1, k) * Math.pow(x, 2 * k) * coeffFunc.applyAsDouble(k);
            term /= Math.pow(4, k) * factorial(k);
            for (int j = 1; j <= k; j++) {
                term /= prodFunc.applyAsDouble(j);
            }
            sum += term;
        }
        return sum;
    }

    private static long factorial(int n) {
        long f = 1;
        for (int i = 2; i <= n; i++) f *= i;
        return f;
    }
}