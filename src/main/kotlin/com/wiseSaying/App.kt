package com.wiseSaying

import java.util.*

class App {
    private val scanner = Scanner(System.`in`)
    private val repository = WiseSayingRepository()
    private val service = WiseSayingService(repository)
    private val controller = WiseSayingController(service, scanner)

    fun run() {
        println("== 명언 앱 ==")
        
        while (true) {
            print("명령) ")
            val input = scanner.nextLine()
            
            if (input == "종료") {
                break
            }
            
            val (command, params) = parseCommand(input)
            
            when (command) {
                "등록" -> controller.actionRegister()
                "목록" -> controller.actionList(params)
                "삭제" -> controller.actionDelete(params)
                "수정" -> controller.actionModify(params)
                "빌드" -> controller.actionBuild()
                "샘플데이터" -> {
                    service.createSampleData()
                    println("샘플 데이터가 생성되었습니다.")
                }
                else -> println("알 수 없는 명령입니다.")
            }
        }
    }

    private fun parseCommand(input: String): Pair<String, Map<String, String>> {
        if (!input.contains("?")) {
            return Pair(input, emptyMap())
        }

        val parts = input.split("?", limit = 2)
        val command = parts[0]
        val paramString = parts[1]

        val params = mutableMapOf<String, String>()
        paramString.split("&").forEach { param ->
            val keyValue = param.split("=", limit = 2)
            if (keyValue.size == 2) {
                params[keyValue[0]] = keyValue[1]
            }
        }

        return Pair(command, params)
    }
}
