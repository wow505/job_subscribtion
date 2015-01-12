import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class TestCrawler {

	private static final int SEED_URL_MAX = 5;
	private static Document doc;

	public static void main(String[] args) {

		try {

			System.setProperty("http.proxyHost", "14.37.69.97");
			System.setProperty("http.proxyPort", "3128");

			String[] url_list = new String[SEED_URL_MAX];
			String root_url = new String(
					"http://www.korchambiz.net/COMPI/compi_part.jsp?page=");

			url_list[0] = new String(
					"&searchName=&searchText=&orderKind3=&orderKind2=2013&orderKind=SALEAMT&page_cd=010302");
			url_list[1] = new String(
					"&searchName=&searchText=&orderKind=1&orderKind2=&country_code=&page_cd=010303");
			url_list[2] = new String(
					"&searchName=&searchText=&orderKind=1&page_cd=010306");
			url_list[3] = new String(
					"&searchName=&searchText=&orderKind=1&country_code=&page_cd=010308");
			url_list[4] = new String(
					"&searchName=&searchText=&orderKind=1&orderKind2=&page_cd=010305");

			for (int i = 4; i < SEED_URL_MAX; i++) {

				BufferedWriter out = new BufferedWriter(new FileWriter(
						String.valueOf(i) + ".txt"));
				// detecting last page

				int last_page = extractLastPageByURL(root_url + "1"
						+ url_list[i]);

				while (last_page != 0) {

					URL url = new URL(root_url + String.valueOf(last_page)
							+ url_list[i]);

					HttpURLConnection urlConn = (HttpURLConnection) url
							.openConnection();
					urlConn.addRequestProperty("User-Agent", "Chrome");
					
					InputStream is = null;
					InputStreamReader isr = null;

					if (urlConn.getResponseCode() >= 400) {
						urlConn.disconnect();
						continue;
					} else {
						is = urlConn.getInputStream();
						isr = new InputStreamReader(is, "UTF-8");
					}

					BufferedReader br = new BufferedReader(isr);
					StringBuffer sbuf = new StringBuffer();
					String str;

					while ((str = br.readLine()) != null) {

						sbuf.append(str + "\r\n");
					}

					doc = Jsoup.parse(sbuf.toString());

					Elements table_tag = doc
							.select("div.board_album_wrap tbody tr");

					for (Element tr_tag : table_tag) {
						if (!tr_tag.select("td a[href^=javascript:checkLogin(")
								.isEmpty()) {
							// System.out.print(tr_tag.select("td a[href^=javascript:checkLogin('").text().replace("\u00a0",
							// "")+ " ");
							out.write(tr_tag
									.select("td a[href^=javascript:checkLogin('")
									.text().replace("\u00a0", "")
									+ " ");
						}

						if (!tr_tag.select("td a[href^=javascript:goHome(")
								.isEmpty()) {
							Pattern pattern = Pattern
									.compile("(\\')(\\S)*(\\')");
							Matcher match = pattern.matcher(tr_tag.select(
									"td a[href^=javascript:goHome(").attr(
									"href"));
							if (match.find()) { // 이미지 태그를 찾았다면,,
								out.write(match.group().replace("\'", ""));
								out.newLine();
								// System.out.println(match.group().replace("\'",""));
							}
						} else {
							out.write("null");
							out.newLine();
						}
					}
					last_page--;
				}
				out.close();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static public int extractLastPageByURL(String url_str) throws IOException {
		int last_page = -1;

		URL url = new URL(url_str);
		URLConnection urlConn = url.openConnection();
		urlConn.addRequestProperty("User-Agent", "Chrome");

		InputStream is = urlConn.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");

		BufferedReader br = new BufferedReader(isr);

		String str;
		StringBuffer sbuf = new StringBuffer();

		while ((str = br.readLine()) != null) {

			sbuf.append(str + "\r\n");
		}

		Document doc = Jsoup.parse(sbuf.toString());

		Elements e = doc.select("img[alt^=마지막]");

		Pattern pattern = Pattern.compile("(page=)([0-9]*)");

		Matcher match = pattern.matcher(e.first().parent().attr("href")
				.toString());

		if (match.find()) { // 이미지 태그를 찾았다면,,
			last_page = Integer.parseInt(match.group(2));
		}

		return last_page;
	}

}
