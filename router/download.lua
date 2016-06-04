local m3u8url = arg[1]
local taskid = arg[2]
local taskdir = "/tmp/storage/mmcblk0p2/cloudvideo/" .. taskid

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

local function execmd(cmd)
    print("execute cmd: " .. cmd)
    os.execute(cmd)
    local file = io.open(taskdir .. "/cmd", "a")
    file:write(cmd)
    file:write("\n")
    file:close()
end

local function down(url, path)
    cmd = 'curl -o ' .. path .. ' "' .. url .. '"'
    execmd(cmd)
end

local function saveProgress(now, total)
    local file = io.open(taskdir .. "/progress", "w")
    file:write(now / total)
    file:close()
end

local function setDownloadFinish()
    os.execute("sh setCachedFinish.sh " .. taskid)
end

local function genVideoJson()
    os.execute("lua genVideoJson.lua")
end

local function getFileLines(path)
    cmd = "wc -l " .. path .. " | awk '{print $1}'"
    local f = io.popen(cmd)
    local count = f:read("*n")
    print("total lines:" .. count)
    return count
end

local function main()
    --create task dir base on taskid
    cmd="mkdir -p " .. taskdir
    execmd(cmd)

    --if is already mp4 os ts file, just download it, no need to parser
    if endwith(m3u8url, ".ts") then
        down(m3u8url, taskdir .. "/out.ts")
        saveProgress(1,1)
    else
        --parser m3u8 url, real download links will be output to download.links file
        os.execute('lua parser.lua "' .. m3u8url .. '" ' .. taskdir)
        index = 0
        count = getFileLines(taskdir .. "/download.links")
        for line in io.lines(taskdir .. "/download.links") do
            line = trim(line)
            out = taskdir .. "/out" .. index .. ".ts"
            print("down load " .. line .. " into " .. out)
            down(line, out)
            index = index + 1 
            saveProgress(index, count)
        end

        -- merge all ts
        count = index
        index = 0
        while index < count
        do
            cmd = 'cat ' .. taskdir .. '/out' .. index .. '.ts >> ' .. taskdir .. '/out.ts'
            execmd(cmd)
            index = index + 1
        end

        setDownloadFinish()
        genVideoJson()
    end
end

main()
