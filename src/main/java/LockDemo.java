import java.util.concurrent.locks.Lock;

public class LockDemo {


    public static void main(String[] args) {
        Lock lock = new KingLock();

        int ticket = 500;

        lock.lock();

    }
}
