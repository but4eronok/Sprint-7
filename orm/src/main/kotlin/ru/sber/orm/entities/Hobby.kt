package ru.sber.orm.entities

import javax.persistence.*

@Entity
class Hobby(
    @Id
    @GeneratedValue
    var id: Long = 0,
    var name: String,
)