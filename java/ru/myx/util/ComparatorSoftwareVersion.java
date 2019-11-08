package ru.myx.util;

import java.util.Comparator;

/** @author myx <code>
        calc f = require("java.class/ru.myx.util.ComparatorSoftwareVersion").INSTANCE
        calc 0 > f.compare("2.09.C.9.1","3.01.C.10.1")
        calc 0 > f.compare("3.01.C.9.1","3.01.C.10.1")
        calc 0 > f.compare("3.01.C.10z.1","3.01.C.9z.1")
        calc 0 > f.compare("3.01.C.10.1","3.02.C.9.1")
 * </code> */
public class ComparatorSoftwareVersion implements Comparator<Object> {

	/**
	 *
	 */
	public static final ComparatorSoftwareVersion INSTANCE = new ComparatorSoftwareVersion();

	/** @param o1
	 * @param o2
	 * @return */
	public static final int compareStatic(final Object o1, final Object o2) {

		if (o1 == o2) {
			return 0;
		}
		return ComparatorSoftwareVersion.INSTANCE.compare(o1, o2);
	}

	@Override
	public int compare(final Object o1, final Object o2) {

		if (o1 == null) {
			return o2 == null
				? 0
				: -1;
		}

		final CharSequence s1 = o1 instanceof CharSequence
			? (CharSequence) o1
			: o1.toString();
		final CharSequence s2 = o2 instanceof CharSequence
			? (CharSequence) o2
			: o2.toString();

		final int l1 = s1.length();
		final int l2 = s2.length();

		final int limit = Math.min(l1, l2);

		/** equals yet */
		int start = 0, i = 0;
		boolean digits = true;
		eq : for (;;) {
			if (i == limit) {
				return l1 == l2
					? 0
					: l1 == limit
						? -1
						: 1;
			}
			final char c1 = s1.charAt(i);
			final char c2 = s2.charAt(i);
			if (c1 != c2) {
				switch (c1) {
					case '.' :
					case '-' :
					case '/' :
						switch (c2) {
							case '.' :
							case '-' :
							case '/' :
								++i;
								start = i;
								digits = true;
								continue eq;
							default :
								break eq;
						}
					default :
						break eq;
				}
			}
			++i;
			switch (c1) {
				case '.' :
				case '-' :
				case '/' :
					start = i;
					digits = true;
					continue eq;
				case '0' :
				case '1' :
				case '2' :
				case '3' :
				case '4' :
				case '5' :
				case '6' :
				case '7' :
				case '8' :
				case '9' :
					continue eq;
				default :
					digits = false;
					continue eq;
			}
		}

		/** check difference */
		String v1;
		v1 : for (int j = i;;) {
			if (j == l1) {
				v1 = s1.subSequence(start, j).toString();
				break v1;
			}
			final char c1 = s1.charAt(j);
			switch (c1) {
				case '.' :
				case '-' :
				case '/' :
					v1 = s1.subSequence(start, j).toString();
					break v1;
				case '0' :
				case '1' :
				case '2' :
				case '3' :
				case '4' :
				case '5' :
				case '6' :
				case '7' :
				case '8' :
				case '9' :
					++j;
					continue v1;
				default :
					++j;
					digits = false;
					continue v1;
			}
		}

		String v2;
		v2 : for (int j = i;;) {
			if (j == l2) {
				v2 = s2.subSequence(start, j).toString();
				break v2;
			}
			final char c2 = s2.charAt(j);
			switch (c2) {
				case '.' :
				case '-' :
				case '/' :
					v2 = s2.subSequence(start, j).toString();
					break v2;
				case '0' :
				case '1' :
				case '2' :
				case '3' :
				case '4' :
				case '5' :
				case '6' :
				case '7' :
				case '8' :
				case '9' :
					++j;
					continue v2;
				default :
					++j;
					digits = false;
					continue v2;
			}
		}

		if (digits) {
			final int i1 = Integer.parseInt(v1);
			final int i2 = Integer.parseInt(v2);
			return i1 == i2
				? 0
				: i1 < i2
					? -1
					: 1;
		}

		return v1.compareTo(v2);
	}

}
