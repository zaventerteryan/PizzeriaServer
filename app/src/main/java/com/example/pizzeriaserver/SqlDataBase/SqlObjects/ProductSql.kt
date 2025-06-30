package com.example.pizzeriaserver.SqlDataBase.SqlObjects

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.json.JSONArray
import org.json.JSONObject

object ProductSql: Table() {
    val color = text("color")
    val costPrice = double("costPrice")
    val name = text("name")
    val productCount = integer("productCount")
    val salePrice = double("salePrice")
    val category = long("category")
    val ingredients = text("ingredients")
    val isDeleted = bool("isDeleted").default(false)
    val createdTime = long("createdTime")
    val serverCreatedTime = long("serverCreatedTime")

    fun save(obj: JSONObject){
        val cTime = obj.getLong("createdTime")
        transaction {
            ProductSql.deleteWhere { ProductSql.createdTime eq cTime }

            ProductSql.insert {
                it[color] = obj.getString("color")
                it[costPrice] = obj.getDouble("costPrice")
                it[name] = obj.getString("name")
                it[productCount] = obj.getInt("productCount")
                it[salePrice] = obj.getDouble("salePrice")
                it[category] = obj.optLong("category", 0)
                it[ingredients] = obj.optString("ingredients", "")
                it[isDeleted] = obj.getBoolean("isDeleted")
                it[createdTime] = obj.getLong("createdTime")
                it[serverCreatedTime] = System.currentTimeMillis()
            }
        }

        println("✅ Saved to SQLite with new ProductSql")
    }

    fun get(syncTime: Long): JSONArray {
        val results = transaction {
            ProductSql
                .selectAll()
                .where { serverCreatedTime greater syncTime }
                .map {
                    JSONObject().apply {
                        put("color", it[color])
                        put("costPrice", it[costPrice])
                        put("name", it[name])
                        put("productCount", it[productCount])
                        put("salePrice", it[salePrice])
                        put("category", it[category])
                        put("ingredients", it[ingredients])
                        put("isDeleted", it[isDeleted])
                        put("createdTime", it[createdTime])
                    }
                }
        }

        return JSONArray(results)
    }
}