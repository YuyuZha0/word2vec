import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created by zhaoyy on 2016/12/19. */
@Slf4j
public final class Word2VecUtils {

  private static final Pattern SENTENCE_PATTERN = Pattern.compile("[\u4e00-\u9fa50-9a-zA-Z]+");

  private Word2VecUtils() {}

  private static Word2Vec fit(Collection<String> sentences, File file) {

    Preconditions.checkArgument(sentences != null && !sentences.isEmpty(), "empty sentences!");
    SentenceIterator iterator = new CollectionSentenceIterator(sentences);
    TokenizerFactory tokenizerFactory = new AnsjTokenizerFactory();
    tokenizerFactory.setTokenPreProcessor(new ChineseTokenPreProcess());

    return fit(iterator, tokenizerFactory, file);
  }

  private static Word2Vec fit(
      SentenceIterator iterator, TokenizerFactory tokenizerFactory, File file) {

    log.info("Building model....");
    Word2Vec vec =
        new Word2Vec.Builder()
            .minWordFrequency(5)
            .iterations(1)
            .layerSize(100)
            .seed(42)
            .windowSize(5)
            .iterate(iterator)
            .tokenizerFactory(tokenizerFactory)
            .build();

    log.info("Fitting Word2Vec model....");
    vec.fit();

    if (file != null) {
      log.info("model will be write to path[{}]", file.getAbsolutePath());
      WordVectorSerializer.writeWord2VecModel(vec, file);
    } else {
      log.info("model will not be saved");
    }

    return vec;
  }

  public static Word2Vec restore(@NonNull String path) throws FileNotFoundException {

    Preconditions.checkArgument(!Strings.isNullOrEmpty(path), "illegal path");
    Word2Vec vec = WordVectorSerializer.readWord2VecModel(path);
    return vec;
  }

  private static StringBuilder readAllText(Collection<File> files, Charset charset) {
    StringBuilder builder = new StringBuilder(files.size() * 1000);
    for (File file : files) {
      log.info("reading text from [{}]...", file.getAbsolutePath());
      try (InputStream in = new FileInputStream(file)) {
        Scanner scanner = new Scanner(in, charset.name());
        while (scanner.hasNextLine()) {
          builder.append(scanner.nextLine());
          // builder.append('\n');
        }
        scanner.close();
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }
    return builder;
  }

  public static Builder newWord2Vec() {
    return new Builder();
  }

  @SuppressWarnings("WeakerAccess")
  public static final class Builder {
    private final ImmutableSet.Builder<File> files = ImmutableSet.builder();
    private Charset charset = Charsets.UTF_8;
    private File file = null;

    public Builder addTextFile(@NonNull File file) {
      Preconditions.checkArgument(file != null && file.isFile(), "invalid file");
      files.add(file);
      return this;
    }

    public Builder addAllTextFile(String path, Predicate<File> filter) {
      File dir = new File(path);
      Preconditions.checkArgument(dir.exists() && dir.isDirectory(), path + " is not a directory");
      Files.fileTreeTraverser().breadthFirstTraversal(dir).filter(filter).forEach(files::add);
      return this;
    }

    public Builder addAllTextFile(@NonNull Collection<File> files) {
      Preconditions.checkArgument(files != null && files.size() != 0, "empty files");
      for (File file : files) {
        addTextFile(file);
      }
      return this;
    }

    public Builder charset(@NonNull Charset charset) {
      Preconditions.checkNotNull(charset, "null charset");
      this.charset = charset;
      return this;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Builder saveAt(@NonNull String path, boolean delOld) {
      Preconditions.checkArgument(!Strings.isNullOrEmpty(path), "illegal path");
      File file = new File(path);
      if (file.exists()) {
        log.info("[{}] already exists", file.getAbsolutePath());
        if (delOld) {
          log.info("[{}] will be deleted", file.getAbsolutePath());
          file.delete();
          try {
            file.createNewFile();
          } catch (IOException e) {
            log.error(e.getMessage(), e);
          }
        } else {
          file = null;
        }
      } else {
        try {
          file.createNewFile();
        } catch (IOException e) {
          log.error(e.getMessage(), e);
        }
      }
      this.file = file;
      return this;
    }

    public Word2Vec build() {
      CharSequence cs = readAllText(files.build(), charset);
      Matcher matcher = SENTENCE_PATTERN.matcher(cs);
      List<String> sentences = new ArrayList<>(1000);
      while (matcher.find()) {
        sentences.add(matcher.group());
      }
      return fit(sentences, file);
    }
  }
}
