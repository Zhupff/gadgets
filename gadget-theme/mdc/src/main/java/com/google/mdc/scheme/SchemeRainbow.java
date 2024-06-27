/*
 * Copyright 2023 Google LLC
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
package com.google.mdc.scheme;

import com.google.mdc.dynamiccolor.DynamicScheme;
import com.google.mdc.dynamiccolor.Variant;
import com.google.mdc.hct.Hct;
import com.google.mdc.palettes.TonalPalette;
import com.google.mdc.utils.MathUtils;

/** A playful theme - the source color's hue does not appear in the theme. */
public class SchemeRainbow extends DynamicScheme {
  public SchemeRainbow(Hct sourceColorHct, boolean isDark, double contrastLevel) {
    super(
        sourceColorHct,
        Variant.RAINBOW,
        isDark,
        contrastLevel,
        TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 48.0),
        TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 16.0),
        TonalPalette.fromHueAndChroma(
            MathUtils.sanitizeDegreesDouble(sourceColorHct.getHue() + 60.0), 24.0),
        TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 0.0),
        TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 0.0));
  }
}