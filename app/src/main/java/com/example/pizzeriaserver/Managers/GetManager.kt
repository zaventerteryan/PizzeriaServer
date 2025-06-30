package com.example.pizzeriaserver.Managers

import com.example.pizzeriaserver.SqlDataBase.SqlObjects.AccountSql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.CategorySql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.IngredientSql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.IngredientSql.default
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.ProductIngredientSql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.ProductSql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.RecordSql
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.json.JSONObject

object GetManager {
    fun getData(syncTime: Long): String {
        val accounts = AccountSql.get(syncTime)
        val ingredients = IngredientSql.get(syncTime)
        val productIngredients = ProductIngredientSql.get(syncTime)
        val categories = CategorySql.get(syncTime)
        val products = ProductSql.get(syncTime)
        val records = RecordSql.get(syncTime)
        val wrapper = JSONObject()
        val map = mutableMapOf<String, Any>()

        if (!accounts.isEmpty){
            map["accounts"] = accounts
        }
        if (!ingredients.isEmpty){
            map["ingredients"] = ingredients
        }
        if (!productIngredients.isEmpty){
            map["productIngredients"] = productIngredients
        }
        if (!categories.isEmpty){
            map["categories"] = categories
        }
        if (!products.isEmpty) {
            map["products"] = products
        }
        if (!records.isEmpty){
            map["records"] = records
        }

        wrapper.put("data", map)
        return wrapper.toString()
    }

    fun save(json: String){
        val obj = JSONObject(json)
        val accounts = obj.optJSONArray("accounts")
        if (accounts != null){
            for (i in 0 until accounts.length()){
                val account = accounts.getJSONObject(i)
                AccountSql.save(account)
            }
        }

        val ingredients = obj.optJSONArray("ingredients")
        if (ingredients != null){
            for (i in 0 until ingredients.length()){
                val ingredient = ingredients.getJSONObject(i)
                IngredientSql.save(ingredient)
            }
        }

        val productIngredients = obj.optJSONArray("productIngredients")
        if (productIngredients != null){
            for (i in 0 until productIngredients.length()){
                val productIngredient = productIngredients.getJSONObject(i)
                ProductIngredientSql.save(productIngredient)
            }
        }

        val categories = obj.optJSONArray("categories")
        if (categories != null){
            for (i in 0 until categories.length()){
                val category = categories.getJSONObject(i)
                CategorySql.save(category)
            }
        }

        val products = obj.optJSONArray("products")
        if (products != null){
            for (i in 0 until products.length()){
                val product = products.getJSONObject(i)
                ProductSql.save(product)
            }
        }

        val records = obj.optJSONArray("records")
        if (records != null){
            for (i in 0 until records.length()){
                val record = records.getJSONObject(i)
                RecordSql.save(record)
            }
        }

        println("✅ Saved to SQLite finish")
    }
}