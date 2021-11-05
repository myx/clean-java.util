/*
 * Created on 17.08.2013
 * 
 * moved from ae3.api/ru.myx.ae3.help package @ 20211105
 */
package ru.myx.util;

import java.util.Arrays;

/** @author myx */
public final class Base58 {
	
	private static final char[] CHARS_BASE58 = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
	
	private static final int[] INDEXES = new int[128];
	static {
		for (int i = 0; i < 128; i++) {
			Base58.INDEXES[i] = -1;
		}
		for (int i = 0; i < 58; i++) {
			Base58.INDEXES[Base58.CHARS_BASE58[i]] = i;
		}
	}

	/** @param input
	 * @return */
	public static byte[] decode(final String input) {
		
		if (input.length() == 0) {
			// paying with the same coin
			return new byte[0];
		}
		
		final byte[] input58 = new byte[input.length()];
		//
		// Transform the String to a base58 byte sequence
		//
		for (int i = 0; i < input.length(); ++i) {
			final char c = input.charAt(i);
			
			int digit58 = -1;
			if (c >= 0 && c < 128) {
				digit58 = Base58.INDEXES[c];
			}
			if (digit58 < 0) {
				throw new RuntimeException("Not a Base58 input: " + input);
			}
			
			input58[i] = (byte) digit58;
		}
		
		//
		// Count leading zeroes
		//
		int zeroCount = 0;
		while (zeroCount < input58.length && input58[zeroCount] == 0) {
			++zeroCount;
		}
		
		//
		// The encoding
		//
		final byte[] temp = new byte[input.length()];
		int j = temp.length;
		
		int startAt = zeroCount;
		while (startAt < input58.length) {

			int remainder = 0;
			for (int i = startAt; i < input58.length; i++) {
				final int digit58 = input58[i] & 0xFF;
				final int temp2 = remainder * 58 + digit58;
				
				input58[i] = (byte) (temp2 / 256);
				
				remainder = temp2 % 256;
			}
			
			if (input58[startAt] == 0) {
				++startAt;
			}
			
			temp[--j] = (byte) remainder;
		}
		
		//
		// Do no add extra leading zeroes, move j to first non null byte.
		//
		while (j < temp.length && temp[j] == 0) {
			++j;
		}
		
		final byte[] result = new byte[temp.length - (j - zeroCount)];
		System.arraycopy(temp, j - zeroCount, result, 0, result.length);
		
		return result;
	}
	
	/** Convert byte array to a BASE58 encoded string.
	 *
	 * @param bytes
	 * @return string */
	public static final String encode(final byte[] bytes) {
		
		if (bytes == null) {
			throw new java.lang.NullPointerException("base58 encoder - input bytes are null!");
		}
		if (bytes.length == 0) {
			return "";
		}
		
		final char[] buffer = new char[bytes.length * 2];
		
		// Count leading zeros.
		int zeros = 0;
		while (zeros < bytes.length && bytes[zeros] == 0) {
			++zeros;
		}
		final byte[] bytea = Arrays.copyOf(bytes, bytes.length); // since we modify it in-place
		int outputStart = buffer.length;
		
		for (int inputStart = zeros; inputStart < bytea.length;) {
			int remainder = 0;
			for (int i = inputStart; i < bytea.length; i++) {
				final int digit = bytea[i] & 0xFF;
				final int temp = remainder * 256 + digit;
				bytea[i] = (byte) (temp / 58);
				remainder = temp % 58;
			}
			buffer[--outputStart] = Base58.CHARS_BASE58[(byte) remainder];
			if (bytea[inputStart] == 0) {
				++inputStart; // optimization - skip leading zeros
			}
		}
		// Preserve exactly as many leading encoded zeros in output as there were leading zeros in
		// input.
		while (outputStart < buffer.length && buffer[outputStart] == '1') {
			++outputStart;
		}
		while (--zeros >= 0) {
			buffer[--outputStart] = '1';
		}
		return new String(buffer, outputStart, buffer.length - outputStart);
	}
	
}
