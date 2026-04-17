
package org.restcomm.protocols.ss7.map.api.service.lsm;

import java.io.Serializable;

/**
 *
 * UtranAdditionalPositioningData ::= OCTET STRING (SIZE (1..35))
 * -- Refers to the Additional Positioning Data defined in 3GPP TS 25.413.
 *
 *
 * @author sergey vetyutnev
 *
 */
public interface UtranAdditionalPositioningData extends Serializable {

    byte[] getData();

}
