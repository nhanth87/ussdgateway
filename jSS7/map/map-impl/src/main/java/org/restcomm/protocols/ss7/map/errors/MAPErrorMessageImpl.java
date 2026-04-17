
package org.restcomm.protocols.ss7.map.errors;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageAbsentSubscriber;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageAbsentSubscriberSM;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageBusySubscriber;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageCUGReject;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageCallBarred;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageExtensionContainer;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageFacilityNotSup;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageParameterless;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessagePositionMethodFailure;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessagePwRegistrationFailure;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageRoamingNotAllowed;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageSMDeliveryFailure;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageSsErrorStatus;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageSsIncompatibility;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageSubscriberBusyForMtSms;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageSystemFailure;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageUnauthorizedLCSClient;
import org.restcomm.protocols.ss7.map.api.errors.MAPErrorMessageUnknownSubscriber;
import org.restcomm.protocols.ss7.map.primitives.MAPAsnPrimitive;

/**
 * Base class of MAP ReturnError messages
 *
 * @author sergey vetyutnev
 * @author amit bhayani
 *
 */
public abstract class MAPErrorMessageImpl implements MAPErrorMessage, MAPAsnPrimitive {

    @JacksonXmlProperty(isAttribute = true)
    protected Long errorCode;

    protected MAPErrorMessageImpl(Long errorCode) {
        this.errorCode = errorCode;
    }

    public MAPErrorMessageImpl() {
    }

    public Long getErrorCode() {
        return errorCode;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmParameterless() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmExtensionContainer() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmFacilityNotSup() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmSMDeliveryFailure() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmSystemFailure() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmUnknownSubscriber() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmAbsentSubscriberSM() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmAbsentSubscriber() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmSubscriberBusyForMtSms() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmCallBarred() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmUnauthorizedLCSClient() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmPositionMethodFailure() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmBusySubscriber() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmCUGReject() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmRoamingNotAllowed() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmSsErrorStatus() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmSsIncompatibility() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isEmPwRegistrationFailure() {
        return false;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageParameterless getEmParameterless() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageExtensionContainer getEmExtensionContainer() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageFacilityNotSup getEmFacilityNotSup() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageSMDeliveryFailure getEmSMDeliveryFailure() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageSystemFailure getEmSystemFailure() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageUnknownSubscriber getEmUnknownSubscriber() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageAbsentSubscriberSM getEmAbsentSubscriberSM() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageAbsentSubscriber getEmAbsentSubscriber() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageSubscriberBusyForMtSms getEmSubscriberBusyForMtSms() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageCallBarred getEmCallBarred() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageUnauthorizedLCSClient getEmUnauthorizedLCSClient() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessagePositionMethodFailure getEmPositionMethodFailure() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageBusySubscriber getEmBusySubscriber() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageCUGReject getEmCUGReject() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageRoamingNotAllowed getEmRoamingNotAllowed() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageSsErrorStatus getEmSsErrorStatus() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessageSsIncompatibility getEmSsIncompatibility() {
        return null;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public MAPErrorMessagePwRegistrationFailure getEmPwRegistrationFailure() {
        return null;
    }

}
