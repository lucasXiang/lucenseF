package com.my.service.lucense.core;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class ReaderByIndexerTest {

	public static void search(String indexDir, String q) throws IOException, ParseException, InvalidTokenOffsetsException {
		// �õ���ȡ�����ļ���·��
		Directory dir = FSDirectory.open(Paths.get(indexDir));

		// ͨ��dir�õ���·���µ����е��ļ�
		IndexReader reader = DirectoryReader.open(dir);

		// �����Ƕ����ļ���Ҳ��������ĵ���
		System.out.println("����ĵ�����" + reader.maxDoc());
		// ��ȡ��ʵ���ĵ���
		System.out.println("ʵ���ĵ�����" + reader.numDocs());

		// ����������ѯ��
		IndexSearcher is = new IndexSearcher(reader);

		// ʵ����������
		Analyzer analyzer = new StandardAnalyzer();

		// ������ѯ������
		/**
		 * ��һ��������Ҫ��ѯ���ֶΣ� �ڶ��������Ƿ�����Analyzer
		 */
		QueryParser parser = new QueryParser("contents", analyzer);

		// ���ݴ�������p����
		Query query = parser.parse(q);

		// ����������ʼʱ��
		long start = System.currentTimeMillis();

		// ��ʼ��ѯ
		/**
		 * ��һ��������ͨ���������Ĳ��������ҵõ���query�� �ڶ���������Ҫ����ѯ������
		 */
		TopDocs hits = is.search(query, 1000);

		// ������������ʱ��
		long end = System.currentTimeMillis();

		System.out.println("ƥ�� " + q + " ���ܹ�����" + (end - start) + "����" + "��ѯ��" + hits.totalHits + "����¼");

		// ����hits.scoreDocs���õ�scoreDoc
		/**
		 * ScoreDoc:�÷��ĵ�,���õ��ĵ� scoreDocs:�������topDocs����ĵ�����
		 * 
		 * @throws Exception
		 */
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = is.doc(scoreDoc.doc);
			System.out.println("fullPath:" + doc.get("fullPath"));
			System.out.println("FileName:" + doc.get("FileName"));
			System.out.println("contents:" + doc.get("contents"));
		}
		System.out.println("==================================================");
		// ���
		QueryScorer scorer = new QueryScorer(query);
		// ��ʾ�÷ָߵ�Ƭ��
		Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
		// ���ñ�ǩ�ڲ��ؼ��ֵ���ɫ
		// ��һ����������ǩ��ǰ�벿�֣��ڶ�����������ǩ�ĺ�벿�֡�
		SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");
		// ��һ�������ǶԲ鵽�Ľ������ʵ�������ڶ�����Ƭ�ε÷֣���ʾ�÷ָߵ�Ƭ�Σ���ժҪ��
		Highlighter highlighter = new Highlighter(simpleHTMLFormatter, scorer);
		// ����Ƭ��
		highlighter.setTextFragmenter(fragmenter);
		// ����topDocs
		/**
		 * ScoreDoc:�Ǵ���һ���������ضȵ÷����ĵ���ŵ���Ϣ�Ķ��� scoreDocs:�����ļ�������
		 * 
		 * @throws Exception
		 */
		for (ScoreDoc scoreDoc : hits.scoreDocs) {

			// ��ȡ�ĵ�
			Document document = is.doc(scoreDoc.doc);

			// ���ȫ·��
			System.out.println("fullPath:" + document.get("fullPath"));
			System.out.println("contents:" + document.get("contents"));

			String desc = document.get("contents");
			if (desc != null) {

				// ��ȫ���÷ָߵ�ժҪ����ʾ����

				// ��һ�������Ƕ��ĸ������������ã��ڶ����������ķ�ʽ����
				TokenStream tokenStream = analyzer.tokenStream("contents", new StringReader(desc));

				// ��ȡ��ߵ�Ƭ��
				System.out.println(highlighter.getBestFragment(tokenStream, desc));
			}
		}

		// �ر�reader
		reader.close();
	}

	// ����
	public static void main(String[] args) {

		String indexDir = "D:\\eclipse-workplace-lucense\\index";
		String q = "/wisdom_house/resources/js/index.js";

		try {
			search(indexDir, q);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
