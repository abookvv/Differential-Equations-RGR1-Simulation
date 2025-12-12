// src/main/java/com/aussieinvader/core/PhysicsModel.java
package com.aussieinvader.core;

public class PhysicsModel {
    // Параметры из Таблицы 1
    public static final double M0 = 9100.0;      // кг
    public static final double M_FINAL = 6300.0; // кг
    public static final double Q = 130.0;        // кг/с
    public static final double U = 1550.0;       // м/с
    public static final double BETA = 0.1;       // коэф. сопротивления
    public static final double MU = 0.5;         // коэф. трения
    public static final double G = 9.81;         // м/с²

    // Производная скорости dv/dt
    public static double dvdt(double v, double m) {
        double thrust = Q * U / m;
        double airResistance = BETA * v * v / m;
        double friction = MU * G;
        return thrust - airResistance - friction;
    }

    // Время горения
    public static double getBurnTime() {
        return (M0 - M_FINAL) / Q;
    }
}