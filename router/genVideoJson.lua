local xstr = require("xstr")
local cjson = require("cjson")

local function genVideoJson()
    local msg = {}
    msg.type = "cached_list"
    msg.videos = {}

    for line in io.lines("tasklist")
    do
        if xstr.endwith(line, ">1") then
            info = xstr.split(line, ">")
            local video = {}
            video.title = info[2]
            video.url = "http://192.168.199.1/cloudvideo/" .. info[1] .. "/out.ts"
            table.insert(msg.videos, video)
        end
    end

    local json = cjson.encode(msg)
    print(json)
    local file = io.open("/tmp/storage/mmcblk0p2/cloudvideo/videos.json", "w")
    file:write(json)
    file:close()
end

genVideoJson()
