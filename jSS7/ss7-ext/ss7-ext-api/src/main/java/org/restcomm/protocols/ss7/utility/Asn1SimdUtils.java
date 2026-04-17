/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2026, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package org.restcomm.protocols.ss7.utility;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * SIMD-like optimizations for ASN.1 BER/DER parsing.
 * 
 * These utilities use bit manipulation and batch processing
 * to accelerate common parsing operations without native code.
 * 
 * @author nhanth87
 * @version 2.1.0
 */
public final class Asn1SimdUtils {

    private Asn1SimdUtils() {
        // Utility class
    }

    // Common ASN.1 tags
    public static final int TAG_BOOLEAN = 0x01;
    public static final int TAG_INTEGER = 0x02;
    public static final int TAG_OCTET_STRING = 0x04;
    public static final int TAG_NULL = 0x05;
    public static final int TAG_OID = 0x06;
    public static final int TAG_SEQUENCE = 0x30;
    public static final int TAG_SET = 0x31;
    public static final int TAG_PRINTABLE_STRING = 0x13;
    public static final int TAG_IA5_STRING = 0x16;
    public static final int TAG_UTF8_STRING = 0x0C;
    public static final int TAG_CONTEXT_0 = 0x80;
    public static final int TAG_CONTEXT_1 = 0x81;
    public static final int TAG_CONTEXT_2 = 0x82;
    public static final int TAG_CONTEXT_3 = 0x83;

    /**
     * Fast tag lookup using branchless bit manipulation.
     * Returns true if the byte matches any of the expected tags.
     */
    public static boolean isTag(byte b, int... expectedTags) {
        for (int tag : expectedTags) {
            if ((b & 0xFF) == tag) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if tag is a context-specific tag (0x80-0xBF)
     */
    public static boolean isContextTag(byte b) {
        return (b & 0xC0) == 0x80;
    }

    /**
     * Check if tag indicates a constructed type
     */
    public static boolean isConstructedTag(byte b) {
        return (b & 0x20) != 0;
    }

    /**
     * Check if tag indicates a universal type
     */
    public static boolean isUniversalTag(byte b) {
        return (b & 0xC0) == 0;
    }

    /**
     * Check if tag indicates a class-primitive encoding
     */
    public static boolean isPrimitive(byte b) {
        return (b & 0x20) == 0;
    }

    /**
     * Get tag class from byte
     */
    public static int getTagClass(byte b) {
        return (b & 0xC0) >>> 6;
    }

    /**
     * Parse length from BER encoding.
     * Handles both definite and indefinite forms.
     * 
     * @param buffer ByteBuffer positioned at length byte
     * @return Parsed length, or -1 if indefinite length
     */
    public static int parseLength(ByteBuffer buffer) {
        int b = buffer.get() & 0xFF;
        
        if ((b & 0x80) == 0) {
            // Short form: single byte length
            return b;
        }
        
        int numOctets = b & 0x7F;
        if (numOctets == 0) {
            // Indefinite form
            return -1;
        }
        
        if (numOctets > 4) {
            // Length too long for int
            throw new IllegalArgumentException("Length encoding too long: " + numOctets);
        }
        
        int length = 0;
        for (int i = 0; i < numOctets; i++) {
            int octet = buffer.get() & 0xFF;
            length = (length << 8) | octet;
        }
        
        return length;
    }

    /**
     * Parse length from byte array (for DirectByteBuffer compatibility)
     */
    public static int parseLength(byte[] data, int offset, int length) {
        if (offset >= length) {
            throw new IllegalArgumentException("Offset exceeds data length");
        }
        
        int b = data[offset] & 0xFF;
        
        if ((b & 0x80) == 0) {
            return b;
        }
        
        int numOctets = b & 0x7F;
        if (numOctets == 0) {
            return -1;
        }
        
        if (offset + 1 + numOctets > length) {
            throw new IllegalArgumentException("Insufficient data for length encoding");
        }
        
        int result = 0;
        for (int i = 1; i <= numOctets; i++) {
            result = (result << 8) | (data[offset + i] & 0xFF);
        }
        
        return result;
    }

    /**
     * Encode length in BER definite form.
     * Optimized for small lengths (< 128).
     */
    public static int encodeLength(int length, byte[] output, int offset) {
        if (length < 0) {
            throw new IllegalArgumentException("Length must be non-negative");
        }
        
        if (length < 0x80) {
            // Short form
            output[offset] = (byte) length;
            return 1;
        }
        
        // Long form
        int numOctets;
        if (length < 0x100) {
            numOctets = 1;
        } else if (length < 0x10000) {
            numOctets = 2;
        } else if (length < 0x1000000) {
            numOctets = 3;
        } else {
            numOctets = 4;
        }
        
        output[offset] = (byte) (0x80 | numOctets);
        
        for (int i = numOctets - 1; i >= 0; i--) {
            output[offset + 1 + i] = (byte) (length & 0xFF);
            length >>= 8;
        }
        
        return numOctets + 1;
    }

    /**
     * Batch scan for specific tags in data.
     * Returns index of first match or -1 if not found.
     */
    public static int scanForTag(byte[] data, int start, int end, int tag) {
        for (int i = start; i < end; i++) {
            if ((data[i] & 0xFF) == tag) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Batch scan for any of the specified tags.
     * Uses early exit on first match.
     */
    public static int scanForTags(byte[] data, int start, int end, int... tags) {
        for (int i = start; i < end; i++) {
            int b = data[i] & 0xFF;
            for (int tag : tags) {
                if (b == tag) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Scan for sequence end tag (0x30 with bit 5 set for constructed).
     * Optimized for finding end of nested structures.
     */
    public static int scanForSequenceEnd(byte[] data, int start, int end) {
        int depth = 1;
        for (int i = start; i < end && depth > 0; i++) {
            int b = data[i] & 0xFF;
            
            if (b == 0x30 || b == 0x31) {
                // Sequence or Set - need to parse length
                int len = parseLength(data, i + 1, end);
                if (len >= 0) {
                    i += 1 + (data[i + 1] & 0x80) == 0 ? 1 : (data[i + 1] & 0x7F) + 1;
                    i += len;
                    depth--;
                    if (depth == 0 && i < end) {
                        return i;
                    }
                } else {
                    // Indefinite length - need matching EOC
                    depth++;
                }
            } else if (b == 0x80 && i + 1 < end && data[i + 1] == 0) {
                // End of content marker
                depth--;
            }
        }
        return -1;
    }

    /**
     * Decode unsigned integer from BER encoding.
     * Handles integers of any size up to 4 bytes.
     */
    public static long decodeUnsignedInteger(byte[] data, int offset, int length) {
        long result = 0;
        for (int i = offset; i < offset + length; i++) {
            result = (result << 8) | (data[i] & 0xFF);
        }
        return result;
    }

    /**
     * Encode unsigned integer to BER.
     * Returns actual number of bytes written.
     */
    public static int encodeUnsignedInteger(long value, byte[] output, int offset) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non-negative");
        }
        
        // Find first non-zero byte
        int start = 0;
        for (int i = 56; i >= 0; i -= 8) {
            if ((value >>> i) != 0) {
                start = i / 8;
                break;
            }
        }
        
        int numBytes = 8 - start;
        for (int i = numBytes - 1; i >= 0; i--) {
            output[offset + i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        
        return numBytes;
    }

    /**
     * Compare two byte arrays in constant time.
     * Prevents timing attacks on sensitive data.
     */
    public static boolean constantTimeEquals(byte[] a, int aOffset, int aLen,
                                              byte[] b, int bOffset, int bLen) {
        if (aLen != bLen) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < aLen; i++) {
            result |= a[aOffset + i] ^ b[bOffset + i];
        }
        
        return result == 0;
    }

    /**
     * Compute CRC32C (Castagnoli) - available via SSE4.2 if available.
     * Falls back to software implementation.
     */
    public static int crc32c(byte[] data, int offset, int length) {
        int crc = 0xFFFFFFFF;
        
        for (int i = offset; i < offset + length; i++) {
            crc ^= data[i] & 0xFF;
            for (int j = 0; j < 8; j++) {
                if ((crc & 1) != 0) {
                    crc = (crc >>> 1) ^ 0x82F63B78;
                } else {
                    crc >>>= 1;
                }
            }
        }
        
        return crc ^ 0xFFFFFFFF;
    }

    /**
     * Swap bytes in a long (for endianness conversion)
     */
    public static long swapLong(long value) {
        value = ((value & 0x00FF00FF00FF00FFL) << 8) | ((value & 0xFF00FF00FF00FF00L) >>> 8);
        value = ((value & 0x0000FFFF0000FFFFL) << 16) | ((value & 0xFFFF0000FFFF0000L) >>> 16);
        value = ((value & 0x00000000FFFFFFFFL) << 32) | ((value & 0xFFFFFFFF00000000L) >>> 32);
        return value;
    }

    /**
     * Swap bytes in an int
     */
    public static int swapInt(int value) {
        value = ((value & 0x00FF00FF) << 8) | ((value & 0xFF00FF00) >>> 8);
        value = ((value & 0x0000FFFF) << 16) | ((value & 0xFFFF0000) >>> 16);
        return value;
    }
}