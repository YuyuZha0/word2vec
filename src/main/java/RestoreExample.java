import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.FileNotFoundException;

/** Created by zhaoyy on 2016/12/19. */
public class RestoreExample {

  public static void main(String[] args) throws FileNotFoundException {
    Word2Vec word2Vec = Word2VecUtils.restore("/Users/zhaoyuyu/temp/result");
    System.out.println(word2Vec.wordsNearest("法律", 10));
  }
}
