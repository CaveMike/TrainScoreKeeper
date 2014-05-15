//   Copyright 2014 Michael T. Corrigan
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.mikecorrigan.trainscorekeeper;


public class JsonSpec {
    // private final static String TAG = JsonSpec.class.getSimpleName();
    // private final static boolean VERBOSE = true;

    public final static String SCORES_KEY = "scores";

    public final static String GAME_SPEC = "spec";

    public final static String SCORE_KEY = "score";
    public final static String SCORE_ID = "id";
    public final static String SCORE_NAME = "name";
    public final static String SCORE_TYPE = "type";
    public final static String SCORE_VALUE = "value";
    public final static String SCORE_HISTORY = "history";

    public final static String BUTTON_KEY = "button";
    public final static String BUTTON_NAME = "name";
    public final static String BUTTON_DESCRIPTION = "description";
    public final static String BUTTON_HISTORY = "history";
    public final static String BUTTON_TYPE = "type";
    public final static String BUTTON_VALUE = "value";

    public final static String BUTTONS_KEY = "buttons";

    public final static String SECTION_KEY = "section";
    public final static String SECTION_NAME = "name";
    public final static String SECTION_COLUMNS = "columns";

    public final static String SECTIONS_KEY = "sections";

    public final static String TAB_KEY = "tab";
    public final static String TAB_NAME = "name";

    public final static String TABS_KEY = "tabs";

    public final static String PLAYERS_KEY = "players";

    public final static String ROOT_KEY = "root";
    public final static String ROOT_NAME = "name";
    public final static String ROOT_DESCRIPTION = "description";

    public final static int DEFAULT_SCORE_ID = 0;
    public final static String DEFAULT_SCORE_NAME = "unknown";
    public final static int DEFAULT_SCORE_TYPE = 0;
    public final static int DEFAULT_SCORE_VALUE = 0;
    public final static String DEFAULT_SCORE_HISTORY = "";

    public final static String DEFAULT_BUTTON_NAME = "";
    public final static String DEFAULT_BUTTON_DESCRIPTION = "";
    public final static String DEFAULT_BUTTON_HISTORY = "";
    public final static int DEFAULT_BUTTON_TYPE = 0;
    public final static int DEFAULT_BUTTON_VALUE = 0;

    public final static String DEFAULT_SECTION_NAME = "";
    public final static int DEFAULT_SECTION_COLUMNS = 1;

    public final static String DEFAULT_TAB_NAME = "tab";

    public static String getSampleData() {

        final String jsonButtonSpecTrain1 =
                "{" +
                        "\"" + BUTTON_NAME + "\":\"1 Car (+1)\"," +
                        "\"" + BUTTON_DESCRIPTION + "\":\"\"," +
                        "\"" + BUTTON_HISTORY + "\":\"built a train car worth %2$d\"," +
                        "\"" + BUTTON_TYPE + "\":0," +
                        "\"" + BUTTON_VALUE + "\":1" +
                "}";

        final String jsonButtonSpecTrain2 =
                "{" +
                        "\"" + BUTTON_NAME + "\":\"2 Cars (+2)\"," +
                        "\"" + BUTTON_DESCRIPTION + "\":\"\"," +
                        "\"" + BUTTON_HISTORY + "\":\"built 2 train cars worth %2$d\"," +
                        "\"" + BUTTON_TYPE + "\":0," +
                        "\"" + BUTTON_VALUE + "\":2" +
                "}";

        final String jsonButtonSpecTrain3 =
                "{" +
                        "\"" + BUTTON_NAME + "\":\"4 Cars (+3)\"," +
                        "\"" + BUTTON_DESCRIPTION + "\":\"\"," +
                        "\"" + BUTTON_HISTORY + "\":\"built 3 train cars worth %2$d\"," +
                        "\"" + BUTTON_TYPE + "\":0," +
                        "\"" + BUTTON_VALUE + "\":3" +
                "}";

        final String jsonButtonSpecTrain4 =
                "{" +
                        "\"" + BUTTON_NAME + "\":\"7 Cars (+4)\"," +
                        "\"" + BUTTON_DESCRIPTION + "\":\"\"," +
                        "\"" + BUTTON_HISTORY + "\":\"built 4 train cars worth %2$d\"," +
                        "\"" + BUTTON_TYPE + "\":0," +
                        "\"" + BUTTON_VALUE + "\":4" +
                "}";

        final String jsonButtonSpecTrain5 =
                "{" +
                        "\"" + BUTTON_NAME + "\":\"5 Cars (+10)\"," +
                        "\"" + BUTTON_DESCRIPTION + "\":\"\"," +
                        "\"" + BUTTON_HISTORY + "\":\"built 5 train cars worth %2$d\"," +
                        "\"" + BUTTON_TYPE + "\":0," +
                        "\"" + BUTTON_VALUE + "\":10" +
                "}";

        final String jsonButtonSpecTrain6 =
                "{" +
                        "\"" + BUTTON_NAME + "\":\"6 Cars (+15)\"," +
                        "\"" + BUTTON_DESCRIPTION + "\":\"\"," +
                        "\"" + BUTTON_HISTORY + "\":\"built 6 train cars worth %2$d\"," +
                        "\"" + BUTTON_TYPE + "\":0," +
                        "\"" + BUTTON_VALUE + "\":15" +
                "}";

        final String jsonButtonSpecTrain7 =
                "{" +
                        "\"" + BUTTON_NAME + "\":\"7 Cars (+18)\"," +
                        "\"" + BUTTON_DESCRIPTION + "\":\"\"," +
                        "\"" + BUTTON_HISTORY + "\":\"built 7 train cars worth %2$d\"," +
                        "\"" + BUTTON_TYPE + "\":0," +
                        "\"" + BUTTON_VALUE + "\":18" +
                "}";

        final String jsonButtonSpecTrain8 =
                "{" +
                        "\"" + BUTTON_NAME + "\":\"8 Cars (+21)\"," +
                        "\"" + BUTTON_DESCRIPTION + "\":\"\"," +
                        "\"" + BUTTON_HISTORY + "\":\"built 8 train cars worth %2$d\"," +
                        "\"" + BUTTON_TYPE + "\":0," +
                        "\"" + BUTTON_VALUE + "\":21" +
                "}";

        final String jsonButtonSpecTrain9 =
                "{" +
                        "\"" + BUTTON_NAME + "\":\"9 Cars (+27)\"," +
                        "\"" + BUTTON_DESCRIPTION + "\":\"\"," +
                        "\"" + BUTTON_HISTORY + "\":\"built 9 train cars worth %2$d\"," +
                        "\"" + BUTTON_TYPE + "\":0," +
                        "\"" + BUTTON_VALUE + "\":27" +
                "}";

        final String jsonTabSpecTrains =
                "{" +
                        "\"" + TAB_NAME + "\":\"Trains\"," +
                            "\"sections\":[" +
                            "{" +
                                "\"" + SECTION_NAME + "\":\"Number of Cars\"," +
                                "\"" + SECTION_COLUMNS + "\":4," +
                                "\"buttons\":[" +
                                    jsonButtonSpecTrain1 + "," +
                                    jsonButtonSpecTrain2 + "," +
                                    jsonButtonSpecTrain3 + "," +
                                    jsonButtonSpecTrain4 + "," +
                                    jsonButtonSpecTrain5 + "," +
                                    jsonButtonSpecTrain6 + "," +
                                    jsonButtonSpecTrain7 + "," +
                                    jsonButtonSpecTrain8 + "," +
                                    jsonButtonSpecTrain9 +
                                    "]" +
                            "}" +
                        "]" +
                "}";

        final String jsonTabSpecCompleted =
                "{" +
                        "\"" + TAB_NAME + "\":\"Completed\"," +
                            "\"sections\":[" +
                            "{" +
                                "\"" + SECTION_NAME + "\":\"Completed Contract\"," +
                                "\"" + SECTION_COLUMNS + "\":4," +
                                "\"buttons\":[" +
                                    "{\"" + BUTTON_NAME + "\":\"+1\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"completed a contract worth %2$d\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":1}," +
                                    "{\"" + BUTTON_NAME + "\":\"+2\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":2}," +
                                    "{\"" + BUTTON_NAME + "\":\"+3\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":3}," +
                                    "{\"" + BUTTON_NAME + "\":\"+4\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":4}," +
                                    "{\"" + BUTTON_NAME + "\":\"+5\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":5}," +
                                    "{\"" + BUTTON_NAME + "\":\"+6\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":6}," +
                                    "{\"" + BUTTON_NAME + "\":\"+7\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":7}," +
                                    "{\"" + BUTTON_NAME + "\":\"+8\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":8}," +
                                    "{\"" + BUTTON_NAME + "\":\"+9\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":9}," +
                                    "{\"" + BUTTON_NAME + "\":\"+10\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":10}," +
                                    "{\"" + BUTTON_NAME + "\":\"+11\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":11}," +
                                    "{\"" + BUTTON_NAME + "\":\"+12\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":12}," +
                                    "{\"" + BUTTON_NAME + "\":\"+13\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":13}," +
                                    "{\"" + BUTTON_NAME + "\":\"+14\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":14}," +
                                    "{\"" + BUTTON_NAME + "\":\"+15\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":15}," +
                                    "{\"" + BUTTON_NAME + "\":\"+16\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":16}," +
                                    "{\"" + BUTTON_NAME + "\":\"+17\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":17}," +
                                    "{\"" + BUTTON_NAME + "\":\"+18\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":18}," +
                                    "{\"" + BUTTON_NAME + "\":\"+19\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":19}," +
                                    "{\"" + BUTTON_NAME + "\":\"+20\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":20}," +
                                    "{\"" + BUTTON_NAME + "\":\"+21\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":21}," +
                                    "{\"" + BUTTON_NAME + "\":\"+22\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":22}," +
                                    "{\"" + BUTTON_NAME + "\":\"+23\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":23}," +
                                    "{\"" + BUTTON_NAME + "\":\"+24\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":24}," +
                                    "{\"" + BUTTON_NAME + "\":\"+25\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":1,\"" + BUTTON_VALUE + "\":25}" +
                                "]" +
                            "}" +
                        "]" +
                "}";

        final String jsonTabSpecFailed =
                "{" +
                        "\"" + TAB_NAME + "\":\"Missed\"," +
                            "\"sections\":[" +
                            "{" +
                                "\"" + SECTION_NAME + "\":\"Missed Contract\"," +
                                "\"" + SECTION_COLUMNS + "\":4," +
                                "\"buttons\":[" +
                                    "{\"" + BUTTON_NAME + "\":\"-1\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"failed to complete a contract worth %2$d\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-1}," +
                                    "{\"" + BUTTON_NAME + "\":\"-2\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-2}," +
                                    "{\"" + BUTTON_NAME + "\":\"-3\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-3}," +
                                    "{\"" + BUTTON_NAME + "\":\"-4\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-4}," +
                                    "{\"" + BUTTON_NAME + "\":\"-5\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-5}," +
                                    "{\"" + BUTTON_NAME + "\":\"-6\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-6}," +
                                    "{\"" + BUTTON_NAME + "\":\"-7\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-7}," +
                                    "{\"" + BUTTON_NAME + "\":\"-8\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-8}," +
                                    "{\"" + BUTTON_NAME + "\":\"-9\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-9}," +
                                    "{\"" + BUTTON_NAME + "\":\"-10\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-10}," +
                                    "{\"" + BUTTON_NAME + "\":\"-11\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-11}," +
                                    "{\"" + BUTTON_NAME + "\":\"-12\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-12}," +
                                    "{\"" + BUTTON_NAME + "\":\"-13\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-13}," +
                                    "{\"" + BUTTON_NAME + "\":\"-14\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-14}," +
                                    "{\"" + BUTTON_NAME + "\":\"-15\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-15}," +
                                    "{\"" + BUTTON_NAME + "\":\"-16\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-16}," +
                                    "{\"" + BUTTON_NAME + "\":\"-17\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-17}," +
                                    "{\"" + BUTTON_NAME + "\":\"-18\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-18}," +
                                    "{\"" + BUTTON_NAME + "\":\"-19\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-19}," +
                                    "{\"" + BUTTON_NAME + "\":\"-20\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-20}," +
                                    "{\"" + BUTTON_NAME + "\":\"-21\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-21}," +
                                    "{\"" + BUTTON_NAME + "\":\"-22\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-22}," +
                                    "{\"" + BUTTON_NAME + "\":\"-23\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-23}," +
                                    "{\"" + BUTTON_NAME + "\":\"-24\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-24}," +
                                    "{\"" + BUTTON_NAME + "\":\"-25\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"\",\"" + BUTTON_TYPE + "\":2,\"" + BUTTON_VALUE + "\":-25}" +
                                "]" +
                            "}" +
                        "]" +
                "}";

        final String jsonTabSpecBonuses =
                "{" +
                        "\"" + TAB_NAME + "\":\"Bonus\"," +
                            "\"sections\":[" +
                            "{" +
                                "\"" + SECTION_NAME + "\":\"Stations Remaining\"," +
                                "\"" + SECTION_COLUMNS + "\":3," +
                                "\"buttons\":[" +
                                    "{\"" + BUTTON_NAME + "\":\"1 Stations (+4)\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"had 1 station remaining worth %2$d\",\"" + BUTTON_TYPE + "\":3,\"" + BUTTON_VALUE + "\":4}," +
                                    "{\"" + BUTTON_NAME + "\":\"2 Stations (+8)\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"had 2 stations remaining worth %2$d\",\"" + BUTTON_TYPE + "\":3,\"" + BUTTON_VALUE + "\":8}," +
                                    "{\"" + BUTTON_NAME + "\":\"3 Stations (+12)\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"had 3 stations remaining worth %2$d\",\"" + BUTTON_TYPE + "\":3,\"" + BUTTON_VALUE + "\":12}" +
                                "]" +
                            "}," +
                            "{" +
                                "\"" + SECTION_NAME + "\":\"Misc\"," +
                                "\"" + SECTION_COLUMNS + "\":2," +
                                "\"buttons\":[" +
                                    "{\"" + BUTTON_NAME + "\":\"Longest Train (+10)\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"received Longest Train worth %2$d\",\"" + BUTTON_TYPE + "\":4,\"" + BUTTON_VALUE + "\":10}," +
                                    "{\"" + BUTTON_NAME + "\":\"Globe Trotter (+15)\",\"" + BUTTON_DESCRIPTION + "\":\"\",\"" + BUTTON_HISTORY + "\":\"received Globe Trotter worth %2$d\",\"" + BUTTON_TYPE + "\":4,\"" + BUTTON_VALUE + "\":15}" +
                                "]" +
                            "}" +
                        "]" +
                "}";

        final String jsonTabsSpec =
                "\"tabs\":[" +
                        jsonTabSpecTrains + "," +
                        jsonTabSpecCompleted + "," +
                        jsonTabSpecFailed + "," +
                        jsonTabSpecBonuses +
                "]";

        final String jsonRootSpec =
                "{" +
                        "\"" + ROOT_NAME + "\":\"\"," +
                        jsonTabsSpec +
                "}";

         return jsonRootSpec;
    }
}
