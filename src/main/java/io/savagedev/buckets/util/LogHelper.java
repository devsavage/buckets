package io.savagedev.buckets.util;

/*
 * LogHelper.java
 * Copyright (C) 2020-2022 Savage - github.com/devsavage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.message.Message;

public class LogHelper
{
    private static Logger logger = LogManager.getLogger(ModReference.MOD_ID);
    public static final Marker MOD_MARKER = MarkerManager.getMarker(ModReference.MOD_ID);
    public static final String TAG = "[" + ModReference.MOD_NAME + "";

    public static void log(Level level, Marker marker, Message message) {
        logger.log(level, marker, message);
    }
    public static void log(Level level, Marker marker, Object object) {
        logger.log(level, marker, object);
    }

    public static void log(Level level, Marker marker, String message) {
        logger.log(level, marker, message);
    }

    public static void log(Level level, Marker marker, String format, Object... params) {
        logger.log(level, marker, format, params);
    }

    public static void log(Level level, Message message) {
        log(level, MOD_MARKER, message);
    }

    public static void log(Level level, Object object) {
        log(level, MOD_MARKER, object);
    }

    public static void log(Level level, String message) {
        log(level, MOD_MARKER, message);
    }

    public static void log(Level level, String format, Object... params) {
        log(level, MOD_MARKER, format, params);
    }

    //Info Logger

    public static void info(Marker marker, Message message) {
        log(Level.INFO, marker, message);
    }

    public static void info(Marker marker, Object object) {
        log(Level.INFO, marker, object);
    }

    public static void info(Marker marker, String message) {
        log(Level.INFO, marker, message);
    }

    public static void info(Marker marker, String format, Object... params) {
        log(Level.INFO, marker, format, params);
    }

    public static void info(Message message) {
        info(MOD_MARKER, message);
    }

    public static void info(Object object) {
        info(MOD_MARKER, object);
    }

    public static void info(String message) {
        info(MOD_MARKER, message);
    }

    public static void info(String format, Object... params) {
        info(MOD_MARKER, format, params);
    }

    // Warning Logger

    public static void warn(Marker marker, Message message) {
        log(Level.WARN, marker, message);
    }

    public static void warn(Marker marker, Object object) {
        log(Level.WARN, marker, object);
    }

    public static void warn(Marker marker, String message) {
        log(Level.WARN, marker, message);
    }

    public static void warn(Marker marker, String format, Object... params) {
        log(Level.WARN, marker, format, params);
    }

    public static void warn(Message message) {
        warn(MOD_MARKER, message);
    }

    public static void warn(Object object) {
        warn(MOD_MARKER, object);
    }

    public static void warn(String message) {
        warn(MOD_MARKER, message);
    }

    public static void warn(String format, Object... params) {
        warn(MOD_MARKER, format, params);
    }

    // Error Logger

    public static void error(Marker marker, Message message) {
        log(Level.ERROR, marker, message);
    }

    public static void error(Marker marker, Object object) {
        log(Level.ERROR, marker, object);
    }

    public static void error(Marker marker, String message) {
        log(Level.ERROR, marker, message);
    }

    public static void error(Marker marker, String format, Object... params) {
        log(Level.ERROR, marker, format, params);
    }

    public static void error(Message message) {
        error(MOD_MARKER, message);
    }

    public static void error(Object object) {
        error(MOD_MARKER, object);
    }

    public static void error(String message) {
        error(MOD_MARKER, message);
    }

    public static void error(String format, Object... params) {
        error(MOD_MARKER, format, params);
    }

    // Debug Logger

    public static void debug(Marker marker, Message message) {
        log(Level.DEBUG, marker, message);
    }

    public static void debug(Marker marker, Object object) {
        log(Level.DEBUG, marker, object);
    }

    public static void debug(Marker marker, String message) {
        log(Level.DEBUG, marker, message);
    }

    public static void debug(Marker marker, String format, Object... params) {
        log(Level.DEBUG, marker, format, params);
    }

    public static void debug(Message message) {
        debug(MOD_MARKER, message);
    }

    public static void debug(Object object) {
        debug(MOD_MARKER, object);
    }

    public static void debug(String message) {
        debug(MOD_MARKER, message);
    }

    public static void debug(String format, Object... params) {
        debug(MOD_MARKER, format, params);
    }
}
