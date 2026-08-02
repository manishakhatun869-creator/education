package com.towfikedutips.app.data

import android.content.Context
import android.content.SharedPreferences
import com.towfikedutips.app.model.SavedItem
import com.towfikedutips.app.model.DownloadedPdf
import org.json.JSONArray
import org.json.JSONObject

class LocalStorageManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("towfik_edutips_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SAVED_ITEMS = "towfik_saved_items_v1"
        private const val KEY_DARK_MODE = "towfik_dark_mode"
    }

    fun isSaved(itemId: String): Boolean {
        return getSavedItems().any { it.itemId == itemId }
    }

    fun getSavedItems(): List<SavedItem> {
        val jsonString = prefs.getString(KEY_SAVED_ITEMS, null) ?: return emptyList()
        val list = mutableListOf<SavedItem>()
        try {
            val arr = JSONArray(jsonString)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    SavedItem(
                        id = obj.optString("id"),
                        itemId = obj.optString("itemId"),
                        itemType = obj.optString("itemType"),
                        title = obj.optString("title"),
                        subtitle = obj.optString("subtitle", null),
                        chapterId = obj.optString("chapterId", null),
                        subjectId = obj.optString("subjectId", null),
                        savedAt = obj.optString("savedAt")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveItem(item: SavedItem) {
        val current = getSavedItems().toMutableList()
        // Remove if exists
        current.removeAll { it.itemId == item.itemId }
        current.add(0, item)
        saveList(current)
    }

    fun removeItem(itemId: String) {
        val current = getSavedItems().toMutableList()
        current.removeAll { it.itemId == itemId }
        saveList(current)
    }

    private fun saveList(list: List<SavedItem>) {
        val arr = JSONArray()
        list.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("itemId", item.itemId)
            obj.put("itemType", item.itemType)
            obj.put("title", item.title)
            obj.put("subtitle", item.subtitle)
            obj.put("chapterId", item.chapterId)
            obj.put("subjectId", item.subjectId)
            obj.put("savedAt", item.savedAt)
            arr.put(obj)
        }
        prefs.edit().putString(KEY_SAVED_ITEMS, arr.toString()).apply()
    }

    fun isDarkMode(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun getDownloadedPdfs(): List<DownloadedPdf> {
        val jsonString = prefs.getString("towfik_downloaded_pdfs", null) ?: return emptyList()
        val list = mutableListOf<DownloadedPdf>()
        try {
            val arr = JSONArray(jsonString)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    DownloadedPdf(
                        id = obj.optString("id"),
                        title = obj.optString("title"),
                        pdfUrl = obj.optString("pdfUrl"),
                        downloadedAt = obj.optString("downloadedAt"),
                        size = obj.optString("size")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveDownloadedPdf(pdf: DownloadedPdf) {
        val current = getDownloadedPdfs().toMutableList()
        current.removeAll { it.id == pdf.id }
        current.add(0, pdf)
        saveDownloadedList(current)
    }

    fun removeDownloadedPdf(pdfId: String) {
        val current = getDownloadedPdfs().toMutableList()
        current.removeAll { it.id == pdfId }
        saveDownloadedList(current)
    }

    private fun saveDownloadedList(list: List<DownloadedPdf>) {
        val arr = JSONArray()
        list.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("title", item.title)
            obj.put("pdfUrl", item.pdfUrl)
            obj.put("downloadedAt", item.downloadedAt)
            obj.put("size", item.size)
            arr.put(obj)
        }
        prefs.edit().putString("towfik_downloaded_pdfs", arr.toString()).apply()
    }
}
