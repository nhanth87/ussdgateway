import javax.xml.stream.*;
import java.io.*;
import java.nio.file.*;

public class TestParse2 {
    public static void main(String[] args) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(args[0])));
        int idx = content.indexOf("?>");
        content = content.substring(0, idx + 2) + "\n<root>" + content.substring(idx + 2) + "</root>";
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XMLStreamReader reader = factory.createXMLStreamReader(new StringReader(content));
        
        int count = 0;
        while (reader.hasNext() && count < 50) {
            int event = reader.next();
            count++;
            if (event == XMLStreamConstants.START_ELEMENT) {
                System.out.println("START: " + reader.getLocalName());
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                System.out.println("END: " + reader.getLocalName());
            } else if (event == XMLStreamConstants.CHARACTERS) {
                String text = reader.getText().trim();
                if (!text.isEmpty()) System.out.println("TEXT: [" + text + "]");
            }
        }
        reader.close();
    }
}
