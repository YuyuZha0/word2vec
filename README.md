# word2vec
a word2vec impl of Chinese language, based on deeplearning4j and ansj 


Fit com.github.yuyu.example:
```
 Word2Vec word2Vec =
        Word2VecCN.builder()
            .charset(Charset.forName("GB2312"))
            .addFile("/Users/zhaoyuyu/Downloads/阅微草堂笔记.txt")
            .addFile("/Users/zhaoyuyu/Downloads/白话加强版阅微草堂笔记.txt")
            .build()
            .fit();

    WordVectorSerializer.writeWord2VecModel(word2Vec, "/Users/zhaoyuyu/Downloads/word2vec");

    System.out.println(word2Vec.wordsNearest("鬼", 10));
```

Restore com.github.yuyu.example:
```
public static void main(String[] args) throws FileNotFoundException {
    Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel("/Users/zhaoyuyu/Downloads/word2vec");
    System.out.println(word2Vec.wordsNearest("鬼", 10));
  }
```
