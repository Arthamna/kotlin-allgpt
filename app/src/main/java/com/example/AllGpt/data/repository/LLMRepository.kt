package com.example.AllGpt.data.repository

import kotlinx.coroutines.delay
import javax.inject.Inject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query


class LLMRepository @Inject constructor() {
    private val apiKey = "" // fill the API key
    private val geminiApi = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeminiApiService::class.java)

    suspend fun generateResponse(userMessage: String): String {
        return try {
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = userMessage)
                        )
                    )
                )
            )

            val response = geminiApi.generateContent(
                modelName = "models/gemini-pro",
                apiKey = apiKey,
                request = request
            )

            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "Sorry, I couldn't generate a response."
        } catch (e: Exception) {
            "Error: ${e.message ?: "Unknown error occurred"}"
        }
    }
}

interface GeminiApiService {
    @POST("{modelName}:generateContent")
    suspend fun generateContent(
        @retrofit2.http.Path("modelName") modelName: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

data class GeminiRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GeminiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: Content
)
