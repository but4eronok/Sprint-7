package ru.sber.rdbms

import java.sql.DriverManager
import java.sql.SQLException

class TransferPessimisticLock {
    private val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "postgres"
    )

    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        connection.use { conn ->
            try {
                conn.autoCommit = false
                val currentAmount = 0L

                val preparedStatement1 = conn.prepareStatement("SELECT * FROM account1 WHERE id = ?")
                preparedStatement1.use { stmnt ->
                    stmnt.setLong(1, accountId1)
                    stmnt.executeQuery().use { resultSet ->
                        resultSet.next()
                        if (currentAmount - amount < 0) {
                            throw SQLException("not enough money")
                        }
                    }

                    val preparedStatement2 =
                        conn.prepareStatement("SELECT * FROM account1 WHERE id IN (?,?) FOR UPDATE")
                    preparedStatement1.use { stmnt ->
                        stmnt.setLong(1, accountId1)
                        stmnt.setLong(2, accountId2)
                        stmnt.executeQuery()
                    }

                    val preparedStatement3 =
                        conn.prepareStatement("UPDATE account1 SET amount = amount - ? WHERE id = ?")
                    preparedStatement2.use { stmnt ->
                        stmnt.setLong(1, amount)
                        stmnt.setLong(2, accountId1)
                        stmnt.executeUpdate()
                    }

                    val preparedStatement4 =
                        conn.prepareStatement("UPDATE account1 SET amount = amount + ? WHERE id = ?")
                    preparedStatement4.use { stmnt ->
                        stmnt.setLong(1, amount)
                        stmnt.setLong(2, accountId2)
                        stmnt.executeUpdate()
                    }
                    conn.commit()
                }
            } catch (exception: Exception) {
                println(exception.message)
                conn.rollback()
            } finally {
                conn.autoCommit = true
            }
        }
    }
}
