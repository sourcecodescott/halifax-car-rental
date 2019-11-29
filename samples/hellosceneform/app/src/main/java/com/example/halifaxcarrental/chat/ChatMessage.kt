package com.example.halifaxcarrental.chat

class ChatMessage(val chat_Id: String, val text: String, val from_person: String, val to_person: String, val timestamp: Long, val image:Boolean, val format:String) {
  constructor() : this("", "", "", "", -1, false,"")
}