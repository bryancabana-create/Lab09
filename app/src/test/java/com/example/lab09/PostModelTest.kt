package com.example.lab09

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class PostModelTest {

    @Test
    fun testPostModelJsonParsing() {
        val json = """
            {
                "userId": 1,
                "id": 10,
                "title": "Test Title",
                "body": "Test Body Content",
                "link": "https://example.com",
                "comment_count": 5
            }
        """.trimIndent()

        val gson = Gson()
        val post = gson.fromJson(json, PostModel::class.java)

        assertNotNull(post)
        assertEquals(1, post.userId)
        assertEquals(10, post.id)
        assertEquals("Test Title", post.title)
        assertEquals("Test Body Content", post.body)
        assertEquals("https://example.com", post.link)
        assertEquals(5, post.commentCount)
    }
}
