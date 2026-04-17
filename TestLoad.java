import javax.xml.stream.*;
import java.io.*;
import java.nio.file.*;
import org.restcomm.protocols.ss7.sccp.impl.parameter.*;
import org.restcomm.protocols.ss7.sccp.parameter.*;
import org.restcomm.protocols.ss7.indicator.*;
import org.restcomm.protocols.ss7.sccp.*;

public class TestLoad {
    public static void main(String[] args) throws Exception {
        String fn = args[0];
        String content = new String(Files.readAllBytes(Paths.get(fn)));
        int idx = content.indexOf("?>");
        content = content.substring(0, idx + 2) + "\n<root>" + content.substring(idx + 2) + "</root>";
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(content));

        int rules = 0;
        int addrs = 0;

        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String localName = reader.getLocalName();
                if ("rule".equals(localName)) {
                    while (reader.hasNext()) {
                        event = reader.next();
                        if (event == XMLStreamConstants.END_ELEMENT && "rule".equals(reader.getLocalName())) {
                            break;
                        }
                        if (event == XMLStreamConstants.START_ELEMENT && "id".equals(reader.getLocalName())) {
                            Integer id = Integer.valueOf(reader.getAttributeValue(null, "value"));
                            while (reader.hasNext()) {
                                event = reader.next();
                                if (event == XMLStreamConstants.START_ELEMENT && "value".equals(reader.getLocalName())) {
                                    readRule(reader);
                                    rules++;
                                    break;
                                }
                            }
                        }
                    }
                } else if ("routingAddress".equals(localName)) {
                    while (reader.hasNext()) {
                        event = reader.next();
                        if (event == XMLStreamConstants.END_ELEMENT && "routingAddress".equals(reader.getLocalName())) {
                            break;
                        }
                        if (event == XMLStreamConstants.START_ELEMENT && "id".equals(reader.getLocalName())) {
                            Integer id = Integer.valueOf(reader.getAttributeValue(null, "value"));
                            while (reader.hasNext()) {
                                event = reader.next();
                                if (event == XMLStreamConstants.START_ELEMENT && "sccpAddress".equals(reader.getLocalName())) {
                                    readSccpAddress(reader);
                                    addrs++;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        reader.close();
        System.out.println("Loaded rules=" + rules + ", addresses=" + addrs);
    }

    static SccpAddressImpl readSccpAddress(XMLStreamReader reader) throws Exception {
        int pc = Integer.parseInt(reader.getAttributeValue(null, "pc"));
        int ssn = Integer.parseInt(reader.getAttributeValue(null, "ssn"));
        int aiValue = 0;
        GlobalTitle gt = null;
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.END_ELEMENT && "sccpAddress".equals(reader.getLocalName())) {
                break;
            }
            if (event == XMLStreamConstants.START_ELEMENT) {
                String name = reader.getLocalName();
                if ("ai".equals(name)) {
                    aiValue = Integer.parseInt(reader.getAttributeValue(null, "value"));
                } else if ("gt".equals(name)) {
                    gt = readGlobalTitle(reader);
                }
            }
        }
        AddressIndicator ai = new AddressIndicator((byte) aiValue, SccpProtocolVersion.ITU);
        return new SccpAddressImpl(ai.getRoutingIndicator(), gt, pc, ssn);
    }

    static EncodingScheme encodingSchemeFromCode(int code) {
        switch (code) {
            case 1: return BCDOddEncodingScheme.INSTANCE;
            case 2: return BCDEvenEncodingScheme.INSTANCE;
            default: return DefaultEncodingScheme.INSTANCE;
        }
    }

    static GlobalTitle readGlobalTitle(XMLStreamReader reader) throws Exception {
        String type = reader.getAttributeValue(null, "type");
        String digits = reader.getAttributeValue(null, "digits");
        if (digits == null) digits = "";
        GlobalTitle gt = null;
        if ("GT0100".equals(type)) {
            int tt = Integer.parseInt(reader.getAttributeValue(null, "tt"));
            int es = Integer.parseInt(reader.getAttributeValue(null, "es"));
            int np = Integer.parseInt(reader.getAttributeValue(null, "np"));
            int nai = Integer.parseInt(reader.getAttributeValue(null, "nai"));
            gt = new GlobalTitle0100Impl(digits, tt, encodingSchemeFromCode(es), NumberingPlan.valueOf(np), NatureOfAddress.valueOf(nai));
        } else if ("GT0011".equals(type)) {
            int tt = Integer.parseInt(reader.getAttributeValue(null, "tt"));
            int es = Integer.parseInt(reader.getAttributeValue(null, "es"));
            int np = Integer.parseInt(reader.getAttributeValue(null, "np"));
            gt = new GlobalTitle0011Impl(digits, tt, encodingSchemeFromCode(es), NumberingPlan.valueOf(np));
        } else if ("GT0010".equals(type)) {
            int tt = Integer.parseInt(reader.getAttributeValue(null, "tt"));
            gt = new GlobalTitle0010Impl(digits, tt);
        } else if ("GT0001".equals(type)) {
            int nai = Integer.parseInt(reader.getAttributeValue(null, "nai"));
            gt = new GlobalTitle0001Impl(digits, NatureOfAddress.valueOf(nai));
        } else if ("NoGlobalTitle".equals(type)) {
            gt = new NoGlobalTitle();
        }
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.END_ELEMENT && "gt".equals(reader.getLocalName())) {
                break;
            }
        }
        return gt;
    }

    static void readRule(XMLStreamReader reader) throws Exception {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.END_ELEMENT && "value".equals(reader.getLocalName())) {
                break;
            }
            if (event == XMLStreamConstants.START_ELEMENT) {
                String name = reader.getLocalName();
                if ("patternSccpAddress".equals(name)) {
                    readSccpAddress(reader);
                } else if ("patternCallingAddress".equals(name)) {
                    readSccpAddress(reader);
                }
            }
        }
    }
}
