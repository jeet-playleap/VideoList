package com.example.videolist.model

import java.io.Serializable
import java.time.ZoneOffset

/*[
    {
        "id": "64a6a1b095b219003d1c6d7a",
        "body": "{\"blocks\":[{\"key\":\"hkI2q\",\"text\":\"feed\",\"type\":\"header-one\",\"depth\":0}]}",
        "type": "post",
        "createdAt": "2023-07-06T11:12:48.831+00:00",
        "status": "public",
        "author": {
            "entityName": "Customer",
            "entityId": "63e1eba52cf34e002f4271ac",
            "name": "undefined undefined",
            "type": "player",
            "alias": "better",
            "communityBlockersCount": 0
        },
        "info": null,
        "comments": [],
        "hasPhoto": false,
        "hashTags": [],
        "videos": [],
        "following": {
            "isFollowing": false
        },
        "hasVideo": true,
        "userReactions": [],
        "stats": {
            "views": 0,
            "reactions": 0,
            "reactionDetails": {},
            "comments": 0
        },
        "skills": [
            {
                "alias": "Power",
                "id": "628cfefff715025018907f1e"
            }
        ],
        "availableForDareBack": false
    },
    {
        "id": "63f36650e24094003ffaa775",
        "body": "{\"blocks\":[{\"key\":\"SBx7S\",\"text\":\"Sticker\",\"type\":\"header-one\",\"depth\":0}]}",
        "type": "post",
        "createdAt": "2023-02-20T12:23:44.498+00:00",
        "status": "public",
        "author": {
            "entityName": "Customer",
            "entityId": "63edee711b3775002761b628",
            "name": "undefined undefined",
            "type": "player",
            "alias": "Yalik",
            "communityBlockersCount": 0
        },
        "info": null,
        "comments": [],
        "hasPhoto": false,
        "hashTags": [],
        "videos": [

        ],
        "following": {
            "isFollowing": false
        },
        "hasVideo": true,
        "userReactions": [],
        "stats": {
            "views": 0,
            "reactions": 0,
            "reactionDetails": {},
            "comments": 0
        },
        "skills": [
            {
                "id": "628cfefff715025018907f20",
                "alias": "Coordination"
            }
        ],
        "availableForDareBack": true,
        "blockedAt": null,
        "communityBlockersCount": 0
    },
 ]*/

data class VideoFeedModel(
    val feeds:List<FeedItem>,
    val meta:Meta
)
data class FeedItem(
    val id:String,
    val videos:List<VideoItem>
):Serializable
/*   {
                "id": "63f3664ddd33070033ff725b",
                "providerVideoId": "f4e9fcac-7be1-4b9e-ac92-700bb6cfb383.mp4",
                "userId": "63edee711b3775002761b628",
                "reference": "https://d1hus0nx0ytxoz.cloudfront.net/out/v1/89f7a84186c94350bb72387c9c334fee/26fab79877664df5bc4cb946a3aeb817/5efb491ddaba42b186fc2ea1575f8020/index.m3u8",
                "resource": "aws",
                "imageLink": "https://d1hus0nx0ytxoz.cloudfront.net/838555f9-a9f7-4870-b328-fe589fc26e5d/thumbnails/f4e9fcac-7be1-4b9e-ac92-700bb6cfb383_thumb.0000000.jpg",
                "hash": "f4e9fcac-7be1-4b9e-ac92-700bb6cfb383.mp4",
                "albumId": null,
                "isDeleted": false,
                "isValidated": true,
                "dareId": "64575971b2a3ca002f410b09",
                "videoStatus": "creator",
                "duration": 6.04,
                "metaData": {
                    "height": 860,
                    "width": 480
                }
            }*/
data class VideoItem(
    val id:String,
    val providerVideoId:String,
    val userId:String,
    val reference:String,
    val resource:String,
    val imageLink:String,
    val hash:String,
    val duration:Float

):Serializable


data class Meta(
    val total:Int,
    val limit: Int,
    val offset: Int
)
data class FilterQuery(
    val offset: Int,
    val limit:Int
)