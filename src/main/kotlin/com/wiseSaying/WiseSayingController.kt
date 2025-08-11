package com.wiseSaying

import java.util.*

class WiseSayingController(private val service: WiseSayingService, private val scanner: Scanner) {

    fun actionRegister() {
        print("명언 : ")
        val content = scanner.nextLine()
        print("작가 : ")
        val author = scanner.nextLine()
        
        val wiseSaying = service.create(content, author)
        println("${wiseSaying.id}번 명언이 등록되었습니다.")
    }

    fun actionList(params: Map<String, String> = emptyMap()) {
        val keywordType = params["keywordType"]
        val keyword = params["keyword"]
        val page = params["page"]?.toIntOrNull() ?: 1

        if (keywordType != null && keyword != null) {
            actionSearch(keywordType, keyword, page)
        } else {
            val (wiseSayings, totalPages) = service.findAllPaged(page)
            
            if (wiseSayings.isEmpty()) {
                println("등록된 명언이 없습니다.")
                return
            }

            println("번호 / 작가 / 명언")
            println("----------------------")
            wiseSayings.forEach { wiseSaying ->
                println("${wiseSaying.id} / ${wiseSaying.author} / ${wiseSaying.content}")
            }
            println("----------------------")
            
            // 페이지 표시
            val pageDisplay = StringBuilder("페이지 : ")
            for (i in 1..totalPages) {
                if (i == page) {
                    pageDisplay.append("[$i]")
                } else {
                    pageDisplay.append(i)
                }
                if (i < totalPages) {
                    pageDisplay.append(" / ")
                }
            }
            println(pageDisplay.toString())
        }
    }

    private fun actionSearch(keywordType: String, keyword: String, page: Int) {
        val (wiseSayings, totalPages) = service.searchPaged(keywordType, keyword, page)
        
        println("----------------------")
        println("검색타입 : $keywordType")
        println("검색어 : $keyword")
        println("----------------------")
        
        if (wiseSayings.isEmpty()) {
            println("검색 결과가 없습니다.")
            return
        }

        println("번호 / 작가 / 명언")
        println("----------------------")
        wiseSayings.forEach { wiseSaying ->
            println("${wiseSaying.id} / ${wiseSaying.author} / ${wiseSaying.content}")
        }
        
        if (totalPages > 1) {
            println("----------------------")
            val pageDisplay = StringBuilder("페이지 : ")
            for (i in 1..totalPages) {
                if (i == page) {
                    pageDisplay.append("[$i]")
                } else {
                    pageDisplay.append(i)
                }
                if (i < totalPages) {
                    pageDisplay.append(" / ")
                }
            }
            println(pageDisplay.toString())
        }
    }

    fun actionDelete(params: Map<String, String>) {
        val idStr = params["id"]
        if (idStr == null) {
            println("삭제할 명언의 번호를 입력해주세요.")
            return
        }

        val id = idStr.toIntOrNull()
        if (id == null) {
            println("올바른 번호를 입력해주세요.")
            return
        }

        val deleted = service.deleteById(id)
        if (deleted) {
            println("${id}번 명언이 삭제되었습니다.")
        } else {
            println("${id}번 명언은 존재하지 않습니다.")
        }
    }

    fun actionModify(params: Map<String, String>) {
        val idStr = params["id"]
        if (idStr == null) {
            println("수정할 명언의 번호를 입력해주세요.")
            return
        }

        val id = idStr.toIntOrNull()
        if (id == null) {
            println("올바른 번호를 입력해주세요.")
            return
        }

        val wiseSaying = service.findById(id)
        if (wiseSaying == null) {
            println("${id}번 명언은 존재하지 않습니다.")
            return
        }

        println("명언(기존) : ${wiseSaying.content}")
        print("명언 : ")
        val newContent = scanner.nextLine()
        
        println("작가(기존) : ${wiseSaying.author}")
        print("작가 : ")
        val newAuthor = scanner.nextLine()

        service.update(id, newContent, newAuthor)
    }

    fun actionBuild() {
        service.buildDataJson()
        println("data.json 파일의 내용이 갱신되었습니다.")
    }
}
