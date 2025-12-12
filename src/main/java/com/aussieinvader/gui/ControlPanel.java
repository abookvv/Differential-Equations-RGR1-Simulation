// src/main/java/com/aussieinvader/gui/ControlPanel.java
package com.aussieinvader.gui;

import com.aussieinvader.core.SimulationResult;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;

public class ControlPanel extends JPanel {
    private final JSlider stepsSlider;
    private final JSlider termsSlider;
    private final JRadioButton eulerButton;
    private final JRadioButton rk4Button;
    private final JCheckBox analyticalCheckBox;
    private final JButton runButton;

    private Consumer<SimulationParams> onRun;

    public ControlPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.DARK_GRAY);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Меньше отступы

        add(createLabel("Управление", Color.WHITE, Font.BOLD, 14)); // Меньший заголовок

        // Слайдер шагов
        stepsSlider = new JSlider(2, 500, 50);
        stepsSlider.setMajorTickSpacing(100);
        stepsSlider.setMinorTickSpacing(25);
        stepsSlider.setPaintTicks(true);
        stepsSlider.setPaintLabels(true);
        setupSliderLabels(stepsSlider, 2, 500, 100);
        add(createLabeledComponent("Шаги:", stepsSlider));

        // Слайдер членов ряда
        termsSlider = new JSlider(2, 100, 20);
        termsSlider.setMajorTickSpacing(20);
        termsSlider.setMinorTickSpacing(10);
        termsSlider.setPaintTicks(true);
        termsSlider.setPaintLabels(false); // Отключаем авто-метки
        termsSlider.addChangeListener(e -> updateTermsLabel());
        setupSliderLabels(termsSlider, 2, 100, 20); // Кастомные метки
        add(createLabeledComponent("Число членов ряда:", termsSlider));

        // Выбор метода
        eulerButton = new JRadioButton("Метод Эйлера");
        rk4Button = new JRadioButton("Рунге-Кутта 4");
        ButtonGroup group = new ButtonGroup();
        group.add(eulerButton);
        group.add(rk4Button);
        rk4Button.setSelected(true); // По умолчанию
        add(createLabel("Численный метод:", Color.LIGHT_GRAY, Font.PLAIN, 12));
        add(eulerButton);
        add(rk4Button);

        // Чекбокс для аналитики
        analyticalCheckBox = new JCheckBox("Показать аналитическое решение");
        analyticalCheckBox.setSelected(true);
        add(analyticalCheckBox);

        // Кнопка запуска
        runButton = new JButton("Запустить симуляцию");
        runButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        runButton.setBackground(new Color(0x4CAF50));
        runButton.setForeground(Color.WHITE);
        runButton.setFocusPainted(false);
        runButton.addActionListener(e -> {
            if (onRun != null) {
                onRun.accept(new SimulationParams(
                        stepsSlider.getValue(),
                        termsSlider.getValue(),
                        eulerButton.isSelected() ? "euler" : "rk4",
                        analyticalCheckBox.isSelected()
                ));
            }
        });
        add(Box.createVerticalStrut(10));
        add(runButton);
    }

    private void updateStepsLabel() {
        int value = stepsSlider.getValue();
        // Можно добавить динамическую метку, но для простоты оставим статические
    }

    private void updateTermsLabel() {
        int value = termsSlider.getValue();
        // Можно добавить динамическую метку, но для простоты оставим статические
    }

    // Установка кастомных меток для слайдера
    private void setupSliderLabels(JSlider slider, int min, int max, int majorStep) {
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for (int i = min; i <= max; i += majorStep) {
            JLabel label = new JLabel(String.valueOf(i));
            label.setFont(label.getFont().deriveFont(Font.PLAIN, 9)); // Меньший шрифт
            labelTable.put(i, label);
        }
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);
    }

    public void setOnRunListener(Consumer<SimulationParams> listener) {
        this.onRun = listener;
    }

    private JLabel createLabel(String text, Color color, int style, int size) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(label.getFont().deriveFont(style, size));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createLabeledComponent(String labelText, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);
        panel.add(createLabel(labelText, Color.LIGHT_GRAY, Font.PLAIN, 12), BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    public static class SimulationParams {
        public final int steps;
        public final int terms;
        public final String method;
        public final boolean showAnalytical;

        public SimulationParams(int steps, int terms, String method, boolean showAnalytical) {
            this.steps = steps;
            this.terms = terms;
            this.method = method;
            this.showAnalytical = showAnalytical;
        }
    }
}