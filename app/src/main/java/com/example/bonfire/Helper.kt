package com.example.bonfire

class Helper {
    // ex. set message with "avatar" = "/images/icon1" to R.id.icon1
    // --- Avatar mapping --- (terribly hardcoded)
    fun getAvatarId(avatarPath: String?) : Int {
        return when (avatarPath) {
            "/images/Logo.png" -> R.drawable.bonfire_icon
            "/images/icon1.png" -> R.drawable.icon1
            "/images/icon2.png" -> R.drawable.icon2
            "/images/icon3.png" -> R.drawable.icon3
            "/images/icon4.png" -> R.drawable.icon4
            "/images/icon5.png" -> R.drawable.icon5
            "/images/icon6.png" -> R.drawable.icon6
            "/images/icon7.png" -> R.drawable.icon7
            "/images/icon8.png" -> R.drawable.icon8
            "/images/icon9.png" -> R.drawable.icon9
            "/images/icon10.png" -> R.drawable.icon10
            "/images/icon11.png" -> R.drawable.icon11
            "/images/icon12.png" -> R.drawable.icon12
            "/images/icon13.png" -> R.drawable.icon13
            "/images/icon14.png" -> R.drawable.icon14
            "/images/icon15.png" -> R.drawable.icon15
            else -> R.drawable.default_pfp
        }
    }
}