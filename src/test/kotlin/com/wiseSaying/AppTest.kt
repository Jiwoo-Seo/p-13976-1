package com.wiseSaying

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import java.io.*

class AppTest {
    private val originalOut = System.out
    private val originalIn = System.`in`
    private lateinit var output: ByteArrayOutputStream

    @BeforeEach
    fun setUp() {
        output = ByteArrayOutputStream()
        System.setOut(PrintStream(output))

        cleanupTestDb()
    }

    @AfterEach
    fun tearDown() {
        System.setOut(originalOut)
        System.setIn(originalIn)
        cleanupTestDb()
    }

    private fun cleanupTestDb() {
        val dbDir = File("db")
        if (dbDir.exists()) {
            dbDir.deleteRecursively()
        }
    }

    private fun runApp(input: String) {
        val inputStream = ByteArrayInputStream(input.toByteArray())
        System.setIn(inputStream)
        
        val app = App()
        app.run()
    }

    @Test
    @DisplayName("종료 명령어 테스트")
    fun t1() {
        runApp("종료")
        val result = output.toString()
        assert(result.contains("== 명언 앱 =="))
    }

    @Test
    @DisplayName("명언 등록 테스트")
    fun t2() {
        val input = """
            등록
            현재를 사랑하라.
            작자미상
            종료
        """.trimIndent()
        
        runApp(input)
        val result = output.toString()
        assert(result.contains("1번 명언이 등록되었습니다."))
    }

    @Test
    @DisplayName("명언 목록 테스트")
    fun t3() {
        val input = """
            등록
            현재를 사랑하라.
            작자미상
            등록
            과거에 집착하지 마라.
            작자미상
            목록
            종료
        """.trimIndent()
        
        runApp(input)
        val result = output.toString()
        assert(result.contains("번호 / 작가 / 명언"))
        assert(result.contains("작자미상"))
        assert(result.contains("현재를 사랑하라."))
        assert(result.contains("과거에 집착하지 마라."))
    }

    @Test
    @DisplayName("명언 삭제 테스트")
    fun t4() {
        val input = """
            등록
            현재를 사랑하라.
            작자미상
            삭제?id=1
            종료
        """.trimIndent()
        
        runApp(input)
        val result = output.toString()
        assert(result.contains("1번 명언이 삭제되었습니다."))
    }

    @Test
    @DisplayName("존재하지 않는 명언 삭제 테스트")
    fun t5() {
        val input = """
            삭제?id=999
            종료
        """.trimIndent()
        
        runApp(input)
        val result = output.toString()
        assert(result.contains("999번 명언은 존재하지 않습니다."))
    }

    @Test
    @DisplayName("명언 수정 테스트")
    fun t6() {
        val input = """
            등록
            현재를 사랑하라.
            작자미상
            수정?id=1
            현재와 자신을 사랑하라.
            홍길동
            종료
        """.trimIndent()
        
        runApp(input)
        val result = output.toString()
        assert(result.contains("명언(기존) : 현재를 사랑하라."))
        assert(result.contains("작가(기존) : 작자미상"))
    }

    @Test
    @DisplayName("빌드 명령어 테스트")
    fun t7() {
        val input = """
            등록
            현재를 사랑하라.
            작자미상
            빌드
            종료
        """.trimIndent()
        
        runApp(input)
        val result = output.toString()
        assert(result.contains("data.json 파일의 내용이 갱신되었습니다."))
    }
}
