package com.diamondedge.chartapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import com.diamondedge.charts.AxisGroup
import com.diamondedge.charts.ChartData
import com.diamondedge.charts.Charts
import com.diamondedge.charts.Charts.Companion.LEGEND_RIGHT
import com.diamondedge.charts.Color
import com.diamondedge.charts.Draw
import com.diamondedge.charts.GridLines
import com.diamondedge.charts.LineAttributes
import com.diamondedge.charts.LogXYGraph
import com.diamondedge.charts.Margins
import com.diamondedge.charts.TickLabelPosition
import com.diamondedge.charts.compose.ComposeGC
import kotlin.math.pow

private val fn1: (Double) -> Double = { x ->
    (x.pow(0.5) )
}
private val fn2: (Double) -> Double = { x ->
    (x.pow(0.5) + 50)
}

private const val minX = 10.0
private const val maxX = 500.0
private val scrubLine = LineAttributes(color = 0xffC4C4C4, width = 1f)
private const val scrubDataPointSizeDp = 8f


@Composable
fun ScrubbingScreen() {
    val density = LocalDensity.current
    var dragX by remember { mutableStateOf(0.0) }
    var isScrubbing by remember { mutableStateOf(false) }
    val data1 = createData(fn1, minX, maxX, "fn1", Color.green)
    val data2 = createData(fn2, minX, maxX, "fn2")
    val data3 = createData(fn1, minX*2, maxX/4, "fn1", Color.green)
    val data4 = createData(fn2, minX*2, maxX/4, "fn2")

    val allData = listOf(data1, data2)

    var zoomBool by remember { mutableStateOf(false) }



    Surface(Modifier.fillMaxSize()) {
        Column (){

            Button(onClick = { zoomBool = !zoomBool }) {
                Text(text = "prova prova 123")
            }


            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            dragX += delta
                        },
                        onDragStarted = { pos ->
                            dragX = pos.x.toDouble()
                            isScrubbing = true
                        },
                        onDragStopped = {
                            isScrubbing = false
                        }
                    )
            ) {

                val charts1 = Charts(size.width, size.height, Margins(25f, 25f, 45f, 10f), LEGEND_RIGHT)

                charts1.add(LogXYGraph(data1, logAxisSelection = AxisGroup.Horizontal))
                charts1.add(LogXYGraph(data2, logAxisSelection = AxisGroup.Horizontal))

                charts1.vertAxis?.apply {
                    majorTickLabelPosition = TickLabelPosition.TickCenter
                    majorTickIncrement = 50.0
                }
                charts1.horizontalAxis?.apply {
                    majorTickLabelPosition = TickLabelPosition.BelowTick
                }
                charts1.gridLines?.apply {
                    minorVerticalLines.isVisible = true
                }

                val charts2 = Charts(size.width, size.height, Margins(25f, 25f, 45f, 10f), LEGEND_RIGHT)

                charts2.add(LogXYGraph(data3, logAxisSelection = AxisGroup.Horizontal))
                charts2.add(LogXYGraph(data4, logAxisSelection = AxisGroup.Horizontal))

                charts2.vertAxis?.apply {
                    majorTickLabelPosition = TickLabelPosition.TickCenter
                    majorTickIncrement = 50.0
                }
                charts2.horizontalAxis?.apply {
                    majorTickLabelPosition = TickLabelPosition.BelowTick
                }
                charts2.gridLines?.apply {
                    minorVerticalLines.isVisible = true
                }

                if(zoomBool){
                    drawIntoCanvas { canvas ->
                        val g = ComposeGC(canvas, density)
                        charts2.draw(g)
                    }
                }else{
                    drawIntoCanvas { canvas ->
                        val g = ComposeGC(canvas, density)
                        charts1.draw(g)

                        if (isScrubbing) {
                            val scrubX =
                                dragX.toInt().coerceIn(charts1.chartBounds.x, charts1.chartBounds.right)
                            val scrubXValue = charts1.horizontalAxis?.convertToValue(scrubX) ?: 0.0
                            val scrubPoints = ChartData.dataPointsAtX(scrubXValue, allData)
                            var text = "x = ${String.format("%.2f", scrubXValue)}"
                            GridLines.drawLine(
                                g,
                                scrubLine,
                                scrubX,
                                charts1.chartBounds.y,
                                scrubX,
                                charts1.chartBounds.bottom
                            )
                            for ((data, value) in scrubPoints) {
                                val y = charts1.vertAxis?.convertToPixel(value) ?: 0
                                Draw.drawCircle(g, scrubX, y, scrubDataPointSizeDp, null, Color.red)
                                text += " ${data.id}: ${String.format("%.2f", value)}"
                            }
                            Draw.drawTileCentered(
                                g,
                                scrubX,
                                charts1.chartBounds.y,
                                null,
                                text,
                                false,
                                8f,
                                8f
                            )
                        }
                    }
                }



            }
        }
    }
}

@Preview
@Composable
private fun ScrubbingScreenPreview() {
    ScrubbingScreen()
}
