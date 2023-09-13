package okestro.internproject;

import org.junit.jupiter.api.Test;

class InternProjectApplicationTests {


    @Test
    void contextLoads() {
        float num = 1f;
        System.out.println(Math.ceil(num / 10));
        System.out.println(Math.ceil(num / 100));
        System.out.println(Math.ceil(num / 1000));
        System.out.println(Math.ceil(num / 10000));
        System.out.println(Math.ceil(num / 100000));
        System.out.println(Math.ceil(num / 1000000));
        System.out.println(Math.ceil(num / 10000000));
        System.out.println(Math.ceil(num / 100000000));
        System.out.println(Math.ceil(num / 1000000000));
        System.out.println(num / 1000000000);

        System.out.println(Math.ceil(num / 10000000000L));
        System.out.println(Math.ceil(num / 100000000000L));
        System.out.println(Math.ceil(num / 1000000000000L));
        System.out.println(Math.ceil(num / 10000000000000L));

        System.out.println((double) 312884470 / 312884469);

    }

}
