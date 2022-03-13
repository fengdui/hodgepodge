CMS GC发生的场景

扩容缩容会触发 CMS GC
Old 区达到回收阈值
MetaSpace 空间不足
Young 区晋升失败
大对象担保失败

只单独回收Old区的只有CMS GC
CMS中需要设置了-XX:+CMSScavengeBeforeRemark才会在发生CMS的remark阶段触发一次YGC

一般CMS的GC耗时 80%都在remark阶段


cms 出现allocation failure 表示fullgc