package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.StudentEntity

@Composable
fun StudentQrCode(
    dataString: String,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp
) {
    val gridSize = 17
    val seed = dataString.hashCode()

    Box(
        modifier = modifier
            .size(size)
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellWidth = this.size.width / gridSize
            val cellHeight = this.size.height / gridSize

            // Draw Finder Patterns (Top-Left, Top-Right, Bottom-Left)
            fun drawFinder(startX: Int, startY: Int) {
                // Outer black 7x7
                drawRect(
                    color = Color.Black,
                    topLeft = Offset(startX * cellWidth, startY * cellHeight),
                    size = Size(5 * cellWidth, 5 * cellHeight)
                )
                // Inner white 5x5
                drawRect(
                    color = Color.White,
                    topLeft = Offset((startX + 1) * cellWidth, (startY + 1) * cellHeight),
                    size = Size(3 * cellWidth, 3 * cellHeight)
                )
                // Center black 3x3
                drawRect(
                    color = Color.Black,
                    topLeft = Offset((startX + 2) * cellWidth, (startY + 2) * cellHeight),
                    size = Size(cellWidth, cellHeight)
                )
            }

            drawFinder(0, 0)
            drawFinder(gridSize - 5, 0)
            drawFinder(0, gridSize - 5)

            // Random modules based on hash
            var currentHash = seed
            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    val inTLFinder = r < 6 && c < 6
                    val inTRFinder = r < 6 && c >= gridSize - 6
                    val inBLFinder = r >= gridSize - 6 && c < 6

                    if (!inTLFinder && !inTRFinder && !inBLFinder) {
                        currentHash = currentHash * 31 + (r * 17 + c)
                        if (Math.abs(currentHash) % 2 == 0) {
                            drawRect(
                                color = Color.Black,
                                topLeft = Offset(c * cellWidth, r * cellHeight),
                                size = Size(cellWidth, cellHeight)
                            )
                        }
                    }
                }
            }
        }
    }
}
