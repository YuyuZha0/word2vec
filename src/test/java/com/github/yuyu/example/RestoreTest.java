package com.github.yuyu.example;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.junit.Test;

/**
 * Created by zhaoyy on 2016/12/19.
 */
public class RestoreTest {

  @Test
  public void test() {
    Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel("/Users/fishzhao/IdeaProjects/word2vec/src/test/resources/word2vec.bin");
    System.out.println(word2Vec.wordsNearest("鬼", 10));
  }
}
