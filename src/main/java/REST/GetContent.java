package REST;

import Beans.ViewContents;
import Models.Content;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Logger;

@Path("/getContent")
@RequestScoped
public class GetContent implements Serializable {

    public static Logger log = Logger.getLogger(ViewContents.class.getName());

    @PersistenceContext(name = "abyss")
    private EntityManager em;

    @GET
    @Produces(value="video/mp4")
    public byte[] getContent(@QueryParam("id") String id, @QueryParam("key") String key, @QueryParam("iv") String iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        UUID ids = UUID.fromString(id);
        log.info(decode(key));
        log.info(decode(iv));
        SecretKey originalKey = new SecretKeySpec(decode(key).getBytes(), 0, decode(key).getBytes().length, "AES");
        IvParameterSpec originalIvSpec = new IvParameterSpec(decode(iv).getBytes());
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, originalKey, originalIvSpec);
        Content content = em.find(Content.class, ids);
        log.info(content.getName());
        return c.doFinal(content.getContent());
    }

    private String decode(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value.replace("%", "%25"), StandardCharsets.UTF_8.toString());
    }
}
