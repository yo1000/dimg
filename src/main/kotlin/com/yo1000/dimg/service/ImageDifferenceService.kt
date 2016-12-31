package com.yo1000.dimg.service

import com.yo1000.dimg.util.Decoder
import com.yo1000.dimg.util.ImageComparator
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.math.BigDecimal
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

/**
 *
 * @author yo1000
 */
@Service
class ImageDifferenceService(
        private val decoder: Decoder,
        private val imageComparator: ImageComparator) {
    fun compareBinaries(input1: String, input2: String, base64: Boolean): BigDecimal {
        ByteArrayInputStream(decoder.decode(input1, base64)).use { input1 ->
            ByteArrayInputStream(decoder.decode(input2, base64)).use { input2 ->
                return imageComparator.matchRatio(input1, input2)
            }
        }
    }

    fun compareBinaries(input1: String, input2: String, base64: Boolean, output: String): BigDecimal {
        return compareBinaries(input1, input2, base64, File(output))
    }

    fun compareBinaries(input1: String, input2: String, base64: Boolean, output: File): BigDecimal {
        checkOutputFile(output)
        ByteArrayInputStream(decoder.decode(input1, base64)).use { input1 ->
            ByteArrayInputStream(decoder.decode(input2, base64)).use { input2 ->
                return imageComparator.matchRatioAndOutput(input1, input2, output)
            }
        }
    }

    fun compareDirectories(input1: String, input2: String, callback: (File, File) -> Unit) {
        compareDirectories(File(input1), File(input2), callback)
    }

    fun compareDirectories(input1: File, input2: File, callback: (File, File) -> Unit) {
        checkInputDirs(input1, input2)

        val path1 = Paths.get(input1.toURI())
        val path2 = Paths.get(input2.toURI())

        Files.walkFileTree(path1, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                val file2 = path2.resolve(file.fileName).toFile()

                if (!file2.exists()) {
                    return super.visitFile(file, attrs)
                }

                callback.invoke(file.toFile(), file2)

                return super.visitFile(file, attrs)
            }
        })
    }

    fun compareFiles(input1: String, input2: String): BigDecimal {
        return compareFiles(File(input1), File(input2))
    }

    fun compareFiles(input1: String, input2: String, output: String): BigDecimal {
        return compareFiles(File(input1), File(input2), File(output))
    }

    fun compareFiles(input1: File, input2: File): BigDecimal {
        checkInputFiles(input1, input2)
        return imageComparator.matchRatio(input1, input2)
    }

    fun compareFiles(input1: File, input2: File, output: File): BigDecimal {
        checkInputFiles(input1, input2)
        checkOutputFile(output)
        return imageComparator.matchRatioAndOutput(input1, input2, output)
    }

    private fun checkInputFiles(input1: File, input2: File) {
        if (!input1.exists() || !input1.isFile) {
            throw FileNotFoundException("File not found. (${input1.absolutePath})")
        }

        if (!input2.exists() || !input2.isFile) {
            throw FileNotFoundException("File not found. (${input2.absolutePath})")
        }
    }

    private fun checkInputDirs(input1: File, input2: File) {
        if (!input1.exists() || !input1.isDirectory) {
            throw FileNotFoundException("Directory not found. (${input1.absolutePath})")
        }

        if (!input2.exists() || !input2.isDirectory) {
            throw FileNotFoundException("Directory not found. (${input2.absolutePath})")
        }
    }

    private fun checkOutputFile(output: File) {
        if (output.exists()) {
            throw FileAlreadyExistsException(output)
        }
    }
}
