package com.example.pizzeriaserver.SqlDataBase.SqlObjects

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.json.JSONArray
import org.json.JSONObject

object IngredientSql : Table() {
    val amount = double("amount")
    val companyName = text("companyName")
    val name = text("name")
    val price = double("price")
    val type = integer("type")
    val isDeleted = bool("isDeleted").default(false)
    val createdTime = long("createdTime")
    val serverCreatedTime = long("serverCreatedTime")

    fun save(obj: JSONObject){
        val cTime = obj.getLong("createdTime")
        transaction {
            IngredientSql.deleteWhere { IngredientSql.createdTime eq cTime }

            IngredientSql.insert {
                it[amount] = obj.getDouble("amount")
                it[companyName] = obj.getString("companyName")
                it[name] = obj.getString("name")
                it[price] = obj.getDouble("price")
                it[type] = obj.getInt("type")
                it[isDeleted] = obj.getBoolean("isDeleted")
                it[createdTime] = obj.getLong("createdTime")
                it[serverCreatedTime] = System.currentTimeMillis()
            }
        }

        println("✅ Saved to SQLite with new IngredientSql")
    }

    fun get(syncTime: Long): JSONArray {
        val results = transaction {
            IngredientSql
                .selectAll()
                .where { serverCreatedTime greater syncTime }
                .map {
                    JSONObject().apply {
                        put("amount", it[amount])
                        put("companyName", it[companyName])
                        put("name", it[name])
                        put("price", it[price])
                        put("type", it[type])
                        put("isDeleted", it[isDeleted])
                        put("createdTime", it[createdTime])
                    }
                }
        }

        return JSONArray(results)
    }
}