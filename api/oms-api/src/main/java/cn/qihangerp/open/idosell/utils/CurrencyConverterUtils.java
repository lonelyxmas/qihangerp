package cn.qihangerp.open.idosell.utils;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConverterUtils {

    /**
     * 波兰币转欧元
     *
     * @param plnAmount 波兰币金额
     * @param exchangeRate 当前汇率(1 EUR = X PLN)
     * @return 欧元金额
     */
    public static BigDecimal plnToEur(BigDecimal plnAmount, BigDecimal exchangeRate) {
        if (plnAmount == null || exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount or exchange rate");
        }

        // 转换公式: EUR = PLN / exchangeRate
        return plnAmount.divide(exchangeRate, 2, RoundingMode.HALF_UP);
    }

    /**
     * 波兰币转欧元
     *
     * @param plnAmount 波兰币金额
     * @param exchangeRate 当前汇率(1 EUR = X PLN)
     * @return 欧元金额
     */
    public static BigDecimal plnToEur(double plnAmount, double exchangeRate) {
        return plnToEur(BigDecimal.valueOf(plnAmount), BigDecimal.valueOf(exchangeRate));
    }
    /**
     * 欧元转波兰币
     *
     * @param eurAmount 欧元金额
     * @param exchangeRate 当前汇率(1 EUR = X PLN)
     * @return 波兰币金额
     */
    public static BigDecimal eurToPln(BigDecimal eurAmount, BigDecimal exchangeRate) {
        if (eurAmount == null || exchangeRate == null || exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount or exchange rate");
        }

        // 转换公式: PLN = EUR * exchangeRate
        return eurAmount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 欧元转波兰币
     *
     * @param eurAmount 欧元金额
     * @param exchangeRate 当前汇率(1 EUR = X PLN)
     * @return 波兰币金额
     */
    public static BigDecimal eurToPln(double eurAmount, double exchangeRate) {
        return eurToPln(BigDecimal.valueOf(eurAmount), BigDecimal.valueOf(exchangeRate));
    }
}
