/*
 * Copyright 2018 natanbc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.natanbc.lavadsp;

public final class DspInfo {
    public static final String VERSION_MAJOR;
    public static final String VERSION_MINOR;
    public static final String VERSION_REVISION;
    public static final String COMMIT_HASH;

    public static final String VERSION;

    static {
        VERSION_MAJOR = "@VERSION_MAJOR@";
        VERSION_MINOR = "@VERSION_MINOR@";
        VERSION_REVISION = "@VERSION_REVISION@";
        COMMIT_HASH = "@COMMIT_HASH@";
        //noinspection ConstantConditions
        VERSION = VERSION_MAJOR.startsWith("@") ? "Dev" : String.format("%s.%s.%s", VERSION_MAJOR, VERSION_MINOR, VERSION_REVISION);
    }

    private DspInfo() {}
}
