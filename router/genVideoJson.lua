local function endwith(str, substr)
    str_tmp = string.reverse(str)
    substr_tmp = string.reverse(substr)
    if string.find(str_tmp, substr_tmp) ~= 1 then
        return false
    else
        return true
    end
end

local function trim(s) 
    return (string.gsub(s, "^%s*(.-)%s*$", "%1")) 
end 

local function split(s, delim)
    local start = 1
    local t = {}
    while true do
        local pos = string.find (s, delim, start, true) -- plain find
        if not pos then
            break
        end

        table.insert (t, string.sub (s, start, pos - 1))
        start = pos + string.len (delim)
    end
    table.insert (t, string.sub (s, start))
    return t
end

local function genVideoJson()
    local json = '{"type":"cached_list", "videos":['
    for line in io.lines("tasklist")
    do
        if endwith(line, ">1") then
            info = split(line, ">")
            local url = "http://192.168.199.1/cloudvideo/" .. info[1] .. "/out.ts"
            json = json .. '{"title":"' .. info[2] .. '", ' 
            json = json .. '"url":"' .. url .. '"},'
        end
    end
    json = string.sub(json, 1, -2)
    json = json .. ']}'
    print(json)
    local file = io.open("/tmp/storage/mmcblk0p2/cloudvideo/videos.json", "w")
    file:write(json)
    file:close()
end

genVideoJson()
