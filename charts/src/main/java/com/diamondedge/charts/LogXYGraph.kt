package com.diamondedge.charts

class LogXYGraph (
    data: ChartData,
    drawLine: Boolean = true,
    fillArea: Boolean = false,
    showBubble: Boolean = false,
    curveSmoothing: Boolean = false,
    private val logAxisSelection: AxisGroup = AxisGroup.Horizontal,
) : XYGraph(LogToLinear(data), drawLine, fillArea, showBubble, curveSmoothing) {

    private val logData = data



    /**
        Based on the log axis selection I'll return the appropriate
        axis or logAxis object for the horizontal and vertical axis.
    */

    override fun createHorizontalAxis(): Axis {
        return when(logAxisSelection){
            AxisGroup.None -> DecimalAxis()
            AxisGroup.Horizontal -> LogAxis()
            AxisGroup.Vertical -> DecimalAxis()
            AxisGroup.Both -> LogAxis()
        }
    }

    override fun createVerticalAxis(): Axis {
        return when(logAxisSelection){
            AxisGroup.None -> DecimalAxis()
            AxisGroup.Horizontal -> LogAxis()
            AxisGroup.Vertical -> DecimalAxis()
            AxisGroup.Both -> LogAxis()
        }
    }

    override fun toString(): String {
        return "LogXYGraph[" + toStringParam() + "]"
    }
}

private fun LogToLinear(data: ChartData): ChartData{
    //TODO implement the conversion
    return data
}

enum class AxisGroup{
    None,
    Horizontal,
    Vertical,
    Both
}
