xstr = {}

function xstr.endwith(str, substr)
    str_tmp = string.reverse(str)
    substr_tmp = string.reverse(substr)
    if string.find(str_tmp, substr_tmp) ~= 1 then
        return false
    else
        return true
    end
end

function xstr.trim(s) 
    return (string.gsub(s, "^%s*(.-)%s*$", "%1")) 
end 

function xstr.startwith(str, substr)
    if string.find(str, substr) ~= 1 then
        return false
    else
        return true
    end
end

function xstr.split(s, delim)
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

function xstr.splitPath(path, delim)
    local rev = string.reverse(path)
    local index = string.find(rev, delim)
    local dir = string.sub(path, 1, -index)
    local file = string.sub(path, -(index-1), -1)
    return dir, file
end

return xstr
