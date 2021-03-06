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

import android.telecom.Call;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.elastos.hive.*;
import org.elastos.hive.Void;
import org.elastos.hive.File;

import java.nio.ByteBuffer;

class ResultHandler<T extends Result> implements Callback<T> {
    private final int handlerId;
    private final CallbackContext callbackContext;
    private final ByteBuffer byteBuffer;

    ResultHandler(int id, CallbackContext callbackContext) {
        this.handlerId = id;
        this.callbackContext = callbackContext;
        this.byteBuffer = null;
    }

    ResultHandler(int id, CallbackContext callbackContext, ByteBuffer buffer) {
        this.handlerId = id;
        this.callbackContext = callbackContext;
        this.byteBuffer = buffer;
    }

    private void sendEvent(JSONObject info) throws JSONException {
        if (callbackContext != null) {
            info.put("hid", handlerId);
            PluginResult res = new PluginResult(PluginResult.Status.OK, info);
            res.setKeepCallback(true);
            callbackContext.sendPluginResult(res);
        }
    }

    @Override
    public void onError(HiveException ex) {
        try {
            JSONObject ret = new JSONObject();
            ret.put("error", ex.getMessage());
            sendEvent(ret);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(T body) {
        JSONObject ret;
        try {
            if (body instanceof Drive) {
                ret = driveToJson((Drive)body);
            } else if (body instanceof Directory) {
                ret = directoryToJson((Directory)body);
            } else if (body instanceof File) {
                ret = fileToJson((File)body);
            } else if (body instanceof Client.Info) {
                ret = clientInfoToJson((Client.Info)body);
            } else if (body instanceof Drive.Info) {
                ret = driveInfoToJson((Drive.Info)body);
            } else if (body instanceof Directory.Info) {
                ret = dirInfoToJson((Directory.Info)body);
            } else if (body instanceof File.Info) {
                ret = fileInfoToJson((File.Info)body);
            } else if (body instanceof ItemInfo) {
                ret = itemInfoToJson((ItemInfo)body);
            } else if (body instanceof Children) {
                ret = childrenToJson((Children) body);
            } else if (body instanceof Length) {
                ret = lengthToJson((Length)body);
            } else {
                ret = hiveVoidToJson((Void)body);
            }

            if (ret != null) sendEvent(ret);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject driveToJson(Drive drive) throws JSONException {
        ObjectMap  map = ObjectMap.acquire(ObjectMap.DRIVE);
        Integer  objId = System.identityHashCode(drive);
        JSONObject ret = new JSONObject();

        ObjectMap.toDriveMap(map).put(objId, drive);
        ret.put("id", objId);
        return ret;
    }

    private JSONObject directoryToJson(Directory directory) throws JSONException {
        ObjectMap  map = ObjectMap.acquire(ObjectMap.DIR);
        Integer  objId = System.identityHashCode(directory);
        JSONObject ret = new JSONObject();

        ObjectMap.toDirMap(map).put(objId, directory);
        ret.put("id", objId);
        return ret;
    }

    private JSONObject fileToJson(File file) throws JSONException {
        ObjectMap  map = ObjectMap.acquire(ObjectMap.FILE);
        Integer  objId = System.identityHashCode(file);
        JSONObject ret = new JSONObject();

        ObjectMap.toFileMap(map).put(objId, file);
        ret.put("id", objId);
        return ret;
    }

    private JSONObject clientInfoToJson(Client.Info info) throws JSONException {
        JSONObjectHolder<Client.Info> holder;

        holder = new JSONObjectHolder<Client.Info>(info);
        holder.put(Client.Info.userId)
              .put(Client.Info.name)
              .put(Client.Info.email)
              .put(Client.Info.phoneNo)
              .put(Client.Info.region);

        return holder.get();
    }

    private JSONObject driveInfoToJson(Drive.Info info) throws JSONException {
        JSONObjectHolder<Drive.Info> holder;

        holder = new JSONObjectHolder<Drive.Info>(info);
        holder.put(Drive.Info.driveId);

        return holder.get();
    }

    private JSONObject dirInfoToJson(Directory.Info info) throws JSONException {
        JSONObjectHolder<Directory.Info> holder;

        holder = new JSONObjectHolder<Directory.Info>(info);
        holder.put(Directory.Info.itemId)
              .put(Directory.Info.name)
              .put(Directory.Info.childCount);

        return holder.get();
    }

    private JSONObject fileInfoToJson(File.Info info) throws JSONException {
        JSONObjectHolder<File.Info> holder;

        holder = new JSONObjectHolder<File.Info>(info);
        holder.put(File.Info.itemId)
              .put(File.Info.name)
              .put(File.Info.size);

        return holder.get();
    }

    private JSONObject itemInfoToJson(ItemInfo info) throws JSONException {
        JSONObjectHolder<ItemInfo> holder;

        holder = new JSONObjectHolder<ItemInfo>(info);
        holder.put(ItemInfo.itemId)
              .put(ItemInfo.name)
              .put(ItemInfo.type)
              .put(ItemInfo.size);

        return holder.get();
    }

    private JSONObject childrenToJson(Children info) throws JSONException {
        JSONObjectHolder<ItemInfo> holder;
        JSONArray array = new JSONArray();

        for(ItemInfo itemInfo: info.getContent()) {
            holder = new JSONObjectHolder<>(itemInfo);
            holder.put(ItemInfo.itemId)
                  .put(ItemInfo.name)
                  .put(ItemInfo.type)
                  .put(ItemInfo.size);

            try {
                array.put(holder.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONObject json = new JSONObject();
        json.put("items", array);
        return json;
    }

    private JSONObject hiveVoidToJson(Void padding) throws JSONException {
        JSONObjectHolder<Void> holder;

        holder = new JSONObjectHolder<Void>(padding);
        holder.put();

        return holder.get();
    }

    private JSONObject lengthToJson(Length length) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("length", length.getLength());
        if (byteBuffer != null)
            json.put("data", new String(byteBuffer.array()));

        return json;
    }
}
