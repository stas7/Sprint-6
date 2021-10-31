import ru.sber.filesystem.VFilesystem
import ru.sber.filesystem.VPath
import java.io.IOException
import java.net.ServerSocket

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
class FileServer {

    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs     A proxy filesystem to serve files from. See the VFilesystem
     *               class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    @Throws(IOException::class)
    fun run(socket: ServerSocket, fs: VFilesystem) {

        /**
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {
            socket.accept().use { s ->
                val reader = s.getInputStream().bufferedReader()
                val clientRequest = reader.readLine()
                val requestParts = clientRequest.split("\\s+".toRegex())

                val vPath = VPath(requestParts[1])
                val content = fs.readFile(vPath)
                val resp: String

                if (content.isNullOrEmpty()) {
                    resp = "HTTP/1.0 404 Not Found\r\nServer: FileServer\r\n\r\n"
                } else {
                    resp = "HTTP/1.0 200 OK\r\nServer: FileServer\r\n\r\n" + content
                }

                val writer = s.getOutputStream().bufferedWriter()
                writer.write(resp)
                writer.flush()
            }
        }
    }
}