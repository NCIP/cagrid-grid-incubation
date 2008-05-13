package org.cagrid.tide.tools.client.retriever.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import org.cagrid.tide.descriptor.Current;
import org.cagrid.tide.descriptor.TideDescriptor;

import com.twmacinta.util.MD5InputStream;


/**
 * The CurrentWriter is a Runnable class that maintains a Queue of collected
 * data chunks that needs to be written to specific areas of a file.
 * 
 * @author hastings
 */
public class CurrentWriter implements Runnable {

    public class CurrentHolder {
        public CurrentCollector collector;
        public int currentIndex;


        public CurrentHolder(CurrentCollector collector, int currentIndex) {
            this.currentIndex = currentIndex;
            this.collector = collector;
        }


        public CurrentCollector getCollector() {
            return collector;
        }


        public int getCurrentIndex() {
            return currentIndex;
        }
    }

    private File outputFile = null;
    private ArrayBlockingQueue<CurrentHolder> currents = new ArrayBlockingQueue<CurrentHolder>(20);
    private RandomAccessFile raf = null;
    private final TideDescriptor tide;
    private boolean failed = false;
    private int chunksProcessed = 0;
    private long totalDataWriteTime = 0;
    private FailedWriterCallback callback;
    CurrentCollector currentCurrentCollector = null;
    int currentCurrentCollectorIndex = -1;


    public CurrentWriter(File outputFile, final TideDescriptor tide, FailedWriterCallback callback) throws Exception {
        this.outputFile = outputFile;
        this.tide = tide;
        this.callback = callback;
        raf = new RandomAccessFile(outputFile, "rw");
    }


    public void addCurrentCollector(CurrentCollector current, int currentIndex) throws Exception {
        this.currents.put(new CurrentHolder(current, currentIndex));
    }


    public CurrentCollector getCurrentCurrentCollector() {
        return currentCurrentCollector;
    }


    public void run() {
        int sleepCount = 0;
        while (chunksProcessed < tide.getTideInformation().getChunks()) {
            CurrentHolder holder = currents.poll();
            if (holder != null) {
                long start = System.currentTimeMillis();
                sleepCount = 0;

                this.currentCurrentCollector = holder.getCollector();
                this.currentCurrentCollectorIndex = holder.getCurrentIndex();
                System.out.println("Writting chunk "
                    + currentCurrentCollector.getCurrent(this.currentCurrentCollectorIndex).getChunkNum());
                Current current = this.tide.getCurrents().getCurrent(
                    currentCurrentCollector.getCurrent(this.currentCurrentCollectorIndex).getChunkNum());
                try {
                    raf.seek(current.getOffset());
                    Iterator<byte[]> it = currentCurrentCollector.getCurrentByteArrayData(
                        this.currentCurrentCollectorIndex).iterator();
                    while (it.hasNext()) {
                        raf.write(it.next());
                    }
                    chunksProcessed++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long stop = System.currentTimeMillis();
                this.totalDataWriteTime += (stop - start);
            } else {
                try {
                    System.out.println("Writer sleeping for " + (1 * sleepCount) + " seconds...");

                    Thread.sleep(1000 * sleepCount++);
                    if (sleepCount > 5) {
                        sleepCount--;
                    }

                } catch (InterruptedException e) {
                    failed = true;
                    e.printStackTrace();
                    if (callback != null) {
                        callback.failedWriter(this);
                    }
                }
            }
        }

        // check the md5sum of the new file
        try {
            MD5InputStream mis = new MD5InputStream(new FileInputStream(outputFile));
            byte[] buf = new byte[65536];
            int num_read;
            while ((num_read = mis.read(buf)) != -1);
            mis.getMD5().asHex();
            if (!mis.getMD5().asHex().equals(tide.getTideInformation().getMd5Sum())) {
                throw new Exception("File download was corrupted!");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (callback != null) {
                callback.failedWriter(this);
            }
        }

    }

}
