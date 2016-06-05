local http = require("socket.http")
local ltn12 = require("ltn12")
local xstr = require("xstr")

local m3u8url = arg[1]
local taskdir = arg[2]

local function httpget(u)
	local t = {}
	local r, c, h = http.request {
		url=u,
		sink=ltn12.sink.table(t)}
	return r,c,h,table.concat(t)
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
    local dir, _ = xstr.splitPath(m3u8url, '/')
    print("pre path = " .. dir)
    for line in io.lines(taskdir .. "/m3u8")
    do
        if not xstr.startwith(line, "#") then
            if not xstr.startwith(line, "http") then 
                line = dir .. line
            end
            table.insert(firstlevel, line)
        end
    end

    local file = io.open(taskdir .. "/download.links", "w")
    for key,value in ipairs(firstlevel) do
        file:write(value)
        file:write("\n")
    end
    file:close()
end

main()
