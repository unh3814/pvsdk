package com.pvcombank.sdk.ekyc.model.request

import com.google.gson.annotations.SerializedName
import com.trustingsocial.tvcoresdk.external.TVFrameClass

data class RequestVerifySelfies(
    @SerializedName("frontal")
    var frontal: List<String>,
    @SerializedName("gesture")
    var gesture: List<Gesture>,
    @SerializedName("videos")
    var videos: List<TVFrameClass>
)

data class Gesture(
    @SerializedName("base64")
    var base64: String,
    @SerializedName("gesture")
    var gesture: String
)