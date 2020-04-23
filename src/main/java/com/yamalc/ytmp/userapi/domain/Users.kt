package com.yamalc.ytmp.userapi.domain

import java.util.*


class Users {
    var user_id: String = ""
        get() = field
        set(value) {
            field = value
        }
    var password: String = ""
        get() = field
        set(value) {
            field = value
        }
    var insert_date: Date = Date()
        get() = field
        set(value) {
            field = value
        }
    var update_date: Date = Date()
        get() = field
        set(value) {
            field = value
        }
}