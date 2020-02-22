package task;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FileScanner {

    //1.核心线程数：始终运行的线程数量
    //2.最大的线程数：有新任务，并且当前运行的线程数小于最大线程数，会创建新的线程来处理任务（正式工 + 零时工）
    //3、4.超过3这个数量，4这个时间单位，2-1（最大线程数-核心线程数）这些线程（零时工）就会关闭
    //5.工作的阻塞队列
    //6.如果超出工作队列的长度，任务要处理的方式(4种策略需要知道)
    private ThreadPoolExecutor pool = new ThreadPoolExecutor(
            3,3,0, TimeUnit.MICROSECONDS,
            new LinkedBlockingDeque<>(),new ThreadPoolExecutor.AbortPolicy()
    );
    //这是一种快捷创建的方式
    //private ExecutorService exe = Executors.newFixedThreadPool(4);

    //计数器，不传入数值表示初始化为0
    private volatile AtomicInteger count = new AtomicInteger();

    //线程等待的锁对象
    private Object lock = new Object();//第一种：synchronized

    private ScanCallback callback;
    public FileScanner(ScanCallback callback) {
        this.callback = callback;
    }

    //  private CountDownLatch latch = new CountDownLatch(1);//第二种：await阻塞等待
  //  private Semaphore semaphore = new Semaphore(0);//第三章acquire()阻塞等待
    /**
     * 扫描文件目录
     * @param path
     */
    public void scan(String path) {
        count.incrementAndGet();//启动根目录扫描任务计数器i++
        doScan(new File(path));
    }
    private void doScan(File dir) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.callback(dir);
                    File[] children = dir.listFiles(); //下级文件和文件夹
                    if (children != null) {
                        for (File child : children) {
                            if (child.isDirectory()) {//如果是文件夹，递归处理
                                count.incrementAndGet();//启动子文件夹扫描任务计数器i++
                                System.out.println("当前任务数："+count.get());
                                doScan(child);
                            }
                        }
                    }
                }finally {
                    //保证线程计数不管是否出现异常都能进行-1操作
                    int r = count.decrementAndGet();
                    if (r == 0) {
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                }
            }
        });
    }

    /**
     * 等待扫瞄任务结束scan方法
     * 多线程的任务等待 thread.start
     * 1.join():需要使用线程Thread类的引用对象
     * 2.wait()线程间通信
     */
    public void waitFinish() throws InterruptedException {
        try {
            synchronized (lock){
                lock.wait();
            }
        } finally {
            //阻塞等待直到任务完成，完成需要关闭线程池
            System.out.println("关闭线程池");
            //内部实现原理，是通过内部的Thread.interrupt()来中断
            pool.shutdownNow();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                synchronized (FileScanner.class) {
                    FileScanner.class.notifyAll();
                }
            }
        });
        t.start();
        synchronized (FileScanner.class) {
                FileScanner.class.wait();
        }
        System.out.println(Thread.currentThread().getName());
    }
}
