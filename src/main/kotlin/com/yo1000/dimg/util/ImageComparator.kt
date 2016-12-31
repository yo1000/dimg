package com.yo1000.dimg.util

import org.springframework.stereotype.Component
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import java.math.BigDecimal
import javax.imageio.ImageIO

/**
 *
 * @author yo1000
 */
@Component
class ImageComparator() {
    fun matchRatioAndOutput(in1: File, in2: File, out: File): BigDecimal {
        val diff = diff(ImageIO.read(in1), ImageIO.read(in2))
        ImageIO.write(diff, "png", out)
        return matchRatio(diff)
    }

    fun matchRatioAndOutput(input1: InputStream, input2: InputStream, out: File): BigDecimal {
        val diff = diff(ImageIO.read(input1), ImageIO.read(input2))
        ImageIO.write(diff, "png", out)
        return matchRatio(diff)
    }

    fun matchRatio(input1: InputStream, input2: InputStream): BigDecimal {
        return matchRatio(diff(ImageIO.read(input1), ImageIO.read(input2)))
    }

    fun matchRatio(in1: File, in2: File): BigDecimal {
        return matchRatio(diff(ImageIO.read(in1), ImageIO.read(in2)))
    }

    protected fun diff(image1: BufferedImage, image2: BufferedImage): BufferedImage {
        val image = BufferedImage(
                if (image1.width >= image2.width) image1.width else image2.width,
                if (image1.height >= image2.height) image1.height else image2.height,
                image1.type)
        val imageGraphics = image.createGraphics()

        imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        imageGraphics.setXORMode(Color.BLACK)
        imageGraphics.drawImage(image1, null, 0, 0)
        imageGraphics.drawImage(image2, null, 0, 0)

        return image
    }

    protected fun matchRatio(image: BufferedImage): BigDecimal {
        var notBlackCount: Int = 0

        (0..image.height - 1).forEach { y ->
            notBlackCount += (0..image.width - 1)
                    .filter { x ->image.getRGB(x, y) and 0xFFFFFF != 0 }
                    .size
        }

        return BigDecimal(notBlackCount).divide(BigDecimal(image.width * image.height), 32, 0)
    }
}