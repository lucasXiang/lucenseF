package com.my.service.lucense.core;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Arrays;

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
			String[] strArrays = doc.getValues("contents");
			for(int i=0;i<strArrays.length;i++){
				int index = strArrays[i].indexOf(q);
				if(index != -1){
					System.out.println("������"+(i+1)+";������"+(index+1));
					System.out.println("�ı����ݣ�"+strArrays[i]);
					break;
				}
			}
//			System.out.println("contents:" + Arrays.toString(doc.getValues("contents")));
			System.out.println("timeKey:" + doc.get("timeKey"));
		}
		

		// �ر�reader
		reader.close();
	}

	// ����
	public static void main(String[] args) {

		String indexDir = System.getProperty("user.dir")+"\\index";
		String q = "nihaoshijie";

		try {
			search(indexDir, q);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
