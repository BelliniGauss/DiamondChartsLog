package com.diamondedge.charts

class LogXYGraph (
    data: ChartData,
    drawLine: Boolean = true,
    fillArea: Boolean = false,
    showBubble: Boolean = false,
    curveSmoothing: Boolean = false,
    private val logAxisSelection: AxisGroup = AxisGroup.Horizontal,
    private val baseLabelPosition: Array<Int>? = null
) : XYGraph(logToLinear(data), drawLine, fillArea, showBubble, curveSmoothing) {


    /**
        Based on the log axis selection I'll return the appropriate
        axis or logAxis object for the horizontal and vertical axis.
    */

    override fun createHorizontalAxis(): Axis {
        return when(logAxisSelection){
            AxisGroup.None -> DecimalAxis()
            AxisGroup.Horizontal -> LogarithmicAxis(baseLabelPosition)
            AxisGroup.Vertical -> DecimalAxis()
            AxisGroup.Both -> LogarithmicAxis(baseLabelPosition)
        }
    }

    override fun createVerticalAxis(): Axis {
        return when(logAxisSelection){
            AxisGroup.None -> DecimalAxis()
            AxisGroup.Horizontal -> DecimalAxis()
            AxisGroup.Vertical -> LogarithmicAxis(baseLabelPosition)
            AxisGroup.Both -> LogarithmicAxis(baseLabelPosition)
        }
    }

    override fun toString(): String {
        return "LogXYGraph[" + toStringParam() + "]"
    }
}

private fun logToLinear(data: ChartData): ChartData{
    //TODO implement the conversion
    return data
}

enum class AxisGroup{
    None,
    Horizontal,
    Vertical,
    Both
}
