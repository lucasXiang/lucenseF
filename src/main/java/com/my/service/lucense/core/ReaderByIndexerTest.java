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
		// 得到读取索引文件的路径
		Directory dir = FSDirectory.open(Paths.get(indexDir));

		// 通过dir得到的路径下的所有的文件
		IndexReader reader = DirectoryReader.open(dir);

		// 公共是多少文件，也就是最大文档数
		System.out.println("最大文档数：" + reader.maxDoc());
		// 读取的实际文档数
		System.out.println("实际文档数：" + reader.numDocs());

		// 建立索引查询器
		IndexSearcher is = new IndexSearcher(reader);

		// 实例化分析器
		Analyzer analyzer = new StandardAnalyzer();

		// 建立查询解析器
		/**
		 * 第一个参数是要查询的字段； 第二个参数是分析器Analyzer
		 */
		QueryParser parser = new QueryParser("contents", analyzer);

		// 根据传进来的p查找
		Query query = parser.parse(q);

		// 计算索引开始时间
		long start = System.currentTimeMillis();

		// 开始查询
		/**
		 * 第一个参数是通过传过来的参数来查找得到的query； 第二个参数是要出查询的行数
		 */
		TopDocs hits = is.search(query, 1000);

		// 计算索引结束时间
		long end = System.currentTimeMillis();

		System.out.println("匹配 " + q + " ，总共花费" + (end - start) + "毫秒" + "查询到" + hits.totalHits + "个记录");

		// 遍历hits.scoreDocs，得到scoreDoc
		/**
		 * ScoreDoc:得分文档,即得到文档 scoreDocs:代表的是topDocs这个文档数组
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
					System.out.println("行数："+(i+1)+";列数："+(index+1));
					System.out.println("文本内容："+strArrays[i]);
					break;
				}
			}
//			System.out.println("contents:" + Arrays.toString(doc.getValues("contents")));
			System.out.println("timeKey:" + doc.get("timeKey"));
		}
		

		// 关闭reader
		reader.close();
	}

	// 测试
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
