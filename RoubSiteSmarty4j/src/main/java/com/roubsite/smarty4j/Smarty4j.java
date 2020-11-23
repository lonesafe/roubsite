package com.roubsite.smarty4j;

//import jargs.gnu.CmdLineParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/**
 * Smarty4j Command Tools
 * 
 * @version 1.5.0, 2011/01/01
 * @author Ouyang Xianwei
 * @since Common 1.0
 */
public class Smarty4j {

	public static void main(String[] args) {
//		CmdLineParser parser = new CmdLineParser();
//		CmdLineParser.Option leftOpt = parser.addStringOption("left");
//		CmdLineParser.Option rightOpt = parser.addStringOption("right");
//		CmdLineParser.Option charsetOpt = parser.addStringOption("charset");
//		CmdLineParser.Option outputOpt = parser.addStringOption('o', "output");
//
//		Reader in = null;
//		Writer out = null;
//
//		try {
//			parser.parse(args);
//
//			String[] fileArgs = parser.getRemainingArgs();
//			
//			Engine engine = new Engine();
//
//			String left = (String) parser.getOptionValue(leftOpt);
//			if (left != null) {
//				engine.setLeftDelimiter(left);
//			}
//
//			String right = (String) parser.getOptionValue(rightOpt);
//			if (right != null) {
//				engine.setRightDelimiter(right);
//			}
//
//			String charset = (String) parser.getOptionValue(charsetOpt);
//			if (charset == null) {
//				charset = System.getProperty("file.encoding");
//				if (charset == null) {
//					charset = "UTF-8";
//				}
//			}
//			engine.setEncoding(charset);
//
//			String output = (String) parser.getOptionValue(outputOpt);
//				if (output == null) {
//					out = new OutputStreamWriter(System.out, charset);
//				} else {
//					out = new OutputStreamWriter(new FileOutputStream(output), charset);
//				}
//
//			try {
//				if (fileArgs.length == 0) {
//					engine.setTemplatePath(System.getProperty("user.dir"));
//					in = new InputStreamReader(System.in, charset);
//				} else {
//					File file = new File(fileArgs[0]);
//					engine.setTemplatePath(file.getParentFile().getAbsolutePath());
//					in = new InputStreamReader(new FileInputStream(file), charset);
//				}
//			} catch (Exception e) {
//				throw new NullPointerException();
//			}
//
//			new Template(engine, null, in, true).merge(new Context(), out);
//		} catch (NullPointerException e) {
//			e.printStackTrace();
//			System.out.println("java -jar [JAR_FILE] filename [OPTIONS]");
//			System.out.println("OPTIONS:");
//			System.out.println("--left");
//			System.out.println("--right");
//			System.out.println("--charset");
//			System.out.println("--output -o");
//			System.exit(1);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (in != null) {
//				try {
//					in.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//
//			if (out != null) {
//				try {
//					out.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}
}
