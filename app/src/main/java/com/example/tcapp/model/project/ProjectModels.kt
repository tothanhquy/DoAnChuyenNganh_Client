package com.example.tcapp.model.project

class ProjectModels {
    class CategoryKeyword{
        public var id:String?=null;
        public var name:String?=null;

        constructor(id: String?, name: String?) {
            this.id = id
            this.name = name
        }
        constructor(){}
    }
    class CategoryKeywords{
        public var keywords:ArrayList<CategoryKeyword>?=null;
        public var isLeader:Boolean=false;
        constructor(){}
        constructor(keywords: ArrayList<CategoryKeyword>?, isLeader: Boolean) {
            this.keywords = keywords
            this.isLeader = isLeader
        }
    }
    class MemberNow{
        public var id:String?=null;
        public var name:String?=null;
        public var avatar:String?=null;
        public var role:String?=null;
        public var isLeader:Boolean=false;

        constructor(id: String?, name: String?, avatar: String?, role: String?, isLeader: Boolean) {
            this.id = id
            this.name = name
            this.avatar = avatar
            this.role = role
            this.isLeader = isLeader
        }
        constructor(){}
    }
    class MembersNow{
        public var members:ArrayList<MemberNow>?=null;
        public var isLeader:Boolean=false;
        constructor(){}
        constructor(members: ArrayList<MemberNow>?, isLeader: Boolean) {
            this.members = members
            this.isLeader = isLeader
        }
    }
    class MemberHistory{
        public var id:String?=null;
        public var name:String?=null;
        public var avatar:String?=null;
        public var role:String?=null;
        public var time:Long=0L;
        public var isOut:Boolean=false;
        constructor(){}
        constructor(
            id: String?,
            name: String?,
            avatar: String?,
            role: String?,
            time: Long,
            isOut: Boolean
        ) {
            this.id = id
            this.name = name
            this.avatar = avatar
            this.role = role
            this.time = time
            this.isOut = isOut
        }
    }
    class MembersHistory{
        public var members:ArrayList<MemberHistory>?=null;
        public var isLeader:Boolean=false;
        constructor(){}
        constructor(members: ArrayList<MemberHistory>?, isLeader: Boolean) {
            this.members = members
            this.isLeader = isLeader
        }
    }
    class ProjectDetails{
        public var projectId:String?=null;
        public var projectName:String?=null;
        public var projectAvatar:String?=null;
        public var leaderId:String?=null;
        public var leaderName:String?=null;
        public var leaderAvatar:String?=null;
        public var slogan:String?=null;
        public var description:String?=null;
        public var relationship:String?=null;
        public lateinit var members:ArrayList<MemberNow>;
        public var invitingMembersNumber:Int=0;
        public lateinit var categoryKeywords:ArrayList<CategoryKeyword>;
        public lateinit var tags:ArrayList<String>;
        public var followsNumber:Int=0;
        public var isFollow:Boolean=false;
        public var imagesNumber:Int=0;
        public var videosNumber:Int=0;
        public var voteStar:Float=0F;
        public var reportsNumber:Int=0;
        constructor(){
            members = arrayListOf();
            categoryKeywords = arrayListOf();
            tags = arrayListOf();
        }
        constructor(
            projectId: String?,
            projectName: String?,
            projectAvatar: String?,
            leaderId: String?,
            leaderName: String?,
            leaderAvatar: String?,
            slogan: String?,
            description: String?,
            relationship: String?
        ) {
            this.projectId = projectId
            this.projectName = projectName
            this.projectAvatar = projectAvatar
            this.leaderId = leaderId
            this.leaderName = leaderName
            this.leaderAvatar = leaderAvatar
            this.slogan = slogan
            this.description = description
            this.relationship = relationship
        }

        constructor(
            projectId: String?,
            projectName: String?,
            projectAvatar: String?,
            leaderId: String?,
            leaderName: String?,
            leaderAvatar: String?,
            slogan: String?,
            description: String?,
            relationship: String?,
            members: ArrayList<MemberNow>,
            invitingMembersNumber: Int,
            categoryKeywords: ArrayList<CategoryKeyword>,
            tags: ArrayList<String>,
            followsNumber: Int,
            isFollow: Boolean,
            imagesNumber: Int,
            videosNumber: Int,
            voteStar: Float,
            reportsNumber: Int
        ) {
            this.projectId = projectId
            this.projectName = projectName
            this.projectAvatar = projectAvatar
            this.leaderId = leaderId
            this.leaderName = leaderName
            this.leaderAvatar = leaderAvatar
            this.slogan = slogan
            this.description = description
            this.relationship = relationship
            this.members = members
            this.invitingMembersNumber = invitingMembersNumber
            this.categoryKeywords = categoryKeywords
            this.tags = tags
            this.followsNumber = followsNumber
            this.isFollow = isFollow
            this.imagesNumber = imagesNumber
            this.videosNumber = videosNumber
            this.voteStar = voteStar
            this.reportsNumber = reportsNumber
        }

    }
    class ProjectEditBasicInfo{
        public var name:String?=null;
        public var slogan:String?=null;
        public var description:String?=null;
        constructor(){}
        constructor(name: String?, slogan: String?, description: String?) {
            this.name = name
            this.slogan = slogan
            this.description = description
        }

    }
    class ProjectListItem{
        public var projectId:String?=null;
        public var projectName:String?=null;
        public var projectAvatar:String?=null;
        public var leaderId:String?=null;
        public var leaderName:String?=null;
        public var leaderAvatar:String?=null;
        public var memberNumber:Int=0;
        public var isLeader:Boolean=false;
        constructor(){}
        constructor(
            projectId: String?,
            projectName: String?,
            projectAvatar: String?,
            leaderId: String?,
            leaderName: String?,
            leaderAvatar: String?,
            memberNumber: Int,
            isLeader: Boolean
        ) {
            this.projectId = projectId
            this.projectName = projectName
            this.projectAvatar = projectAvatar
            this.leaderId = leaderId
            this.leaderName = leaderName
            this.leaderAvatar = leaderAvatar
            this.memberNumber = memberNumber
            this.isLeader = isLeader
        }

    }
    class MyProjectsAndRequest{
        public var projects:ArrayList<ProjectListItem>?=null;
        public var invitingRequestNumber:Int=0;
        constructor(){
            projects = arrayListOf();
        }
        constructor(projects: ArrayList<ProjectListItem>?, invitingRequestNumber: Int) {
            this.projects = projects
            this.invitingRequestNumber = invitingRequestNumber
        }
    }
    class InvitingMember {
        public var id:String?=null;
        public var name:String?=null;
        public var avatar:String?=null;
        public var role:String?=null;
        public var time:Long=0L;
        constructor(){}
        constructor(id: String?, name: String?, avatar: String?, role: String?, time: Long) {
            this.id = id
            this.name = name
            this.avatar = avatar
            this.role = role
            this.time = time
        }
    }
    class InvitingProject {
        public var id:String?=null;
        public var name:String?=null;
        public var avatar:String?=null;
        public var role:String?=null;
        public var time:Long=0L;
        constructor(){}
        constructor(id: String?, name: String?, avatar: String?, role: String?, time: Long) {
            this.id = id
            this.name = name
            this.avatar = avatar
            this.role = role
            this.time = time
        }
    }
    class InvitingMembers{
        public var invitingMembers:ArrayList<InvitingMember>?=null;
        constructor(){
            invitingMembers= arrayListOf()
        }
        constructor(invitingMembers: ArrayList<InvitingMember>?) {
            this.invitingMembers = invitingMembers
        }
    }
    class InvitingProjects{
        public var invitingProjects:ArrayList<InvitingProject>?=null;
        constructor(){
            invitingProjects= arrayListOf()
        }
        constructor(invitingProjects: ArrayList<InvitingProject>?) {
            this.invitingProjects = invitingProjects
        }
    }
    class GeneralNegativeReport{
        public var id:String?=null;
        public var number:Int=0;
        constructor(){}
        constructor(id: String?, number: Int) {
            this.id = id
            this.number = number
        }
    }
    class GeneralNegativeReports{
        public var reports:ArrayList<GeneralNegativeReport>?=null;
        constructor(){}
        constructor(reports: ArrayList<GeneralNegativeReport>?) {
            this.reports = reports
        }
    }
    class Tags{
        public var tags:ArrayList<String?>?=null;
        constructor(){}
        constructor(tags: ArrayList<String?>?) {
            this.tags = tags
        }

    }
    class Resource{
        public var path:String?=null;
        public var alt:String?=null;
        constructor(){}
        constructor(path: String?, alt: String?) {
            this.path = path
            this.alt = alt
        }
    }
    class Resources{
        public var resources:ArrayList<Resource>?=null;
        public var isLeader:Boolean=false;
        constructor(){}
        constructor(resources: ArrayList<Resource>?, isLeader: Boolean) {
            this.resources = resources
            this.isLeader = isLeader
        }
    }
}