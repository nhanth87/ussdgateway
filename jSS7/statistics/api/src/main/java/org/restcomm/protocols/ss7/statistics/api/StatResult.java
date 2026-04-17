
package org.restcomm.protocols.ss7.statistics.api;

import java.util.Map;

/**
 *
 * @author sergey vetyutnev
 *
 */
public interface StatResult {

    long getLongValue();

    Map<String, LongValue> getStringLongValue();

}
