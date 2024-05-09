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

class LogarithmicAxis(var baseLabelPosition: Array<Int> = arrayOf(1, 2, 5, 8)) : DecimalAxis(
) {
    init {
        minorTickIncNum = 10
    }

    private fun tag(): String = if (isVertical) "VertLogAxis" else "HorLogAxis"




    override fun calcMetrics(rangePix: Int, g: GraphicsContext, font: Font?) {
        //super.calcMetrics(rangePix, g, font)

        /**
         * from Axis:
         */
        maxValueOverride?.let { overrideValue ->
            if (overrideValue > maxValue)
                maxValue = overrideValue
        }

        minValueOverride?.let { overrideValue ->
            if (overrideValue < minValue && overrideValue > 0.0)
                minValue = overrideValue
        }

        calcScale(rangePix)
        if (numberMinorIncrements > 0) {
            minorTickIncNum = numberMinorIncrements
        }
        log.d(tag()) { "calcMetrics($rangePix) $this" }

        /**
         * from DecimalAxis:
         */
        majorTickInc = 2.0




        if (isAutoScaling) {
            // make minVal be an exact multiple of majorTickInc just smaller than minVal
            minValue = previousMajorValue(minValue, acceptEqual = true)

            // make maxVal be an exact multiple of majorTickInc just larger than maxVal
            maxValue = nextMajorValue(maxValue, acceptEqual = true )

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

    fun nextMajorValue(pos: Double, acceptEqual: Boolean = false):Double{
        var orderOfMagnitude = if(pos > 0.0) 10.0.pow((ln(pos) / LOG10).toInt().toDouble()) else {
            0.00001
        }

        val baseValue = (pos / (orderOfMagnitude)).toInt()

        val nextBaseValue =
            if(acceptEqual){

                val candidateBaseValue = baseLabelPosition.filter { it >= baseValue }.minOrNull()
                if(candidateBaseValue != null) {
                    if (candidateBaseValue.toDouble() * orderOfMagnitude in pos*0.99..pos*1.01)
                        candidateBaseValue
                    else
                        baseLabelPosition.filter { it > baseValue }.minOrNull()
                }else
                    baseLabelPosition.filter { it > baseValue }.minOrNull()
            }
            else
                baseLabelPosition.filter { it > baseValue }.minOrNull()

        /**     if i could not find a base value bigger than pos i'll take the biggest baseValue at the
         *       next smaller order of magnitude:
         */
        return if (nextBaseValue == null)
            baseLabelPosition.min().toDouble() * orderOfMagnitude * 10
        else
            nextBaseValue.toDouble() * orderOfMagnitude
    }

    fun previousMajorValue(pos: Double, acceptEqual: Boolean = false ):Double{
        var orderOfMagnitude = if(pos > 0.0) 10.0.pow((ln(pos) / LOG10).toInt().toDouble()) else {
            0.00001
        }

        val baseValue = (pos / (orderOfMagnitude)).toInt()

        val smallerBaseValue =
            if(acceptEqual){

                val candidateBaseValue = baseLabelPosition.filter { it <= baseValue }.maxOrNull()
                if(candidateBaseValue != null) {
                    if (candidateBaseValue.toDouble() * orderOfMagnitude in pos*0.99..pos*1.01)
                        candidateBaseValue
                    else
                        baseLabelPosition.filter { it < baseValue }.maxOrNull()
                }else
                    baseLabelPosition.filter { it < baseValue }.maxOrNull()

            }
            else
                baseLabelPosition.filter { it < baseValue }.maxOrNull()



        /**  if i could not find a base value smaller than pos i'll take the biggest baseValue at the
         *       next smaller order of magnitude:
         */
        return if (smallerBaseValue == null)
            baseLabelPosition.max().toDouble() * orderOfMagnitude * 0.1
        else
            smallerBaseValue.toDouble() * orderOfMagnitude
    }

    override fun nextMajorIncVal(pos: Double, incrementVal: Double): Double {
        return nextMajorValue(pos) - pos
    }

    fun previousMajourIncVal(pos: Double, incrementVal: Double):Double{
        return pos - previousMajorValue(pos)
    }

    override fun adjustMinMax() {
        // cannot have log scales with negative numbers
        if (minValue < 0)
            minValue = 0.0001
    }

    /**
     * Return the screen coordinate (in pixels) based on a data value
     */
    override fun convertToPixel(dataValue: Double): Int {
        var value = scaleData(dataValue)
        if (isVertical)
            value = y - value
        else
            value += x
        return value
    }

    /**
     * Return the data value corresponding to the screen coordinate (in pixels)
     */
    override fun convertToValue(pixelValue: Int): Double {
        val minValPixel = convertToPixel(minValue)
        return minValue + scalePixel(abs(pixelValue - minValPixel))
    }


    /**
     * Return data value scaled to be in pixels
     */
    override fun scaleData(dataValue: Double): Int {
        return ((log10(dataValue)- log10(minValue)) / scale).toLong().toInt()
    }

    override fun scalePixel(pixelValue: Int): Double {
        return 10.0.pow((pixelValue * scale) + log10(minValue))
    }

    override fun toString(): String {
        return "LogarithmicAxis[" + toStringParam() + "]"
    }

    companion object {
        private val LOG10 = ln(10.0)

        private val log = moduleLogging()

        private fun log10(value: Double): Double {
            /*var v = value
            val sign = if (v < 0) -1 else 1
            v = abs(v)
            if (v < 10)
                v += (10 - v) / 10   // make 0 correspond to 0
            return ln(v) / LOG10 * sign*/

            val sign = if (value < 0) -1 else 1

            if(value == 0.0)
                return 0.0

            return kotlin.math.log10(value) * sign
        }
    }
}
