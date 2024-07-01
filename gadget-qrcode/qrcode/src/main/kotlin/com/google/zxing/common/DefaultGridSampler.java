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

package com.google.zxing.common;

import com.google.zxing.NotFoundException;

import zhupff.gadgets.qrcode.common.BitMatrix;

/**
 * @author Sean Owen
 */
public final class DefaultGridSampler extends GridSampler {

  @Override
  public BitMatrix sampleGrid(BitMatrix image,
                              int dimensionX,
                              int dimensionY,
                              PerspectiveTransform transform) throws NotFoundException {
    if (dimensionX <= 0 || dimensionY <= 0) {
      throw NotFoundException.getNotFoundInstance();
    }
    BitMatrix bits = new BitMatrix(dimensionX, dimensionY);
    float[] points = new float[2 * dimensionX];
    for (int y = 0; y < dimensionY; y++) {
      int max = points.length;
      float iValue = y + 0.5f;
      for (int x = 0; x < max; x += 2) {
        points[x] = (float) (x / 2) + 0.5f;
        points[x + 1] = iValue;
      }
      transform.transformPoints(points);
      // Quick check to see if points transformed to something inside the image;
      // sufficient to check the endpoints
      checkAndNudgePoints(image, points);
      try {
        for (int x = 0; x < max; x += 2) {
          if (image.get((int) points[x], (int) points[x + 1])) {
            // Black(-ish) pixel
            bits.set(x / 2, y);
          }
        }
      } catch (ArrayIndexOutOfBoundsException aioobe) {
        // This feels wrong, but, sometimes if the finder patterns are misidentified, the resulting
        // transform gets "twisted" such that it maps a straight line of points to a set of points
        // whose endpoints are in bounds, but others are not. There is probably some mathematical
        // way to detect this about the transformation that I don't know yet.
        // This results in an ugly runtime exception despite our clever checks above -- can't have
        // that. We could check each point's coordinates but that feels duplicative. We settle for
        // catching and wrapping ArrayIndexOutOfBoundsException.
        throw NotFoundException.getNotFoundInstance();
      }
    }
    return bits;
  }

}
