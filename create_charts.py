import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import numpy as np
import os

# Set up fonts properly
plt.rcParams["font.sans-serif"] = ["DejaVu Sans", "Arial", "Liberation Sans"]
plt.rcParams["axes.unicode_minus"] = False

# Color palette - Retro Metro
colors = ["#ea5545", "#f46a9b", "#ef9b20", "#edbf33", "#ede15b", "#bdcf32", "#87bc45", "#27aeef", "#b33dc6"]

# Create output directory
os.makedirs('C:/Users/Windows/Desktop/ethiopia-working-dir/charts', exist_ok=True)

# Chart 1: io_uring vs epoll Performance Comparison
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 6))

# Throughput comparison
operations = ['epoll\n(mitigation)', 'epoll\n(no mitigation)', 'io_uring n=1\n(mitigation)', 'io_uring n=1\n(no mitigation)', 'io_uring n=1000\n(mitigation)']
overhead_ns = [2200, 1730, 2600, 2130, 1607]  # Calculated: (1900+700)/1000 ≈ 2.6, then /1000 for batching

bars1 = ax1.bar(operations, overhead_ns, color=[colors[0], colors[0], colors[1], colors[1], colors[2]])
ax1.set_ylabel('Overhead per Operation (ns)', fontsize=11, fontweight='bold')
ax1.set_title('io_uring vs epoll: Per-Operation Overhead', fontsize=13, fontweight='bold', pad=15)
ax1.set_ylim(0, 3000)
ax1.grid(axis='y', alpha=0.3, linestyle='--')
for i, v in enumerate(overhead_ns):
    ax1.text(i, v + 80, f'{v}ns', ha='center', fontsize=10, fontweight='bold')

# Throughput at scale
config_labels = ['epoll\n1000 conn', 'io_uring\n1000 conn\n(~10% better)']
throughput_relative = [100, 110]  # epoll baseline, io_uring 10% better

bars2 = ax2.bar(config_labels, throughput_relative, color=[colors[0], colors[2]])
ax2.set_ylabel('Relative Throughput (%)', fontsize=11, fontweight='bold')
ax2.set_title('Network Throughput at Scale (1000 connections)', fontsize=13, fontweight='bold', pad=15)
ax2.set_ylim(0, 120)
ax2.axhline(y=100, color='gray', linestyle='--', linewidth=1, alpha=0.5)
ax2.grid(axis='y', alpha=0.3, linestyle='--')
for i, v in enumerate(throughput_relative):
    ax2.text(i, v + 2, f'{v}%', ha='center', fontsize=11, fontweight='bold')

plt.tight_layout()
plt.savefig('C:/Users/Windows/Desktop/ethiopia-working-dir/charts/iouring_vs_epoll_performance.png', dpi=300, bbox_inches='tight')
plt.close()

print("Chart 1 created: io_uring vs epoll performance comparison")

# Chart 2: SCTP CPU Overhead (from research paper)
fig, ax = plt.subplots(figsize=(12, 7))

protocols = ['TCP Send', 'SCTP Send\n(Unoptimized)', 'SCTP Send\n(Optimized)', 'TCP Receive', 'SCTP Receive\n(Unoptimized)', 'SCTP Receive\n(Optimized)']
cpu_util = [41.6, 89.0, 48.3, 40.3, 69.4, 50.4]  # Optimized values calculated from improvement ratios

bars = ax.bar(protocols, cpu_util, color=[colors[0], colors[3], colors[2], colors[0], colors[3], colors[2]])
ax.set_ylabel('CPU Utilization (%)', fontsize=12, fontweight='bold')
ax.set_title('SCTP vs TCP CPU Utilization (8KB Transfers, ~930 Mbps)', fontsize=14, fontweight='bold', pad=15)
ax.set_ylim(0, 100)
ax.axhline(y=50, color='gray', linestyle='--', linewidth=1, alpha=0.4, label='50% threshold')
ax.grid(axis='y', alpha=0.3, linestyle='--')
ax.legend(loc='upper right')

for i, v in enumerate(cpu_util):
    ax.text(i, v + 2, f'{v}%', ha='center', fontsize=10, fontweight='bold')

# Add annotations
ax.annotate('2.1x overhead', xy=(1, 89), xytext=(1.5, 95), 
            arrowprops=dict(arrowstyle='->', color='red', lw=1.5),
            fontsize=10, color='red', fontweight='bold')
ax.annotate('1.16x overhead\n(after optimization)', xy=(2, 48.3), xytext=(2.5, 60), 
            arrowprops=dict(arrowstyle='->', color='green', lw=1.5),
            fontsize=9, color='green', fontweight='bold')

plt.tight_layout()
plt.savefig('C:/Users/Windows/Desktop/ethiopia-working-dir/charts/sctp_cpu_overhead.png', dpi=300, bbox_inches='tight')
plt.close()

print("Chart 2 created: SCTP CPU overhead comparison")

# Chart 3: Network Offload Features Support Matrix
fig, ax = plt.subplots(figsize=(10, 6))

features = ['TSO\n(TCP Segmentation)', 'GSO\n(Generic Segmentation)', 'GRO\n(Generic Receive)', 'Checksum Offload']
tcp_support = [1, 1, 1, 1]  # Full hardware support
sctp_support = [0, 0.7, 0.7, 0]  # GSO software support, no hardware TSO/checksum

x = np.arange(len(features))
width = 0.35

bars1 = ax.bar(x - width/2, tcp_support, width, label='TCP (Hardware)', color=colors[0])
bars2 = ax.bar(x + width/2, sctp_support, width, label='SCTP (Limited)', color=colors[3])

ax.set_ylabel('Support Level', fontsize=11, fontweight='bold')
ax.set_title('Network Offload Feature Support: TCP vs SCTP', fontsize=13, fontweight='bold', pad=15)
ax.set_xticks(x)
ax.set_xticklabels(features, fontsize=10)
ax.set_yticks([0, 0.25, 0.5, 0.75, 1.0])
ax.set_yticklabels(['None', 'Limited', 'Partial', 'Good', 'Full'])
ax.legend(loc='upper right', fontsize=11)
ax.grid(axis='y', alpha=0.3, linestyle='--')

# Add annotations
ax.text(1, 0.75, 'Software\nGSO only', ha='center', fontsize=9, style='italic')
ax.text(0, 0.05, 'Not supported\nfor SCTP', ha='center', fontsize=8, color='red', style='italic')
ax.text(3, 0.05, 'CRC-32 required\n(not CRC-16)', ha='center', fontsize=8, color='red', style='italic')

plt.tight_layout()
plt.savefig('C:/Users/Windows/Desktop/ethiopia-working-dir/charts/network_offload_support.png', dpi=300, bbox_inches='tight')
plt.close()

print("Chart 3 created: Network offload support matrix")

# Chart 4: io_uring Architecture Diagram
fig, ax = plt.subplots(figsize=(12, 8))
ax.set_xlim(0, 10)
ax.set_ylim(0, 10)
ax.axis('off')

# User space
user_box = plt.Rectangle((0.5,6), 4, 3.5, facecolor='#e8f4f8', edgecolor=colors[7], linewidth=2)
ax.add_patch(user_box)
ax.text(2.5, 9, 'User Space', fontsize=14, fontweight='bold', ha='center')

# Submission Queue
sq_box = plt.Rectangle((0.8, 7.2), 1.5, 1.2, facecolor=colors[7], edgecolor='black', linewidth=1.5, alpha=0.7)
ax.add_patch(sq_box)
ax.text(1.55, 7.8, 'Submission\nQueue (SQ)', fontsize=10, ha='center', va='center', color='white', fontweight='bold')

# Completion Queue  
cq_box = plt.Rectangle((3.2, 7.2), 1.5, 1.2, facecolor=colors[2], edgecolor='black', linewidth=1.5, alpha=0.7)
ax.add_patch(cq_box)
ax.text(3.95, 7.8, 'Completion\nQueue (CQ)', fontsize=10, ha='center', va='center', color='white', fontweight='bold')

# Shared memory region
ax.add_patch(plt.Rectangle((0.5, 6.5), 4, 2.3, fill=False, edgecolor='purple', linewidth=2, linestyle='--'))
ax.text(4.6, 8.5, 'Shared\nMemory', fontsize=9, style='italic', color='purple')

# Kernel space
kernel_box = plt.Rectangle((0.5, 1.5), 4, 4, facecolor='#fff4e6', edgecolor=colors[3], linewidth=2)
ax.add_patch(kernel_box)
ax.text(2.5, 5.2, 'Kernel Space', fontsize=14, fontweight='bold', ha='center')

# io_uring subsystem
uring_box = plt.Rectangle((0.8, 3.5), 3.4, 1.3, facecolor=colors[3], edgecolor='black', linewidth=1.5, alpha=0.6)
ax.add_patch(uring_box)
ax.text(2.5, 4.1, 'io_uring Subsystem', fontsize=11, ha='center', color='white', fontweight='bold')

# Socket operations
sock_box = plt.Rectangle((0.8, 2), 3.4, 1, facecolor=colors[0], edgecolor='black', linewidth=1.5, alpha=0.6)
ax.add_patch(sock_box)
ax.text(2.5, 2.5, 'Socket Operations\n(send/recv/connect/accept)', fontsize=9, ha='center', color='white', fontweight='bold')

# Arrows
ax.annotate('', xy=(1.55, 6.9), xytext=(1.55, 6.5), arrowprops=dict(arrowstyle='->', lw=2, color=colors[7]))
ax.text(1.0, 6.7, 'Submit', fontsize=9, fontweight='bold', color=colors[7])

ax.annotate('', xy=(3.95, 6.5), xytext=(3.95, 6.9), arrowprops=dict(arrowstyle='->', lw=2, color=colors[2]))
ax.text(4.4, 6.7, 'Complete', fontsize=9, fontweight='bold', color=colors[2])

ax.annotate('', xy=(2.5, 3.4), xytext=(2.5, 3), arrowprops=dict(arrowstyle='<->', lw=2, color='black'))

# Supported protocols box
protocol_box = plt.Rectangle((5.5, 6), 4, 3.5, facecolor='#f0f0f0', edgecolor='black', linewidth=2)
ax.add_patch(protocol_box)
ax.text(7.5, 9, 'Supported Socket Types', fontsize=13, fontweight='bold', ha='center')

protocols_text = """TCP (SOCK_STREAM)
UDP (SOCK_DGRAM)  
SCTP (SOCK_STREAM/SEQPACKET)
Unix Domain Sockets
Raw Sockets

All socket families supported
(AF_INET, AF_INET6, etc.)
All protocols supported
(IPPROTO_TCP, IPPROTO_SCTP, etc.)
"""
ax.text(7.5, 7.2, protocols_text, fontsize=9, ha='center', va='center', family='monospace')

# Title
ax.text(5, 10.5, 'io_uring Architecture & Socket Protocol Support', fontsize=16, fontweight='bold', ha='center')

plt.tight_layout()
plt.savefig('C:/Users/Windows/Desktop/ethiopia-working-dir/charts/iouring_architecture.png', dpi=300, bbox_inches='tight')
plt.close()

print("Chart 4 created: io_uring architecture diagram")

print("\nAll charts created successfully!")
print("Output directory: C:/Users/Windows/Desktop/ethiopia-working-dir/charts/")
