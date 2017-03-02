package com.codexter.botguise;

public final class Keys {
    public static final int INTRO_BOT_KEY_CODE = 1;
    public static final int MOVIE_QUESTION_BOT_KEY_CODE = 2;
    public static final int MOVIE_ANSWER_BOT_KEY_CODE = 3;
    private static final String INTRO_BOT_KEY = "Hf4YLkhvvGU.cwA.rHU.Kmr_HAeiwCed3a6J07dd2nk53mDGVD16PgFJd2HU1jo";
    private static final String MOVIE_QUETION_BOT_KEY = "WA4yrs8M47o.cwA.6EU.BDtRy_KpdrsDN-TGRcuv-Qo6LljrS19-JCL_2pmPjQ4";
    private static final String MOVIE_ANSWER_BOT_KEY = "XmqKV4bJQ3E.cwA.fdI.5UMoyiBDkRfpCYxYNJZGouVIBNVsQmmD8w_dYvDXLBA";

    public static String getKey(int keyCode) {
        switch (keyCode) {
            case INTRO_BOT_KEY_CODE:
                return INTRO_BOT_KEY;
            case MOVIE_QUESTION_BOT_KEY_CODE:
                return MOVIE_QUETION_BOT_KEY;
            case MOVIE_ANSWER_BOT_KEY_CODE:
                return MOVIE_ANSWER_BOT_KEY;
            default:
                return null;
        }
    }
}
