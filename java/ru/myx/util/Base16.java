/*
 * Created on 17.08.2013
 *
 * moved from ae3.api/ru.myx.ae3.help package @ 20211105
 */
package ru.myx.util;

/**
 * Please, use:
 * <code>String.format("%016X", new BigInteger(1, this.nextDirectArray()))</code>
 * where possible.
 *
 * Pity I cannot find an large-scale efficient implementation within JDK :(
 *
 *
 * @author myx
 */
public final class Base16 {

    private static final char[] CHARS_BASE16 = "0123456789abcdef".toCharArray();

    private static final int[] INDEXES = new int[128];
    static {
	for (int i = 0; i < 128; i++) {
	    Base16.INDEXES[i] = -1;
	}
	for (int i = 0; i < 58; i++) {
	    Base16.INDEXES[Base16.CHARS_BASE16[i]] = i;
	}
    }

    private static final int[] XLATDE16 = new int[256];
    static {
	for (int i = Base16.CHARS_BASE16.length - 1; i >= 0; --i) {
	    Base16.XLATDE16[Base16.CHARS_BASE16[i]] = i;
	}
    }

    /**
     * @param input
     * @return
     */
    public static byte[] decode(final String input) {

	final int inputLength = input.length();
	if (inputLength == 0) {
	    // paying with the same coin
	    return new byte[0];
	}

	final byte[] result = new byte[(inputLength + 1) / 2];
	int target = 0;
	int source = 0;
	for (;;) {
	    final char char1 = input.charAt(source++);
	    if (source == inputLength) {
		result[target++] = (byte) ((Base16.XLATDE16[char1] & 0x0F) << 4);
		break;
	    }
	    final char char2 = input.charAt(source++);
	    result[target++] = (byte) (((Base16.XLATDE16[char1] & 0x0F) << 4) + ((Base16.XLATDE16[char2] & 0x0F) << 0));
	    if (source == inputLength) {
		break;
	    }
	}
	return result;
    }

    /**
     * Convert byte array to a BASE16 encoded string.
     *
     * @param bytes
     * @return string
     */
    public static final String encode(final byte[] bytes) {

	if (bytes == null) {
	    throw new java.lang.NullPointerException("base16 encoder - input bytes are null!");
	}
	if (bytes.length == 0) {
	    return "";
	}

	final char[] result = new char[bytes.length << 1];
	int target = 0;
	int source = 0;
	for (int i = bytes.length - 1; i >= 0; --i) {
	    final byte current = bytes[source++];
	    result[target++] = Base16.CHARS_BASE16[(current & 0xF0) >> 4];
	    result[target++] = Base16.CHARS_BASE16[(current & 0x0F) >> 0];
	}
	return new String(result, 0, target);
    }

}
