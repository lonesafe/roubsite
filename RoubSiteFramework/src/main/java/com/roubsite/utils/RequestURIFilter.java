package com.roubsite.utils;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletRequest;

public class RequestURIFilter {
	private final Pattern[] patterns;

	public RequestURIFilter(String uris) {
		if ((uris == null) || (uris.equals(""))) {
			uris = "";
		}
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Pattern> patterns = new ArrayList<Pattern>();
		for (String uri : uris.split(",")) {
			uri = uri.trim();
			if ((uri != null) && (!uri.equals(""))) {
				names.add(uri);
				patterns.add(Pattern.compile(pathNameToRegex(uri, 16384)));
			}
		}
		if (!patterns.isEmpty()) {
			this.patterns = ((Pattern[]) patterns.toArray(new Pattern[patterns.size()]));
		} else {
			this.patterns = null;
		}
	}

	public boolean matches(HttpServletRequest request) {
		if (this.patterns != null) {
			String requestURI = request.getRequestURI();
			for (Pattern pattern : this.patterns) {
				if (pattern.matcher(requestURI).find()) {
					return true;
				}
			}
		}
		return false;
	}

	public static String normalizePathName(String name) {
		if (name == null) {
			return null;
		}
		return name.trim().replaceAll("[/\\\\]+", "/");
	}

	public static String pathNameToRegex(String pattern, int options) throws PatternSyntaxException {
		pattern = normalizePathName(pattern);

		int lastToken = 0;
		StringBuilder buf = new StringBuilder(pattern.length() * 2);

		boolean forceMatchPrefix = (options & 0x4000) != 0;
		boolean forceAbsolutePath = (options & 0x1000) != 0;
		boolean forceRelativePath = (options & 0x2000) != 0;

		if (((forceMatchPrefix) && (!pattern.startsWith("*")) && (!pattern.startsWith("/"))
				&& (!pattern.startsWith("?"))) || ((pattern.length() > 0) && (pattern.charAt(0) == '/'))) {
			buf.append("^");
		}

		if ((pattern.length() == 1) && (pattern.charAt(0) == '/')) {
			pattern = "";
		}

		for (int i = 0; i < pattern.length(); ++i) {
			char ch = pattern.charAt(i);

			if ((forceAbsolutePath) && (lastToken == 0) && (ch != '/')) {
				throw new PatternSyntaxException("Syntax Error", pattern, i);
			}

			switch (ch) {
			case '/':
				if (lastToken == 1)
					throw new PatternSyntaxException("Syntax Error", pattern, i);
				if ((forceRelativePath) && (lastToken == 0)) {
					throw new PatternSyntaxException("Syntax Error", pattern, i);
				}

				if (lastToken != 4) {
					buf.append("\\/(?!\\/)");
				}

				lastToken = 1;
				break;
			case '*':
				int j = i + 1;

				if ((j < pattern.length()) && (pattern.charAt(j) == '*')) {
					i = j;

					if ((lastToken != 0) && (lastToken != 1)) {
						throw new PatternSyntaxException("Syntax Error", pattern, i);
					}

					lastToken = 4;
					buf.append("([\\w\\-\\.]+(?:\\/(?!\\/)[\\w\\-\\.]*)*(?=\\/|$)|)\\/?");
				} else {
					if ((lastToken == 3) || (lastToken == 4)) {
						throw new PatternSyntaxException("Syntax Error", pattern, i);
					}

					lastToken = 3;
					buf.append("([\\w\\-\\.]*)");
				}

				break;
			case '?':
				lastToken = 5;
				buf.append("([\\w\\-\\.])");
				break;
			default:
				if (lastToken == 4) {
					throw new PatternSyntaxException("Syntax Error", pattern, i);
				}

				if ((Character.isLetterOrDigit(ch)) || (ch == '_') || (ch == '-')) {
					if ((lastToken == 0) && (((!forceMatchPrefix) || (i != 0))))
						buf.append("\\b").append(ch);
					else if (i + 1 == pattern.length())
						buf.append(ch).append("\\b");
					else
						buf.append(ch);
				} else if (ch == '.')
					buf.append('\\').append('.');
				else {
					throw new PatternSyntaxException("Syntax Error", pattern, i);
				}

				lastToken = 2;
			}
		}

		return buf.toString();
	}
}
