package my.asyncall.domain;

import java.io.Serializable;

public class Model implements Serializable {

    private static final long serialVersionUID = -5410019316926096126L;

    public Model(int id, String name, String detail) {
        this.id = id;
        this.name = name;
        this.detail = detail;
    }

    public int id;
    public String name;
    public String detail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "Model [detail=" + detail + ", id=" + id + ", name=" + name + "]";
    }
}
