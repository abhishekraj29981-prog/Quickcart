package com.example.data

import com.example.BuildConfig
import com.example.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiMealAssistant {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun generateRecipeAndProducts(prompt: String, availableProducts: List<Product>): RecipeSuggestionResult = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalRecipeSuggestion(prompt, availableProducts)
        }

        val catalogListText = availableProducts.joinToString("\n") { "- ${it.name} (ID: ${it.id}, Category: ${it.categoryName}, Price: ₹${it.price})" }
        val systemPrompt = """
            You are QuickCart AI Meal Assistant. The user asks: "$prompt".
            Suggest a dish or shopping bundle and select the matching product IDs from our catalog below.

            Catalog Products:
            $catalogListText

            Your output MUST strictly be in this text format:
            RECIPE_TITLE: <Short Dish Title>
            EXPLANATION: <2 sentence quick cooking tip or summary>
            PRODUCT_IDS: <comma separated list of product IDs from the catalog that match>
        """.trimIndent()

        try {
            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", systemPrompt)
                            })
                        })
                    })
                })
            }

            val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val jsonResp = JSONObject(responseBody)
                val candidates = jsonResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text", "")
                        return@withContext parseGeminiRecipeOutput(text, availableProducts)
                    }
                }
            }
            generateLocalRecipeSuggestion(prompt, availableProducts)
        } catch (e: Exception) {
            generateLocalRecipeSuggestion(prompt, availableProducts)
        }
    }

    private fun parseGeminiRecipeOutput(rawText: String, availableProducts: List<Product>): RecipeSuggestionResult {
        var title = "Smart Recipe Pick"
        var explanation = "Here are top ingredients selected from QuickCart dark store for your recipe."
        val matchedIds = mutableListOf<String>()

        rawText.lines().forEach { line ->
            when {
                line.startsWith("RECIPE_TITLE:") -> title = line.removePrefix("RECIPE_TITLE:").trim()
                line.startsWith("EXPLANATION:") -> explanation = line.removePrefix("EXPLANATION:").trim()
                line.startsWith("PRODUCT_IDS:") -> {
                    val ids = line.removePrefix("PRODUCT_IDS:").split(",").map { it.trim() }
                    matchedIds.addAll(ids)
                }
            }
        }

        val matchedProducts = availableProducts.filter { matchedIds.contains(it.id) }
        val finalProducts = if (matchedProducts.isNotEmpty()) matchedProducts else availableProducts.take(3)

        return RecipeSuggestionResult(
            title = title,
            explanation = explanation,
            recommendedProducts = finalProducts
        )
    }

    private fun generateLocalRecipeSuggestion(prompt: String, availableProducts: List<Product>): RecipeSuggestionResult {
        val lower = prompt.lowercase()
        return when {
            lower.contains("paneer") || lower.contains("butter masala") -> {
                RecipeSuggestionResult(
                    title = "Quick Paneer Butter Masala Kit",
                    explanation = "Classic North Indian curry with rich butter gravy and soft paneer.",
                    recommendedProducts = availableProducts.filter { it.id in listOf("p204", "p202", "p102", "p101") }
                )
            }
            lower.contains("breakfast") || lower.contains("morning") -> {
                RecipeSuggestionResult(
                    title = "High Energy Morning Breakfast",
                    explanation = "Protein packed eggs, whole wheat bread, fresh bananas and toned milk.",
                    recommendedProducts = availableProducts.filter { it.id in listOf("p203", "p205", "p104", "p201") }
                )
            }
            lower.contains("snack") || lower.contains("party") || lower.contains("chips") -> {
                RecipeSuggestionResult(
                    title = "10-Min Party Snack Combo",
                    explanation = "Crunchy chips, cold Coke and salted roasted almonds for quick chilling.",
                    recommendedProducts = availableProducts.filter { it.id in listOf("p401", "p501", "p404", "p402") }
                )
            }
            else -> {
                RecipeSuggestionResult(
                    title = "10-Min Fast Gourmet Meal",
                    explanation = "Hot instant noodles with fresh capsicum and chilled fruit juice.",
                    recommendedProducts = availableProducts.filter { it.id in listOf("p301", "p103", "p502") }
                )
            }
        }
    }
}

data class RecipeSuggestionResult(
    val title: String,
    val explanation: String,
    val recommendedProducts: List<Product>
)
