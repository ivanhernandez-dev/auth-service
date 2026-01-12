package dev.ivanhernandez.authservice.application.port.output;

public interface TokenBlacklist {

    void blacklist(String tokenId, long expirationSeconds);

    boolean isBlacklisted(String tokenId);
}
