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

                // 确保x和y都在0-9范围内，并且能够整除
                if (y != 0) {
                    // 选择小的倍数，保证x在0-9范围内
                    x = (RANDOM.nextInt(9) + 1) % 10;
                    y = Math.max(1, x); // 确保y不为0

                    // 交换x和y，使表达式为 x/y
                    int temp = x;
                    x = y * temp; // x现在是被除数
                    y = temp;     // y现���是除数

                    // 确保x不超过9
                    if (x > 9) {
                        x = RANDOM.nextInt(9) + 1;
                        y = 1; // 简化为整除
                    }
                }
                break;
            case '*':
                // 限制乘法，确保结果不超过9
                if (x > 3) x = RANDOM.nextInt(3) + 1;
                if (y > 3) y = RANDOM.nextInt(3) + 1;
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
        // 确保索引在数组范围内
        if (x >= 0 && x < NUMBERS.length && y >= 0 && y < NUMBERS.length) {
            return NUMBERS[x] + operator + NUMBERS[y];
        } else {
            // 如果有越界风险，使用数字字符串
            return String.valueOf(x) + operator + String.valueOf(y);
        }
    }
}