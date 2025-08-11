package com.wiseSaying

class WiseSayingService(private val repository: WiseSayingRepository) {

    fun create(content: String, author: String): WiseSaying {
        val wiseSaying = WiseSaying(0, content, author)
        return repository.save(wiseSaying)
    }

    fun findById(id: Int): WiseSaying? {
        return repository.findById(id)
    }

    fun findAll(): List<WiseSaying> {
        return repository.findAll()
    }

    fun deleteById(id: Int): Boolean {
        return repository.deleteById(id)
    }

    fun update(id: Int, content: String, author: String): WiseSaying? {
        val existingWiseSaying = repository.findById(id)
        return if (existingWiseSaying != null) {
            val updatedWiseSaying = existingWiseSaying.copy(content = content, author = author)
            repository.save(updatedWiseSaying)
        } else {
            null
        }
    }

    fun buildDataJson() {
        repository.buildDataJson()
    }

    fun search(keywordType: String, keyword: String): List<WiseSaying> {
        return repository.search(keywordType, keyword)
    }

    fun findAllPaged(page: Int): Pair<List<WiseSaying>, Int> {
        return repository.findAllPaged(page)
    }

    fun searchPaged(keywordType: String, keyword: String, page: Int): Pair<List<WiseSaying>, Int> {
        return repository.searchPaged(keywordType, keyword, page)
    }

    fun createSampleData() {
        for (i in 1..10) {
            create("명언 $i", "작자미상 $i")
        }
    }
}
