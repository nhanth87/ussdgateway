
package org.restcomm.protocols.ss7.isup.impl.message.parameter;

import org.restcomm.protocols.ss7.isup.ParameterException;
import org.restcomm.protocols.ss7.isup.message.parameter.GenericDigits;
import org.restcomm.protocols.ss7.isup.util.BcdHelper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Start time:12:24:47 2009-03-31<br>
 * Project: mobicents-isup-stack<br>
 *
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:grzegorz.figiel@pro-ids.com"> Grzegorz Figiel </a>
 */
public class GenericDigitsImpl extends AbstractISUPParameter implements GenericDigits {

    private static final Charset asciiCharset = Charset.forName("ASCII");

    private int encodingScheme;
    private int typeOfDigits;
    private byte[] digits;

    public GenericDigitsImpl(byte[] b) throws ParameterException {
        super();
        decode(b);
    }

    public GenericDigitsImpl(int encodingScheme, int typeOfDigits, byte[] digits) {
        super();
        this.encodingScheme = encodingScheme;
        this.typeOfDigits = typeOfDigits;
        this.setEncodedDigits(digits);
    }

    public GenericDigitsImpl(int encodingScheme, int typeOfDigits, String digits) throws UnsupportedEncodingException {
        super();
        this.typeOfDigits = typeOfDigits;
        setDecodedDigits(encodingScheme, digits);
    }

    public GenericDigitsImpl() {
        super();

    }

    public String getDecodedDigits() throws UnsupportedEncodingException {
        switch (encodingScheme) {
            case GenericDigits._ENCODING_SCHEME_BCD_EVEN:
            case GenericDigits._ENCODING_SCHEME_BCD_ODD:
                return BcdHelper.bcdDecodeToHexString(encodingScheme, digits);
            case GenericDigits._ENCODING_SCHEME_IA5:
                return new String(digits, asciiCharset);
            default:
                //TODO: add other encoding schemas support
                throw new UnsupportedEncodingException("Specified GenericDigits encoding: " + encodingScheme + " is unsupported");
        }

    }

    public void setDecodedDigits(int encodingScheme, String digits) throws UnsupportedEncodingException {
        if (digits == null || digits.length() < 1) {
            throw new IllegalArgumentException("Digits must not be null or zero length");
        }
        switch (encodingScheme) {
            case GenericDigits._ENCODING_SCHEME_BCD_EVEN:
            case GenericDigits._ENCODING_SCHEME_BCD_ODD:
                if ((digits.length() % 2) == 0) {
                    if (encodingScheme == GenericDigits._ENCODING_SCHEME_BCD_ODD)
                        throw new UnsupportedEncodingException("SCHEME_BCD_ODD is possible only for odd digits count");
                } else {
                    if (encodingScheme == GenericDigits._ENCODING_SCHEME_BCD_EVEN)
                        throw new UnsupportedEncodingException("SCHEME_BCD_EVEN is possible only for odd digits count");
                }
                this.encodingScheme = encodingScheme;
                this.setEncodedDigits(BcdHelper.encodeHexStringToBCD(digits));
                break;
            case GenericDigits._ENCODING_SCHEME_IA5:
                this.encodingScheme = encodingScheme;
                this.setEncodedDigits(digits.getBytes(asciiCharset));
                break;
            default:
                //TODO: add other encoding schemas support
                throw new UnsupportedEncodingException("Specified GenericDigits encoding: " + encodingScheme + " is unsupported");
        }
    }

    public int decode(byte[] b) throws ParameterException {
        if (b == null || b.length < 2) {
            throw new ParameterException("byte[] must not be null or has size less than 2");
        }
        this.typeOfDigits = b[0] & 0x1F;
        this.encodingScheme = (b[0] >> 5) & 0x07;
        this.digits = new byte[b.length - 1];

        for (int index = 1; index < b.length; index++) {
            this.digits[index - 1] = b[index];
        }
        return 1 + this.digits.length;
    }

    public byte[] encode() throws ParameterException {

        byte[] b = new byte[this.digits.length + 1];

        b[0] |= this.typeOfDigits & 0x1F;
        b[0] |= ((this.encodingScheme & 0x07) << 5);

        for (int index = 1; index < b.length; index++) {
            b[index] = (byte) this.digits[index - 1];
        }
        return b;

    }

    public int getEncodingScheme() {
        return encodingScheme;
    }

    public void setEncodingScheme(int encodingScheme) {
        this.encodingScheme = encodingScheme;
    }

    public int getTypeOfDigits() {
        return typeOfDigits;
    }

    public void setTypeOfDigits(int typeOfDigits) {
        this.typeOfDigits = typeOfDigits;
    }

    public byte[] getEncodedDigits() {
        return digits;
    }

    public void setEncodedDigits(byte[] digits) {
        if (digits == null)
            throw new IllegalArgumentException("Digits must not be null");
        this.digits = digits;
    }

    public int getCode() {

        return _PARAMETER_CODE;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("GenericDigits [encodingScheme=");
        sb.append(encodingScheme);
        sb.append(", typeOfDigits=");
        sb.append(typeOfDigits);
        if (digits != null) {
            sb.append(", digits=<binary>");
        }
        sb.append("]");

        return sb.toString();
    }

}
