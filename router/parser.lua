local http = require("socket.http")
local ltn12 = require("ltn12")

local m3u8url = arg[1]
local taskdir = arg[2]

local function httpget(u)
	local t = {}
	local r, c, h = http.request {
		url=u,
		sink=ltn12.sink.table(t)}
	return r,c,h,table.concat(t)
end

local function startwith(str, substr)
    if string.find(str, substr) ~= 1 then
        return false
    else
        return true
    end
end

local function endwith(str, substr)
    if str == nil or substr == nil then
        return nil, "the string or the sub-string parameter is nil"
    end
    str_tmp = string.reverse(str)
    substr_tmp = string.reverse(substr)
    if string.find(str_tmp, substr_tmp) ~= 1 then
        return false
    else
        return true
    end
end

local function main()
    --get m3u8
    print("m3u8url = " .. m3u8url)

	local r,c,h,body=httpget(m3u8url)
	if c ~= 200 then
		print("get m3u8 info error!")
	end
	local m3u8file = io.open(taskdir .. "/m3u8", "w")
    --print(body)
	m3u8file:write(body)
	m3u8file:close()

    --parse m3u8 get first level urls
    local firstlevel = {}
    local candown = false
    for line in io.lines(taskdir .. "/m3u8")
    do
        if not startwith(line, "#") then
            if startwith(line, "http") then 
                candown = true
            end
            table.insert(firstlevel, line)
        end
    end

    if candown then
        local file = io.open(taskdir .. "/download.links", "w")
        for key,value in ipairs(firstlevel) do
            file:write(value)
            file:write("\n")
        end
        file:close()
    end

end

main()
