package jsondatabase

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.io.File
import java.io.FileWriter

val json = Json { prettyPrint = true}
fun main() {
    val filePath = "C:\\Users\\dell\\IdeaProjects\\JSON Database (Kotlin)\\JSON Database (Kotlin)\\task\\src\\jsondatabase\\server\\data\\test.json"

    val data = parseJson("{\n" +
            "      \"name\":\"Elon Musk\",\n" +
            "      \"car\":{\n" +
            "         \"model\":\"Tesla Roadster\",\n" +
            "         \"year\":\"2018\"\n" +
            "      },\n" +
            "      \"rocket\":{\n" +
            "         \"name\":\"Falcon 9\",\n" +
            "         \"launches\":\"87\"\n" +
            "      }\n" +
            "   }")
    val jsonData = json.encodeToString(data)
    println(jsonData)
    try {
        FileWriter(
            filePath
        ).use {
            it.write(jsonData)
        }
        println("data saved to file")
        val loadedData = File(filePath).readText()
        println("loaded data : $loadedData")
    } catch (e: Exception) {
        throw e
    }
}
fun parseJson(jsonString: String): Map<String, JsonElement> {
    return json.decodeFromString<Map<String, JsonElement>>(jsonString)
}