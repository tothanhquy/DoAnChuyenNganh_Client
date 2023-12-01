package com.example.tcapp.model.search


class SearchModels {
    companion object Static {
        fun convertObjectTypeToString(type: ObjectType): String {
            return when (type) {
                ObjectType.Project -> "project"
                ObjectType.Team -> "team"
                else -> "user"
            };
        }

        fun convertStringToObjectType(type: String?): ObjectType {
            return when (type) {
                "project" -> ObjectType.Project
                "team" -> ObjectType.Team
                else -> ObjectType.User
            };
        }
    }
    enum class ObjectType{
        User,Team,Project
    }

    class SearchItem{
        public var id:String?="";
        public var name:String?="";
        public var avatar:String?="";
        public var type:ObjectType = ObjectType.User;

        constructor(id: String?, name: String?, avatar: String?, type: ObjectType) {
            this.id = id
            this.name = name
            this.avatar = avatar
            this.type = type
        }
        constructor(crude:SearchItemCrude) {
            this.id = crude.id
            this.name = crude.name
            this.avatar = crude.avatar
            this.type = convertStringToObjectType(crude.type)
        }
        constructor(){}
    }
    class SearchItemCrude{
        public var id:String?="";
        public var name:String?="";
        public var avatar:String?="";
        public var type:String?="";
        constructor(id: String?, name: String?, avatar: String?, type: String?) {
            this.id = id
            this.name = name
            this.avatar = avatar
            this.type = type
        }
        constructor(){}
    }
    class SearchItems{
        public var items:ArrayList<SearchItem>?= arrayListOf()

        constructor(items: ArrayList<SearchItem>?) {
            this.items = items
        }
        constructor(crude:SearchItemsCrude) {
            this.items = ArrayList(crude.items!!.map{SearchItem(it)})
        }
    }
    class SearchItemsCrude{
        public var items:ArrayList<SearchItemCrude>?= arrayListOf()

        constructor(items: ArrayList<SearchItemCrude>) {
            this.items = items
        }
        constructor(){}
    }
}