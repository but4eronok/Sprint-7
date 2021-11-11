package com.example.demo.persistance


import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class EntityRepositoryTest {
    @Autowired
    private lateinit var entityRepository: EntityRepository

    @Test
    fun `save and find entity`(){
        val saved = entityRepository.save(Entity(name = "Kirill"))

        val founded = entityRepository.findById(saved.id!!)

        assertTrue(founded.get() == saved)
    }
}