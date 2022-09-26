package com.pvcombank.sdk.model.request

import com.google.gson.annotations.SerializedName

data class RequestVerifySelfies(
    @SerializedName("frontal")
    var frontal: List<String>,
    @SerializedName("gesture")
    var gesture: List<Gesture>,
    @SerializedName("videos")
    var videos: List<Video>
)

data class Gesture(
    @SerializedName("base64")
    var base64: String,
    @SerializedName("gesture")
    var gesture: String
)

data class Video(
    @SerializedName("base64")
    var base64: String,
    @SerializedName("bbox")
    var bbox: List<Double>,
    @SerializedName("index")
    var index: Int,
    @SerializedName("label")
    var label: String,
    @SerializedName("landmarks")
    var landmarks: List<List<Double>>,
    @SerializedName("original_bbox")
    var originalBbox: List<Double>,
    @SerializedName("original_landmarks")
    var originalLandmarks: List<List<Double>>,
    @SerializedName("score")
    var score: Double,
    @SerializedName("time")
    var time: Long
)