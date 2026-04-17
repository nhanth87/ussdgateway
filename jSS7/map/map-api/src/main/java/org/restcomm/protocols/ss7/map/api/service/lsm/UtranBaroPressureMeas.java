
package org.restcomm.protocols.ss7.map.api.service.lsm;

import java.io.Serializable;

/**
 *
 * UtranBaroPressureMeas ::= OCTET STRING (SIZE (2))
 * -- Refers to the Barometric Pressure Measurement defined in 3GPP TS 25.413.
 *
 *
 * @author sergey vetyutnev
 *
 */
public interface UtranBaroPressureMeas extends Serializable {

    byte[] getData();

}
