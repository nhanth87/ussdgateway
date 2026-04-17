
package org.restcomm.protocols.ss7.m3ua.impl;

import org.apache.log4j.Logger;
import org.restcomm.protocols.ss7.m3ua.M3UAManagementEventListener;
import org.restcomm.protocols.ss7.m3ua.State;
import org.restcomm.protocols.ss7.m3ua.impl.fsm.FSMState;
import org.restcomm.protocols.ss7.m3ua.impl.fsm.FSMStateEventHandler;

/**
 *
 * @author amit bhayani
 *
 */
public abstract class SEHAsStateEnterDown implements FSMStateEventHandler {

    private static final Logger logger = Logger.getLogger(SEHAsStateEnterDown.class);

    private AsImpl asImpl;

    public SEHAsStateEnterDown(AsImpl asImpl) {
        this.asImpl = asImpl;
    }

    @Override
    public void onEvent(FSMState state) {
        // Call listener and indicate of state change only if not already done
        if (!this.asImpl.state.getName().equals(State.STATE_DOWN)) {
            AsState oldState = AsState.getState(this.asImpl.state.getName());
            this.asImpl.state = AsState.DOWN;

            for (M3UAManagementEventListener m3uaManagementEventListener : this.asImpl.m3UAManagementImpl.managementEventListeners) {
                try {
                    m3uaManagementEventListener.onAsDown(this.asImpl, oldState);
                } catch (Throwable ee) {
                    logger.error("Exception while invoking onAsDown", ee);
                }
            }
        }
    }

}
