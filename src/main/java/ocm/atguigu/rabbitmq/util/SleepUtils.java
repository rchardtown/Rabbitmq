package ocm.atguigu.rabbitmq.util;

/**
 * @author cuitao
 * @ className:
 * @ description:
 * @ create 2021-08-20 17:25
 **/
public class SleepUtils {
    public static void sleep(int second){
        try {
            Thread.sleep(1000*second);
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    } }