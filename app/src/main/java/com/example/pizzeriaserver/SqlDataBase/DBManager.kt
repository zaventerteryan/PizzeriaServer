package com.example.pizzeriaserver.SqlDataBase

import com.example.pizzeriaserver.SqlDataBase.SqlObjects.AccountSql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.CategorySql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.IngredientSql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.ProductIngredientSql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.ProductSql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.RecordSql
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DBManager {
    private const val dbPath: String = "app/data.db"
    private var isStarted: Boolean = false

    fun start() {
        if (isStarted){
            return
        }

        synchronized(this){
            if (isStarted){
                return
            }
            initDatabase()
            isStarted = true
        }
    }

    private fun initDatabase() {
        try {
            val dbUrl = File(dbPath)
            val dbPath = dbUrl.absolutePath
            println("ℹ️ Connecting to SQLite at: $dbPath")

            Database.connect("jdbc:sqlite:$dbPath", driver = "org.sqlite.JDBC")
            this.onCreate()

            println("✅ DB initialized")
        } catch (e: Exception) {
            println("❌ DB failed: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun onCreate() {
        transaction {
            SchemaUtils.create(AccountSql)
            SchemaUtils.create(IngredientSql)
            SchemaUtils.create(ProductIngredientSql)
            SchemaUtils.create(CategorySql)
            SchemaUtils.create(ProductSql)
            SchemaUtils.create(RecordSql)
        }
    }
}