package fr.coppernic.lib.interactors.common;

import fr.coppernic.sdk.utils.core.CpcResult;
import io.reactivex.functions.Predicate;

/**
 * Predicate for retry observable that return true if the error concerns barcode timeout
 */
public final class TimeoutRetryPredicate implements Predicate<Throwable> {
    @Override
    public boolean test(Throwable throwable) throws Exception {
        if (throwable instanceof CpcResult.ResultException) {
            CpcResult.ResultException e = (CpcResult.ResultException) throwable;
            CpcResult.RESULT res = e.getResult();
            return res == CpcResult.RESULT.CANCELLED || res == CpcResult.RESULT.TIMEOUT;
        }
        return false;
    }
}
