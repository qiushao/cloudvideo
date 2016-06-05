local socket = require("socket")
local cjson = require("cjson")
local xstr = require("xstr")
local tcpClient = nil
local host = "10.117.3.22"
local port = 11111

local function getMac()
	os.execute("sh mac.sh")
	local fd = io.open("mac.txt", "r")
	local mac = fd:read()
    --return mac
    return "D4:EE:07:12:7E:06"
end

local function getNextTaskID()
    local file = io.open("taskid", "r")
    local id = file:read("*n")
    file:close()

    file = io.open("taskid", "w")
    file:write(id+1)
    file:close()

    return id
end

local function saveTask(msg, id)
    file = io.open("tasklist", "a")
    file:write(id .. ">" .. msg["title"] .. ">" .. msg["url"] .. ">0")
    file:write("\n")
    file:close()
end

local function cacheRequest(msg)
    local id = getNextTaskID()
    saveTask(msg, id)
    local cmd = 'lua download.lua "' .. msg['url'] .. '" ' .. id .. ' &'
    print("cmd = " .. cmd)
    os.execute(cmd)
end

local function cacheStateRequest(msg)
    print("cacheStateRequest")
    local res = {}
    res.type = "cache_state_response"
    res.phoneid = msg['phoneid']
    res.data = {}

    for line in io.lines("tasklist")
    do
        info = xstr.split(line, ">")
        local video = {}
        video.title = info[2]
        video.url = info[3]
        video.progress = 50
        video.state = "dowloading"
        video.reason = "foobar"
        table.insert(res.data, video)
    end

    json = cjson.encode(res)
    print("send: " .. json)
	tcpClient:send(json .. '\n')
end

local function main()
    print("cloud video client running...")
	tcpClient  = socket.tcp()
	local ret = tcpClient:connect(host, port)
    print("connect server success...")

    local msg = {}
    msg.type = "login_request"
    msg.mac = getMac()
    local json = cjson.encode(msg)
	tcpClient:send(json .. '\n')
    print("send msg to server: " .. json)

	while true do
		print("\n")
		local response, status = tcpClient:receive()
		print("rece msg from server: " .. response)
		local msg = cjson.decode(response)
		if msg["type"] == "login_response" then
			print("login_response success = " .. msg["success"])

		elseif msg["type"] == "cache_request" then
			cacheRequest(msg)

		elseif msg["type"] == "cache_state_request" then
            cacheStateRequest(msg)
            print("phoneid = " .. msg['phoneid'])
		end
	end
end

main()

