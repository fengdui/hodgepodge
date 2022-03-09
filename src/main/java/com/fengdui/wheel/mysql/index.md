覆盖索引
延迟关联
通过使用覆盖索引查询返回需要的主键,再根据主键关联原表获得需要的数据。


eq_ref 关联查询 使用唯一索引或者主键
ref 普通索引

using where是指优化器需要通过索引回表查询数据
using index 索引覆盖
using index condition 查询的列不全在索引中，where条件中是一个前导列的范围



possible_keys：可能在哪个索引找到记录。
key：实际使用索引。
ref：哪些列，或者常量用于查找索引上的值。