package com.diamondedge.charts

class LogXYGraph (
    data: ChartData,
    drawLine: Boolean = true,
    fillArea: Boolean = false,
    showBubble: Boolean = false,
    curveSmothing: Boolean = false,
    val logAxisSelection: axis = axis.horizontal,
) :
    XYGraph(data, drawLine, fillArea, showBubble, curveSmothing) {


    /**
        Based on the log axis selection I'll return the appropriate
        axis or logAxis object for the horizontal and vertical axis.
    */

    override fun createHorizontalAxis(): Axis {
        return when(logAxisSelection){
            axis.none -> DecimalAxis()
            axis.horizontal -> LogAxis()
            axis.vertical -> DecimalAxis()
            axis.both -> LogAxis()
        }
    }

    override fun createVerticalAxis(): Axis {
        return when(logAxisSelection){
            axis.none -> DecimalAxis()
            axis.horizontal -> LogAxis()
            axis.vertical -> DecimalAxis()
            axis.both -> LogAxis()
        }
    }

    override fun toString(): String {
        return "LogXYGraph[" + toStringParam() + "]"
    }
}

enum class axis(){
    none,
    horizontal,
    vertical,
    both
}
