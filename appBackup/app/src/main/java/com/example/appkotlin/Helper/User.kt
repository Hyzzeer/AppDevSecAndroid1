package com.example.appkotlin.Helper

abstract class User {

    private var id : Int
    private var name : String
    private var lastname: String
    private var pin: Int?

    constructor(id:Int, name:String, lastname:String){
        this.id = id
        this.name = name
        this.lastname = lastname
        this.pin = null
    }

}