/**
 * @author: nthienan
 */

package agu.thesis2015.security.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;

import agu.thesis2015.domain.User;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class TokenUtils {
	public static final String MAGIC_KEY = "nth!3n@N#N@n3!htn";

	public static String createToken(UserDetails userDetails) {
		/* Expires in one hour */
		long expires = System.currentTimeMillis() + 1000L * 60 * 60;

		StringBuilder tokenBuilder = new StringBuilder();
		tokenBuilder.append(userDetails.getUsername());
		tokenBuilder.append(":");
		tokenBuilder.append(expires);
		tokenBuilder.append(":");
		tokenBuilder.append(TokenUtils.computeSignature(userDetails, expires));

		return tokenBuilder.toString();
	}

	public static String computeSignature(UserDetails userDetails, long expires) {
		StringBuilder signatureBuilder = new StringBuilder();
		signatureBuilder.append(userDetails.getUsername());
		signatureBuilder.append(":");
		signatureBuilder.append(expires);
		signatureBuilder.append(":");
		signatureBuilder.append(userDetails.getPassword());
		signatureBuilder.append(":");
		signatureBuilder.append(TokenUtils.MAGIC_KEY);

		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm available!");
		}

		return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
	}

	public static String getUserNameFromToken(String authToken) {
		if (null == authToken) {
			return null;
		}

		String[] parts = authToken.split(":");
		return parts[0];
	}

	public static boolean validateToken(String authToken, UserDetails userDetails) {
		String[] parts = authToken.split(":");
		long expires = Long.parseLong(parts[1]);
		String signature = parts[2];

		if (expires < System.currentTimeMillis()) {
			return false;
		}

		return signature.equals(TokenUtils.computeSignature(userDetails, expires));
	}

	public static String createActiveToken(User user) {
		StringBuilder tokenBuilder = new StringBuilder();
		tokenBuilder.append(user.getUsername());
		tokenBuilder.append(":");
		tokenBuilder.append(user.getPassword());
		tokenBuilder.append(":");
		tokenBuilder.append(TokenUtils.MAGIC_KEY);

		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm available!");
		}
		String md5Hash = new String(Hex.encode(digest.digest(tokenBuilder.toString().getBytes())));
		return new String(Base64.encode(md5Hash.getBytes()));
	}

	public static boolean validateActiveToken(String activeToken, User user) {
		String signature = TokenUtils.createActiveToken(user);
		return signature.equals(activeToken);
	}
}