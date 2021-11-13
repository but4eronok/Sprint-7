package ru.sber.rdbms

import java.sql.DriverManager
import java.sql.SQLException

class TransferConstraint {
    private val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "postgres"
    )

    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        connection.use { conn ->
            try {
                val prepareStatement1 = conn.prepareStatement("UPDATE account1 SET amount = amount - ? WHERE id = ?")
                prepareStatement1.use { statement ->
                    statement.setLong(1, amount)
                    statement.setLong(2, accountId1)
                    statement.executeUpdate()
                }


                val prepareStatement2 = conn.prepareStatement("UPDATE account1 SET amount = amount - ? WHERE id = ?")
                prepareStatement2.use { statement ->
                    statement.setLong(1, amount)
                    statement.setLong(2, accountId2)
                    statement.executeUpdate()
                }

            } catch (ex: SQLException) {
                println(ex.message)
            }
        }
    }
}
