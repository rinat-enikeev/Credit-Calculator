package su.ugatu.moodle.is.credit_calc;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author rinat.enikeev@gmail.com
 * Date: 13.01.14
 * Time: 20:44
 */
public interface CreditProposal {
    /**
     * @return payments sorted by date.
     */
    List<CreditPayment> getPayments();

    BigDecimal getCreditAmount();

    BigDecimal getTotalPayment();

    BigDecimal getEffectiveRate();

    BigDecimal getInitialCreditCommission();
}
