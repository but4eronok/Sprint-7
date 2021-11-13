package ru.sber.orm.entities

import javax.persistence.*

@Entity
class Person(
    @Id
    @GeneratedValue
    var id: Long = 0,

    @Column(name = "first_name")
    var name: String,

    @ManyToOne (
        cascade = [CascadeType.ALL],
        fetch = FetchType.EAGER
            )
    var family: Family,

    @OneToOne(
        cascade = [CascadeType.ALL],
        fetch = FetchType.EAGER
    )
    val hobby: Hobby,
) {
    override fun toString(): String {
        return "Person(id='$id', name='$name', family = '${family.name}', hobby = '${hobby.name})"
    }
}