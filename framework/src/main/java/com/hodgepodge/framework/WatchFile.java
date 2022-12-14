package com.hodgepodge.framework;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class WatchFile {

    private WatchService watcher;

    public WatchFile(Path path) throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    }

    public void handleEvents() throws InterruptedException {
        while (true) {
            WatchKey key = watcher.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == OVERFLOW) { //事件可能是lost or discarded
                    continue;
                }

                WatchEvent<Path> e = (WatchEvent<Path>) event;
                Path fileName = e.context();

                System.out.println("happen: " + kind.name() + "----" + fileName);
                System.out.println("=======================");
            }

            if (!key.reset()) {
                break;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        new WatchFile(Paths.get("E:\\test")).handleEvents();
    }

	/*
	 情况1 文件操作：
	操作：新建一个文本文档，
	happen: ENTRY_CREATE----新建文本文档.txt
	=======================

	操作：改名为1
	happen: ENTRY_DELETE----新建文本文档.txt
	=======================
	happen: ENTRY_CREATE----1.txt
	=======================
	happen: ENTRY_MODIFY----1.txt
	=======================

	操作：修改内容
	happen: ENTRY_MODIFY----1.txt
	=======================

	操作：删除
	happen: ENTRY_DELETE----1.txt
	=======================

	情况2：文件夹操作
	操作：新建文件夹
	happen: ENTRY_CREATE----新建文件夹
	=======================

	操作：改名文件夹
	happen: ENTRY_DELETE----新建文件夹
	=======================
	happen: ENTRY_CREATE----2
	=======================

	操作：删除文件夹
	happen: ENTRY_DELETE----2
	=======================

	情况3：文件夹中文件的相关操作
	操作：文件夹中建立、删除、修改文件，只会报如下错误
	happen: ENTRY_MODIFY----1
	*/
}
