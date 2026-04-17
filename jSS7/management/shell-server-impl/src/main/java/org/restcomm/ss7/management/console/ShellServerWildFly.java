
package org.restcomm.ss7.management.console;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;


import org.restcomm.protocols.ss7.scheduler.Scheduler;

/**
 * @author <a href="mailto:enmanuelcalero61@gmail.com"> Enmanuel Calero </a>
 *
 */
public class ShellServerWildFly extends ShellServer {

    private String securityDomain;
    private boolean started;

    public ShellServerWildFly(Scheduler scheduler, List<ShellExecutor> shellExecutors) throws IOException {
        super(scheduler, shellExecutors);
    }

    @Override
    protected void startSecurityManager(InitialContext initialContext, String securityDomain) throws NamingException {
        this.securityDomain = securityDomain;
        this.started = true;
    }

    @Override
    protected void putPrincipal(Map map, Principal principal) {
        map.put("principal", principal);
    }

    @Override
    protected boolean isAuthManagementLoaded() {
        return started;
    }

    @Override
    protected boolean isValid(Principal principal, Object credential) {
        // Permission extension bypassed - returning true for all valid principals
        return principal != null;
    }

    @Override
    protected String getLocalSecurityDomain() {
        return securityDomain;
    }

}
