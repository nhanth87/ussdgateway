package org.restcomm.protocols.ss7.m3ua.impl.scheduler;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

/**
 *
 * @author amit bhayani
 *
 */
public class M3UAScheduler implements Runnable {
    private static final Logger logger = Logger.getLogger(M3UAScheduler.class);

    // TODO : Synchronize tasks? Use Iterator?
    protected final CopyOnWriteArrayList<M3UATask> tasks = new CopyOnWriteArrayList<M3UATask>();

    private final CopyOnWriteArrayList<M3UATask> removed = new CopyOnWriteArrayList<M3UATask>();

    public void execute(M3UATask task) {
        if (task == null) {
            return;
        }
        this.tasks.add(task);
    }

    public void run() {
        long now = System.currentTimeMillis();
        for (M3UATask task : tasks) {
            // check if has been canceled from different thread.
            if (task.isCanceled()) {
                // tasks.delete(n);
                removed.add(task);
            } else {

                try {
                    task.run(now);
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failure on task run.", e);
                    }
                }
                // check if its canceled after run;
                if (task.isCanceled()) {
                    removed.add(task);
                }
            }
            // tempTask = null;
        }

        if (this.removed.size() > 0) {
            this.tasks.removeAll(this.removed);
            this.removed.clear();
        }
    }
}
