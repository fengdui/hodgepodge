egrep -i 'killed process' /var/log/messages 查看内核杀掉的进程号
日志
cat -n显示行号
more enter下一行 空格下一页 F下一屏 B上一屏
less /XX查找XX 高亮
tail -n 显示最后n行 -f不退出 持续显示
head -n显示开头几行
sort 按照字符排序 -n按照数字排序 -r逆序 -k 2 根据第二列排序 -t ' '分隔符
wc 字符统计 -l 行数 -c字节数 -L最长行的长度 -w多少个单词
uniq 行重复的次数 针对连续的2行 -c 每一行最前面加上该行出现次数 -u 只展示出现1次的行 -d展示重复的行
grep -c
find xxx -name -print
whereis 查找可执行文件的位置
expr 表达式求值
1.2.3.4的sshd的监听端口是22，如何统计1.2.3.4的sshd服务各种连接状态(TIME_WAIT/ CLOSE_WAIT/ ESTABLISHED)的连接数。
netstat -n | grep 1.2.3.4:22 | awk '/^tcp/ {++S[$NF]} END {for(a in S) print a, S[a]}’
netstat -lnpta | grep ssh | egrep “TIME_WAIT | CLOSE_WAIT | ESTABLISHED”

从已备份的suyun.2017-06-26.log.bz2日志中，找出包含关键字1.2.3.4的日志有多少条。
bzcat suyun.2017-06-26.log.bz2 | grep '1.2.3.4' | wc -l
bzgrep '1.2.3.4' suyun.2017-06-26.log.bz2 | wc -l
less suyun.2017-06-26.log.bz2 | grep '10.37.9.11' | wc -l
找大文件
find / -type f -name "*log*" | xargs ls -lSh | more
du -a / | sort -rn | grep log | more
find / -name '*log*' -size +1000M -exec du -h {} \;

显示server.conf 文件，屏蔽掉#号开头的注释行
sed -n '/^[#]/!p' server.conf
sed -e '/^#/d' server.conf
grep -v "^#" server.conf
压缩
tar -zcvf /opt/backup/shenjian.tar.gz \
-exclude /opt/web/suyun_web/logs \
/opt/web/suyun_web

磁盘操作
iotop -o
du -sch
df -h
du：disk usage
通过搜索文件来计算每个文件的大小然后累加得到的值。
df：disk free
通过文件系统来获取空间大小的信息。
如果用户删除了一个正在运行的应用程序所打开的某个目录下的文件：
du命令返回的值，显示出减去了该文件后的总大小
df命令返回的值，则不显示减去该文件后的大小（文件句柄还在被使用），直到该运行的应用程序关闭了这个打开的文件（才会真正释放空间）
dmesg
或
cat /var/log/message

pidstat -dt -p 1567 3000 3
dstat 1 10
iostat -x 3 3
lsof -p pid
cat  /proc/1567/io
df -h /capaa
printf "%x\n" 22542
jmap -heap 10765
jinfo -flags 9656
jmap -dump:format=b,file=/tmp/dump.hprof pid
jmap -histo:live 10765 | more
vmtool --action getInstances --className com.caucho.network.listen.TcpPort
--limit 3 --expand 4


lsof | grep deleted
echo "" > access.log
如何查看某个端口的连接情况
lsof -i :port or netstat -lap | fgrep port

线程数
ps -eLf | wc -l
pstree -p | wc -l
查占用cpu最多的线程
ps H -eo pid,pcpu | sort -nk2 | tail
最耗CPU的进程ID，对应的服务名是什么呢
ps aux | fgrep pid    ll /proc/pid

netstat -nap | grep '13306' | grep -v mysqld | grep EST |awk '{print $7}' | sort |uniq -c


