package org.acc.word2vec.demo;

import org.acc.word2vec.core.Word2VecUtils;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.FileNotFoundException;

/**
 * Created by zhaoyy on 2016/12/19.
 */
public class RestoreDemo {

    public static void main(String[] args) throws FileNotFoundException {
        Word2Vec word2Vec = Word2VecUtils
                .restore("D:\\tmp\\word2vec\\result");
        System.out.println(word2Vec.wordsNearest("正义", 10));
    }
}
