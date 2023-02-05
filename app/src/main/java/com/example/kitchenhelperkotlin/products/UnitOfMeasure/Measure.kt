package com.example.kitchenhelperkotlin.products.UnitOfMeasure

enum class Measure(val unit: String) {

    /*
    TODO: Узнать как использовать строки из resources.
     Resources.getSystem().getString(R.string.kg) не работает
     */
    kg("Кг"),
    g("г"),
    l("Л"),
    ml("мЛ"),
    stuff("Шт"),
    pack("Уп");
}