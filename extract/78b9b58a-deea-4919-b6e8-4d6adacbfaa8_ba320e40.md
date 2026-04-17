## Configuration Overview, Continued

Environmental variables (continued)

<table><tr><td>Environment variable</td><td>Default</td><td>Description</td></tr><tr><td>TCAPIF_DEF</td><td>tcapif.def</td><td>Defines the name of the SLEE TCAP Interface's configuration file.</td></tr></table>

## Setting parameters

Each parameter value has a defined default value that will be used if the parameter is not defined.

The values for a parameter may be defined in two ways, either:

• the command line, or

![](extract/subset_21_25_8f3a20f0_e734050e/images/page_1_img_in_image_box_165_480_213_522.jpg)

• in the tcapif.def file.

Note: If a parameter is set in both the command line and the tcapif.def file, the command line setting will be used.

Defining the parameters

For a parameter 'paramval', the value may be set in the configuration file with a line such as:

![](extract/subset_21_25_8f3a20f0_e734050e/images/page_1_img_in_image_box_165_621_213_661.jpg)

PARAM VAL=<value>

Note: Spaces can be inserted into the parameter name without effect. Or it may be defined on the command line, for example:

sinapTcapInterface -paramval <value>

![](extract/subset_21_25_8f3a20f0_e734050e/images/page_1_img_in_image_box_166_719_213_760.jpg)

Note: Spaces can not be inserted into the parameter names on the command line, and parameter names are case sensitive.

Setting the command line parameters

Since the executable is started by the SLEE the only way to set the command line parameters is via an intermediate shell script. It is then this shell script that should be referenced by the SLEE configuration file. Example scripts are shown below:

![](extract/subset_21_25_8f3a20f0_e734050e/images/page_1_img_in_image_box_165_862_213_902.jpg)

Note: To split over a line (example 2), use "\" at the end of the line.

Example 1:

#!/bin/sh
exec sinapTcapInterface -ssns 123 -proto inap -tcap ccitt

## Example 2:

#!/bin/sh

exec tcpTcapInterface -pc 160 -ssns 123 -proto inap -tcp \
ccitt

### tcpif.def

The tcapif.def file can be used to define common configuration for all TCAP interfaces running on the same system.

Where different TCAP interfaces required different configuration, the file-set configuration options can be overridden on the command line on a binary by binary basis. Command line options are set in the startup shell scripts. Every option in the tcap.def file can be overridden in this way.

Note: In the file, the options are all uppercase. On the command line, they are lowercase.

Continued on next page

## Configuration Overview, Continued

<table><tr><td>Variable</td><td>Possible values</td><td>Default</td><td>Description</td></tr><tr><td>alwayssendaddr</td><td>true or false</td><td>false</td><td>If set to true the TCAP interface will send up the SCCP destination address (and origination address if sendorigaddr=true) in all messages not just the initial message received in a dialogue.</td></tr><tr><td>autoac</td><td>yes or no</td><td>yes</td><td>If set to yes the Application context of a CCITT white book TCAP message received will be used in the response to the message if the SLEE application does not supply one. This effectively acknowledges the white book application context used. In addition dialogues initiated by the interface will use the default application context defined by the defoutac variable below.</td></tr><tr><td>defoutac</td><td>none or &lt;oid1&gt;,&lt;oid2&gt;...</td><td>none</td><td>If more than 0, this parameter defines the default CCITT white book TCAP Application Context to use for dialogues initiated by the interface.</td></tr><tr><td>displaymonitors</td><td>true or false</td><td>false</td><td>If set to true new call attempt rates (CAPS) will be logged to stdout at period defined by -reportperiod.</td></tr><tr><td>dpause</td><td>integer</td><td>0</td><td>Sleep for the specified number of seconds at start up. Useful to allow a global session to be attached to the process.</td></tr><tr><td>inapssns</td><td>&lt;s1&gt;,&lt;s2&gt; where sn is a decimal integer 0-255</td><td></td><td>A comma-separated list of the SCCP subsystem numbers (SSNs) that the TCAP interface will treat as INAP (regardless of the default protocol defined by -proto).</td></tr><tr><td>mapssns</td><td>&lt;s1&gt;,&lt;s2&gt; where sn is a decimal integer 0-255</td><td></td><td>A comma-separated list of the SCCP subsystem numbers (SSNs) that the TCAP interface will treat as MAP (regardless of the default protocol defined by -proto).</td></tr><tr><td>monitorperiod</td><td>decimal integer</td><td>1000</td><td>Period (in msecs) over which call rate rejection monitoring occurs. Default of 1 sec allows -rejectlevel to represent CAPS.</td></tr><tr><td>polltime</td><td>decimal integer</td><td>1000</td><td>Polling time of interface in microseconds.</td></tr><tr><td>proto</td><td>inap, map, is41d</td><td>inap</td><td>Protocol to be conveyed by this TCAP interface.</td></tr><tr><td>rejectlevel</td><td>decimal integer</td><td>0</td><td>If more than 0, this sets the maximum number of new call attempts that will be processed within a given interval (as determined by 'monitorperiod'). In conjunction with -monitorperiod it provides a call limiter for the interface.</td></tr></table>

## Configuration Overview, Continued

<div>Generic configuration (continued)</div>

<table><tr><td>Variable</td><td>Possible values</td><td>Default</td><td>Description</td></tr><tr><td>reportperiod</td><td>decimal integer</td><td>30</td><td>Period (in seconds) that rejection indications are logged to the alarm system (if rejections are occurring) and call rates are logged (if -displaymonitors) is set.</td></tr><tr><td>retgt</td><td>&lt;gttype&gt;, ...</td><td>none</td><td>If not set to none a default SCCP Origination Address will be used which will contain this Global Title.</td></tr><tr><td>retpc</td><td>hex or decimal integer</td><td>0</td><td>If set to a non-zero value a default SCCP Origination Address will be used which will contain this Point Code.</td></tr><tr><td>retri</td><td>0 or 1</td><td>0</td><td>Default SCCP Origination Address's routing indicator (0=route on GT, 1=route on PC). Used in conjunction with rettsn, retpc, retgt options.</td></tr><tr><td>retssn</td><td>integer 0-255</td><td>0</td><td>If set to a non-zero value a default SCCP Origination Address will be used in the first outgoing TC-CONTINUE message which will contain this SSN.</td></tr><tr><td>sendorigaddr</td><td>true or false</td><td>false</td><td>If set to true the TCAP interface will send up the SCCP origination address as well as the destination address.
Note: If statsif is defined, then sendorigaddr is set true regardless of the configuration value.</td></tr><tr><td>sleekey</td><td>hex or decimal integer</td><td>0x1</td><td>Base SLEE service key value to use. 0xabcd represents a base SLEE service key of 0xabcd000000000</td></tr><tr><td>ssns</td><td>&lt;s1&gt;,&lt;s2&gt; where sn is decimal integer 0 to 255</td><td>19</td><td>A comma separated list of SCCP subsystem numbers (SSNs) that the TCAP will register to.</td></tr><tr><td>statsif</td><td>none or SLEE interface name</td><td>none</td><td>If not set to none this defines a SLEE interface to which all initial messages in a TCAP dialogue are copied to allow statistics monitoring. Currently this is only used with the SLEE Callmapping solution.</td></tr><tr><td>stderr</td><td>0 or 1</td><td>0</td><td>Should syslog messages generated by the interface class also be printed to syserr as well as the system log file.</td></tr><tr><td>stps</td><td>none or &lt;pc1&gt;,&lt;pc2&gt; where pcn is a decimal integer</td><td>none</td><td>If not set to none it is a comma-separated list of STP point codes to which the interface should round-robin route outward messages. Each PC will be substituted into the MTP destination addresses.</td></tr></table>

Continued on next page

## Configuration Overview, Continued

## Generic configuration (continued)

![](extract/subset_21_25_8f3a20f0_e734050e/images/page_4_img_in_image_box_165_238_214_279.jpg)

## Note:

If statsif is defined, sendorigaddr is set true regardless of the configuration value.

If any of these parameters are set (note: all default to not set) then the TCAP interface will use a default SCCP Origination Address in the first TC-CONTINUE that it sends out (by default the TCAP will use the SCCP destination address of the first incoming message). This capability can be used to ensure any subsequent messages sent by the far end in the same dialogue will be routed to this address, this can be useful when initial messages are sent to aliased addresses and round-robin routed by an STP to a series of UASs.

The format of a Global Title depends on the GT type as follows:

• Type 1: "1.<noa>,<address digits>"

• Type 2: "2.<trans type><address digits>"

• Type 3: "3.<trans type>,<num plan>,<address digits>"

• Type 4: "4.<trans type>,<num plan>,<noa>,<address digits>"

<div>Hughes SCCP Interface Configuration</div>

<table><tr><td>Introduction</td><td colspan="3">This section defines the configuration variables that can be set for the Hughes SigTran TCAP interface (the hssSccpInterface executable).</td></tr><tr><td>Configuration components - Hughes SCCP</td><td colspan="3">The Hughes stack is configured by the following components:</td></tr><tr><td></td><td>Component</td><td>Locations</td><td>Description</td></tr><tr><td></td><td>hssSclf.sh</td><td>all SCPs</td><td>This file sets the command line parameters for the hssSccpTcapInterface binary.</td></tr><tr><td></td><td>peers.conf</td><td>all SCPs</td><td>This file configures SUA/STP peers.</td></tr><tr><td></td><td>smsStatsDaemon SuaStats.cfg</td><td>all SCPs</td><td>This file defines the statistics which are produced by the Hughes interface.</td></tr><tr><td></td><td>tcapif.def</td><td>all SCPs</td><td>Provides common configuration for all TCAP level interfaces. These options can be overridden by command line options on a interface by interface basis.</td></tr><tr><td>Hughes interface configuration</td><td colspan="3">The Hughes SigTran variant of the TCAP IF (hssSclf) accepts the following command line parameters.</td></tr><tr><td><img src="imgs/img_in_image_box_145_774_215_825.jpg"/></td><td colspan="3">Note: These parameters are usually set in the start up script, hssSclf.sh.</td></tr><tr><td></td><td>Default</td><td colspan="2">Description</td></tr><tr><td>asidbase</td><td>1</td><td colspan="2">Application Server IDs are allocated incrementally, starting from this number. They must:  • be unique within the network, and  • match those configured in the gateways.</td></tr><tr><td>asmode</td><td>2</td><td colspan="2">How the SG should handle collisions on routing keys (PC/SSN combinations).  • 1 = override (the last connection wins)  • 2 = load share (default)  • 3 = broadcast (the data is sent to all matches)</td></tr><tr><td>assoc_max_retrans</td><td>8</td><td colspan="2">The maximum number of times that SCTP will attempt to establish a connection with its signaling peer or gateway before deleting an association. Used in conjunction with path_max_retrans.</td></tr><tr><td>bundling_time</td><td>20</td><td colspan="2">Milliseconds before Hughes will send a bundle of TCAP messages. Hughes can bundle more than one TCAP message into an IP packet.</td></tr><tr><td>closed_stats_dir</td><td>/IN/service_packages/ SLEE/stats/closed</td><td colspan="2">Directory to copy statistics files that have been fully written to. These files are moved from current_stats_dir to closed_stats_dir.</td></tr></table>

Continued on next page