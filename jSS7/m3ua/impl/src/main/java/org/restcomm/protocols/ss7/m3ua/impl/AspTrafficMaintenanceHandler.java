
package org.restcomm.protocols.ss7.m3ua.impl;

import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.m3ua.Asp;
import org.restcomm.protocols.ss7.m3ua.ExchangeType;
import org.restcomm.protocols.ss7.m3ua.Functionality;
import org.restcomm.protocols.ss7.m3ua.IPSPType;
import org.restcomm.protocols.ss7.m3ua.impl.fsm.FSM;
import org.restcomm.protocols.ss7.m3ua.impl.fsm.UnknownTransitionException;
import org.restcomm.protocols.ss7.m3ua.message.MessageClass;
import org.restcomm.protocols.ss7.m3ua.message.MessageType;
import org.restcomm.protocols.ss7.m3ua.message.asptm.ASPActive;
import org.restcomm.protocols.ss7.m3ua.message.asptm.ASPActiveAck;
import org.restcomm.protocols.ss7.m3ua.message.asptm.ASPInactive;
import org.restcomm.protocols.ss7.m3ua.message.asptm.ASPInactiveAck;
import org.restcomm.protocols.ss7.m3ua.parameter.ErrorCode;
import org.restcomm.protocols.ss7.m3ua.parameter.TrafficModeType;

/**
 *
 * @author amit bhayani
 *
 */
public class AspTrafficMaintenanceHandler extends MessageHandler {

    private static final Logger logger = Logger.getLogger(AspTrafficMaintenanceHandler.class);

    public AspTrafficMaintenanceHandler(AspFactoryImpl aspFactoryImpl) {
        super(aspFactoryImpl);
    }

    protected void handleAspActive(ASPActive aspActive) {
        if (aspFactoryImpl.getFunctionality() == Functionality.SGW
                || (aspFactoryImpl.getFunctionality() == Functionality.AS && aspFactoryImpl.getExchangeType() == ExchangeType.DE)
                || (aspFactoryImpl.getFunctionality() == Functionality.IPSP && aspFactoryImpl.getExchangeType() == ExchangeType.DE)
                || (aspFactoryImpl.getFunctionality() == Functionality.IPSP
                        && aspFactoryImpl.getExchangeType() == ExchangeType.SE && aspFactoryImpl.getIpspType() == IPSPType.SERVER)) {

            long[] rcs = null;
            if (aspActive.getRoutingContext() != null) {
                rcs = aspActive.getRoutingContext().getRoutingContexts();
            }

            for (Asp asp : this.aspFactoryImpl.aspList) {
                AspImpl aspImpl = (AspImpl) asp;

                // We handle this ASPActive only if the RC matches
                if (rcs != null) {
                    if (aspImpl.getAs().getRoutingContext() == null) {
                        // If there is no RC for this Asp, but ASPActive
                        // contains RC, its error
                        ErrorCode errorCodeObj = this.aspFactoryImpl.parameterFactory
                                .createErrorCode(ErrorCode.No_Configured_AS_for_ASP);

                        this.sendError(aspActive.getRoutingContext(), errorCodeObj);
                        return;
                    }

                    long[] thisAspRC = aspImpl.getAs().getRoutingContext().getRoutingContexts();

                    boolean foundRC = false;
                    for (long rcl : rcs) {
                        for (long thisASpRCl : thisAspRC) {
                            if (rcl == thisASpRCl) {
                                foundRC = true;
                                break;
                            }
                        }
                        if (foundRC) {
                            break;
                        }
                    }

                    if (!foundRC) {
                        continue;
                    }
                }

                FSM aspPeerFSM = aspImpl.getPeerFSM();
                if (aspPeerFSM == null) {
                    logger.error(String.format("Received ASPACTIVE=%s for ASP=%s. But peer FSM is null.", aspActive,
                            this.aspFactoryImpl.getName()));
                    return;
                }

                if (AspState.getState(aspPeerFSM.getState().getName()) == AspState.ACTIVE) {
                    ErrorCode errorCodeObj = this.aspFactoryImpl.parameterFactory.createErrorCode(ErrorCode.Unexpected_Message);
                    this.sendError(aspActive.getRoutingContext(), errorCodeObj);
                }

                ASPActiveAck aspActiveAck = (ASPActiveAck) this.aspFactoryImpl.messageFactory.createMessage(
                        MessageClass.ASP_TRAFFIC_MAINTENANCE, MessageType.ASP_ACTIVE_ACK);
                aspActiveAck.setRoutingContext(aspActive.getRoutingContext());

                if (aspActive.getTrafficModeType() != null) {
                    if (aspActive.getTrafficModeType().getMode() == TrafficModeType.Override) {
                        // If the Traffic Mode is Override, check if there is
                        // already an ASP ACTIVE. If yes we respond with error
                        // to calling ASP and stay in INACTIVE

                        for (Asp asptmp : aspImpl.getAs().getAspList()) {
                            AspImpl aspImplTmp = (AspImpl) asptmp;
                            if (aspImplTmp.getName().equals(aspImpl.getName())) {
                                continue;
                            }

                            FSM fsm = aspImplTmp.getPeerFSM();
                            AspState aspState = AspState.getState(fsm.getState().getName());

                            if (aspState == AspState.ACTIVE) {
                                aspActiveAck.setTrafficModeType(aspImpl.getAs().getTrafficModeType());

                                // TODO : If the SGP responds with an ASP
                                // Active Ack message, the ASP MUST check the
                                // Traffic Mode Type in the ASP Active Ack
                                // message. If the ASP Active Ack message
                                // does not include a Traffic Mode Type,
                                // the ASP that originated the ASP Active
                                // Ack message MUST assume that the SGP is
                                // operating in Override mode.
                                // Now if TrafficModeType received from M3UA
                                // stack is not Override, we just ignore
                                break;
                            }
                        }
                    } else if (aspActive.getTrafficModeType().getMode() == TrafficModeType.Loadshare) {
                        aspActiveAck.setTrafficModeType(aspActive.getTrafficModeType());
                    }
                }
                this.aspFactoryImpl.write(aspActiveAck);

                try {
                    aspPeerFSM.setAttribute(FSM.ATTRIBUTE_MESSAGE, aspActive);
                    aspPeerFSM.signal(TransitionState.ASP_ACTIVE);

                    FSM asLocalFSM = ((AsImpl) aspImpl.getAs()).getLocalFSM();

                    asLocalFSM.setAttribute(AsImpl.ATTRIBUTE_ASP, aspImpl);
                    asLocalFSM.signal(TransitionState.ASP_ACTIVE);
                } catch (UnknownTransitionException e) {
                    logger.error(e.getMessage(), e);
                }
            }// for
        } else {
            // TODO : Should we silently drop ASP_ACTIVE?

            // ASP_ACTIVE is unexpected in this state
            ErrorCode errorCodeObj = this.aspFactoryImpl.parameterFactory.createErrorCode(ErrorCode.Unexpected_Message);
            sendError(aspActive.getRoutingContext(), errorCodeObj);
        }
    }

    protected void handleAspActiveAck(ASPActiveAck aspActiveAck) {
        if (!this.aspFactoryImpl.started) {
            return;
        }

        if (aspFactoryImpl.getFunctionality() == Functionality.AS
                || (aspFactoryImpl.getFunctionality() == Functionality.SGW && aspFactoryImpl.getExchangeType() == ExchangeType.DE)
                || (aspFactoryImpl.getFunctionality() == Functionality.IPSP && aspFactoryImpl.getExchangeType() == ExchangeType.DE)
                || (aspFactoryImpl.getFunctionality() == Functionality.IPSP
                        && aspFactoryImpl.getExchangeType() == ExchangeType.SE && aspFactoryImpl.getIpspType() == IPSPType.CLIENT)) {

            for (Asp asp : this.aspFactoryImpl.aspList) {
                AspImpl aspImpl = (AspImpl) asp;

                FSM aspLocalFSM = aspImpl.getLocalFSM();
                if (aspLocalFSM == null) {
                    logger.error(String.format("Received ASPACTIVE_ACK=%s for ASP=%s. But local FSM is null.", aspActiveAck,
                            this.aspFactoryImpl.getName()));
                    return;
                }

                try {
                    aspLocalFSM.signal(TransitionState.ASP_ACTIVE_ACK);
                } catch (UnknownTransitionException e) {
                    logger.error(e.getMessage(), e);
                }

            }// for
        } else {
            // TODO : Should we silently drop?

            // ASP_ACTIVE_ACK is unexpected in this state
            ErrorCode errorCodeObj = this.aspFactoryImpl.parameterFactory.createErrorCode(ErrorCode.Unexpected_Message);
            sendError(null, errorCodeObj);
        }
    }

    protected void handleAspInactive(ASPInactive aspInactive) {
        if (aspFactoryImpl.getFunctionality() == Functionality.SGW
                || (aspFactoryImpl.getFunctionality() == Functionality.AS && aspFactoryImpl.getExchangeType() == ExchangeType.DE)
                || (aspFactoryImpl.getFunctionality() == Functionality.IPSP && aspFactoryImpl.getExchangeType() == ExchangeType.DE)
                || (aspFactoryImpl.getFunctionality() == Functionality.IPSP
                        && aspFactoryImpl.getExchangeType() == ExchangeType.SE && aspFactoryImpl.getIpspType() == IPSPType.SERVER)) {

            long[] rcs = null;
            if (aspInactive.getRoutingContext() != null) {
                rcs = aspInactive.getRoutingContext().getRoutingContexts();
            }

            for (Asp asp : this.aspFactoryImpl.aspList) {
                AspImpl aspImpl = (AspImpl) asp;

                // We handle this ASPInactive only if the RC matches
                if (rcs != null) {
                    if (aspImpl.getAs().getRoutingContext() == null) {
                        // If there is no RC for this Asp, but ASPInactive
                        // contains RC, its error
                        ErrorCode errorCodeObj = this.aspFactoryImpl.parameterFactory
                                .createErrorCode(ErrorCode.No_Configured_AS_for_ASP);

                        this.sendError(aspInactive.getRoutingContext(), errorCodeObj);
                        return;
                    }

                    long[] thisAspRC = aspImpl.getAs().getRoutingContext().getRoutingContexts();

                    boolean foundRC = false;
                    for (long rcl : rcs) {
                        for (long thisASpRCl : thisAspRC) {
                            if (rcl == thisASpRCl) {
                                foundRC = true;
                                break;
                            }
                        }
                        if (foundRC) {
                            break;
                        }
                    }

                    if (!foundRC) {
                        continue;
                    }
                }

                FSM aspPeerFSM = aspImpl.getPeerFSM();
                if (aspPeerFSM == null) {
                    logger.error(String.format("Received ASPINACTIVE=%s for ASP=%s. But peer FSM is null.", aspInactive,
                            this.aspFactoryImpl.getName()));
                    return;
                }

                ASPInactiveAck aspInactiveAck = (ASPInactiveAck) this.aspFactoryImpl.messageFactory.createMessage(
                        MessageClass.ASP_TRAFFIC_MAINTENANCE, MessageType.ASP_INACTIVE_ACK);
                aspInactiveAck.setRoutingContext(aspInactive.getRoutingContext());
                this.aspFactoryImpl.write(aspInactiveAck);

                try {
                    aspPeerFSM.setAttribute(FSM.ATTRIBUTE_MESSAGE, aspInactive);
                    aspPeerFSM.signal(TransitionState.ASP_INACTIVE);

                    FSM asLocalFSM = ((AsImpl) aspImpl.getAs()).getLocalFSM();

                    asLocalFSM.setAttribute(AsImpl.ATTRIBUTE_ASP, aspImpl);
                    asLocalFSM.signal(TransitionState.ASP_INACTIVE);
                } catch (UnknownTransitionException e) {
                    logger.error(e.getMessage(), e);
                }
            }// for
        } else {
            // TODO : Should we silently drop ASPINACTIVE?

            // ASPINACTIVE is unexpected in this state
            ErrorCode errorCodeObj = this.aspFactoryImpl.parameterFactory.createErrorCode(ErrorCode.Unexpected_Message);
            sendError(aspInactive.getRoutingContext(), errorCodeObj);
        }

    }

    protected void handleAspInactiveAck(ASPInactiveAck aspInactiveAck) {
        if (!this.aspFactoryImpl.started) {
            return;
        }

        if (aspFactoryImpl.getFunctionality() == Functionality.AS
                || (aspFactoryImpl.getFunctionality() == Functionality.SGW && aspFactoryImpl.getExchangeType() == ExchangeType.DE)
                || (aspFactoryImpl.getFunctionality() == Functionality.IPSP && aspFactoryImpl.getExchangeType() == ExchangeType.DE)
                || (aspFactoryImpl.getFunctionality() == Functionality.IPSP
                        && aspFactoryImpl.getExchangeType() == ExchangeType.SE && aspFactoryImpl.getIpspType() == IPSPType.CLIENT)) {

            for (Asp asp : this.aspFactoryImpl.aspList) {
                AspImpl aspImpl = (AspImpl) asp;

                FSM aspLocalFSM = aspImpl.getLocalFSM();
                if (aspLocalFSM == null) {
                    logger.error(String.format("Received ASPINACTIVE_ACK=%s for ASP=%s. But local FSM is null.", aspInactiveAck,
                            this.aspFactoryImpl.getName()));
                    return;
                }

                try {
                    aspLocalFSM.signal(TransitionState.ASP_INACTIVE_ACK);
                } catch (UnknownTransitionException e) {
                    logger.error(e.getMessage(), e);
                }
            }// for
        } else {
            // TODO : Should we silently drop?

            // ASP_INACTIVE_ACK is unexpected in this state
            ErrorCode errorCodeObj = this.aspFactoryImpl.parameterFactory.createErrorCode(ErrorCode.Unexpected_Message);
            sendError(null, errorCodeObj);
        }
    }
}
