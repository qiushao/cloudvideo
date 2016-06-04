ifconfig | grep HWaddr | head -n1 | awk '{print $5}' > mac.txt
