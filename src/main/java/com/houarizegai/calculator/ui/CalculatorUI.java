package com.houarizegai.calculator.ui;

import com.houarizegai.calculator.theme.properties.Theme;
import com.houarizegai.calculator.theme.ThemeLoader;
import com.houarizegai.calculator.util.ExpressionEvaluator;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.util.Map;
import java.util.regex.Pattern;
import java.awt.Color;
import javax.swing.*;

import static com.houarizegai.calculator.util.ColorUtil.hex2Color;

public class CalculatorUI {

    private static final String FONT_NAME = "Comic Sans MS";
    private static final String DOUBLE_OR_NUMBER_REGEX = "([-]?\\d+[.]\\d*)|(\\d+)|(-\\d+)";
    private static final String APPLICATION_TITLE = "Calculator";
    private static final int WINDOW_WIDTH = 410;
    private static final int WINDOW_HEIGHT = 600;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 70;
    private static final int MARGIN_X = 20;
    private static final int MARGIN_Y = 60;

    private final JFrame window;
    private JComboBox<String> comboCalculatorType;
    private JComboBox<String> comboTheme;
    private JTextField inputScreen;
    private JButton btnC;
    private JButton btnBack;
    private JButton btnMod;
    private JButton btnDiv;
    private JButton btnMul;
    private JButton btnSub;
    private JButton btnAdd;
    private JButton btn0;
    private JButton btn1;
    private JButton btn2;
    private JButton btn3;
    private JButton btn4;
    private JButton btn5;
    private JButton btn6;
    private JButton btn7;
    private JButton btn8;
    private JButton btn9;
    private JButton btnPoint;
    private JButton btnEqual;
    private JButton btnRoot;
    private JButton btnPower;
    private JButton btnLog;
    private JButton btnSquare;
    private JButton btnReciprocal;
    private JLabel expressionLabel;
    private String currentExpression = "";

    private boolean addToDisplay = true;

    private final Map<String, Theme> themesMap;

    public CalculatorUI() {
        themesMap = ThemeLoader.loadThemes();

        window = new JFrame(APPLICATION_TITLE);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setLocationRelativeTo(null);

        int[] columns = {MARGIN_X, MARGIN_X + 90, MARGIN_X + 90 * 2, MARGIN_X + 90 * 3, MARGIN_X + 90 * 4};
        int[] rows = {MARGIN_Y, MARGIN_Y + 100, MARGIN_Y + 100 + 80, MARGIN_Y + 100 + 80 * 2, MARGIN_Y + 100 + 80 * 3, MARGIN_Y + 100 + 80 * 4};

        initCalculatorTypeSelectorTop(20, 10);
        initExpressionLabelTop(MARGIN_X, 45);
        initInputScreen(columns, new int[]{80, rows[1], rows[2], rows[3], rows[4], rows[5]});
        initButtons(columns, rows);
        initThemeSelectorTop(230, 10);

        window.setLayout(null);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    public double calculate(double firstNumber, double secondNumber, char operator) {
        switch (operator) {
            case '+':
                return firstNumber + secondNumber;
            case '-':
                return firstNumber - secondNumber;
            case '*':
                return firstNumber * secondNumber;
            case '/':
                return firstNumber / secondNumber;
            case '%':
                return firstNumber % secondNumber;
            case '^':
                return Math.pow(firstNumber, secondNumber);
            default:
                return secondNumber;
        }
    }

    private void initThemeSelectorTop(int x, int y) {
        comboTheme = createComboBox(themesMap.keySet().toArray(new String[0]), x, y, "Theme");
        comboTheme.setBounds(x, y, 140, 25);
        comboTheme.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED)
                return;
            String selectedTheme = (String) event.getItem();
            applyTheme(themesMap.get(selectedTheme));
        });
        if (themesMap.entrySet().iterator().hasNext()) {
            applyTheme(themesMap.entrySet().iterator().next().getValue());
        }
    }

    private void initInputScreen(int[] columns, int[] rows) {
        inputScreen = new JTextField("0");
        inputScreen.setBounds(columns[0], rows[0], 350, 70);
        inputScreen.setEditable(false);
        inputScreen.setBackground(Color.WHITE);
        inputScreen.setFont(new Font(FONT_NAME, Font.PLAIN, 33));
        window.add(inputScreen);
    }

    private void initExpressionLabelTop(int x, int y) {
        expressionLabel = new JLabel("");
        expressionLabel.setBounds(x, y, 350, 30);
        expressionLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 20));
        expressionLabel.setForeground(Color.GRAY);
        expressionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        window.add(expressionLabel);
    }

    private void initCalculatorTypeSelectorTop(int x, int y) {
        comboCalculatorType = createComboBox(new String[]{"Standard", "Scientific"}, x, y, "Calculator type");
        comboCalculatorType.setBounds(x, y, 140, 25);
        comboCalculatorType.addItemListener(event -> {
            if (event.getStateChange() != ItemEvent.SELECTED)
                return;
            String selectedItem = (String) event.getItem();
            switch (selectedItem) {
                case "Standard":
                    window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
                    btnRoot.setVisible(false);
                    btnPower.setVisible(false);
                    btnLog.setVisible(false);
                    btnSquare.setVisible(false);
                    btnReciprocal.setVisible(false);
                    break;
                case "Scientific":
                    window.setSize(WINDOW_WIDTH + 80, WINDOW_HEIGHT);
                    btnRoot.setVisible(true);
                    btnPower.setVisible(true);
                    btnLog.setVisible(true);
                    btnSquare.setVisible(true);
                    btnReciprocal.setVisible(true);
                    break;
            }
        });
    }

    private void initButtons(int[] columns, int[] rows) {
        btnC = createButton("C", columns[0], rows[1]);
        btnC.addActionListener(event -> {
            inputScreen.setText("0");
            currentExpression = "";
            updateExpressionLabel("");
            addToDisplay = true;
        });

        btnBack = createButton("<-", columns[1], rows[1]);
        btnBack.addActionListener(event -> {
            String str = inputScreen.getText();
            StringBuilder str2 = new StringBuilder();
            for (int i = 0; i < (str.length() - 1); i++) {
                str2.append(str.charAt(i));
            }
            if (str2.toString().equals("")) {
                inputScreen.setText("0");
            } else {
                inputScreen.setText(str2.toString());
            }
            if (!currentExpression.isEmpty()) {
                currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
            }
            updateExpressionLabel(currentExpression);
            addToDisplay = true;
        });

        btnMod = createButton("%", columns[2], rows[1]);
        btnMod.addActionListener(event -> {
            if (inputScreen.getText().endsWith("%")) return;
            inputScreen.setText(inputScreen.getText() + "%");
            currentExpression += "%";
            updateExpressionLabel(currentExpression);
            addToDisplay = true;
        });

        btnDiv = createButton("/", columns[3], rows[1]);
        btnDiv.addActionListener(event -> {
            if (inputScreen.getText().endsWith("/")) return;
            inputScreen.setText(inputScreen.getText() + "/");
            currentExpression += "/";
            updateExpressionLabel(currentExpression);
            addToDisplay = true;
        });

        btn7 = createButton("7", columns[0], rows[2]);
        btn7.addActionListener(event -> {
            if (addToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("7");
                } else {
                    inputScreen.setText(inputScreen.getText() + "7");
                }
            } else {
                inputScreen.setText("7");
                addToDisplay = true;
            }
            currentExpression += "7";
            updateExpressionLabel(currentExpression);
        });

        btn8 = createButton("8", columns[1], rows[2]);
        btn8.addActionListener(event -> {
            if (addToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("8");
                } else {
                    inputScreen.setText(inputScreen.getText() + "8");
                }
            } else {
                inputScreen.setText("8");
                addToDisplay = true;
            }
            currentExpression += "8";
            updateExpressionLabel(currentExpression);
        });

        btn9 = createButton("9", columns[2], rows[2]);
        btn9.addActionListener(event -> {
            if (addToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("9");
                } else {
                    inputScreen.setText(inputScreen.getText() + "9");
                }
            } else {
                inputScreen.setText("9");
                addToDisplay = true;
            }
            currentExpression += "9";
            updateExpressionLabel(currentExpression);
        });

        btnMul = createButton("*", columns[3], rows[2]);
        btnMul.addActionListener(event -> {
            if (inputScreen.getText().endsWith("*")) return;
            inputScreen.setText(inputScreen.getText() + "*");
            currentExpression += "*";
            updateExpressionLabel(currentExpression);
            addToDisplay = true;
        });

        btn4 = createButton("4", columns[0], rows[3]);
        btn4.addActionListener(event -> {
            if (addToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("4");
                } else {
                    inputScreen.setText(inputScreen.getText() + "4");
                }
            } else {
                inputScreen.setText("4");
                addToDisplay = true;
            }
            currentExpression += "4";
            updateExpressionLabel(currentExpression);
        });

        btn5 = createButton("5", columns[1], rows[3]);
        btn5.addActionListener(event -> {
            if (addToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("5");
                } else {
                    inputScreen.setText(inputScreen.getText() + "5");
                }
            } else {
                inputScreen.setText("5");
                addToDisplay = true;
            }
            currentExpression += "5";
            updateExpressionLabel(currentExpression);
        });

        btn6 = createButton("6", columns[2], rows[3]);
        btn6.addActionListener(event -> {
            if (addToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("6");
                } else {
                    inputScreen.setText(inputScreen.getText() + "6");
                }
            } else {
                inputScreen.setText("6");
                addToDisplay = true;
            }
            currentExpression += "6";
            updateExpressionLabel(currentExpression);
        });

        btnSub = createButton("-", columns[3], rows[3]);
        btnSub.addActionListener(event -> {
            if (inputScreen.getText().endsWith("-")) return;
            inputScreen.setText(inputScreen.getText() + "-");
            currentExpression += "-";
            updateExpressionLabel(currentExpression);
            addToDisplay = true;
        });

        btn1 = createButton("1", columns[0], rows[4]);
        btn1.addActionListener(event -> {
            if (addToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("1");
                } else {
                    inputScreen.setText(inputScreen.getText() + "1");
                }
            } else {
                inputScreen.setText("1");
                addToDisplay = true;
            }
            currentExpression += "1";
            updateExpressionLabel(currentExpression);
        });

        btn2 = createButton("2", columns[1], rows[4]);
        btn2.addActionListener(event -> {
            if (addToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("2");
                } else {
                    inputScreen.setText(inputScreen.getText() + "2");
                }
            } else {
                inputScreen.setText("2");
                addToDisplay = true;
            }
            currentExpression += "2";
            updateExpressionLabel(currentExpression);
        });

        btn3 = createButton("3", columns[2], rows[4]);
        btn3.addActionListener(event -> {
            if (addToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("3");
                } else {
                    inputScreen.setText(inputScreen.getText() + "3");
                }
            } else {
                inputScreen.setText("3");
                addToDisplay = true;
            }
            currentExpression += "3";
            updateExpressionLabel(currentExpression);
        });

        btnAdd = createButton("+", columns[3], rows[4]);
        btnAdd.addActionListener(event -> {
            if (inputScreen.getText().endsWith("+")) return;
            inputScreen.setText(inputScreen.getText() + "+");
            currentExpression += "+";
            updateExpressionLabel(currentExpression);
            addToDisplay = true;
        });

        btnPoint = createButton(".", columns[0], rows[5]);
        btnPoint.addActionListener(event -> {
            if (addToDisplay) {
                if (inputScreen.getText().equals("0")) {
                    inputScreen.setText("0.");
                } else {
                    inputScreen.setText(inputScreen.getText() + ".");
                }
            } else {
                inputScreen.setText("0.");
                addToDisplay = true;
            }
            currentExpression += ".";
            updateExpressionLabel(currentExpression);
        });

        btn0 = createButton("0", columns[1], rows[5]);
        btn0.addActionListener(event -> {
            if (addToDisplay) {
                if (Pattern.matches("[0]*", inputScreen.getText())) {
                    inputScreen.setText("0");
                } else {
                    inputScreen.setText(inputScreen.getText() + "0");
                }
            } else {
                inputScreen.setText("0");
                addToDisplay = true;
            }
            currentExpression += "0";
            updateExpressionLabel(currentExpression);
        });

        btnEqual = createButton("=", columns[2], rows[5]);
        btnEqual.addActionListener(event -> {
            try {
                String expression = inputScreen.getText();
                double result = ExpressionEvaluator.evaluate(expression);
                inputScreen.setText(String.valueOf(result));
                updateExpressionLabel(expression + "=" + result);
                currentExpression = String.valueOf(result);
                addToDisplay = false;
            } catch (Exception ex) {
                inputScreen.setText("Error");
                updateExpressionLabel(ex.getMessage());
                currentExpression = "";
                addToDisplay = false;
            }
            btnEqual.setSize(2 * BUTTON_WIDTH + 10, BUTTON_HEIGHT);
        });
        btnEqual.setSize(2 * BUTTON_WIDTH + 10, BUTTON_HEIGHT);

        btnRoot = createButton("√", columns[4], rows[1]);
        btnRoot.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (currentExpression.isEmpty()) {
                currentExpression = "√(" + inputScreen.getText() + ")";
            } else {
                currentExpression = "√(" + currentExpression + ")";
            }
            updateExpressionLabel(currentExpression);
            addToDisplay = true;
        });
        btnRoot.setVisible(false);

        btnPower = createButton("pow", columns[4], rows[2]);
        btnPower.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (currentExpression.isEmpty()) {
                currentExpression = "pow(" + inputScreen.getText() + ", 2)";
            } else {
                currentExpression = "pow(" + currentExpression + ", 2)";
            }
            updateExpressionLabel(currentExpression);
            addToDisplay = true;
        });
        btnPower.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        btnPower.setVisible(false);

        btnLog = createButton("ln", columns[4], rows[3]);
        btnLog.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;

            if (currentExpression.isEmpty()) {
                currentExpression = "ln(" + inputScreen.getText() + ")";
            } else {
                currentExpression = "ln(" + currentExpression + ")";
            }
            updateExpressionLabel(currentExpression);
            addToDisplay = true;
        });
        btnLog.setVisible(false);

        btnSquare = createButton("x²", columns[4], rows[4]);
        btnSquare.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;
            double value = Double.parseDouble(inputScreen.getText());
            double result = value * value;
            if (currentExpression.isEmpty()) {
                currentExpression = "(" + inputScreen.getText() + ")^2";
            } else {
                currentExpression = "(" + currentExpression + ")^2";
            }
            inputScreen.setText(String.valueOf(result));
            updateExpressionLabel(currentExpression);
            addToDisplay = false;
        });
        btnSquare.setVisible(false);

        btnReciprocal = createButton("1/x", columns[4], rows[5]);
        btnReciprocal.addActionListener(event -> {
            if (!Pattern.matches(DOUBLE_OR_NUMBER_REGEX, inputScreen.getText()))
                return;
            double value = Double.parseDouble(inputScreen.getText());
            if (value == 0) {
                inputScreen.setText("Error");
                addToDisplay = false;
                return;
            }
            double result = 1.0 / value;
            if (currentExpression.isEmpty()) {
                currentExpression = "1/" + inputScreen.getText();
            } else {
                currentExpression = "1/" + currentExpression;
            }
            inputScreen.setText(String.valueOf(result));
            updateExpressionLabel(currentExpression);
            addToDisplay = false;
        });
        btnReciprocal.setVisible(false);
    }

    private JComboBox<String> createComboBox(String[] items, int x, int y, String toolTip) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBounds(x, y, 140, 25);
        combo.setToolTipText(toolTip);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        window.add(combo);

        return combo;
    }

    private JButton createButton(String label, int x, int y) {
        JButton btn = new JButton(label);
        btn.setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        btn.setFont(new Font(FONT_NAME, Font.PLAIN, 28));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusable(false);
        window.add(btn);
        return btn;
    }

    private JButton createButton(String label, int x, int y, int colSpan) {
        JButton btn = new JButton(label);
        int width = colSpan * BUTTON_WIDTH + (colSpan - 1) * 10;
        btn.setBounds(x, y, width, BUTTON_HEIGHT);
        btn.setFont(new Font(FONT_NAME, Font.PLAIN, 28));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusable(false);
        window.add(btn);
        return btn;
    }

    private void applyTheme(Theme theme) {
        window.getContentPane().setBackground(hex2Color(theme.getApplicationBackground()));

        comboCalculatorType.setForeground(hex2Color(theme.getTextColor()));
        comboTheme.setForeground(hex2Color(theme.getTextColor()));
        inputScreen.setForeground(hex2Color(theme.getTextColor()));
        btn0.setForeground(hex2Color(theme.getTextColor()));
        btn1.setForeground(hex2Color(theme.getTextColor()));
        btn2.setForeground(hex2Color(theme.getTextColor()));
        btn3.setForeground(hex2Color(theme.getTextColor()));
        btn4.setForeground(hex2Color(theme.getTextColor()));
        btn5.setForeground(hex2Color(theme.getTextColor()));
        btn6.setForeground(hex2Color(theme.getTextColor()));
        btn7.setForeground(hex2Color(theme.getTextColor()));
        btn8.setForeground(hex2Color(theme.getTextColor()));
        btn9.setForeground(hex2Color(theme.getTextColor()));
        btnPoint.setForeground(hex2Color(theme.getTextColor()));
        btnC.setForeground(hex2Color(theme.getTextColor()));
        btnBack.setForeground(hex2Color(theme.getTextColor()));
        btnMod.setForeground(hex2Color(theme.getTextColor()));
        btnDiv.setForeground(hex2Color(theme.getTextColor()));
        btnMul.setForeground(hex2Color(theme.getTextColor()));
        btnSub.setForeground(hex2Color(theme.getTextColor()));
        btnAdd.setForeground(hex2Color(theme.getTextColor()));
        btnRoot.setForeground(hex2Color(theme.getTextColor()));
        btnLog.setForeground(hex2Color(theme.getTextColor()));
        btnPower.setForeground(hex2Color(theme.getTextColor()));
        btnEqual.setForeground(hex2Color(theme.getBtnEqualTextColor()));
        btnSquare.setForeground(hex2Color(theme.getTextColor()));
        btnReciprocal.setForeground(hex2Color(theme.getTextColor()));

        comboCalculatorType.setBackground(hex2Color(theme.getApplicationBackground()));
        comboTheme.setBackground(hex2Color(theme.getApplicationBackground()));
        inputScreen.setBackground(hex2Color(theme.getApplicationBackground()));
        btn0.setBackground(hex2Color(theme.getNumbersBackground()));
        btn1.setBackground(hex2Color(theme.getNumbersBackground()));
        btn2.setBackground(hex2Color(theme.getNumbersBackground()));
        btn3.setBackground(hex2Color(theme.getNumbersBackground()));
        btn4.setBackground(hex2Color(theme.getNumbersBackground()));
        btn5.setBackground(hex2Color(theme.getNumbersBackground()));
        btn6.setBackground(hex2Color(theme.getNumbersBackground()));
        btn7.setBackground(hex2Color(theme.getNumbersBackground()));
        btn8.setBackground(hex2Color(theme.getNumbersBackground()));
        btn9.setBackground(hex2Color(theme.getNumbersBackground()));
        btnPoint.setBackground(hex2Color(theme.getNumbersBackground()));
        btnC.setBackground(hex2Color(theme.getOperatorBackground()));
        btnBack.setBackground(hex2Color(theme.getOperatorBackground()));
        btnMod.setBackground(hex2Color(theme.getOperatorBackground()));
        btnDiv.setBackground(hex2Color(theme.getOperatorBackground()));
        btnMul.setBackground(hex2Color(theme.getOperatorBackground()));
        btnSub.setBackground(hex2Color(theme.getOperatorBackground()));
        btnAdd.setBackground(hex2Color(theme.getOperatorBackground()));
        btnRoot.setBackground(hex2Color(theme.getOperatorBackground()));
        btnLog.setBackground(hex2Color(theme.getOperatorBackground()));
        btnPower.setBackground(hex2Color(theme.getOperatorBackground()));
        btnEqual.setBackground(hex2Color(theme.getBtnEqualBackground()));
        btnSquare.setBackground(hex2Color(theme.getOperatorBackground()));
        btnReciprocal.setBackground(hex2Color(theme.getOperatorBackground()));
    }

    private void updateExpressionLabel(String newText) {
        expressionLabel.setText(newText);
    }
}