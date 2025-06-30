package com.example.pizzeriaserver.SqlDataBase.SqlObjects

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.json.JSONArray
import org.json.JSONObject

object AccountSql : Table() {
    val accountType = text("accountType")
    val balance = double("balance")
    val initialAmount = double("initialAmount")
    val name = text("name")
    val isDeleted = bool("isDeleted").default(false)
    val createdTime = long("createdTime")
    val serverCreatedTime = long("serverCreatedTime")

    fun save(obj: JSONObject) {
        val cTime = obj.getLong("createdTime")
        transaction {
            AccountSql.deleteWhere { AccountSql.createdTime eq cTime }

            AccountSql.insert {
                it[accountType] = obj.getString("accountType")
                it[balance] = obj.getDouble("balance")
                it[initialAmount] = obj.getDouble("initialAmount")
                it[name] = obj.getString("name")
                it[isDeleted] = obj.getBoolean("isDeleted")
                it[createdTime] = obj.getLong("createdTime")
                it[serverCreatedTime] = System.currentTimeMillis()
            }
        }

        println("✅ Saved to SQLite with new Account")
    }

    fun get(syncTime: Long): JSONArray {
        val results = transaction {
            AccountSql
                .selectAll()
                .where { serverCreatedTime greater syncTime }
                .map {
                    JSONObject().apply {
                        put("accountType", it[accountType])
                        put("balance", it[balance])
                        put("initialAmount", it[initialAmount])
                        put("name", it[name])
                        put("isDeleted", it[isDeleted])
                        put("createdTime", it[createdTime])
                    }
                }
        }

        return JSONArray(results)
    }
}