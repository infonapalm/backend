package com.infonapalm.ridbackend.TemplateStructs

/**
 * Created with IntelliJ IDEA.
 * User: infonapalm
 * Date: 5/10/15
 * Time: 11:01 AM

 */
case class FriendInfoTemplate(id: Int, label: String, image: String, group: String = "", shape: String = "image",
                              is_deleted: Boolean,first_name: String = "",last_name: String = "",
                              city: String="", country: String = "",marked: String = "",comments: String = "")
case class FriendsPairTemplate(source: Int, target: Int, value: Int =1)