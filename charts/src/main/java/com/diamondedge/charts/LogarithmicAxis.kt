/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

class LogarithmicAxis(var baseLabelPosition: Array<Int> = arrayOf(1, 2, 5)) : DecimalAxis(
) {
    init {
        minorTickIncNum = 10
    }




    override fun calcMetrics(rangePix: Int, g: GraphicsContext, font: Font?) {
        super.calcMetrics(rangePix, g, font)

        if (isAutoScaling) {
            // make minVal be an exact multiple of majorTickInc just smaller than minVal
            var tickInc = nextMajorIncVal(minValue, 1.0)
            minValue = floor(minValue / tickInc) * tickInc

            // make maxVal be an exact multiple of majorTickInc just larger than maxVal
            tickInc = nextMajorIncVal(maxValue, tickInc)
            maxValue = ceil(maxValue / tickInc) * tickInc

            adjustMinMax()
            calcScale(rangePix)
        }
    }

    override fun calcScale(rangePix: Int): Double {
        val rangeVal = log10(maxValue) - log10(minValue)
        scale = rangeVal / rangePix
        if (scale == 0.0)
            scale = 1.0
        return scale
    }

    override fun nextMinorIncVal(pos: Double, incVal: Double): Double {
        var incrementVal = 10.0.pow((ln(pos) / LOG10).toInt().toDouble())
        if (incrementVal == 0.0)
            incrementVal = 1.0
        return incrementVal
    }

    override fun nextMajorIncVal(pos: Double, incrementVal: Double): Double {
        val lol = (ln(pos) / LOG10).toInt().toDouble()
        var orderOfMagnitude = 10.0.pow((ln(pos) / LOG10).toInt().toDouble())
        //if (orderOfMagnitude == 0.0)
        //    orderOfMagnitude = 1.0

        val baseValue = (pos / (orderOfMagnitude)).toInt()

        val indexLastBaseValue = baseLabelPosition.lastIndexOf(baseValue)

        if( indexLastBaseValue == -1)
            return baseLabelPosition[0].toDouble() * (orderOfMagnitude)

        val oldPosition = baseLabelPosition[indexLastBaseValue].toDouble() * (orderOfMagnitude)

        if(indexLastBaseValue < (baseLabelPosition.size -1))
            return (baseLabelPosition[indexLastBaseValue + 1 ].toDouble() * (orderOfMagnitude)) - oldPosition
        else
            return (baseLabelPosition[0].toDouble() * (orderOfMagnitude) * 10) - oldPosition
/*

        //  In case pos is greater than the maximum label for this order of magnitude level
        //  I'll then return the smallest label of the next order of magnitude level
        if ( baseValue >  baseLabelPosition.max())
        {
            return orderOfMagnitude *  baseLabelPosition.min() * 10
        }

        //  Otherwise I'll just search for the next label
        val nextLabelBase = (baseLabelPosition.filter{it > baseValue }).min()*/

        //return nextLabelBase * orderOfMagnitude
    }

    override fun adjustMinMax() {
        // cannot have log scales with negative numbers
        if (minValue < 0)
            minValue = 0.0


    }

    /** Return data value scaled to be in pixels
     */
    override fun scaleData(dataValue: Double): Int {
        return (log10(dataValue) / scale).toLong().toInt()
    }

    override fun scalePixel(pixelValue: Int): Double {
        return 10.0.pow(pixelValue * scale)
    }

    override fun toString(): String {
        return "LogarithmicAxis[" + toStringParam() + "]"
    }

    companion object {
        private val LOG10 = ln(10.0)

        private fun log10(value: Double): Double {
            var v = value
            val sign = if (v < 0) -1 else 1
            v = abs(v)
            if (v < 10)
                v += (10 - v) / 10   // make 0 correspond to 0
            return ln(v) / LOG10 * sign
        }
    }
}
