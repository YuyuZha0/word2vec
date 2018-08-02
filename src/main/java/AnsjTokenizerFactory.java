import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.InputStream;
import java.util.Objects;

/** Created by zhaoyy on 2016/12/19. */
public final class AnsjTokenizerFactory implements TokenizerFactory {

  private TokenPreProcess tokenPreProcess = null;

  public AnsjTokenizerFactory() {}

  @Override
  public Tokenizer create(String toTokenize) {
    Tokenizer t = new AnsjTokenizer(toTokenize);
    t.setTokenPreProcessor(tokenPreProcess);
    return t;
  }

  @Override
  public Tokenizer create(InputStream toTokenize) {
    throw new UnsupportedOperationException(
        "Could not create Tokenizer with InputStream,Try with String");
  }

  @Override
  public TokenPreProcess getTokenPreProcessor() {
    Objects.requireNonNull(tokenPreProcess, "null tokenPreProcess");
    return tokenPreProcess;
  }

  @Override
  public void setTokenPreProcessor(TokenPreProcess preProcessor) {
    this.tokenPreProcess = preProcessor;
  }
}
