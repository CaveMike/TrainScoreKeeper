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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.content.res.AssetManager;

public class FileUtils {
    private final static String TAG = FileUtils.class.getSimpleName();
    private final static boolean VERBOSE = false;

    public static String readStringFromFile(final String filename) {
        Log.vc(VERBOSE, TAG, "readStringFromFile: filename=" + filename);
        return readStringFromFile(new File(filename));
    }

    public static String readStringFromFile(final File file) {
        Log.vc(VERBOSE, TAG, "readStringFromFile: file=" + file);

        String string = null;

        InputStream is = null;
        try {
            is = new FileInputStream(file);
            string = readStringFromFile(is);
            is.close();
        } catch (FileNotFoundException e) {
            Log.th(TAG, e, "readStringFromFile: open failed");
        } catch (IOException e) {
            Log.th(TAG, e, "readStringFromFile: I/O failed");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.th(TAG, e, "readStringFromFile: close failed");
                }
            }
        }

        return string;
    }

    public static String readStringFromAssets(final Context context, final String assetname) {
        Log.vc(VERBOSE, TAG, "readStringFromAssets: context=" + context
                + ", assetname=" + assetname);

        String string = null;

        AssetManager assetManager = context.getAssets();

        InputStream is = null;
        try {
            is = assetManager.open(assetname);
            string = readStringFromFile(is);
            is.close();
        } catch (FileNotFoundException e) {
            Log.th(TAG, e, "readStringFromAssets: open failed");
        } catch (IOException e) {
            Log.th(TAG, e, "readStringFromAssets: I/O failed");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.th(TAG, e, "readStringFromAssets: close failed");
                }
            }
        }

        return string;
    }

    public static String readStringFromFile(final InputStream is) {
        Log.vc(VERBOSE, TAG, "readStringFromFile: is=" + is);

        String string = null;

        BufferedReader reader = null;
        try {
            StringBuffer stringBuffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(is));

            while (true) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }

                stringBuffer.append(line);
            }

            string = stringBuffer.toString();
            reader.close();
        } catch (IOException e) {
            Log.th(TAG, e, "readStringFromFile: I/O failed");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.th(TAG, e, "readStringFromFile: close failed");
                }
            }
        }

        return string;
    }

    public static boolean writeStringToFile(final String filename, final String string) {
        Log.vc(VERBOSE, TAG, "writeStringToFile: filename=" + filename + ", string=" + string);
        return writeStringToFile(new File(filename), string);
    }

    public static boolean writeStringToFile(final File file, final String string) {
        Log.vc(VERBOSE, TAG, "writeStringToFile: file=" + file + ", string=" + string);

        boolean result = false;

        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            result = writeStringToFile(os, string);
            os.close();
        } catch (FileNotFoundException e) {
            Log.th(TAG, e, "writeStringToFile: open failed");
        } catch (IOException e) {
            Log.th(TAG, e, "writeStringToFile: I/O failed");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.th(TAG, e, "writeStringToFile: close failed");
                }
            }
        }

        return result;
    }

    public static boolean writeStringToFile(final OutputStream os, final String string) {
        Log.vc(VERBOSE, TAG, "writeStringToFile: os=" + os + ", string=" + string);

        boolean result = false;

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(os));
            writer.write(string);
            writer.close();
            result = true;
        } catch (IOException e) {
            Log.th(TAG, e, "writeStringToFile");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    Log.th(TAG, e, "writeStringToFile");
                }
            }
        }

        return result;
    }
}
