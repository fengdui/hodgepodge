insert 语句会先在插入间隙上加上插入意向锁，然后开始写数据，写完数据之后再对记录加上 X 记录锁

insert ……on duplicate key update 的加锁流程其实就是 insert 和 update 的结合，
如果没有唯一键冲突，和 insert 加锁流程一样，如果有唯一键冲突，会对唯一键加 Next-Key 锁（lock_mode X），
对主键加记录锁（lock_mode X locks rec but not gap），然后再执行 update 操作。

记录锁（LOCK_REC_NOT_GAP）: lock_mode X locks rec but not gap
间隙锁（LOCK_GAP）: lock_mode X locks gap before rec
Next-key 锁（LOCK_ORNIDARY）: lock_mode X
插入意向锁（LOCK_INSERT_INTENTION）: lock_mode X locks gap before rec insert intention

如果在 supremum record 上加锁，
locks gap before rec 会省略掉，间隙锁会显示成 
lock_mode X，插入意向锁会显示成 
lock_mode X insert intention。
间隙锁和间隙锁并不冲突

insert on duplicate key update 如果命中主键或者唯一键索引，加行锁，未命中加gap锁，即会阻塞插入数据
bug在5.7.26以及8.0.15版本上已经修复了，当插入数据时，不会在形成间隙锁 