package com.roubsite.smarty4j.statement.modifier;

import java.util.regex.Pattern;

import com.roubsite.smarty4j.expression.check.TrueCheck;
import com.roubsite.smarty4j.statement.Definition;
import com.roubsite.smarty4j.statement.Definition.Type;
import com.roubsite.smarty4j.statement.Modifier;

/**
 * This strips out markup tags, basically anything between &lt; and &gt;.
 * 
 * <code>
 * <br>
 * <b>Example:</b><br>
 * context.set("articleTitle", "Blind Woman Gets &lt;font face=\"helvetica\"&gt;New Kidney&lt;/font&gt; from Dad she Hasn't Seen in &lt;b&gt;years&lt;/b&gt;.");<br>
 * <br>
 * <b>Template:</b><br>
 * {$articleTitle}<br>
 * {$articleTitle|strip_tags}<br>
 * {$articleTitle|strip_tags:false}<br>
 * <br>
 * <b>Output:</b><br>
 * Blind Woman Gets &lt;font face="helvetica"&gt;New Kidney&lt;/font&gt; from Dad she Hasn't Seen in &lt;b&gt;years&lt;/b&gt;.<br>
 * Blind Woman Gets  New Kidney  from Dad she Hasn't Seen in  years .<br>
 * Blind Woman Gets New Kidney from Dad she Hasn't Seen in years.<br>
 * </code>
 * 
 * @version 1.1.0, 2015/05/16
 * @author Rick Jone
 * @soubSite Smarty1.0
 */
public class $strip_tags extends Modifier {

	private static final Definition[] definitions = { Definition.forModifier(Type.BOOLEAN,
	    TrueCheck.VALUE) };

	private static final Pattern p = Pattern
	    .compile("</?\\p{Alpha}+ *( +\\p{Alpha}+ *=(\"(\\\\.|[\\x00-\\x21\\x23-\\x5b\\u005d-\\uffff])*\"|'(\\\\.|[\\x00-\\x26\\x28-\\x5b\\u005d-\\uffff])*'|[\\x00-\\x3d\\u003f-\\uffff]*) *)*>");

	public static Object execute(Object obj, boolean oneSpace) {
		return p.matcher(obj.toString()).replaceAll(oneSpace ? " " : "");
	}

	@Override
	public Definition[] getDefinitions() {
		return definitions;
	}
}