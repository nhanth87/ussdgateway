import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.restcomm.protocols.ss7.sccp.impl.RemoteSubSystemMap;

public class TestJackson {
    public static void main(String[] args) throws Exception {
        XmlMapper mapper = new XmlMapper();
        RemoteSubSystemMap<Integer, String> map = new RemoteSubSystemMap<>();
        map.put(1, "a");
        map.put(2, "b");
        String xml = mapper.writeValueAsString(map);
        System.out.println("Serialized:");
        System.out.println(xml);
        RemoteSubSystemMap<Integer, String> map2 = mapper.readValue(xml, RemoteSubSystemMap.class);
        System.out.println("Deserialized size: " + map2.size());
        System.out.println("Entry 1: " + map2.get(1));
    }
}
