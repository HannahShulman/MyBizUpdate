package com.app.mybiz.objects

import java.util.*

data class Category(val title: String = "",
                    val profileImage: String = "") {

    val subCategoroies = HashMap<String, Category>()
    val categoryTenders = HashMap<String, Tenders>()
}