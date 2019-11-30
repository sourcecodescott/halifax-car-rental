package com.csci4176.halifaxcarrental

class User {
    var username: String? = null
    var password: String? = null
    var isadmin: Boolean = false
        set(isamin) {
            field = this.isadmin
        }

    var profileImageUrl: String? = null
}
