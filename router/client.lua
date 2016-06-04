local socket = require("socket")
local cjson = require("cjson")
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
    local cmd = 'lua download.lua "' .. msg['url'] .. '" ' .. id
    print("cmd = " .. cmd)
    os.execute(cmd)
end

local function cacheStateRequest(msg)
    print("cacheStateRequest")
end

local function main()
	local mac = getMac()
	print("mac addr = " .. mac)
	tcpClient  = socket.tcp()
	local ret = tcpClient:connect(host, port)

	tcpClient:send('{"type":"login_request", "mac":"' .. mac .. '"}\n')

	while true do
		print("\n")
		local response, status = tcpClient:receive()
		print("rece = " .. response)
		local msg = cjson.decode(response)
		if msg["type"] == "login_response" then
			print("login_response success = " .. msg["success"])

		elseif msg["type"] == "cache_request" then
			cacheRequest(msg)

		elseif msg["type"] == "cache_state_request" then
            cacheStateRequest(msg)
		end
	end
end

main()

