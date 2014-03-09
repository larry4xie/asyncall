package my.asyncall.domain;

import java.util.List;

public interface Dao {
    public Model find(int sleep, String name);

    public List<Model> findAll(int sleep);
}
