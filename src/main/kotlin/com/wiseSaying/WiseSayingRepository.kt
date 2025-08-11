package com.wiseSaying

import java.io.File

class WiseSayingRepository {
    private val dbPath = "db/wiseSaying"
    private val lastIdPath = "$dbPath/lastId.txt"
    private val dataJsonPath = "$dbPath/data.json"

    init {
        createDirectoryIfNotExists()
    }

    private fun createDirectoryIfNotExists() {
        val directory = File(dbPath)
        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    private fun getLastId(): Int {
        val file = File(lastIdPath)
        return if (file.exists()) {
            file.readText().trim().toIntOrNull() ?: 0
        } else {
            0
        }
    }

    private fun setLastId(id: Int) {
        File(lastIdPath).writeText(id.toString())
    }

    private fun wiseSayingToJson(wiseSaying: WiseSaying): String {
        return """{
  "id": ${wiseSaying.id},
  "content": "${wiseSaying.content}",
  "author": "${wiseSaying.author}"
}"""
    }

    private fun jsonToWiseSaying(json: String): WiseSaying? {
        return try {
            val lines = json.lines()
            val id = lines.find { it.contains("\"id\"") }
                ?.substringAfter(":")
                ?.trim()
                ?.removeSuffix(",")
                ?.toInt() ?: return null
            
            val content = lines.find { it.contains("\"content\"") }
                ?.substringAfter(":")
                ?.trim()
                ?.removeSurrounding("\"")
                ?.removeSuffix(",") ?: return null
            
            val author = lines.find { it.contains("\"author\"") }
                ?.substringAfter(":")
                ?.trim()
                ?.removeSurrounding("\"")
                ?.removeSuffix(",") ?: return null
            
            WiseSaying(id, content, author)
        } catch (e: Exception) {
            null
        }
    }

    fun save(wiseSaying: WiseSaying): WiseSaying {
        val newId = if (wiseSaying.id == 0) getLastId() + 1 else wiseSaying.id
        val newWiseSaying = wiseSaying.copy(id = newId)
        
        val file = File("$dbPath/$newId.json")
        val jsonContent = wiseSayingToJson(newWiseSaying)
        file.writeText(jsonContent)
        
        if (wiseSaying.id == 0) {
            setLastId(newId)
        }
        
        return newWiseSaying
    }

    fun findById(id: Int): WiseSaying? {
        val file = File("$dbPath/$id.json")
        return if (file.exists()) {
            jsonToWiseSaying(file.readText())
        } else {
            null
        }
    }

    fun findAll(): List<WiseSaying> {
        val directory = File(dbPath)
        val wiseSayings = mutableListOf<WiseSaying>()
        
        if (directory.exists()) {
            directory.listFiles { file -> 
                file.name.matches(Regex("\\d+\\.json"))
            }?.forEach { file ->
                val wiseSaying = jsonToWiseSaying(file.readText())
                if (wiseSaying != null) {
                    wiseSayings.add(wiseSaying)
                }
            }
        }
        
        return wiseSayings.sortedByDescending { it.id }
    }

    fun deleteById(id: Int): Boolean {
        val file = File("$dbPath/$id.json")
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    fun buildDataJson() {
        val wiseSayings = findAll().sortedBy { it.id }
        val jsonArray = StringBuilder("[\n")
        wiseSayings.forEachIndexed { index, wiseSaying ->
            if (index > 0) jsonArray.append(",\n")
            jsonArray.append("  {\n")
            jsonArray.append("    \"id\": ${wiseSaying.id},\n")
            jsonArray.append("    \"content\": \"${wiseSaying.content}\",\n")
            jsonArray.append("    \"author\": \"${wiseSaying.author}\"\n")
            jsonArray.append("  }")
        }
        jsonArray.append("\n]")
        File(dataJsonPath).writeText(jsonArray.toString())
    }

    fun search(keywordType: String, keyword: String): List<WiseSaying> {
        return findAll().filter { wiseSaying ->
            when (keywordType) {
                "content" -> wiseSaying.content.contains(keyword)
                "author" -> wiseSaying.author.contains(keyword)
                else -> false
            }
        }
    }

    fun findAllPaged(page: Int, pageSize: Int = 5): Pair<List<WiseSaying>, Int> {
        val allWiseSayings = findAll()
        val totalPages = (allWiseSayings.size + pageSize - 1) / pageSize
        val startIndex = (page - 1) * pageSize
        val endIndex = minOf(startIndex + pageSize, allWiseSayings.size)
        
        val pagedWiseSayings = if (startIndex < allWiseSayings.size) {
            allWiseSayings.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        return Pair(pagedWiseSayings, totalPages)
    }

    fun searchPaged(keywordType: String, keyword: String, page: Int, pageSize: Int = 5): Pair<List<WiseSaying>, Int> {
        val searchResults = search(keywordType, keyword)
        val totalPages = (searchResults.size + pageSize - 1) / pageSize
        val startIndex = (page - 1) * pageSize
        val endIndex = minOf(startIndex + pageSize, searchResults.size)
        
        val pagedResults = if (startIndex < searchResults.size) {
            searchResults.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        
        return Pair(pagedResults, totalPages)
    }
}
