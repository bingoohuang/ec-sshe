ec-sshe
=======

an easy java tool for batch non-interactive operations on multiple ssh remote hosts

just try to change sshe.conf(UTF-8 required) and run java -jar ec-sshe-x.x.x.jar -h for help。


## sshe.conf examples 1
```
*settings
charset= GBK

*hosts
10.142.194.155 webapp inddf6c
10.142.194.156
10.142.194.157

*operations
cd xxx/log
grep 18602506990 *.log

*collectors
[contains] .log:

```

## sshe.conf example 2
```
*settings
excludeLinePattern= ^\||^--

*hosts
10.142.194.155 webapp inddf6c
10.142.194.156
10.142.194.157

*operations
pkill -f flume
rm -fr n3r-flume-1.1-dist
[sftp] put ../n3r-flume-1.1-dist.tar.gz .
tar zxf n3r-flume-1.1-dist.tar.gz
rm n3r-flume-1.1-dist.tar.gz
cd n3r-flume-1.1-dist
nohup java -jar n3r-flume-1.1.jar -i > /dev/null &

```

## settings parameters of config
***charset***

GBK or UTF-8.
When undefined, the system default charset will used. (eg. Windows 7 CHS will use GBK)

**expect**

shell commands executed prompt, default is dollar($)

**excludeLinePattern**

an regular expression used to remove block welcome information to clean the output.

**ptyType**

vt100 or dump, default is vt100.

**confirm**

wait the user to confirm while operations are executing.
byOp: confirm every operation command.
byHost: confirm all operations of every host.
none: no need to confirm.

**confirmMaxWaitMillis**

the max waiting milliseconds to be confirmed.


## hosts
this part of config just list all the hosts and their user/password for operations be executed on line by line.
the ip, user and password is space splitted.
the hosts line can be ignore the user and password if they are same to the pre host.

## operations
The operations to be executed.
Thare are six types of operations can be defined.
the operation format is:
[operation type] command

** shell **

the default operation type.

** exec **

** sftp **

examples:
```
[sftp] put localpath/localfile remotepath
[sftp] get remotepath/remotefile localpath
```

** scp **

** sleep **

sleep for several milliseconds.
examples:
```
[sleep] 1000
```

** confirm **

examples:
```
[confirm]
[confirm] 3000
```

## collectors
format:
[contains] xyz
[not contains] xyz
[matches] regex
[not matches] regex

examples:
```
*collectors
[contains] No such file or directory
```

