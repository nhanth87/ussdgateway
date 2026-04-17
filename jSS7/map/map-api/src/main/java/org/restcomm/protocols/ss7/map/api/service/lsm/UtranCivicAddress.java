
package org.restcomm.protocols.ss7.map.api.service.lsm;

import java.io.Serializable;

/**
 *
 * UtranCivicAddress ::= OCTET STRING (SIZE (1..255))
 * -- Refers to the Civic Address defined in 3GPP TS 25.413.
 *
 *
 * @author sergey vetyutnev
 *
 */
public interface UtranCivicAddress extends Serializable {

    byte[] getData();

}
