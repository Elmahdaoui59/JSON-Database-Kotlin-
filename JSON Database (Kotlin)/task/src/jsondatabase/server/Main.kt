package jsondatabase.server

import jsondatabase.Sresponse
import jsondatabase.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

var map = mutableMapOf<String, JsonElement>()
val filePath =
    "C:\\Users\\dell\\IdeaProjects\\JSON Database (Kotlin)\\JSON Database (Kotlin)\\task\\src\\jsondatabase\\server\\data\\db.json"
val lock: ReadWriteLock = ReentrantReadWriteLock()
val readLock: Lock = lock.readLock()
val writeLock: Lock = lock.writeLock()
fun main() {

    val address = "127.0.1.1"
    val port = 23456
    val server = ServerSocket(port, 50, InetAddress.getByName(address))
    println("Server started!")

    while (true) {
        val socket = server.accept()
        val executor = Executors.newSingleThreadExecutor()
        executor.submit {
            try {
                processClientConnection(socket) {
                    server.close()
                    executor.shutdownNow()

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

fun processClientConnection(socket: Socket, onShutDown: () -> Unit) {
    val input = DataInputStream(socket.getInputStream())
    val output = DataOutputStream(socket.getOutputStream())
    val jsonRq = input.readUTF()
    println("Received: $jsonRq")
    //val request = Json.decodeFromString<Srequest>(jsonRq)
    val mapRq = parseJson(jsonRq)
    var response = Sresponse()
    when (mapRq["type"]?.jsonPrimitive?.content) {
        "exit" -> {
            response = response.copy(response = "OK")
            output.writeUTF(Json.encodeToString(response))
            onShutDown()
        }

        "set" -> {
            if (mapRq["key"] is JsonPrimitive) {
                val key = mapRq["key"]?.jsonPrimitive?.content
                val text = mapRq["value"]
                val tmpMap = mapOf(key to text)
                map.putAll(parseJson(Json.encodeToString(tmpMap)))
                //map[key] = text?.trim().toString()
                writeDbToFile()
            } else {
                updateValue(mapRq["key"]?.jsonArray, mapRq["value"]?.jsonPrimitive?.content.toString())
            }
            response = response.copy(response = "OK")
            output.writeUTF(Json.encodeToString(response))
        }

        "get" -> {
            val keys: JsonArray? = mapRq["key"]?.jsonArray
            if (keys.isNullOrEmpty()) {
                println("keys array is null or empty")
            }
            readDbFromFile()
            if (checkKey(keys!!)) {
                val value = getValue(keys)
                if (value.toString().isEmpty()) {
                    response = response.copy(response = "ERROR", reason = "No such key")
                    output.writeUTF(Json.encodeToString(response))
                } else {
                    response = response.copy(response = "OK", value = value)
                    output.writeUTF(Json.encodeToString(response))
                }
            } else {
                response = response.copy(response = "ERROR", reason = "No such key")
                output.writeUTF(Json.encodeToString(response))
            }
        }

        "delete" -> {
            val keys = mapRq["key"]?.jsonArray
            if (keys.isNullOrEmpty()) {
                println("keys array is null or empty")
            }
            readDbFromFile()
            if (checkKey(keys!!)) {
                val value = getValue(keys)
                if (value.toString().isEmpty()) {
                    response = response.copy(response = "ERROR", reason = "No such key")
                    output.writeUTF(Json.encodeToString(response))
                } else {
                    //map[key] = ""
                    updateValue(keys, "")
                    writeDbToFile()
                    response = response.copy(response = "OK")
                    output.writeUTF(Json.encodeToString(response))
                }

            } else {
                response = response.copy(response = "ERROR", reason = "No such key")
                output.writeUTF(Json.encodeToString(response))
            }
        }

        else -> {}
    }

}

fun updateValue(jsonArray: JsonArray?, value: String) {
    when (jsonArray?.size) {
        1 -> {
            //return map[getKey(0, keys)]?.jsonObject.toString()
        }

        2 -> {
            /*
            val s = map[getKey(0, keys)]?.jsonObject?.get(getKey(1, keys))
            return if (s is JsonObject) {
                s.jsonObject.toString()
            } else {
                s?.jsonPrimitive?.content.toString()
            }

             */
        }

        3 -> {
            val secondMap = map[getKey(0, jsonArray)]?.jsonObject?.toMutableMap()
            val thirdMap = map[getKey(0, jsonArray)]?.jsonObject?.get(getKey(1, jsonArray))
                ?.jsonObject?.toMutableMap()
            if (value.isEmpty()){
                thirdMap?.remove(getKey(2, jsonArray))
            } else {
                val tmpMap = mapOf(getKey(2, jsonArray) to Json.encodeToJsonElement(value))
                thirdMap?.putAll(parseJson(Json.encodeToString(tmpMap)))
            }
            val tmpMap1 = mapOf(getKey(1, jsonArray) to Json.encodeToJsonElement(thirdMap))
            secondMap?.putAll(tmpMap1)
            map.putAll(
                mapOf(getKey(0, jsonArray) to Json.encodeToJsonElement(secondMap))
            )

            writeDbToFile()
        }

        else -> {}
    }
}

fun parseJson(jsonString: String): Map<String, JsonElement> {
    return json.decodeFromString<Map<String, JsonElement>>(jsonString)
}

fun writeDbToFile() {
    writeLock.lock()
    FileWriter(filePath).use {
        it.write(Json.encodeToString(map))
    }
    writeLock.unlock()
}

fun readDbFromFile() {
    readLock.lock()
    val loadedData = File(filePath).readText()
    map = parseJson(loadedData).toMutableMap()
    readLock.unlock()
}

fun getValue(keys: JsonArray): JsonElement {
    when (keys.size) {
        1 -> {
            return map[getKey(0, keys)]?.jsonObject as JsonElement
        }

        2 -> {
            val s = map[getKey(0, keys)]?.jsonObject?.get(getKey(1, keys))
            return if (s is JsonObject) {
                s.jsonObject as JsonElement
            } else {
                Json.encodeToJsonElement(s?.jsonPrimitive?.content)
            }
        }

        3 -> {
            return Json.encodeToJsonElement(
                map[getKey(0, keys)]?.jsonObject?.get(getKey(1, keys))
                    ?.jsonObject?.get(getKey(2, keys))?.jsonPrimitive?.content
            )
        }

        else -> {
            return Json.encodeToJsonElement("")
        }
    }
}

fun checkKey(keys: JsonArray): Boolean {

    when (keys.size) {
        1 -> {
            return map.containsKey(getKey(0, keys))
        }

        2 -> {

            return (map.containsKey(getKey(0, keys))
                    && (map[getKey(0, keys)]?.jsonObject?.containsKey(getKey(1, keys)) == true))
        }

        3 -> {
            return (map.containsKey(getKey(0, keys))
                    && (map[getKey(0, keys)]?.jsonObject?.containsKey(getKey(1, keys)) == true))
                    && (map[getKey(0, keys)]?.jsonObject?.get(getKey(1, keys))?.jsonObject?.containsKey(
                getKey(
                    2,
                    keys
                )
            ) == true)
        }

        else -> {
            return false
        }
    }
}

fun getKey(index: Int, keys: JsonArray): String {
    return keys[index].jsonPrimitive.content
}