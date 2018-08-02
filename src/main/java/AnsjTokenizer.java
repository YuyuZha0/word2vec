import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/** Created by zhaoyy on 2016/12/19. */
public final class AnsjTokenizer implements Tokenizer {

  private static final AtomicIntegerFieldUpdater<AnsjTokenizer> INDEX_UPDATER =
      AtomicIntegerFieldUpdater.newUpdater(AnsjTokenizer.class, "index");

  private final List<String> tokenizer;
  private volatile int index = 0;
  private TokenPreProcess tokenPreProcess;

  public AnsjTokenizer(String toTokenize) {
    this.tokenizer = tokenize(toTokenize);
  }

  private static List<String> tokenize(String toTokenize) {
    if (Strings.isNullOrEmpty(toTokenize)) return Collections.emptyList();
    return StreamSupport.stream(ToAnalysis.parse(toTokenize).spliterator(), false)
        .map(Term::getName)
        .filter(s -> !Strings.isNullOrEmpty(s))
        .collect(Collectors.toList());
  }

  @Override
  public boolean hasMoreTokens() {
    return index < tokenizer.size();
  }

  @Override
  public int countTokens() {
    return tokenizer.size();
  }

  @Override
  public String nextToken() {
    int i = INDEX_UPDATER.getAndIncrement(this);
    Preconditions.checkPositionIndex(i, tokenizer.size());
    String base = tokenizer.get(i);
    if (tokenPreProcess != null) base = tokenPreProcess.preProcess(base);
    return base;
  }

  @Override
  public List<String> getTokens() {
    return tokenizer;
  }

  @Override
  public void setTokenPreProcessor(TokenPreProcess tokenPreProcessor) {
    this.tokenPreProcess = tokenPreProcessor;
  }
}
