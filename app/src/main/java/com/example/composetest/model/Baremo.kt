package com.example.composetest.model

class Baremo(
    val id: String,
    val valores: Array<Int>
) {

    companion object {
        fun empty(id: String) : Baremo = Baremo(id, emptyArray())
    }

    override fun equals(other: Any?): Boolean = (other as? Baremo)?.id == id

    override fun hashCode(): Int = id.hashCode()
}