/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.common;

import com.google.zxing.FormatException;

import java.nio.charset.Charset;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates a Character Set ECI, according to "Extended Channel Interpretations" 5.3.1.1
 * of ISO 18004.
 *
 * @author Sean Owen
 */
public enum CharacterSetECI {

  // Enum name is a Java encoding valid for java.lang and java.io
  Cp437(new int[]{0,2}),
  ISO8859_1(new int[]{1,3}, "ISO-8859-1"),
  ISO8859_2(new int[]{4}, "ISO-8859-2"),
  ISO8859_3(new int[]{5}, "ISO-8859-3"),
  ISO8859_4(new int[]{6}, "ISO-8859-4"),
  ISO8859_5(new int[]{7}, "ISO-8859-5"),
  ISO8859_6(new int[]{8}, "ISO-8859-6"),
  ISO8859_7(new int[]{9}, "ISO-8859-7"),
  ISO8859_8(new int[]{10}, "ISO-8859-8"),
  ISO8859_9(new int[]{11}, "ISO-8859-9"),
  ISO8859_10(new int[]{12}, "ISO-8859-10"),
  ISO8859_11(new int[]{13}, "ISO-8859-11"),
  ISO8859_13(new int[]{15}, "ISO-8859-13"),
  ISO8859_14(new int[]{16}, "ISO-8859-14"),
  ISO8859_15(new int[]{17}, "ISO-8859-15"),
  ISO8859_16(new int[]{18}, "ISO-8859-16"),
  SJIS(new int[]{20}, "Shift_JIS"),
  Cp1250(new int[]{21}, "windows-1250"),
  Cp1251(new int[]{22}, "windows-1251"),
  Cp1252(new int[]{23}, "windows-1252"),
  Cp1256(new int[]{24}, "windows-1256"),
  UnicodeBigUnmarked(new int[]{25}, "UTF-16BE", "UnicodeBig"),
  UTF8(new int[]{26}, "UTF-8"),
  ASCII(new int[] {27, 170}, "US-ASCII"),
  Big5(new int[]{28}),
  GB18030(new int[]{29}, "GB2312", "EUC_CN", "GBK"),
  EUC_KR(new int[]{30}, "EUC-KR");

  // only character sets supported by the current JVM are registered here
  private static final Map<Integer,CharacterSetECI> VALUE_TO_ECI = new HashMap<>();
  static {
    for (CharacterSetECI eci : values()) {
      if (Charset.isSupported(eci.name())) {
        for (int value : eci.values) {
          VALUE_TO_ECI.put(value, eci);
        }
      }
    }
  }

  private final int[] values;

  CharacterSetECI(int[] values, String... otherEncodingNames) {
    this.values = values;
  }

  public int getValue() {
    return values[0];
  }

  public Charset getCharset() {
    return Charset.forName(name());
  }

  /**
   * @param value character set ECI value
   * @return {@code CharacterSetECI} representing ECI of given value, or null if it is legal but
   *   unsupported
   * @throws FormatException if ECI value is invalid
   */
  public static CharacterSetECI getCharacterSetECIByValue(int value) throws FormatException {
    if (value < 0 || value >= 900) {
      throw FormatException.getFormatInstance();
    }
    return VALUE_TO_ECI.get(value);
  }
}
