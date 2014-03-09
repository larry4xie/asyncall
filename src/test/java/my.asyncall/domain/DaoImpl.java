package my.asyncall.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DaoImpl implements Dao {
    private static int id = 1
            ;
    @Override
    public Model find(int sleep, String name) {
        if (sleep > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new Model(id++, name, name);
    }

    @Override
    public List<Model> findAll(int sleep) {
        if (sleep > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<Model> models = new ArrayList<Model>(10);
        for(int i = 1; i <= 10; i++) {
            models.add(new Model(i, i + "", i + ""));
        }

        return models;
    }
}
