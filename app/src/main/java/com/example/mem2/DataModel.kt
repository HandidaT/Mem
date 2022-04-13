package com.example.mem2

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class DataModel : RealmObject() {
    @PrimaryKey
    var _id: String?= null
    @Required
    //var title: String = ""
    var description: String? = null

}