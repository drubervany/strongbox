/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Schibsted Products & Technology AS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.schibsted.security.strongbox.sdk.internal.encryption;

import java.util.HashMap;
import java.util.Map;

/**
 * @author stiankri
 */
public enum KMSKeyState {
    ENABLED("Enabled"),
    DISABLED("Disabled"),
    PENDING_DELETION("PendingDeletion");

    private String name;
    private static Map<String, KMSKeyState> keyStateMap = new HashMap<>();
    static {
        for (KMSKeyState state : values()) {
            keyStateMap.put(state.name, state);
        }
    }

    KMSKeyState(String name) {
        this.name = name;
    }

    public static KMSKeyState fromString(String keyState) {
        KMSKeyState state = keyStateMap.get(keyState);
        if (state == null) {
            throw new IllegalArgumentException("Unrecognized keyState '" + state + "', expected one of " + keyStateMap.keySet());
        }
        return state;
    }

    @Override
    public String toString() {
        return name;
    }
}
