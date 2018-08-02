import java.nio.charset.Charset;

/** Created by zhaoyy on 2016/12/19. */
public class FitExample {

  public static void main(String[] args) {

    Word2VecUtils.newWord2Vec()
        .addAllTextFile(
            "/Users/zhaoyuyu/Downloads/【TXT-006】百科全书/", file -> file.getName().endsWith(".txt"))
        .charset(Charset.forName("GB2312"))
        .saveAt("/Users/zhaoyuyu/temp/result", true)
        .build();
  }
}
