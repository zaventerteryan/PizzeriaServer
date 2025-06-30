package com.example.pizzeriaserver.SqlDataBase.SqlObjects

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.json.JSONArray
import org.json.JSONObject

object ProductIngredientSql: Table() {
    val ingredientType = integer("ingredientType")
    val value = double("value")
    val isDeleted = bool("isDeleted").default(false)
    val createdTime = long("createdTime")
    val serverCreatedTime = long("serverCreatedTime")
    val ingredient = long("ingredient")

    fun save(obj: JSONObject){
        val cTime = obj.getLong("createdTime")
        transaction {
            ProductIngredientSql.deleteWhere { ProductIngredientSql.createdTime eq cTime }

            ProductIngredientSql.insert {
                it[ingredientType] = obj.getInt("ingredientType")
                it[value] = obj.getDouble("value")
                it[ingredient] = obj.optLong("ingredient", 0)
                it[isDeleted] = obj.getBoolean("isDeleted")
                it[createdTime] = obj.getLong("createdTime")
                it[serverCreatedTime] = System.currentTimeMillis()
            }
        }

        println("✅ Saved to SQLite with new ProductIngredientSql")
    }

    fun get(syncTime: Long): JSONArray {
        val results = transaction {
            ProductIngredientSql
                .selectAll()
                .where { serverCreatedTime greater syncTime }
                .map {
                    JSONObject().apply {
                        put("ingredientType", it[ingredientType])
                        put("value", it[value])
                        put("ingredient", it[ingredient])
                        put("isDeleted", it[isDeleted])
                        put("createdTime", it[createdTime])
                    }
                }
        }

        return JSONArray(results)
    }
}