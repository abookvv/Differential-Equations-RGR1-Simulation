// src/main/java/com/aussieinvader/gui/MainFrame.java
package com.aussieinvader.gui;

import com.aussieinvader.core.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class MainFrame extends JFrame {
    private final JTextArea resultArea;
    private final ChartPanel chartPanel;
    private final ControlPanel controlPanel;

    public MainFrame() {
        setTitle("🚀 Aussie Invader 5R Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // === ЛЕВАЯ ПАНЕЛЬ: ОТЧЁТ (раньше была справа внизу) ===
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15)); // Крупный, жирный шрифт
        resultArea.setBackground(Color.BLACK);
        resultArea.setForeground(Color.WHITE);
        resultArea.setText(
                "=== Aussie Invader 5R Simulation ===\n\n" +
                        "Запустите симуляцию, чтобы увидеть результаты.\n" +
                        "Максимальная скорость, сравнение методов и вывод о рекорде\n" +
                        "будут отображены здесь.\n\n" +
                        "🔹 Макс. скорость достигается в момент t = 21.54 с\n" +
                        "🔹 Рекорд: 1000 миль/ч = 447.04 м/с"
        );
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setPreferredSize(new Dimension(500, getHeight())); // Широкая панель слева

        // === ПРАВАЯ ПАНЕЛЬ: УПРАВЛЕНИЕ + ГРАФИК ===
        JPanel rightPanel = new JPanel(new BorderLayout());

        // Управление (джойстики) — помещаем ВВЕРХ
        controlPanel = new ControlPanel();
        controlPanel.setPreferredSize(new Dimension(0, 220)); // Компактная высота
        rightPanel.add(controlPanel, BorderLayout.NORTH);

        // График — занимает оставшееся пространство
        XYSeriesCollection dataset = new XYSeriesCollection();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Скорость от времени",
                "Время (с)", "Скорость (км/ч)",
                dataset, PlotOrientation.VERTICAL, true, true, false
        );
        chartPanel = new ChartPanel(chart);
        rightPanel.add(chartPanel, BorderLayout.CENTER);

        // === СОБИРАЕМ ВСЁ ===
        add(resultScroll, BorderLayout.WEST);  // Отчёт — СЛЕВА
        add(rightPanel, BorderLayout.CENTER); // Управление+График — СПРАВА

        // Подключаем обработчик (без изменений)
        controlPanel.setOnRunListener(this::runSimulation);
    }

    private void runSimulation(ControlPanel.SimulationParams params) {
        SwingUtilities.invokeLater(() -> {
            resultArea.setText("Выполнение симуляции...\n");
            resultArea.setCaretPosition(resultArea.getDocument().getLength());
        });

        new Thread(() -> {
            try {
                // Выполняем расчеты
                SimulationResult numericalResult = "euler".equals(params.method) ?
                        NumericalSolver.solveEuler(params.steps) :
                        NumericalSolver.solveRK4(params.steps);

                SimulationResult analyticalResult;
                if (params.showAnalytical) {
                    analyticalResult = AnalyticalSolver.solve(params.terms, 200);
                } else {
                    analyticalResult = null;
                }

                // Строим график
                XYSeries numericalSeries = new XYSeries(numericalResult.getMethodName());
                for (var point : numericalResult.getData()) {
                    numericalSeries.add(point.time, point.velocity * 3.6);
                }

                XYSeriesCollection dataset = new XYSeriesCollection();
                dataset.addSeries(numericalSeries);

                if (analyticalResult != null) {
                    XYSeries analyticalSeries = new XYSeries(analyticalResult.getMethodName());
                    for (var point : analyticalResult.getData()) {
                        analyticalSeries.add(point.time, point.velocity * 3.6);
                    }
                    dataset.addSeries(analyticalSeries);
                }

                // Обновляем UI в EDT
                SwingUtilities.invokeLater(() -> {
                    chartPanel.setChart(ChartFactory.createXYLineChart(
                            "Скорость Aussie Invader 5R от времени",
                            "Время (с)", "Скорость (км/ч)",
                            dataset, PlotOrientation.VERTICAL, true, true, false
                    ));

                    // Формируем отчет
                    StringBuilder report = new StringBuilder();
                    DecimalFormat df = new DecimalFormat("#.##");
                    DecimalFormat df4 = new DecimalFormat("#.####");

                    report.append("=== РЕЗУЛЬТАТЫ СИМУЛЯЦИИ ===\n\n");
                    report.append("Параметры:\n");
                    report.append("  - Метод: ").append(numericalResult.getMethodName()).append("\n");
                    report.append("  - Число шагов: ").append(params.steps).append("\n");
                    if (params.showAnalytical) {
                        report.append("  - Число членов ряда: ").append(params.terms).append("\n\n");
                    }

                    double maxVNum = numericalResult.getMaxVelocity();
                    report.append("ЧИСЛЕННЫЙ МЕТОД:\n");
                    report.append("  - Макс. скорость: ").append(df.format(maxVNum)).append(" м/с\n");
                    report.append("                  = ").append(df.format(maxVNum * 3.6)).append(" км/ч\n");
                    report.append("                  = ").append(df.format(maxVNum * 3.6 / 1.609)).append(" миль/ч\n");
                    report.append("  - Время максимума: ").append(df.format(numericalResult.getTimeOfMaxVelocity())).append(" с\n\n");

                    if (analyticalResult != null) {
                        double maxVAna = analyticalResult.getMaxVelocity();
                        double error = Math.abs(maxVNum - maxVAna);
                        double relError = (error / maxVAna) * 100;

                        report.append("АНАЛИТИЧЕСКОЕ РЕШЕНИЕ:\n");
                        report.append("  - Макс. скорость: ").append(df.format(maxVAna)).append(" м/с\n");
                        report.append("  - Погрешность:    ").append(df4.format(error)).append(" м/с (").append(df.format(relError)).append("%)\n\n");

                        if (maxVAna * 3.6 / 1.609 > 1000) {
                            report.append("✅ ТЕОРЕТИЧЕСКИ ПОБЬЕТ РЕКОРД В 1000 МИЛЬ/ЧАС!\n");
                        } else {
                            report.append("❌ Не хватает для рекорда в 1000 миль/час.\n");
                        }
                    }

                    resultArea.setText(report.toString());
                    resultArea.setCaretPosition(0);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                        resultArea.setText("Ошибка во время симуляции:\n" + e.getMessage()));
            }
        }).start();
    }
}