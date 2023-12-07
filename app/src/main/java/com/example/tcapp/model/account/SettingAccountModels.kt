package com.example.tcapp.model.account

class SettingAccountModels {
    class RegisterReceiveEmailResponse{
        public var isVerifyEmail:Boolean=false;
        public var addFriendRequest:Boolean=false;
        public var teamRecruitRequest:Boolean=false;
        public var teamJoinRequest:Boolean=false;
        public var projectInviteRequest:Boolean=false;
        constructor(
            isVerifyEmail:Boolean=false,
            addFriendRequest:Boolean=false,
            teamRecruitRequest:Boolean=false,
            teamJoinRequest:Boolean=false,
            projectInviteRequest:Boolean=false,
        ){
            this.isVerifyEmail=isVerifyEmail;
            this.addFriendRequest=addFriendRequest;
            this.teamRecruitRequest=teamRecruitRequest;
            this.teamJoinRequest=teamJoinRequest;
            this.projectInviteRequest=projectInviteRequest;
        }
        constructor(
            clone:RegisterReceiveEmailResponse
        ){
            this.isVerifyEmail=clone.isVerifyEmail;
            this.addFriendRequest=clone.addFriendRequest;
            this.teamRecruitRequest=clone.teamRecruitRequest;
            this.teamJoinRequest=clone.teamJoinRequest;
            this.projectInviteRequest=clone.projectInviteRequest;
        }
    }

    class EditRegisterReceiveEmailStatusRequest{
        companion object Static {
            public const val AddFriendRequest:String="AddFriendRequest";
            public const val TeamRecruitRequest:String="TeamRecruitRequest";
            public const val TeamJoinRequest:String="TeamJoinRequest";
            public const val ProjectInviteRequest:String="ProjectInviteRequest";
        }
    }
}