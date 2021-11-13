package ru.sber.orm

import ru.sber.orm.entities.Family
import ru.sber.orm.entities.Hobby
import ru.sber.orm.entities.Person
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration


fun main() {
    val sessionFactory = Configuration().configure()
        .addAnnotatedClass(Person::class.java)
        .addAnnotatedClass(Family::class.java)
        .addAnnotatedClass(Hobby::class.java)
        .buildSessionFactory()

    sessionFactory.use { sessionFactory ->
        val dao = PersonDAO(sessionFactory)
        val family = Family(name = "Smith")

        val person1 = Person(
            name = "John",
            family = family,
            hobby = Hobby(name = "Football")
        )

        val person2 = Person(
            name = "Anna",
            family = family,
            hobby = Hobby(name = "Hockey")
        )

        val person3 = Person(
            name = "Jack",
            family = family,
            hobby = Hobby(name = "Box")
        )

        dao.save(person1)
        dao.save(person2)
        dao.save(person3)

    }
}

class PersonDAO(
    private val sessionFactory: SessionFactory
) {
    fun save(person: Person) {
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            session.save(person)
            session.transaction.commit()
        }
    }

    fun find(id: Long): Person? {
        val result: Person?
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            result = session.get(Person::class.java, id)
            session.transaction.commit()
        }
        return result
    }

    fun findFamily(name: String): Family? {
        val result: Family?
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            result = session.get(Family::class.java, name)
            session.transaction.commit()
        }
        return result
    }


    fun findAll(): List<Person>{
        val result: List<Person>
        sessionFactory.openSession().use { session ->
            session.beginTransaction()
            result = session.createQuery("from Person").list() as List<Person>
            session.transaction.commit()
        }
        return result
    }
}
