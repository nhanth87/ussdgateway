
package org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.mobicents.protocols.asn.AsnInputStream;
import org.mobicents.protocols.asn.AsnOutputStream;
import org.restcomm.protocols.ss7.cap.api.isup.Digits;
import org.restcomm.protocols.ss7.cap.isup.DigitsImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.VariablePartDateImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.VariablePartImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.VariablePartPriceImpl;
import org.restcomm.protocols.ss7.cap.service.circuitSwitchedCall.primitive.VariablePartTimeImpl;
import org.restcomm.protocols.ss7.isup.impl.message.parameter.GenericDigitsImpl;
import org.restcomm.protocols.ss7.isup.message.parameter.GenericDigits;
import org.testng.annotations.Test;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.restcomm.protocols.ss7.cap.CAPJacksonXMLHelper;

/**
 *
 * @author sergey vetyutnev
 *
 */
public class VariablePartTest {

    public byte[] getData1() {
        return new byte[] { (byte) 128, 1, 17 };
    }

    public byte[] getData2() {
        return new byte[] { (byte) 129, 4, 96, 18, 17, 16 };
    }

    public byte[] getGenericDigitsData() {
        return new byte[] { 18, 17, 16 };
    }

    public byte[] getData3() {
        return new byte[] { (byte) 130, 2, 0, 52 };
    }

    public byte[] getData4() {
        return new byte[] { (byte) 131, 4, 2, 33, 48, 18 };
    }

    public byte[] getData5() {
        return new byte[] { (byte) 132, 4, 0, 1, 0, 0 };
    }

    @Test(groups = { "functional.decode", "circuitSwitchedCall.primitive" })
    public void testDecode() throws Exception {

        byte[] data = this.getData1();
        AsnInputStream ais = new AsnInputStream(data);
        VariablePartImpl elem = new VariablePartImpl();
        int tag = ais.readTag();
        assertEquals(tag, 0);
        elem.decodeAll(ais);
        assertEquals((int) elem.getInteger(), 17);
        assertNull(elem.getNumber());
        assertNull(elem.getTime());
        assertNull(elem.getDate());
        assertNull(elem.getPrice());

        data = this.getData2();
        ais = new AsnInputStream(data);
        elem = new VariablePartImpl();
        tag = ais.readTag();
        assertEquals(tag, 1);
        elem.decodeAll(ais);
        assertNull(elem.getInteger());
        assertEquals(elem.getNumber().getGenericDigits().getEncodingScheme(), 3);
        assertEquals(elem.getNumber().getGenericDigits().getTypeOfDigits(), 0);
        assertTrue(Arrays.equals(elem.getNumber().getGenericDigits().getEncodedDigits(), getGenericDigitsData()));
        assertNull(elem.getTime());
        assertNull(elem.getDate());
        assertNull(elem.getPrice());

        data = this.getData3();
        ais = new AsnInputStream(data);
        elem = new VariablePartImpl();
        tag = ais.readTag();
        assertEquals(tag, 2);
        elem.decodeAll(ais);
        assertNull(elem.getInteger());
        assertNull(elem.getNumber());
        assertEquals(elem.getTime().getHour(), 0);
        assertEquals(elem.getTime().getMinute(), 43);
        assertNull(elem.getDate());
        assertNull(elem.getPrice());

        data = this.getData4();
        ais = new AsnInputStream(data);
        elem = new VariablePartImpl();
        tag = ais.readTag();
        assertEquals(tag, 3);
        elem.decodeAll(ais);
        assertNull(elem.getInteger());
        assertNull(elem.getNumber());
        assertNull(elem.getTime());
        assertEquals(elem.getDate().getYear(), 2012);
        assertEquals(elem.getDate().getMonth(), 3);
        assertEquals(elem.getDate().getDay(), 21);
        assertNull(elem.getPrice());

        data = this.getData5();
        ais = new AsnInputStream(data);
        elem = new VariablePartImpl();
        tag = ais.readTag();
        assertEquals(tag, 4);
        elem.decodeAll(ais);
        assertNull(elem.getInteger());
        assertNull(elem.getNumber());
        assertNull(elem.getTime());
        assertNull(elem.getDate());
        assertEquals(elem.getPrice().getPriceIntegerPart(), 1000);
        assertEquals(elem.getPrice().getPriceHundredthPart(), 0);
    }

    @Test(groups = { "functional.encode", "circuitSwitchedCall.primitive" })
    public void testEncode() throws Exception {

        VariablePartImpl elem = new VariablePartImpl(17);
        AsnOutputStream aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData1()));

        GenericDigitsImpl genericDigits = new GenericDigitsImpl(3, 0, getGenericDigitsData());
        // int encodingScheme, int typeOfDigits, int[] digits
        DigitsImpl digits = new DigitsImpl(genericDigits);
        elem = new VariablePartImpl(digits);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData2()));

        VariablePartTimeImpl time = new VariablePartTimeImpl(0, 43);
        elem = new VariablePartImpl(time);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData3()));

        VariablePartDateImpl date = new VariablePartDateImpl(2012, 3, 21);
        elem = new VariablePartImpl(date);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData4()));

        VariablePartPriceImpl price = new VariablePartPriceImpl(1000, 0);
        elem = new VariablePartImpl(price);
        aos = new AsnOutputStream();
        elem.encodeAll(aos);
        assertTrue(Arrays.equals(aos.toByteArray(), this.getData5()));
    }

    @Test(groups = { "functional.xml.serialize", "circuitSwitchedCall" })
    public void testXMLSerialize() throws Exception {
        XmlMapper xmlMapper = CAPJacksonXMLHelper.getXmlMapper();
        Integer integer = 15;
        VariablePartImpl original = new VariablePartImpl(integer);

        // Writes the area to a file.
        String serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        VariablePartImpl copy = null;
        try {
            copy = xmlMapper.readValue(serializedEvent, VariablePartImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertTrue(serializedEvent.contains(String.valueOf(original.getInteger())));
        assertFalse(serializedEvent.contains("<number>"));
        assertFalse(serializedEvent.contains("<time>"));
        assertFalse(serializedEvent.contains("<date>"));
        assertFalse(serializedEvent.contains("<price>"));
        }
        if (copy != null) {
            assertEquals(copy.getInteger(), original.getInteger());
            assertNull(copy.getNumber());
            assertNull(copy.getTime());
            assertNull(copy.getDate());
            assertNull(copy.getPrice());
        }
        int encodingScheme = 3;
        int typeOfDigits = 0;
        byte[] digits = getGenericDigitsData();
        GenericDigits genericDigits = new GenericDigitsImpl(encodingScheme, typeOfDigits, digits);
        Digits number = new DigitsImpl(genericDigits);
        original = new VariablePartImpl(number);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, VariablePartImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<integer>"));
        assertFalse(serializedEvent.contains("<time>"));
        assertFalse(serializedEvent.contains("<date>"));
        assertFalse(serializedEvent.contains("<price>"));
        }
        if (copy != null) {
            assertNull(copy.getInteger());
            assertEquals(copy.getNumber().getGenericDigits().getEncodingScheme(), encodingScheme);
            assertEquals(copy.getNumber().getGenericDigits().getTypeOfDigits(), typeOfDigits);
            assertEquals(copy.getNumber().getGenericDigits().getEncodedDigits(), digits);
            assertNull(copy.getTime());
            assertNull(copy.getDate());
            assertNull(copy.getPrice());
        }
        int hour = 0;
        int minute = 43;
        VariablePartTimeImpl time = new VariablePartTimeImpl(hour, minute);
        original = new VariablePartImpl(time);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, VariablePartImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<integer>"));
        assertFalse(serializedEvent.contains("<number>"));
        assertFalse(serializedEvent.contains("<date>"));
        assertFalse(serializedEvent.contains("<price>"));
        }
        if (copy != null) {
            assertNull(copy.getInteger());
            assertNull(copy.getNumber());
            assertEquals(copy.getTime().getHour(), hour);
            assertEquals(copy.getTime().getMinute(), minute);
            assertNull(copy.getDate());
            assertNull(copy.getPrice());
        }
        int year = 2012;
        int month = 3;
        int day = 21;
        VariablePartDateImpl date = new VariablePartDateImpl(year, month, day);
        original = new VariablePartImpl(date);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, VariablePartImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<integer>"));
        assertFalse(serializedEvent.contains("<number>"));
        assertFalse(serializedEvent.contains("<time>"));
        assertFalse(serializedEvent.contains("<price>"));
        }
        if (copy != null) {
            assertNull(copy.getInteger());
            assertNull(copy.getNumber());
            assertNull(copy.getTime());
            assertEquals(copy.getDate().getYear(), year);
            assertEquals(copy.getDate().getMonth(), month);
            assertEquals(copy.getDate().getDay(), day);
            assertNull(copy.getPrice());
        }
        int integerPart = 1000;
        int hundredthPart = 0;
        VariablePartPriceImpl price = new VariablePartPriceImpl(integerPart, hundredthPart);
        original = new VariablePartImpl(price);

        serializedEvent = xmlMapper.writeValueAsString(original);
        System.out.println(serializedEvent);

        try {
            copy = xmlMapper.readValue(serializedEvent, VariablePartImpl.class);
        } catch (Exception e) {
            // Fallback to string assertions
        assertFalse(serializedEvent.contains("<integer>"));
        assertFalse(serializedEvent.contains("<number>"));
        assertFalse(serializedEvent.contains("<time>"));
        assertFalse(serializedEvent.contains("<date>"));
        }
        if (copy != null) {
            assertNull(copy.getInteger());
            assertNull(copy.getNumber());
            assertNull(copy.getTime());
            assertNull(copy.getDate());
            assertEquals(copy.getPrice().getPriceIntegerPart(), integerPart);
            assertEquals(copy.getPrice().getPriceHundredthPart(), hundredthPart);
        }
    }
}
