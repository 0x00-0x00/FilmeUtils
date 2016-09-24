package filmeUtils.utils.http;

import java.io.Serializable;
import java.util.Date;

import org.apache.http.cookie.Cookie;

public class SerializableCookie implements Cookie, Serializable {

    private static final long serialVersionUID = 1L;
    private final String name;
    private final String value;
    private final String comment;
    private final String commentUrl;
    private final Date expiryDate;
    private final boolean persistent;
    private final String domain;
    private final String path;
    private final int[] ports;
    private final boolean secure;
    private final int version;

    public SerializableCookie(final Cookie cookie) {
        name = cookie.getName();
        value = cookie.getValue();
        comment = cookie.getComment();
        commentUrl = cookie.getCommentURL();
        expiryDate = cookie.getExpiryDate();
        persistent = cookie.isPersistent();
        domain = cookie.getDomain();
        path = cookie.getPath();
        ports = cookie.getPorts();
        secure = cookie.isSecure();
        version = cookie.getVersion();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public String getCommentURL() {
        return commentUrl;
    }

    @Override
    public Date getExpiryDate() {
        return expiryDate;
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int[] getPorts() {
        return ports;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public boolean isExpired(final Date date) {
        return date.after(expiryDate);
    }

}
