import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Restaurant
{

    private final int tables = 3; // Количество столиков
    private int occupiedTables = 0; // Занятые столики
    private final Lock lock = new ReentrantLock();

    public void enter(String visitorName) {
        lock.lock();
        try {
            while (occupiedTables >= tables) {
                System.out.println(visitorName + " ждет, пока освободится столик.");
                lock.unlock(); // Освобождаем блокировку перед ожиданием
                synchronized (this) {
                    try {
                        wait(); // Ожидаем, пока освободится столик
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Восстанавливаем статус прерывания
                        return; // Выходим из метода, если поток прерван
                    }
                }
                lock.lock(); // Получаем блокировку снова
            }
            occupiedTables++;
            System.out.println(visitorName + " занял столик. Занято столиков: " + occupiedTables);
        } finally {
            lock.unlock();
        }
    }

    public void leave(String visitorName) {
        lock.lock();
        try {
            occupiedTables--;
            System.out.println(visitorName + " покинул столик. Занято столиков: " + occupiedTables);
            synchronized (this) {
                notify(); // Уведомляем ожидающих посетителей
            }
        } finally {
            lock.unlock();
        }
    }
}

class Visitor extends Thread {
    private final Restaurant restaurant;

    public Visitor(Restaurant restaurant, String name) {
        super(name);
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        restaurant.enter(getName());
        try {
            // Симуляция времени, проведенного за столиком
            Thread.sleep((long) (Math.random() * 5000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            restaurant.leave(getName());
        }
    }
}