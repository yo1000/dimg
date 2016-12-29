package com.yo1000.dimg.cli

import com.yo1000.dimg.service.ImageDifferenceService
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.stream.Collectors

/**
 *
 * @author yo1000
 */
@Component
class DimgCommandLineRunner(val imageDifferenceService: ImageDifferenceService) : CommandLineRunner {
    val LOGGER: Logger = LoggerFactory.getLogger(DimgCommandLineRunner::class.java)

    override fun run(vararg args: String?) {
        val options = Options()
        options.addOption("f1", "file1", true, "File to diff 1.\nMust use with f2.")
        options.addOption("f2", "file2", true, "File to diff 2.\nMust use with f1.")
        options.addOption("d1", "dir1", true, "Directory containing files to diff 1.\nMust use with d2.")
        options.addOption("d2", "dir2", true, "Directory containing files to diff 2.\nMust use with d1.")
        options.addOption("o", "out", true, "Output location.")
        options.addOption("s", "suppress", false, "Suppress standard output.")
        options.addOption("b64", "base64", false, "Base64 format input.\nUse only for piped standard input.")
        options.addOption("?", "help", false, "Print this message.")

        val parser = DefaultParser()
        val cl = parser.parse(options, args)

        if (cl.hasOption("?")) {
            val formatter = HelpFormatter()
            formatter.printHelp("""
dimg [-b64 | -f1 <imageFile1> -f2 <imageFile2> |
      -d1 <imageContainsDir1> -d2 <imageContainsDir2>]
     [-o <outLocation> [-s]]

""",
                    options)
        }

        val stdin = System.`in`

        if (cl.hasOption("s") && !cl.hasOption("o")) {
            throw IllegalArgumentException("When using `s` option, must use with `o` option.")
        }

        if (stdin.available() != 0 &&
                !cl.hasOption("f1") && !cl.hasOption("f2") &&
                !cl.hasOption("d1") && !cl.hasOption("d2")) {
            val blocks = BufferedReader(InputStreamReader(stdin, Charset.defaultCharset()))
                    .lines()
                    .collect(Collectors.joining())
                    .split("\\s+")

            if (blocks.size != 2) {
                throw IllegalArgumentException("Standard input requires 2 blocks separated by white space.")
            }

            printMatchRatio("stdin", "stdin",
                    if (cl.hasOption("o"))
                        imageDifferenceService.compareBinaries(
                                blocks[0], blocks[1], cl.hasOption("b64"), cl.getOptionValue("o"))
                    else
                        imageDifferenceService.compareBinaries(
                                blocks[0], blocks[1], cl.hasOption("b64")),
                    cl.hasOption("s"))

            return
        }

        if (cl.hasOption("f1") && cl.hasOption("f2") && !cl.hasOption("d1") && !cl.hasOption("d2")) {
            val path1 = cl.getOptionValue("f1")
            val path2 = cl.getOptionValue("f2")

            printMatchRatio(path1, path2,
                    if (cl.hasOption("o"))
                        imageDifferenceService.compareFiles(
                                path1, path2, cl.getOptionValue("o"))
                    else
                        imageDifferenceService.compareFiles(
                                path1, path2),
                    cl.hasOption("s"))

            return
        }

        if (cl.hasOption("d1") && cl.hasOption("d2") && !cl.hasOption("f1") && !cl.hasOption("f2")) {
            imageDifferenceService.compareDirectories(cl.getOptionValue("d1"), cl.getOptionValue("d2"), {
                file1: File, file2: File -> printMatchRatio(
                    file1.absolutePath,
                    file2.absolutePath,
                    if (cl.hasOption("o"))
                        imageDifferenceService.compareFiles(
                                file1, file2, File(cl.getOptionValue("o")))
                    else
                        imageDifferenceService.compareFiles(
                                file1, file2),
                    cl.hasOption("s"))
            })

            return
        }
    }

    protected fun printMatchRatio(f1: String, f2: String, ratio: Double, suppress: Boolean) {
        if (suppress) {
            LOGGER.info("$1: $f1")
            LOGGER.info("$2: $f2")
            LOGGER.info("Match ratio: $ratio")
        } else {
            println("$1: $f1")
            println("$2: $f2")
            println("Match ratio: $ratio")
        }
    }
}