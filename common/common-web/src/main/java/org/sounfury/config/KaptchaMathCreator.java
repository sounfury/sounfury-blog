package org.sounfury.config;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.security.SecureRandom;

/**
 * 数学验证码生成器
 */
public class KaptchaMathCreator extends DefaultTextCreator {
    private static final String[] NUMBERS = "0,1,2,3,4,5,6,7,8,9".split(",");
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String getText() {
        int x = RANDOM.nextInt(10);
        int y = RANDOM.nextInt(10);
        char operator = getRandomOperator();

        // 处理特殊情况
        switch (operator) {
            case '-':
                // 确保减法结果为非负数
                if (x < y) {
                    int temp = x;
                    x = y;
                    y = temp;
                }
                break;
            case '/':
                // 避免除数为0
                if (y == 0) {
                    y = RANDOM.nextInt(9) + 1; // 1-9之间的数
                }

                // 确保能够整除
                if (y % x != 0 && x != 0) {
                    // 调整被除数使其能被整除
                    y = x * (RANDOM.nextInt(9) + 1);
                }

                // 交换x和y，使表达式为 y/x，结果为整数
                int temp = x;
                x = y;
                y = temp;
                break;
            case '*':
                if (x > 5) x = RANDOM.nextInt(5); // 限制乘法结果不超过45
                break;
        }

        int result = calculate(x, y, operator);
        return appendExpression(x, operator, y) + "=?@" + result;
    }

    /**
     * 随机选择运算符
     */
    private char getRandomOperator() {
        char[] operators = {'+', '-', '*', '/'};
        return operators[RANDOM.nextInt(operators.length)];
    }

    /**
     * 根据运算符计算结果
     */
    private int calculate(int x, int y, char operator) {
        switch (operator) {
            case '*':
                return x * y;
            case '/':
                return x / y; // x现在是被除数，y是除数
            case '-':
                return x - y;
            default:
                return x + y;
        }
    }

    /**
     * 生成数学表达式字符串
     */
    private String appendExpression(int x, char operator, int y) {
        return NUMBERS[x] + operator + NUMBERS[y];
    }
}