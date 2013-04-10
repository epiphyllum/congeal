package congeal.sc

import scala.collection.JavaConversions.seqAsJavaList

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/** Executes a child process, capturing exit status, output, and error streams.
  *
  * @param command The list containing the program and its arguments
  */
class ProcessExecution(command: List[String]) {

  private val (_exit, _output, _error) = runProcess()

  /** The exit status of the process. */
  def exit: Int = _exit

  /** The process output stream, converted to a string. */
  def output: String = _output

  /** The process error stream, converted to a string. */
  def error: String = _error

  private def runProcess(): (Int, String, String) = {
    val processBuilder = new ProcessBuilder(command)
    val process = processBuilder.start()

    val outputReaderThread = new InputStreamConsumerThread(process.getInputStream)
    outputReaderThread.start()

    val errorReaderThread = new InputStreamConsumerThread(process.getErrorStream)
    errorReaderThread.start()

    process.waitFor()
    outputReaderThread.join()
    errorReaderThread.join()

    (process.exitValue, outputReaderThread.toString, errorReaderThread.toString)
  }
    
  private class InputStreamConsumerThread(inputStream: InputStream) extends Thread {
    private val bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
    private val stringBuilder = new StringBuilder

    override def run {
      readLines(stringBuilder)
      bufferedReader.close()
    }

    private def readLines(stringBuilder: StringBuilder) {
      while (true) {
        val line = bufferedReader.readLine
        if (line == null) return
        stringBuilder.append(line).append('\n')
      }
    }

    override def toString = stringBuilder.toString
  }
}
