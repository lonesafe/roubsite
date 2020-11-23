package com.roubsite.security.utils;

import java.security.SecureRandom;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class RSPasswordUtil {
	/**
	 * Implementation of PasswordEncoder that uses the BCrypt strong hashing
	 * function. Clients can optionally supply a "strength" (a.k.a. log rounds in
	 * BCrypt) and a SecureRandom instance. The larger the strength parameter the
	 * more work will have to be done (exponentially) to hash the passwords. The
	 * default value is 10.
	 *
	 * @author Dave Syer
	 *
	 */
	private Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
	private final Logger logger = Logger.getLogger(getClass());

	private final int strength;

	private final SecureRandom random;

	public RSPasswordUtil() {
		this(-1);
	}

	/**
	 * @param strength the log rounds to use, between 4 and 31
	 */
	public RSPasswordUtil(int strength) {
		this(strength, null);
	}

	/**
	 * @param strength the log rounds to use, between 4 and 31
	 * @param random   the secure random instance to use
	 *
	 */
	public RSPasswordUtil(int strength, SecureRandom random) {
		if (strength != -1 && (strength < BCrypt.MIN_LOG_ROUNDS || strength > BCrypt.MAX_LOG_ROUNDS)) {
			throw new IllegalArgumentException("Bad strength");
		}
		this.strength = strength;
		this.random = random;
	}

	public String encode(CharSequence rawPassword) {
		String salt;
		if (strength > 0) {
			if (random != null) {
				salt = BCrypt.gensalt(strength, random);
			} else {
				salt = BCrypt.gensalt(strength);
			}
		} else {
			salt = BCrypt.gensalt();
		}
		return BCrypt.hashpw(rawPassword.toString(), salt);
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (encodedPassword == null || encodedPassword.length() == 0) {
			logger.warn("Empty encoded password");
			return false;
		}

		if (!BCRYPT_PATTERN.matcher(encodedPassword).matches()) {
			logger.warn("Encoded password does not look like BCrypt");
			return false;
		}
		return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
	}

	public static void main(String[] args) {
		RSPasswordUtil d = new RSPasswordUtil();
		System.out.println(d.encode("admin"));
		System.out.println(d.encode("asdfasdf"));
		System.out.println(d.matches("asdfasdf", "$2a$10$jgE5w59ccswnDH9TvXql.OeVPsoLpBtlMJ9xCWhsoJwkEd8muHdUC"));
	}
}