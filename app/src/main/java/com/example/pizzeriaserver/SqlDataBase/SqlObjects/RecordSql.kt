package com.example.pizzeriaserver.SqlDataBase.SqlObjects

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.json.JSONArray
import org.json.JSONObject

object RecordSql: Table() {
    val costPrice = double("costPrice")
    val count = integer("count")
    val note = text("note")
    val salePrice = double("salePrice")
    val time = long("time")
    val type = integer("type")
    val account = long("account")
    val product = long("product")
    val isDeleted = bool("isDeleted").default(false)
    val createdTime = long("createdTime")
    val serverCreatedTime = long("serverCreatedTime")

    fun save(obj: JSONObject){
        val cTime = obj.getLong("createdTime")
        transaction {
            RecordSql.deleteWhere { RecordSql.createdTime eq cTime }

            RecordSql.insert {
                it[costPrice] = obj.getDouble("costPrice")
                it[count] = obj.getInt("count")
                it[note] = obj.getString("note")
                it[salePrice] = obj.getDouble("salePrice")
                it[time] = obj.getLong("time")
                it[type] = obj.getInt("type")
                it[account] = obj.optLong("account", 0)
                it[product] = obj.optLong("product", 0)
                it[isDeleted] = obj.getBoolean("isDeleted")
                it[createdTime] = obj.getLong("createdTime")
                it[serverCreatedTime] = System.currentTimeMillis()
            }
        }

        println("✅ Saved to SQLite with new RecordSql")
    }

    fun get(syncTime: Long): JSONArray {
        val results = transaction {
            RecordSql
                .selectAll()
                .where { serverCreatedTime greater syncTime }
                .map {
                    JSONObject().apply {
                        put("costPrice", it[costPrice])
                        put("count", it[count])
                        put("note", it[note])
                        put("salePrice", it[salePrice])
                        put("time", it[time])
                        put("type", it[type])
                        put("account", it[account])
                        put("product", it[product])
                        put("isDeleted", it[isDeleted])
                        put("createdTime", it[createdTime])
                    }
                }
        }

        return JSONArray(results)
    }
}