package org.restcomm.protocols.ss7.cap.errors;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessage;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessageCancelFailed;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessageParameterless;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessageRequestedInfoError;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessageSystemFailure;
import org.restcomm.protocols.ss7.cap.api.errors.CAPErrorMessageTaskRefused;
import org.restcomm.protocols.ss7.cap.primitives.CAPAsnPrimitive;

/**
 * Base class of CAP ReturnError messages
 *
 * @author sergey vetyutnev
 *
 */

@JacksonXmlRootElement(localName = "capErrorMessage")
public abstract class CAPErrorMessageImpl implements CAPErrorMessage, CAPAsnPrimitive {

    @JacksonXmlProperty(isAttribute = true)
    protected Long errorCode;

    protected CAPErrorMessageImpl(Long errorCode) {
        this.errorCode = errorCode;
    }

    public CAPErrorMessageImpl() {
    }

    @Override
    public Long getErrorCode() {
        return errorCode;
    }

    @Override
    public boolean isEmParameterless() {
        return false;
    }

    @Override
    public boolean isEmCancelFailed() {
        return false;
    }

    @Override
    public boolean isEmRequestedInfoError() {
        return false;
    }

    @Override
    public boolean isEmSystemFailure() {
        return false;
    }

    @Override
    public boolean isEmTaskRefused() {
        return false;
    }

    @Override
    @JsonIgnore
    public CAPErrorMessageParameterless getEmParameterless() {
        return null;
    }

    @Override
    @JsonIgnore
    public CAPErrorMessageCancelFailed getEmCancelFailed() {
        return null;
    }

    @Override
    @JsonIgnore
    public CAPErrorMessageRequestedInfoError getEmRequestedInfoError() {
        return null;
    }

    @Override
    @JsonIgnore
    public CAPErrorMessageSystemFailure getEmSystemFailure() {
        return null;
    }

    @Override
    @JsonIgnore
    public CAPErrorMessageTaskRefused getEmTaskRefused() {
        return null;
    }

}


