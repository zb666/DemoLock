import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

public class KingLock implements Lock {

    //----------轮询等待 线程抢锁.锁的本质是让未抢到锁的线程去等待

    //CAS原始操作类，记录谁是拥有者
    AtomicReference<Thread> owner = new AtomicReference<Thread>();
    //队列 --- 存放没有抢到锁的线程 多线程+安全
    Queue<Thread> waiters = new LinkedBlockingQueue<Thread>();

    //实现lock方法，加锁的方式
    public void lock() {
        //--希望是null,放入当前线程，多线程抢资源

        //没有抢到锁的线程
        while (!owner.compareAndSet(null, Thread.currentThread())) {
            //让线程阻塞
            waiters.add(Thread.currentThread());
            //线程阻塞
//            waiters.wait(); 要用同步代码块
            LockSupport.park(); //停车 -- 当前线程阻塞
            waiters.remove(Thread.currentThread());//停车中的线程被unpark唤醒了.
        }
    }

    //实现unlock方法，解锁的方式
    public void unlock() {
        //判断持有锁的线程才能成功解锁 CAS多线程安全操作

        //来的那个人是当前线程 ---> null 解锁成功
        if(owner.compareAndSet(Thread.currentThread(),null)){
            //解锁成功->去唤醒其他等待的队列中的线程
            for (Object object : waiters.toArray()) {
                Thread thread = (Thread) object;
                LockSupport.unpark(thread)   ;
            }
        }

    }

    public void lockInterruptibly() throws InterruptedException {

    }

    public boolean tryLock() {
        return false;
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }


    public Condition newCondition() {
        return null;
    }
}
