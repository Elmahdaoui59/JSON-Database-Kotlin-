package jsondatabase.client

import jsondatabase.Srequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.net.InetAddress
import java.net.Socket

fun main(args: Array<String>) {
    var method: String? = null
    var index: String? = null
    var text: String? = null
    var fileName: String? = null
    var rqFilePath: String? = null
    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "-t" -> {
                method = args.getOrNull(i+1)
                i += 2
            }
            "-k" -> {
                index = args.getOrNull(i+1)
                i += 2
            }
            "-v" -> {
                text = args.drop(i+ 1).joinToString(" ")
                i = args.size
            }
            "-in" -> {
                fileName = args.getOrNull(i+1)
                i = args.size
            }
            else -> {
                println("Error: Unknown argument ${args[i]}")
                return
            }
        }
    }
    val address = "127.0.1.1"
    val port = 23456
    val socket = Socket(InetAddress.getByName(address), port)
    val input = DataInputStream(socket.getInputStream())
    val output = DataOutputStream(socket.getOutputStream())
    println("Client started!")
    var request = Srequest()
    method?.let {
        request = request.copy(type = it)
    }
    index?.let {
        request = request.copy(key = it)
    }
    text?.let {
        request = request.copy(value = it)
    }
    var jsonRq = Json.encodeToString(request)
    fileName?.let {
        rqFilePath = "C:\\Users\\dell\\IdeaProjects\\JSON Database (Kotlin)\\JSON Database (Kotlin)" +
                "\\task\\src\\jsondatabase\\client\\data\\$fileName"
        jsonRq = File(rqFilePath!!).readText()

    }
    output.writeUTF(jsonRq)
    println("Sent: $jsonRq")
    val message: String = input.readUTF()
    println("Received: $message")
}