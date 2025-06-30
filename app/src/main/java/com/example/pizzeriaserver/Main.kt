package com.example.pizzeriaserver

import com.example.pizzeriaserver.Managers.GetManager
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.AccountSql
import com.example.pizzeriaserver.SqlDataBase.DBManager
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.CategorySql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.IngredientSql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.ProductIngredientSql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.ProductSql
import com.example.pizzeriaserver.SqlDataBase.SqlObjects.RecordSql
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*

fun main() {
    DBManager.start()
    embeddedServer(Netty, port = 8081) {
        install(ContentNegotiation) {
            json()
        }

        routing {
            get("/getData") {
                try {
                    val syncTimeParam = call.request.queryParameters["syncTime"]
                    val syncTime = syncTimeParam?.toLongOrNull()

                    if (syncTime == null) {
                        call.respond(mapOf("error" to "Missing syncTime"))
                        return@get
                    }

                    val result = GetManager.getData(syncTime)
                    println("✅ Send to App:\n$result")
                    call.respond(result)
                } catch (ex: Exception){
                    println("❌ Failed: ${ex.message}")
                    ex.printStackTrace()

                    call.respondText(
                        ex.localizedMessage,
                        status = HttpStatusCode.InternalServerError
                    )
                }
            }
            post("/sync") {
                val json = call.receiveText()
                println("✅ Received from App new Records:\n$json")
                try {
                    GetManager.save(json)
                    call.respond(mapOf("data" to mapOf("success" to true)))
                } catch (ex: Exception) {
                    println("❌ Failed: ${ex.message}")
                    ex.printStackTrace()
                    call.respond(mapOf("data" to mapOf("success" to false), "error" to ex.message))
                }
            }
        }
    }.start(wait = true)
}