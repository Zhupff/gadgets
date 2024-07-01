/*
 * Copyright 2007 ZXing authors
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

package com.google.zxing.qrcode.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.common.DecoderResult;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import zhupff.gadgets.qrcode.common.BitSource;
import zhupff.gadgets.qrcode.common.Mode;

/**
 * <p>QR Codes can encode text as bits in one of several modes, and can use multiple modes
 * in one QR Code. This class decodes the bits back into text.</p>
 *
 * <p>See ISO 18004:2006, 6.4.3 - 6.4.7</p>
 *
 * @author Sean Owen
 */
final class DecodedBitStreamParser {

  private DecodedBitStreamParser() {
  }

  static DecoderResult decode(byte[] bytes,
                              Version version,
                              ErrorCorrectionLevel ecLevel) throws FormatException {
    BitSource bits = new BitSource(bytes);
    StringBuilder result = new StringBuilder(50);
    List<byte[]> byteSegments = new ArrayList<>(1);
    int symbologyModifier;

    try {
      CharacterSetECI currentCharacterSetECI = null;
      Mode mode;
      do {
        // While still another segment to read...
        if (bits.available() < 4) {
          // OK, assume we're done. Really, a TERMINATOR mode should have been recorded here
          mode = Mode.TERMINATOR;
        } else {
          mode = Mode.Companion.forBits(bits.readBits(4)); // mode is encoded by 4 bits
        }
        switch (mode) {
          case TERMINATOR:
            break;
          case ECI:
            // Count doesn't apply to ECI
            int value = parseECIValue(bits);
            currentCharacterSetECI = CharacterSetECI.getCharacterSetECIByValue(value);
            if (currentCharacterSetECI == null) {
              throw FormatException.getFormatInstance();
            }
            break;
          case BYTE:
            int count = bits.readBits(mode.getBits(version));
            decodeByteSegment(bits, result, count, currentCharacterSetECI, byteSegments);
            break;
          default:
            throw FormatException.getFormatInstance();
        }
      } while (mode != Mode.TERMINATOR);

      if (currentCharacterSetECI != null) {
        symbologyModifier = 2;
      } else {
        symbologyModifier = 1;
      }

    } catch (IllegalArgumentException iae) {
      // from readBits() calls
      throw FormatException.getFormatInstance();
    }

    return new DecoderResult(bytes,
                             result.toString(),
                             byteSegments.isEmpty() ? null : byteSegments,
                             ecLevel == null ? null : ecLevel.toString(),
                             symbologyModifier);
  }

  private static void decodeByteSegment(BitSource bits,
                                        StringBuilder result,
                                        int count,
                                        CharacterSetECI currentCharacterSetECI,
                                        Collection<byte[]> byteSegments) throws FormatException {
    // Don't crash trying to read more bits than we have available.
    if (8 * count > bits.available()) {
      throw FormatException.getFormatInstance();
    }

    byte[] readBytes = new byte[count];
    for (int i = 0; i < count; i++) {
      readBytes[i] = (byte) bits.readBits(8);
    }
    Charset encoding = currentCharacterSetECI == null ? StandardCharsets.UTF_8 : currentCharacterSetECI.getCharset();
    result.append(new String(readBytes, encoding));
    byteSegments.add(readBytes);
  }

  private static int parseECIValue(BitSource bits) throws FormatException {
    int firstByte = bits.readBits(8);
    if ((firstByte & 0x80) == 0) {
      // just one byte
      return firstByte & 0x7F;
    }
    if ((firstByte & 0xC0) == 0x80) {
      // two bytes
      int secondByte = bits.readBits(8);
      return ((firstByte & 0x3F) << 8) | secondByte;
    }
    if ((firstByte & 0xE0) == 0xC0) {
      // three bytes
      int secondThirdBytes = bits.readBits(16);
      return ((firstByte & 0x1F) << 16) | secondThirdBytes;
    }
    throw FormatException.getFormatInstance();
  }

}
