# word2vec
a word2vec impl of Chinese language, based on deeplearning4j and ansj 


Fit example:
```
Word2VecUtils.newWord2Vec()
        .addAllTextFile(
            "/Users/zhaoyuyu/Downloads/【TXT-006】百科全书/", file -> file.getName().endsWith(".txt"))
        .charset(Charset.forName("GB2312"))
        .saveAt("/Users/zhaoyuyu/temp/result", true)
        .build();
```
