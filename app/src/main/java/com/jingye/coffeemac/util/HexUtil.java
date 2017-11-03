package com.jingye.coffeemac.util;

public class HexUtil {

	// 将1个byte转化为hex，如56转化之后为38
	public static String Int2HexString(int input) {
		String res = "";
		if (input >= 0x00 && input <= 0xFF) {
			res += Int2Hex(input >> 4 & 0xF);
			res += Int2Hex(input & 0xF);
			return res;
		} else if (input > 0xFF && input <= 0xFFFF) {
			res += Int2Hex(input >> 12 & 0xF);
			res += Int2Hex(input >> 8 & 0xF);
			res += " ";
			res += Int2Hex(input >> 4 & 0xF);
			res += Int2Hex(input & 0xF);
			return res;
		} else if (input > 0xFFFF && input <= 0xFFFFFF) {
			res += Int2Hex(input >> 20 & 0xF);
			res += Int2Hex(input >> 16 & 0xF);
			res += " ";
			res += Int2Hex(input >> 12 & 0xF);
			res += Int2Hex(input >> 8 & 0xF);
			res += " ";
			res += Int2Hex(input >> 4 & 0xF);
			res += Int2Hex(input & 0xF);
			return res;
		} else if (input > 0xFFFFFF && input <= 0xFFFFFFFF) {
			res += Int2Hex(input >> 28 & 0xF);
			res += Int2Hex(input >> 24 & 0xF);
			res += " ";
			res += Int2Hex(input >> 20 & 0xF);
			res += Int2Hex(input >> 16 & 0xF);
			res += " ";
			res += Int2Hex(input >> 12 & 0xF);
			res += Int2Hex(input >> 8 & 0xF);
			res += " ";
			res += Int2Hex(input >> 4 & 0xF);
			res += Int2Hex(input & 0xF);
			return res;
		} else {
			return res;
		}
	}

	// input必须小于16
	private static char Int2Hex(int input) {
		if (input < 10 && input >= 0) {
			return (char) (input + '0');
		} else {
			return (char) (input + 'A' - 10);
		}
	}
}
