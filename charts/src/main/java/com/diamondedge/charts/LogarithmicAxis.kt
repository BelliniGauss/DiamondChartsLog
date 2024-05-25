/**
 * Copyright 2004-2022 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.charts

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

class LogarithmicAxis(var baseLabelPosition: Array<Int> = arrayOf(1, 2, 5)) : DecimalAxis(
) {
    init {
        minorTickIncNum = 10
    }
    /**
     * This property will make the plot area extend further than the data, up to the next major tick.
     */
    //public var extendRangeToMajorTick = true



    private fun tag(): String = if (isVertical) "VertLogAxis" else "HorLogAxis"


    override fun calcMetrics(rangePix: Int, g: GraphicsContext, font: Font?) {
        //super.calcMetrics(rangePix, g, font)

        /**
         * from Axis:
         */
        maxValueOverride?.let { overrideValue ->
            //if (overrideValue > maxValue)
                maxValue = overrideValue
        }

        minValueOverride?.let { overrideValue ->
            //if (overrideValue < minValue && overrideValue > 0.0)
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




        /*if (isAutoScaling) {
            // make minVal be an exact multiple of majorTickInc just smaller than minVal
            minValue = previousMajorValue(minValue, acceptEqual = true)

            // make maxVal be an exact multiple of majorTickInc just larger than maxVal
            maxValue = nextMajorValue(maxValue, acceptEqual = true )
            adjustMinMax()
            calcScale(rangePix)
        }*/

        adjustMinMax()
        calcScale(rangePix)

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




    private fun nextMajorValue(pos: Double, acceptEqual: Boolean = false):Double{
        var orderOfMagnitude = if(pos > 0.0) 10.0.pow(floor(ln(pos) / LOG10)) else {
            0.000001
        }

        /**
         *  Calc the pos value translated to the 1..9.999 range
         */
        var baseValue = (pos / (orderOfMagnitude))

        /**
         *  Correction for number near 10 at the end of the 1..10 interval as they should be treated
         *  as 1 at the next orderOfMagnitude.S
         */
        if(baseValue in 10.0*0.999..10*1.001){
            baseValue = 1.0
            orderOfMagnitude *= 10
        }

        /**
         *  I'll now search for the basic next value, regardless of acceptEqual
         */
        var nextBaseValue = baseLabelPosition.filter { it.toDouble() > baseValue }.minOrNull()

        /**
         *  If acceptEqual is set I'll conduct a specific search for a candidate baseValue near
         *  the target Position, if found I'll overwrite nextBaseValue.
         */
        if(acceptEqual){
            val equalBaseValue = baseLabelPosition.filter {  baseValue in it.toDouble()*0.999..it.toDouble()*1.001 }.minOrNull()

            if(equalBaseValue != null) {
                    nextBaseValue = equalBaseValue
            }
        }

        /**     if i could not find a base value bigger than pos i'll take the biggest baseValue at the
         *      next smaller order of magnitude:
         */
        return if (nextBaseValue == null)
            baseLabelPosition.min().toDouble() * orderOfMagnitude * 10
        else
            nextBaseValue.toDouble() * orderOfMagnitude
    }

    private fun previousMajorValue(pos: Double, acceptEqual: Boolean = false ):Double{

        var orderOfMagnitude = if(pos > 0.0) 10.0.pow(floor(ln(pos) / LOG10)) else {
            0.000001
        }

        var baseValue = (pos / (orderOfMagnitude))


        /**
         * Correction for number near 10 at the end of the 1..10 interval as they should be treated
         * as 1 at the next orderOfMagnitude.S
         */
        if(baseValue  in 10.0*0.999..10*1.001){
            baseValue = 1.0
            orderOfMagnitude *= 10
        }

        /**
         *  I'll now search for the basic next value, regardless of acceptEqual
         */
        var previousBaseValue = baseLabelPosition.filter { it.toDouble() < baseValue }.maxOrNull()

        /**
         *  If acceptEqual is set I'll conduct a specific search for a candidate baseValue near
         *  the target Position, if found I'll overwrite nextBaseValue.
         */
        if(acceptEqual){
            val equalBaseValue = baseLabelPosition.filter { baseValue in  it.toDouble()*0.999..it.toDouble()*1.001  }.maxOrNull()

            if(equalBaseValue != null) {
                    previousBaseValue = equalBaseValue
            }
        }

        /**  if i could not find a base value smaller than pos i'll take the biggest baseValue at the
         *       next smaller order of magnitude:
         */
        return if (previousBaseValue == null)
            baseLabelPosition.max().toDouble() * orderOfMagnitude * 0.1
        else
            previousBaseValue.toDouble() * orderOfMagnitude
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
        return  scalePixel(abs(pixelValue - minValPixel))
    }


    /**
     * Return data value scaled to be in pixels
     */
    override fun scaleData(dataValue: Double): Int {
        return ((log10(dataValue)- log10(minValue)) / scale).toLong().toInt()
    }


    /**
     * Return the Data value corresponding to the axis coordinate (in pixels)
     */
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



class LogarithmicFormatter(){

    companion object : NumberFormatter {
        override operator fun invoke(value: Double): String{

            return  if (value.toInt() < 1000) {
                value.toInt().toString()
            }else {
                if ((value / 1000).toInt() < 9.9) {
                    (value / 1000).toInt().toString() + "." + ((value / 100).toInt()/10).toString() + "K"
                } else
                    (value / 1000).toInt().toString() + "K"

            }

        }
    }

}