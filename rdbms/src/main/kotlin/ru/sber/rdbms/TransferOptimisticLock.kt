package ru.sber.rdbms

import java.sql.DriverManager
import java.sql.SQLException


class TransferOptimisticLock {
    private val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/db",
        "postgres",
        "postgres"
    )

    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        connection.use { conn ->
            val autoCommit = conn.autoCommit
            try {
                conn.autoCommit = false
                var version: Long
                val currentAmount = 0L

                val prepareStatement1 = conn.prepareStatement("select * from account1 where id = ?")
                prepareStatement1.use { stmnt ->
                    stmnt.setLong(1, accountId1)
                    stmnt.executeQuery().use { resultSet ->
                        resultSet.next()
                        if (currentAmount - amount < 0) {
                            throw SQLException("not enough money")
                        }
                        version = resultSet.getLong("version")
                    }
                }

                val prepareStatement2 = conn.prepareStatement("select * from account1 where id = ?")
                prepareStatement2.use { stmnt ->
                    stmnt.setLong(1, accountId2)
                    stmnt.executeQuery().use { resultSet ->
                        resultSet.next()
                        version = resultSet.getLong("version")
                    }
                }

                val prepareStatement3 = conn.prepareStatement("update account1 set amount = amount - ?, version = version + 1 where id = ? and version = ?"
                )
                prepareStatement3.use { stmnt ->
                    stmnt.setLong(1, amount)
                    stmnt.setLong(2, accountId1)
                    stmnt.setLong(3, version)
                    val updatedRows = stmnt.executeUpdate()
                    if (updatedRows == 0)
                        throw SQLException("Concurrent update")
                }

                val prepareStatement4 = conn.prepareStatement("update account1 set amount = amount + ?, version = version + 1 where id = ? and version = ?"
                )
                prepareStatement4.use { statement ->
                    statement.setLong(1, amount)
                    statement.setLong(2, accountId2)
                    statement.setLong(3, version)
                    val updatedRows = statement.executeUpdate()
                    if (updatedRows == 0)
                        throw SQLException("Concurrent update")
                }
                conn.commit()
            } catch (exception: SQLException) {
                println(exception.message)
                conn.rollback()
            } finally {
                conn.autoCommit = autoCommit
            }
        }
    }
}
