package com.example.tcapp.model

class GeneralModel {
    class Keyword{
        public var id:String?=null;
        public var name:String?=null;
        constructor(){}
        constructor(id: String?, name: String?) {
            this.id = id
            this.name = name
        }
    }
    class Keywords{
        public var keywords:ArrayList<Keyword>?=null;
        constructor(){}
        constructor(keywords: ArrayList<Keyword>?) {
            this.keywords = keywords
        }
    }

}