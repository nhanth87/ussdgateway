import javax.xml.stream.*;
import java.io.*;
import java.nio.file.*;

public class TestParse {
    public static void main(String[] args) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(args[0])));
        int idx = content.indexOf("?>");
        content = content.substring(0, idx + 2) + "\n<root>" + content.substring(idx + 2) + "</root>";
        System.out.println("Wrapped: " + content);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(content));
        int count = 0;
        while (reader.hasNext() && count < 20) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                System.out.println("START: " + reader.getLocalName());
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                System.out.println("END: " + reader.getLocalName());
            }
            count++;
        }
    }
}
