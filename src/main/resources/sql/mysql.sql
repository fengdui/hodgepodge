--判断首字符中英文 就是英文，大于1就是中文;
length(left(column_name,1))=1
--列中等待拿锁的线程:
SELECT * FROM information schema. INNODB TRX WHERE trx state=’LOCK WAIT ’;