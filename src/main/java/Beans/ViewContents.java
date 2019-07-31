package Beans;

import Models.Content;
import Models.POST.Ecryptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.logging.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static java.util.stream.Collectors.joining;
import static net.bytebuddy.matcher.ElementMatchers.is;

@Named
@ViewScoped
public class ViewContents implements Serializable {

    public static Logger log = Logger.getLogger(ViewContents.class.getName());

    @PersistenceContext(name = "abyss")
    private EntityManager em;
    private List<Content> content;
    private UploadedFile file;
    private ObjectMapper mapper = new ObjectMapper();

    @Transactional
    public void onLoad() {
        log.info("=> onLoad");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery(Content.class);
        Root<Content> root = cq.from(Content.class);
        content = em.createQuery(cq).getResultList();
        log.info("=< onLoad");
    }

    //http://localhost:8080/abyss/rest/getContent?id=431f591a-7014-37dd-9a0f-4fd80060466e&key=(ыCN:€F~Nd-X",&iv=цяЃФ›Д$ТI«Ё5e3Ґ
    @Transactional
    public void handleFileUpload(FileUploadEvent event) throws IOException {
        log.info("File upload");
        try {
            SecureRandom rnd = new SecureRandom();
            IvParameterSpec iv = new IvParameterSpec(rnd.generateSeed(16));
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128);
            SecretKey k = generator.generateKey();
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, k, iv);
            byte[] encrypt = c.doFinal(event.getFile().getContents());
            Content content = new Content(UUID.nameUUIDFromBytes(encrypt), encrypt, event.getFile().getContentType(), event.getFile().getFileName());
            em.persist(content);
            log.info(new String(k.getEncoded()));
            log.info(new String(iv.getIV()));
            log.info(encodeValue("http://localhost:8080/abyss/rest/getContent", content.getId().toString(), new String(k.getEncoded()), new String(iv.getIV())));
            log.info("File saved");
            FacesContext.getCurrentInstance().getExternalContext().redirect("start.xhtml");
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
    }

    private String encodeValue(String host, String id, String key, String iv) throws UnsupportedEncodingException {
        return  host.concat("?id=").concat(id)
                .concat("&key=").concat(URLEncoder.encode(key, StandardCharsets.UTF_8.toString()))
                .concat("&iv=").concat(URLEncoder.encode(iv, StandardCharsets.UTF_8.toString()));
    }

    public static byte[] asBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    public List<Content> getContent() {
        return content;
    }

    public void setContent(List<Content> content) {
        this.content = content;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
}
