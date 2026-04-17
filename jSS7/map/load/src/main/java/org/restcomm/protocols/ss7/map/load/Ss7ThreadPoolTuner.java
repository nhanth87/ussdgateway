package org.restcomm.protocols.ss7.map.load;

import java.util.concurrent.Executors;

import org.restcomm.protocols.ss7.mtp.Mtp3UserPart;
import org.restcomm.protocols.ss7.sccp.impl.SccpStackImpl;
import org.restcomm.protocols.ss7.tcap.api.TCAPStack;

/**
 * Utility tự động phát hiện số CPU core và phân phối thread-pool tối đa
 * cho toàn bộ jSS7 stack (M3UA → SCCP → TCAP → MAP/CAP/INAP).
 *
 * <p>Logic thiết kế để <b>sử dụng tối đa CPU</b> cho các tầng xử lý nặng
 * (ASN.1 parse, dialog management, FSM, routing), chỉ để lại reserve tối thiểu
 * cho OS / GC.</p>
 *
 * <p>Khi <b>Client và Server chạy cùng một physical server</b>, mỗi process
 * nên dùng {@link ProcessType} để chia đôi CPU (hoặc theo tỷ lệ mong muốn).</p>
 *
 * <p>Ví dụ với 64 core chia đôi (32 core mỗi process):</p>
 * <pre>
 *   Netty I/O        = 4   (SCTP read/write buffer từ kernel)
 *   M3UA delivery    = 6   (M3UA parse + AS/ASP FSM)
 *   SCCP delivery    = 6   (XUDT parse + GT routing)
 *   TCAP scheduled   = 10  (dialog + component + timer — tầng nặng nhất)
 *   MAP/CAP/INAP     = 5   (business callbacks + encode/decode ASN.1)
 *   Reserve (OS/GC)  = 1   (tối thiểu)
 * </pre>
 */
public class Ss7ThreadPoolTuner {

    public enum ProcessType {
        /** Client process — lấy một nửa CPU của server */
        CLIENT,
        /** Server process — lấy một nửa CPU của server */
        SERVER
    }

    private static final int MIN_NETTY_IO = 2;
    private static final int MIN_M3UA = 2;
    private static final int MIN_SCCP = 2;
    private static final int MIN_TCAP = 4;
    private static final int MIN_MAP_CAP_INAP = 2;
    private static final int MIN_RESERVE = 1;

    private final int totalCpus;
    private final ProcessType processType;
    private final double cpuRatio;

    // Kết quả tính toán
    private int nettyIoThreads;
    private int m3uaDeliveryThreads;
    private int sccpDeliveryThreads;
    private int tcapScheduledThreads;
    private int mapCapInapBusinessThreads;
    private int reservedThreads;

    public Ss7ThreadPoolTuner(ProcessType processType) {
        this(Runtime.getRuntime().availableProcessors(), processType, 0.5);
    }

    public Ss7ThreadPoolTuner(int totalCpus, ProcessType processType, double cpuRatio) {
        if (totalCpus < 1) {
            throw new IllegalArgumentException("totalCpus must be >= 1");
        }
        if (cpuRatio <= 0.0 || cpuRatio > 1.0) {
            throw new IllegalArgumentException("cpuRatio must be in (0.0, 1.0]");
        }
        this.totalCpus = totalCpus;
        this.processType = processType;
        this.cpuRatio = cpuRatio;
        recalculate();
    }

    /**
     * Tính toán lại thread counts theo heuristic tối đa hóa CPU usage.
     *
     * <p>TCAP được ưu tiên cao nhất vì là tầng nặng nhất (BER-ASN.1 decode,
     * dialog lifecycle, component matching, timeout scheduler).</p>
     */
    public void recalculate() {
        int cpus = Math.max(1, (int) (this.totalCpus * this.cpuRatio));

        if (cpus <= 4) {
            this.nettyIoThreads = Math.max(1, cpus / 4);
            this.m3uaDeliveryThreads = Math.max(1, cpus / 4);
            this.sccpDeliveryThreads = Math.max(1, cpus / 4);
            this.tcapScheduledThreads = Math.max(2, cpus / 2);
            this.mapCapInapBusinessThreads = Math.max(1, cpus / 4);
            this.reservedThreads = Math.max(0, cpus - (nettyIoThreads + m3uaDeliveryThreads
                    + sccpDeliveryThreads + tcapScheduledThreads + mapCapInapBusinessThreads));
            return;
        }

        // Netty I/O: cố định 4-8 thread (đủ cho 4+ SCTP associations)
        this.nettyIoThreads = Math.min(8, Math.max(MIN_NETTY_IO, cpus / 10));

        // TCAP: nặng nhất → 28-30%
        this.tcapScheduledThreads = Math.max(MIN_TCAP, (int) (cpus * 0.30));

        // M3UA + SCCP: mỗi tầng ~18-20%
        this.m3uaDeliveryThreads = Math.max(MIN_M3UA, (int) (cpus * 0.19));
        this.sccpDeliveryThreads = Math.max(MIN_SCCP, (int) (cpus * 0.19));

        // MAP/CAP/INAP business: ~18%
        this.mapCapInapBusinessThreads = Math.max(MIN_MAP_CAP_INAP, (int) (cpus * 0.18));

        int allocated = nettyIoThreads + m3uaDeliveryThreads + sccpDeliveryThreads
                + tcapScheduledThreads + mapCapInapBusinessThreads;

        // Reserve: tối đa 4 core, hoặc ít hơn nếu CPU ít
        this.reservedThreads = Math.max(MIN_RESERVE, Math.min(4, cpus - allocated));

        // Nếu vẫn còn dư CPU sau reserve, cho thêm vào TCAP (bottleneck chính)
        int leftover = cpus - (allocated + reservedThreads);
        if (leftover > 0) {
            this.tcapScheduledThreads += leftover;
        }

        // Sanity check
        int finalAllocated = nettyIoThreads + m3uaDeliveryThreads + sccpDeliveryThreads
                + tcapScheduledThreads + mapCapInapBusinessThreads;
        if (finalAllocated > cpus) {
            int over = finalAllocated - cpus;
            this.tcapScheduledThreads = Math.max(MIN_TCAP, this.tcapScheduledThreads - over);
        }
    }

    /**
     * Áp dụng thread count cho M3UA.
     * <p><b>Lưu ý:</b> phải gọi trước {@code m3uaMgmt.start()}.</p>
     */
    public void applyM3UA(Mtp3UserPart m3uaMgmt) throws Exception {
        if (m3uaMgmt != null) {
            m3uaMgmt.setDeliveryMessageThreadCount(this.m3uaDeliveryThreads);
        }
    }

    /**
     * Áp dụng thread count cho SCCP.
     * <p><b>Lưu ý:</b> phải gọi trước {@code sccpStack.start()}.</p>
     */
    public void applySCCP(SccpStackImpl sccpStack) throws Exception {
        if (sccpStack != null) {
            sccpStack.setDeliveryMessageThreadCount(this.sccpDeliveryThreads);
        }
    }

    /**
     * Patch TCAP ScheduledExecutorService để tăng số thread từ mặc định 4
     * lên giá trị đã tính toán.
     * <p><b>Lưu ý:</b> nên gọi trước {@code tcapStack.start()}.</p>
     */
    public void applyTCAP(TCAPStack tcapStack) {
        if (tcapStack == null || this.tcapScheduledThreads <= 0) {
            return;
        }
        try {
            java.lang.reflect.Field providerField = tcapStack.getClass().getDeclaredField("provider");
            providerField.setAccessible(true);
            Object provider = providerField.get(tcapStack);
            if (provider == null) {
                return;
            }

            java.lang.reflect.Field execField = provider.getClass().getDeclaredField("_EXECUTOR");
            execField.setAccessible(true);

            java.util.concurrent.ScheduledExecutorService oldExec =
                    (java.util.concurrent.ScheduledExecutorService) execField.get(provider);

            java.util.concurrent.ScheduledExecutorService newExec =
                    Executors.newScheduledThreadPool(this.tcapScheduledThreads,
                            new io.netty.util.concurrent.DefaultThreadFactory("Tcap-Thread"));

            execField.set(provider, newExec);

            if (oldExec != null) {
                oldExec.shutdown();
            }
        } catch (Exception e) {
            System.err.println("Failed to patch TCAP executor thread count: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Áp dụng cấu hình vào các stack instance cùng lúc.
     * <p><b>Lưu ý:</b> các stack phải được gọi <em>trước</em> {@code start()}.</p>
     */
    public void applyToStacks(Mtp3UserPart m3uaMgmt,
                              SccpStackImpl sccpStack,
                              TCAPStack tcapStack) throws Exception {
        applyM3UA(m3uaMgmt);
        applySCCP(sccpStack);
        applyTCAP(tcapStack);
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------

    public int getTotalCpus() { return totalCpus; }
    public ProcessType getProcessType() { return processType; }
    public double getCpuRatio() { return cpuRatio; }
    public int getNettyIoThreads() { return nettyIoThreads; }
    public int getM3uaDeliveryThreads() { return m3uaDeliveryThreads; }
    public int getSccpDeliveryThreads() { return sccpDeliveryThreads; }
    public int getTcapScheduledThreads() { return tcapScheduledThreads; }
    public int getMapCapInapBusinessThreads() { return mapCapInapBusinessThreads; }
    public int getReservedThreads() { return reservedThreads; }

    public int getAllocatedThreads() {
        return nettyIoThreads + m3uaDeliveryThreads + sccpDeliveryThreads
                + tcapScheduledThreads + mapCapInapBusinessThreads;
    }

    /**
     * In bảng phân bổ ra console/log.
     */
    public void printAllocation() {
        System.out.println("===== SS7 Thread Pool Allocation (Total CPUs=" + totalCpus
                + ", Process=" + processType + ", Ratio=" + cpuRatio + ") =====");
        System.out.printf("%-26s = %d%n", "Netty I/O", nettyIoThreads);
        System.out.printf("%-26s = %d%n", "M3UA delivery", m3uaDeliveryThreads);
        System.out.printf("%-26s = %d%n", "SCCP delivery", sccpDeliveryThreads);
        System.out.printf("%-26s = %d%n", "TCAP scheduled", tcapScheduledThreads);
        System.out.printf("%-26s = %d%n", "MAP/CAP/INAP business", mapCapInapBusinessThreads);
        System.out.printf("%-26s = %d%n", "Reserve (OS/GC)", reservedThreads);
        System.out.printf("%-26s = %d%n", "Total allocated", getAllocatedThreads());
        System.out.println("===================================================================");
    }
}
