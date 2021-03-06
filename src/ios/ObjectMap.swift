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
 
import Foundation
import ElastosHiveSDK

private let clientMap = ClientMap()
private let driveMap  = DriveMap()
private let dirMap    = DirMap()
private let fileMap   = FileMap()

let CLIENT: Int = 1
let DRIVE : Int = 2
let DIR   : Int = 3
let FILE  : Int = 4

/*
class ObjectMap<T> {
    var objMap = Dictionary<Int, T>()

    func get(_ index: Int) -> T {
        return objMap[index]!
    }

    func put(_ index: Int, _ value: T) {
        objMap[index] = value
    }
}*/

class ObjectMap {}

class ClientMap: ObjectMap {
    private var map = Dictionary<Int, HiveClientHandle>()
    private static var curObjId: Int = 0

    class func nextObjId() -> Int {
        curObjId += 1
        return curObjId
    }

    func get(_ index: Int) -> HiveClientHandle {
        return map[index]!
    }

    func put(_ index: Int, _ value: HiveClientHandle) {
        map[index] = value
    }
}

class DriveMap: ObjectMap {
    private var map = Dictionary<Int, HiveDriveHandle>()
    private static var curObjId: Int = 0

    class func nextObjId() -> Int {
        curObjId += 1
        return curObjId
    }

    func get(_ index: Int) -> HiveDriveHandle {
        return map[index]!
    }

    func put(_ index: Int, _ value: HiveDriveHandle) {
        map[index] = value
    }
}

class DirMap: ObjectMap {
    private var map = Dictionary<Int, HiveDirectoryHandle>()
    private static var curObjId: Int = 0;

    class func nextObjId() -> Int {
        curObjId += 1
        return curObjId
    }

    func get(_ index: Int) -> HiveDirectoryHandle {
        return map[index]!
    }

    func put(_ index: Int, _ value: HiveDirectoryHandle) {
        map[index] = value
    }
}

class FileMap: ObjectMap {
    private var map = Dictionary<Int, HiveFileHandle>()
    private static var curObjId: Int = 0;

    class func nextObjId() -> Int {
        curObjId += 1
        return curObjId
    }

    func get(_ index: Int) -> HiveFileHandle {
        return map[index]!
    }

    func put(_ index: Int, _ value: HiveFileHandle) {
        map[index] = value
    }
}

class ObjectMapHolder {
    class func acquire(_ mapId: Int) -> ObjectMap? {
        var objMap: ObjectMap?

        switch(mapId) {
        case CLIENT:
            objMap = clientMap as ObjectMap

        case DRIVE:
            objMap = driveMap as ObjectMap

        case DIR:
            objMap = dirMap as ObjectMap

        case FILE:
            objMap = fileMap as ObjectMap

        default:
            objMap = nil
        }

        return objMap
    }

    class func isClientMap(_ map: ObjectMap?) -> Bool {
        return map != nil && map is ClientMap
    }

    class func isDriveMap(_ map: ObjectMap?) -> Bool {
        return map != nil && map is DriveMap
    }

    class func isDirMap(_ map: ObjectMap?) -> Bool {
        return map != nil && map is DirMap
    }

    class func isFileMap(_ map: ObjectMap?) -> Bool {
        return map != nil && map is FileMap
    }

    class func asClientMap(_ map: ObjectMap) -> ClientMap {
        return map as! ClientMap
    }

    class func asDriveMap(_ map: ObjectMap) -> DriveMap {
        return map as! DriveMap
    }

    class func asDirMap(_ map: ObjectMap) -> DirMap {
        return map as! DirMap
    }

    class func asFileMap(_ map: ObjectMap) -> FileMap {
        return map as! FileMap
    }
}
