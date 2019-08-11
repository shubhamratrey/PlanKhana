package com.sillylife.plankhana.enums

enum class LanguageEnum constructor(val code: String, val title: String, val slug: String, val shareMessage: String,val channelShareMessage: String, val id: Int) {

    /**
    contentLanguage is removed to add back uncomment
     */

    HINDI("hi", "हिंदी", "hindi", "सुनने के लिए तुरंत इस लिंक से KUKU FM App *डाउनलोड करें:* ","ऑनलाइन रेडियो ऐप कुकू एफएम पर सुनें चैनल *\"%s\"*।\n" +
            "सुनने के लिए इस लिंक पर क्लिक करके एप्लिकेशन डाउनलोड करें: ",1),
    ENGLISH("en", "English", "english", "To listen *download KUKU FM App* now: ",
        "Check out the channel *\"%s\"* on the online radio app Kuku FM.\n" + "Download the app and listen to it by tapping on this link: ",2),
    TELUGU("te", "తెలుగు", "telugu", "వినడానికి ఈ లింక్ నుండి వెంటనే KUKU FM *అనువర్తనాన్ని డౌన్లోడ్ చేయండి:* ",
        "ఆన్లైన్ రేడియో అనువర్తనం Kuke FM ఛానల్ వినండి *\"%s\"*.\n" + "వినడానికి ఈ లింక్పై క్లిక్ చేయడం ద్వారా అనువర్తనాన్ని డౌన్లోడ్ చేయండి: ",3);
    //MARATHI("mr", "Marathi", "marathi", "some message",4);

    companion object {

        fun getLanguageBySlug(slug: String): LanguageEnum {
            for (le in LanguageEnum.values()) {
                if (le.slug.equals(slug, ignoreCase = true)) {
                    return le
                }
            }
            return ENGLISH
        }

        fun getLanguageByCode(code: String): LanguageEnum {
            for (le in LanguageEnum.values()) {
                if (le.code.equals(code, ignoreCase = true)) {
                    return le
                }
            }
            return ENGLISH
        }
    }

}
