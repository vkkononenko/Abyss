package Models;

import javax.persistence.*;
import java.util.UUID;

@Entity
public class Content {

    @Id
    private UUID id;

    @Column(columnDefinition="bytea")
    private byte[] content;

    private String mimeType;

    private String name;

    public Content(UUID id, byte[] content, String mimeType, String name) {
        this.id = id;
        this.content = content;
        this.mimeType = mimeType;
        this.name = name;
    }

    Content() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
