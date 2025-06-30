package com.example.pizzeriaserver.SqlDataBase.SqlObjects

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.json.JSONArray
import org.json.JSONObject

object CategorySql: Table() {
    val name = text("name")
    val products = text("products")
    val isDeleted = bool("isDeleted").default(false)
    val createdTime = long("createdTime")
    val serverCreatedTime = long("serverCreatedTime")

    fun save(obj: JSONObject){
        val cTime = obj.getLong("createdTime")
        transaction {
            CategorySql.deleteWhere { CategorySql.createdTime eq cTime }

            CategorySql.insert {
                it[name] = obj.getString("name")
                it[products] = obj.optString("products", "")
                it[isDeleted] = obj.getBoolean("isDeleted")
                it[createdTime] = obj.getLong("createdTime")
                it[serverCreatedTime] = System.currentTimeMillis()
            }
        }

        println("✅ Saved to SQLite with new CategorySql")
    }

    fun get(syncTime: Long): JSONArray {
        val results = transaction {
            CategorySql
                .selectAll()
                .where { serverCreatedTime greater syncTime }
                .map {
                    JSONObject().apply {
                        put("name", it[name])
                        put("products", it[products])
                        put("isDeleted", it[isDeleted])
                        put("createdTime", it[createdTime])
                    }
                }
        }

        return JSONArray(results)
    }
}