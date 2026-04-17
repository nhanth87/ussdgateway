package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


import org.restcomm.protocols.ss7.cap.MessageImpl;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CAPDialogCircuitSwitchedCall;
import org.restcomm.protocols.ss7.cap.api.service.circuitSwitchedCall.CircuitSwitchedCallMessage;
import org.restcomm.protocols.ss7.cap.primitives.CAPAsnPrimitive;


/**
 *
 * @author sergey vetyutnev
 * @author Amit Bhayani
 *
 */
@JacksonXmlRootElement(localName = "circuitSwitchedCallMessage")
public abstract class CircuitSwitchedCallMessageImpl extends MessageImpl implements CircuitSwitchedCallMessage, CAPAsnPrimitive {

    public CAPDialogCircuitSwitchedCall getCAPDialog() {
        return (CAPDialogCircuitSwitchedCall) super.getCAPDialog();
    }
}


