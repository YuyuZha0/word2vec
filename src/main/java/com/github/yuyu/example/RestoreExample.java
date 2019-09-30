package com.github.yuyu.example;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.FileNotFoundException;

/** Created by zhaoyy on 2016/12/19. */
public class RestoreExample {

  public static void main(String[] args) throws FileNotFoundException {
    Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel("/Users/zhaoyuyu/Downloads/word2vec");
    System.out.println(word2Vec.wordsNearest("鬼", 10));
  }
}
