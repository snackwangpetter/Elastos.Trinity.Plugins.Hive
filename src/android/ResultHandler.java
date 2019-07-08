/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.trinity.plugins.hive;

import android.util.Base64;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import org.elastos.hive.*;

public class ResultHandler<T extends Result> implements Callback<T> {
    private final int handlerId;
    private final CallbackContext callbackContext;

    ResultHandler(int id, CallbackContext callbackContext) {
        this.handlerId = id;
        this.callbackContext = callbackContext;
    }

    private void sendEvent(JSONObject info) throws JSONException {
        info.put("id", handlerId);

        if (callbackContext != null) {
            PluginResult res = new PluginResult(PluginResult.Status.OK, info);
            res.setKeepCallback(true);
            callbackContext.sendPluginResult(res);
        }
    }

    @Override
    void onError(HiveException e) {
        JSONObject ret = new JSONObject();
        try {
            ret.put("error", e.getMessage());
            sendEvent(ret);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    void onSuccess(T body) {
        JSONObject ret;
        try {
            if (body instanceof Drive) {
                ret = driveToJson(body);
            } else if (body instanceof Directory) {
                ret = directoryToJson(body);
            } else if (body instanceof File) {
                ret = fileToJson(body);
            } else if (body instanceof Client.Info) {
                ret = clientInfoToJson(body);
            } else if (body instanceof Drive.Info) {
                ret = driveInfoToJson(body);
            } else if (body instanceof Directory.Info) {
                ret = dirInfoToJson(body);
            } else if (body instanceof File.Info) {
                ret = fileInfoToJson(body);
            } else if (body instanceof ItemInfo) {
                ret = itemInfoToJson(body);
            } else if (body instanceof Children) {
                ret = childrenToJson(body);
            } else {
                ret = hiveVoidToJson(body);
            }

            sendEvent(ret);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject driveToJson(Drive drive) {
        ObjectMap  map = ObjectMap.acquire(ObjectMap.DRIVE);
        Integer  objId = System.identityHashCode(drive);
        JSONObject ret = new JSONObject();

        ObjectMap.toDriveMap(map).put(objId, drive);
        ret.put("id", objId);
        return ret;
    }

    private JSONObject directoryToJson(Directory directory) {
        ObjectMap  map = ObjectMap.acquire(ObjectMap.DIR);
        Integer  objId = System.identityHashCode(directory);
        JSONObject ret = new JSONObject();

        ObjectMap.toDriveMap(map).put(objId, directory);
        ret.put("id", objId);
        return ret;
    }

    private JSONObject fileToJson(File file) {
        ObjectMap  map = ObjectMap.acquire(ObjectMap.FILE);
        Integer  objId = System.identityHashCode(file);
        JSONObject ret = new JSONObject();

        ObjectMap.toFileMap(map).put(objId, file);
        ret.put("id", objId);
        return ret;
    }

    private JSONObject clientInfoToJson(Client.Info info) {
        JSONObjectHolder<Client.Info> holder;

        holder = new JSONObjectHolder<Client.Info>(info);
        holder.put(Client.Info.userId)
              .put(Client.Info.name)
              .put(Client.Info.email)
              .put(Client.Info.phoneNo)
              .put(Client.Info.region);

        return holder.get();
    }

    private JSONObject driveInfoToJson(Drive.Info info) {
        JSONObjectHolder<Drive.Info> holder;

        holder = new JSONObjectHolder<Drive.Info>(info);
        holder.put(Drive.Info.driveId)
              .put(Drive.Info.name);

        return holder.get();
    }

    private JSONObject dirInfoToJson(Directory.Info info) {
        JSONObjectHolder<Directory.Info> holder;

        holder = new JSONObjectHolder<Directory.Info>(info);
        holder.put(Directory.Info.itemId)
              .put(Directory.Info.name)
              .put(Directory.Info.childCount);

        return holder.get();
    }

    private JSONObject fileInfoToJson(File.Info info) {
        JSONObjectHolder<File.Info> holder;

        holder = new JSONObjectHolder<File.Info>(info);
        holder.put(File.Info.itemId)
              .put(File.Info.name)
              .put(File.Info.size);

        return holder.get();
    }

    private JSONObject itemInfoToJson(ItemInfo info) {
        // TODO;
    }

    private JSONObject childrenToJson(Children info) {
        // TODO;
    }

    private JSONObject hiveVoidToJson(HiveVoid padding) {
        // TODO;
    }
}
