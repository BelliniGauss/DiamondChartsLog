package com.diamondedge.charts

class LogXYGraph (
    data: ChartData,
    drawLine: Boolean = true,
    fillArea: Boolean = false,
    showBubble: Boolean = false,
    curveSmoothing: Boolean = false,
    private val logAxisSelection: AxisGroup = AxisGroup.Horizontal,
) : XYGraph(logToLinear(data), drawLine, fillArea, showBubble, curveSmoothing) {


    /**
     * Origianl data, real numerical representation.
     */
    private val logData = data

    /**
     * Data converted to a linear coordinate axis for a logarithmic representation
     */
    private val linearData = logToLinear(data)



    /**
        Based on the log axis selection I'll return the appropriate
        axis or logAxis object for the horizontal and vertical axis.
    */

    override fun createHorizontalAxis(): Axis {
        return when(logAxisSelection){
            AxisGroup.None -> DecimalAxis()
            AxisGroup.Horizontal -> LogarithmicAxis()
            AxisGroup.Vertical -> DecimalAxis()
            AxisGroup.Both -> LogarithmicAxis()
        }
    }

    override fun createVerticalAxis(): Axis {
        return when(logAxisSelection){
            AxisGroup.None -> DecimalAxis()
            AxisGroup.Horizontal -> DecimalAxis()
            AxisGroup.Vertical -> LogarithmicAxis()
            AxisGroup.Both -> LogarithmicAxis()
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
