import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Scrapes information about the basic Perl functions from http://perl.org,
 * so we can shove it into XML for code completion by RSyntaxTextArea.<p>
 *
 * Note this class is dependent on the HTML structure of the pages of
 * http://perldoc.perl.org, so if they change, this program may no longer
 * work without modification.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Main {

	private static final String SITE_BASE = "http://perldoc.perl.org/";
	private static final Pattern EMPTY_PARAGRAPH_PATTERN = Pattern.compile("^<p>\\s*</p>$");
	private static final String FUNCTION_LIST = SITE_BASE + "index-functions.html";
	private static final Pattern FUNCTION_LIST_PATTERN = Pattern.compile("<a href=\"functions/(.+).html\">\\1</a>");


	public static void main(String[] args) throws Exception {

		long start = System.currentTimeMillis();
		File file = new File("perl5.txt");
		PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		w.println("# Format:");
		w.println("# function func-name");
		w.println("# arg-1-name|");
		w.println("# arg-2-name|");
		w.println("# ...");
		w.println("# desc-lines");
		w.println("# -----");
		w.println();

		int noDescCount = 0;
		// Get the list of Perl functions.
		URL url = new URL(FUNCTION_LIST);
		URLConnection con = url.openConnection();
		BufferedReader r = new BufferedReader(new InputStreamReader(con.getInputStream()));

		try {

			String line;
			while ((line=r.readLine())!=null) {

				Matcher m = FUNCTION_LIST_PATTERN.matcher(line);
				if (m.find()) {

					String func = m.group(1);
					String desc = null;

					URL url2 = new URL(SITE_BASE + "functions/" + func + ".html");
					URLConnection con2 = url2.openConnection();
					BufferedReader r2 = new BufferedReader(new InputStreamReader(con2.getInputStream()));
					boolean foundDeclaration = false;
					List overloads = new ArrayList();

					try {

						Pattern p = Pattern.compile("<li><a name=\"" + func + "[^>]+></a><b>" + func + "\\s?(.+)?</b>$");
OUTER:
						while ((line=r2.readLine())!=null) {

							m = p.matcher(line);
							if (m.find()) {

								String[] parms = null;
								if (m.groupCount()>0 && m.group(1)!=null) {
									parms = m.group(1).toLowerCase().split("\\s*,\\s*");
								}
								StringBuilder overload = new StringBuilder();
								overload.append("function ").append(func).append('\n');
								int parmCount = parms==null ? 0 : parms.length;
								for (int i=0; i<parmCount; i++ ) {
									overload.append(parms[i]).append("|\n");
								}
								overloads.add(overload.toString());
								foundDeclaration = true;
								//break; May be overloads here, so don't quit

							}

							else if (foundDeclaration && line.matches("^<p>.+")) {
								if (EMPTY_PARAGRAPH_PATTERN.matcher(line).matches()) { // Sometimes just 1 of these at beginning
									continue;
								}
								StringBuilder sb = new StringBuilder(line.substring(3));
								sb.append('\n');
								boolean possiblyDone = false;
								while ((line=r2.readLine())!=null) {
									if (possiblyDone && line.equals("</ul>")) {
										desc = sb.substring(0, sb.length()-11);
										break OUTER;
									}
									else if (line.endsWith("</li>")) {
										possiblyDone = true;
									}
									sb.append(line).append('\n');
								}
							}

						}

					} finally {
						r2.close();
					}

					if (!foundDeclaration) {
						System.err.println("... Info not found for: " + func + " (" + url2.toExternalForm() + ")");
					}
					else if (desc==null) {
						System.out.println("... Description not found!  (" + url2.toExternalForm() + ")");
						noDescCount++;
					}

					if (foundDeclaration) {
						for (int i=0; i<overloads.size(); i++) {
							String overload = (String)overloads.get(i);
							w.print(overload); // Already has a newline
							if (desc!=null) {
								w.println(desc);
							}
							w.println("-----\n");
						}
					}

				}

			}

		} finally {
			r.close();
		}

		w.close();
		System.out.println("Functions with no description scraped: " + noDescCount);
		System.out.println("NOTE: As of 03feb2010, expect 1 - shmread (has no desc!)");
		System.out.println("\nOutput saved to: " + file.getAbsolutePath());
		long time = System.currentTimeMillis() - start;
		System.out.println("Time: " + (time/1000f) + " seconds");

	}


}