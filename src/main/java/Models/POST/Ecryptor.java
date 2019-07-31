package Models.POST;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Ecryptor implements Serializable {

    @JsonProperty("id")
    private String id;

    @JsonProperty("key")
    private String key;

    @JsonProperty("iv")
    private String iv;

    public Ecryptor(String id, String key, String iv) {
        this.id = id;
        this.key = key;
        this.iv = iv;
    }

    Ecryptor() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}
