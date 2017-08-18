package com.my.service.lucense.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.my.util.DateUtil;

public class Indexer {
	// д������ʵ����ָ��Ŀ¼��
	private IndexWriter writer;

	public Indexer(String indexDir) throws IOException {
		// �õ���������Ŀ¼��·��
		Directory dir = FSDirectory.open(Paths.get(indexDir));

		// ʵ����������
		Analyzer analyzer = new StandardAnalyzer();

		// ʵ����IndexWriterConfig
		IndexWriterConfig con = new IndexWriterConfig(analyzer);

		// ʵ����IndexWriter
		writer = new IndexWriter(dir, con);
	}

	/**
	 * �ر�д����
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	public void close() throws IOException {
		writer.close();
	}

	/**
	 * ����ָ��Ŀ¼�������ļ�
	 * 
	 * @throws Exception
	 */
	public int index(String dataDir) throws Exception {

		// �����ļ����飬ѭ���ó�Ҫ���������ļ�
		File[] file = new File(dataDir).listFiles();

		for (File files : file) {

			// ���⿪ʼ����ÿ���ļ�������
			indexFile(files);
		}

		// ���������˶��ٸ��ļ����м����ļ����ؼ���
		return writer.numDocs();
	}

	/**
	 * ����ָ���ļ�
	 * 
	 * @throws Exception
	 */
	private void indexFile(File files) throws Exception {

		System.out.println("�����ļ���" + files.getCanonicalPath());

		// ����Ҫһ��һ�е��ң�����������Ϊ�ĵ�������Ҫ�õ������У����ĵ�
		Document document = getDocument(files);

		// ��ʼд��,�Ͱ��ĵ�д���������ļ���ȥ�ˣ�
		writer.addDocument(document);

	}

	/**
	 * ����ĵ������ĵ��������������ֶ�
	 * 
	 * ����ĵ����൱�����ݿ����һ��
	 * 
	 * @throws Exception
	 */
	private Document getDocument(File files) throws Exception {

		// ʵ����Document
		Document doc = new Document();
		BufferedReader bif = new BufferedReader(new FileReader(files));
		String tempStr = "";
		while((tempStr = bif.readLine()) != null){
			doc.add(new TextField("contents", tempStr,Field.Store.YES));
		}
		bif.close();
		// add():�����úõ������ӵ�Document��Ա���ȷ���������ĵ���
//		doc.add(new TextField("contents", new FileReader(files)));

		// Field.Store.YES�����ļ����������ļ��ΪNO��˵������Ҫ�ӵ������ļ���ȥ
		doc.add(new TextField("FileName", files.getName(), Field.Store.YES));

		// ������·�����������ļ���
		doc.add(new TextField("fullPath", files.getCanonicalPath(), Field.Store.YES));
		
		//��ȡ�ļ�����޸ĵ�ʱ��
		doc.add(new TextField("timeKey",DateUtil.data2String(new Date(files.lastModified()),"yyyy-MM-dd HH:mm:ss"),Field.Store.YES));
		// ����document
		return doc;
	}

	// ��ʼ����д������
	public static void main(String[] args) {

		// ����ָ�����ĵ�·��
		String indexDir = System.getProperty("user.dir")+"\\index" ;

		// ���������ݵ�·��
		String dataDir = "D:\\eclipse-workplace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\logs";

		// д����
		Indexer indexer = null;
		int numIndex = 0;

		// ������ʼʱ��
		long start = System.currentTimeMillis();

		try {
			// ͨ������ָ����·�����õ�indexer
			indexer = new Indexer(indexDir);

			// ��Ҫ����������·��(int:��Ϊ����Ҫ���������ݣ��ж��پͷ��ض��������������ļ�)
			numIndex = indexer.index(dataDir);

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			try {
				indexer.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// ��������ʱ��
		long end = System.currentTimeMillis();

		// ��ʾ���
		System.out.println("������  " + numIndex + "  ���ļ���������  " + (end - start) + "  ����");

	}
}
