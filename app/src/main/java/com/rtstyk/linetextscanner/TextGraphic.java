// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.rtstyk.linetextscanner;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.rtstyk.linetextscanner.GraphicOverlay.Graphic;
import com.google.mlkit.vision.text.Text;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class TextGraphic extends Graphic {

    private static final String TAG = "TextGraphic";
    private static final int TEXT_COLOR = Color.BLACK;

    private int color;
    private int id;
    private static final float TEXT_SIZE = 46.0f;
    private static final float STROKE_WIDTH = 14.0f;

    private final Paint rectPaint;
    private final Paint textPaint;
    private final Text.Element element;

    TextGraphic(GraphicOverlay overlay, Text.Element element) {
        this(overlay, element, TEXT_COLOR);
    }
    TextGraphic(GraphicOverlay overlay, Text.Element element, int color) {
        super(overlay);

        this.element = element;
        this.id = color;
        //this.color = getDistinctColor(color);
        this.color=Color.BLACK;

        rectPaint = new Paint();
        rectPaint.setColor(Color.WHITE);
        //rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setStrokeWidth(STROKE_WIDTH);
        rectPaint.setAlpha(200);

        textPaint = new Paint();
        textPaint.setColor(this.color);
        textPaint.setTextSize(TEXT_SIZE);
        //textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
        textPaint.setShadowLayer(4, 0, 0, Color.WHITE); // Example: Add a black shadow with a radius of 3

        //textPaint.sette
        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        //Log.d(TAG, "on draw text graphic");
        if (element == null) {
            throw new IllegalStateException("Attempting to draw a null text.");
        }

        // Draws the bounding box around the TextBlock.
        RectF rect = new RectF(element.getBoundingBox());
        canvas.drawRect(rect, rectPaint);

        // Renders the text at the bottom of the box.
        //canvas.drawText(this.id+ " ", rect.left, rect.bottom, textPaint);
        canvas.drawText(element.getText(), rect.left, rect.bottom, textPaint);

    }

    public Rect getBoundingBox()
    {
        return element.getBoundingBox();
    }

    public String getText()
    {
        return element.getText();
    }

    public Text.Element getTextElement()
    {
        return element;
    }



    private static int getDistinctColor(int index) {
        // List of visually distinct Android colors
        int[] colors = {
                Color.parseColor("#2E7D32"), // Dark Green
                Color.parseColor("#4E342E"), // Brown
                Color.parseColor("#1A237E"), // Dark Blue
                Color.parseColor("#6A1B9A"), // Purple
                Color.parseColor("#880E4F"), // Dark Pink
                Color.parseColor("#3E2723"), // Dark Brown
                Color.parseColor("#004D40"), // Teal
                Color.parseColor("#E65100"), // Orange
                Color.parseColor("#BF360C"), // Deep Orange
                Color.parseColor("#4A148C"), // Indigo
                Color.parseColor("#263238"), // Dark Blue Gray
                Color.parseColor("#D84315"), // Dark Deep Orange
                Color.parseColor("#006064"), // Dark Cyan
                Color.parseColor("#5D4037"), // Dark Gray
        };

        // Calculate the index within the bounds of the color array
        int colorIndex = index % colors.length;

        // Return the selected color
        return colors[colorIndex];
    }

}
